package com.testdroid.api;

import com.google.api.client.http.HttpResponse;
import com.testdroid.api.dto.Context;
import com.testdroid.api.filter.FilterEntry;
import com.testdroid.api.model.*;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.testdroid.api.dto.MappingKey.*;
import static com.testdroid.api.dto.Operand.EQ;
import static com.testdroid.api.filter.FilterEntry.trueFilterEntry;
import static com.testdroid.api.model.APIAccessGroup.Scope.USER;
import static com.testdroid.api.model.APIDevice.OsType.ANDROID;
import static com.testdroid.api.model.APIFileConfig.Action.INSTALL;
import static com.testdroid.api.model.APIFileConfig.Action.RUN_TEST;
import static com.testdroid.api.model.APITestRun.State.WAITING;
import static com.testdroid.api.util.BitbarUtils.loadFile;
import static com.testdroid.cloud.test.categories.TestTags.API_CLIENT;
import static java.lang.Integer.MAX_VALUE;
import static java.util.Collections.singletonMap;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * @author Damian Sniezek <damian.sniezek@bitbar.com>
 */
@Tag(API_CLIENT)
class APIUserAPIClientTest extends BaseAPIClientTest {

    @ParameterizedTest
    @ArgumentsSource(APIClientProvider.class)
    void getDevicesTest(APIClient apiClient) throws APIException {
        APIList<APIDevice> allDevices = apiClient.getDevices(new Context<>(APIDevice.class)).getEntity();
        assertThat(allDevices.isEmpty()).isFalse();

        APIList<APIDevice> androidDevices = apiClient.getDevices(new Context<>(APIDevice.class)
                .addFilter(new FilterEntry(OS_TYPE, EQ, ANDROID.name()))).getEntity();
        assertThat(androidDevices.isEmpty()).isFalse();
        assertThat(androidDevices.getData().stream().allMatch(d -> d.getOsType().equals(ANDROID))).isTrue();
        assertThat(androidDevices.getTotal()).isLessThanOrEqualTo(allDevices.getTotal());

        APIList<APIDevice> samsungDevices = apiClient.getDevices(new Context<>(APIDevice.class)
                .addFilter(new FilterEntry(DISPLAY_NAME, EQ, "%Samsung%"))).getEntity();
        assertThat(samsungDevices.getTotal()).isLessThanOrEqualTo(androidDevices.getTotal());
    }

    @ParameterizedTest
    @ArgumentsSource(APIClientProvider.class)
    void getLabelGroups(APIClient apiClient) throws APIException {
        APIList<APILabelGroup> labelGroups = apiClient.getLabelGroups(new Context<>(APILabelGroup.class)
                .addFilter(new FilterEntry(DISPLAY_NAME, EQ, "Device groups"))).getEntity();
        assertThat(labelGroups.getTotal()).isEqualTo(1);
        APILabelGroup deviceGroupLabelGroup = labelGroups.getData().stream().findFirst().orElseThrow(APIException::new);
        assertThat(deviceGroupLabelGroup.getDisplayName()).isEqualTo("Device groups");
        APIList<APIDeviceProperty> apiDeviceProperties = deviceGroupLabelGroup.getDevicePropertiesResource(new
                Context<>(APIDeviceProperty.class).setLimit(0)).getEntity();
        assertThat(apiDeviceProperties.getTotal()).isLessThanOrEqualTo(4);
        assertThat(apiDeviceProperties.getData()).extracting(APIDeviceProperty::getDisplayName)
                .containsExactly("Android devices", "iOS devices", "Trial devices", "Desktops");
    }

    @ParameterizedTest
    @ArgumentsSource(APIClientProvider.class)
    void uploadFileTest(APIClient apiClient) throws APIException {
        APIUserFile apiUserFile = apiClient.me().uploadFile(loadFile(APP_PATH));
        assertThat(apiUserFile.getName()).isEqualTo("BitbarSampleApp.apk");
        assertThat(apiUserFile.isDuplicate()).isFalse();
        HttpResponse httpResponse;
        //For oAuth2 Authenticated users(DefaultAPIClient) we serve files from backend instead of s3
        if (!(apiClient instanceof DefaultAPIClient)) {
            httpResponse = apiClient.getHttpResponse("/me/files/" + apiUserFile.id + "/file", null);
            assertThat(httpResponse.getHeaders().getFirstHeaderStringValue("x-amz-tagging-count")).isEqualTo("1");
            assertThat(httpResponse.getHeaders()
                    .getFirstHeaderStringValue("x-amz-expiration")).contains("rule-id=\"keep 365d\"");
        }
        //Verify file duplication
        apiUserFile = apiClient.me().uploadFile(loadFile(APP_PATH));
        assertThat(apiUserFile.getName()).isEqualTo("BitbarSampleApp.apk");
        assertThat(apiUserFile.isDuplicate()).isTrue();
        //For oAuth2 Authenticated users(DefaultAPIClient) we serve files from backend instead of s3
        if (!(apiClient instanceof DefaultAPIClient)) {
            httpResponse = apiClient.getHttpResponse("/me/files/" + apiUserFile.id + "/file", null);
            assertThat(httpResponse.getHeaders().getFirstHeaderStringValue("x-amz-tagging-count")).isEqualTo("1");
            assertThat(httpResponse.getHeaders()
                    .getFirstHeaderStringValue("x-amz-expiration")).contains("rule-id=\"keep 365d\"");
        }
    }

    @ParameterizedTest
    @ArgumentsSource(APIClientProvider.class)
    void getAvailableFrameworksTest(APIClient apiClient) throws APIException {
        Context<APIFramework> context = new Context<>(APIFramework.class, 0, MAX_VALUE, EMPTY, EMPTY);
        context.addFilter(new FilterEntry(OS_TYPE, EQ, ANDROID.name()));
        context.addFilter(trueFilterEntry(FOR_PROJECTS));
        context.addFilter(trueFilterEntry(CAN_RUN_FROM_UI));
        APIList<APIFramework> availableFrameworks = apiClient.me().getAvailableFrameworksResource(context)
                .getEntity();
        assertThat(availableFrameworks.getData().stream().allMatch(f -> f.getForProjects() && f.getCanRunFromUI() && f
                .getOsType() == ANDROID)).isTrue();
    }

    @ParameterizedTest
    @ArgumentsSource(APIClientProvider.class)
    void startTestRunTest(APIClient apiClient) throws APIException, InterruptedException {
        APIUser me = apiClient.me();
        APITestRunConfig config = new APITestRunConfig();
        config.setProjectName(generateUnique("testProject"));
        APIFramework defaultApiFramework = getApiFramework(apiClient, "Android Instrumentation");
        config.setOsType(defaultApiFramework.getOsType());
        config.setFrameworkId(defaultApiFramework.getId());
        APIUserFile apkFile = me.uploadFile(loadFile(APP_PATH));
        APIFileConfig apkFileConfig = new APIFileConfig(apkFile.getId(), INSTALL);
        APIUserFile testFile = me.uploadFile(loadFile(TEST_PATH));
        APIFileConfig testFileConfig = new APIFileConfig(testFile.getId(), RUN_TEST);
        config.setFiles(Arrays.asList(apkFileConfig, testFileConfig));
        APIUserFile.waitForVirusScans(apkFile, testFile);
        me.validateTestRunConfig(config);
        APITestRun apiTestRun = me.startTestRun(config);
        assertThat(apiTestRun.getState()).isIn(RUNNING, WAITING);

        apiTestRun.delete();
    }

    @ParameterizedTest
    @ArgumentsSource(APIClientProvider.class)
    void addTagTest(APIClient apiClient) throws APIException, InterruptedException {
        APIUser me = apiClient.me();
        APITestRunConfig config = new APITestRunConfig();
        config.setProjectName(generateUnique("testProject"));
        APIFramework defaultApiFramework = getApiFramework(apiClient, "Android Instrumentation");
        config.setOsType(defaultApiFramework.getOsType());
        config.setFrameworkId(defaultApiFramework.getId());
        APIUserFile apkFile = me.uploadFile(loadFile(APP_PATH));
        APIFileConfig apkFileConfig = new APIFileConfig(apkFile.getId(), INSTALL);
        APIUserFile testFile = me.uploadFile(loadFile(TEST_PATH));
        APIFileConfig testFileConfig = new APIFileConfig(testFile.getId(), RUN_TEST);
        config.setFiles(Arrays.asList(apkFileConfig, testFileConfig));
        APIUserFile.waitForVirusScans(apkFile, testFile);
        me.validateTestRunConfig(config);
        APITestRun apiTestRun = me.startTestRun(config);
        assertThat(apiTestRun.getState()).isIn(RUNNING, WAITING);
        apiTestRun.abort();
        String tag = "aborted";
        APITag apiTag = apiTestRun.addTag(tag);
        apiTestRun.refresh();
        assertThat(apiTestRun.getTagsResource().getTotal()).isEqualTo(1);
        assertThat(apiTestRun.getTagsResource().getEntity().get(0).getName()).isEqualTo(tag);
        apiTag.delete();
        apiTestRun.refresh();
        assertThat(apiTestRun.getTagsResource().getTotal()).isZero();
        apiTestRun.delete();
    }

    @ParameterizedTest
    @ArgumentsSource(APIClientProvider.class)
    void requestScreenshotsZip(APIClient apiClient) throws APIException, InterruptedException, IOException {
        APIUser me = apiClient.me();
        APITestRunConfig config = new APITestRunConfig();
        config.setProjectName(generateUnique("testProject"));
        APIFramework defaultApiFramework = getApiFramework(apiClient, "Android Instrumentation");
        config.setOsType(defaultApiFramework.getOsType());
        config.setFrameworkId(defaultApiFramework.getId());
        APIUserFile apkFile = me.uploadFile(loadFile(APP_PATH));
        APIFileConfig apkFileConfig = new APIFileConfig(apkFile.getId(), INSTALL);
        APIUserFile testFile = me.uploadFile(loadFile(TEST_PATH));
        APIFileConfig testFileConfig = new APIFileConfig(testFile.getId(), RUN_TEST);
        config.setFiles(Arrays.asList(apkFileConfig, testFileConfig));
        APIUserFile.waitForVirusScans(apkFile, testFile);
        me.validateTestRunConfig(config);
        APITestRun apiTestRun = me.startTestRun(config);
        assertThat(apiTestRun.getState()).isIn(RUNNING, WAITING);
        apiTestRun.requestScreenshotsZip();
        APIUserFile file = apiTestRun.getScreenshotsZip();
        while (file.getState() != APIUserFile.State.READY) {
            try {
                TimeUnit.SECONDS.sleep(3);
                file.refresh();
            } catch (InterruptedException ignore) {
            }
        }
        try (InputStream inputStream = file.getFile()) {
            FileUtils.copyInputStreamToFile(inputStream, Files.createTempFile(null, null).toFile());
        }
        apiTestRun.delete();
    }

    @Tag("SDCC-2690")
    @ParameterizedTest
    @ArgumentsSource(APIClientProvider.class)
    void projectSharingTest(APIClient apiClient) throws APIException, InterruptedException {
        APIUser user1 = apiClient.me();
        String projectName = generateUnique("sharedProject");
        APIProject project = user1.createProject(projectName);
        Map<String, Object> data = new HashMap<>();
        data.put(NAME, "sharedProjectAccessGroup");
        data.put(SCOPE, USER);
        APIAccessGroup accessGroup = user1.postResource("/me/access-groups", data, APIAccessGroup.class);

        APIUser apiUser2 = create(ADMIN_API_CLIENT);
        user1.postResource(accessGroup.getSelfURI() + "/users", singletonMap(EMAIL, apiUser2.getEmail()), APIAccessGroup.class);
        user1.postResource(project.getSelfURI() + "/share", singletonMap(ACCESS_GROUP_ID, accessGroup.getId()), APIList.class);

        APIClient apiClient2 = new APIKeyClient(CLOUD_URL, apiUser2.getApiKey());
        apiUser2 = apiClient2.me();

        APITestRunConfig config = new APITestRunConfig();
        config.setProjectId(project.getId());
        APIFramework defaultApiFramework = getApiFramework(apiClient, "Android Instrumentation");
        config.setOsType(defaultApiFramework.getOsType());
        config.setFrameworkId(defaultApiFramework.getId());
        APIUserFile apkFile = apiUser2.uploadFile(loadFile(APP_PATH));
        APIFileConfig apkFileConfig = new APIFileConfig(apkFile.getId(), INSTALL);
        APIUserFile testFile = apiUser2.uploadFile(loadFile(TEST_PATH));
        APIFileConfig testFileConfig = new APIFileConfig(testFile.getId(), RUN_TEST);
        config.setFiles(Arrays.asList(apkFileConfig, testFileConfig));
        APIUserFile.waitForVirusScans(apkFile, testFile);
        apiUser2.validateTestRunConfig(config);
        APITestRun apiTestRun = apiUser2.startTestRun(config);
        apiTestRun.abort();
        assertDoesNotThrow(() -> user1.getFile(apkFile.getId()));
    }

}
