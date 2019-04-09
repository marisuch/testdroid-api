package com.testdroid.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.testdroid.api.APIEntity;
import com.testdroid.api.APIException;
import com.testdroid.api.APIListResource;
import com.testdroid.api.dto.Context;
import com.testdroid.api.util.TimeConverter;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.File;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Łukasz Kajda <lukasz.kajda@bitbar.com>
 * @author Slawomir Pawluk <slawomir.pawluk@bitbar.com>
 */
@XmlRootElement
public class APIUser extends APIEntity {

    public enum Status {
        INACTIVE("Inactive"),
        DISABLED("Disabled"),
        ENABLED("Enabled");

        private final String displayName;

        Status(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    private List<Long> serviceIds;

    private Long accountId;

    private List<Long> accountServiceIds;

    private String address;

    private String city;

    private String code;

    private String country;

    private Date createTime;

    private Date deleteTime;

    private String email;

    private boolean enabled;

    private Boolean isMainUser;

    private Date lastLoginTime;

    private String mainUserEmail;

    private Long mainUserId;

    private String name;

    private String organization;

    private String phone;

    private APIRole[] roles;

    private String state;

    private Status status;

    private String timeZone;

    private String vatId;

    private String apiKey;

    private Long createdById;

    private String createdByEmail;

    @Deprecated
    @XmlType(namespace = "APIUser", name = "APIUserEmailNotification")
    public enum EmailNotification {
        ALWAYS("always"),
        NEVER("never"),
        ON_FAILURE("on failure");

        private final String displayName;

        EmailNotification(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public APIUser() {
    }

    public APIUser(Long id, String email, String name, String state, String country, String city, String code,
            String address, String phone, String organization, String vatId, String timeZone, LocalDateTime createTime,
            LocalDateTime deleteTime, LocalDateTime lastLoginTime, Status status) {
        super(id);
        this.email = email;
        this.name = name;
        this.state = state;
        this.country = country;
        this.city = city;
        this.code = code;
        this.address = address;
        this.phone = phone;
        this.organization = organization;
        this.vatId = vatId;
        this.timeZone = timeZone;
        this.createTime = TimeConverter.toDate(createTime);
        this.deleteTime = TimeConverter.toDate(deleteTime);
        this.lastLoginTime = TimeConverter.toDate(lastLoginTime);
        this.status = status;
        this.enabled = status == Status.ENABLED;
    }

    public APIUser(
            Long id, Long accountId, String email, String name, String state, String country, String city, String code,
            String address, String phone, String organization, String vatId, String timeZone, LocalDateTime createTime,
            LocalDateTime deleteTime, LocalDateTime lastLoginTime, Boolean isMainUser, Long mainUserId,
            String mainUserEmail, String apiKey, Status status, Long createdById, String createdByEmail) {
        this(id, email, name, state, country, city, code, address, phone, organization, vatId, timeZone, createTime,
                deleteTime, lastLoginTime, status);
        this.accountId = accountId;
        this.isMainUser = isMainUser;
        this.mainUserId = mainUserId;
        this.mainUserEmail = mainUserEmail;
        this.apiKey = apiKey;
        this.createdById = createdById;
        this.createdByEmail = createdByEmail;
        this.selfURI = String.format("/users/%s", id);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getVatId() {
        return vatId;
    }

    public void setVatId(String vatId) {
        this.vatId = vatId;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean getIsMainUser() {
        return isMainUser;
    }

    public void setIsMainUser(Boolean isMainUser) {
        this.isMainUser = isMainUser;
    }

    public APIRole[] getRoles() {
        return roles;
    }

    public void setRoles(APIRole[] roles) {
        this.roles = roles;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
        this.enabled = status == Status.ENABLED;
    }

    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public Long getMainUserId() {
        return mainUserId;
    }

    public void setMainUserId(Long mainUserId) {
        this.mainUserId = mainUserId;
    }

    public String getMainUserEmail() {
        return mainUserEmail;
    }

    public void setMainUserEmail(String mainUserEmail) {
        this.mainUserEmail = mainUserEmail;
    }

    public Long getCreatedById() {
        return createdById;
    }

    public void setCreatedById(Long createdById) {
        this.createdById = createdById;
    }

    public String getCreatedByEmail() {
        return createdByEmail;
    }

    public void setCreatedByEmail(String createdByEmail) {
        this.createdByEmail = createdByEmail;
    }

    private String getProjectsURI() {
        return createUri(selfURI, "/projects");
    }

    private String getProjectURI(Long id) {
        return createUri(selfURI, "/projects/" + id);
    }

    private String getDeviceGroupsURI() {
        return createUri(selfURI, "/device-groups");
    }

    private String getNotificationsURI() {
        return createUri(selfURI, "/notifications");
    }

    private String getNotificationURI(long id) {
        return createUri(selfURI, "/notifications/" + id);
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public Date getDeleteTime() {
        return deleteTime;
    }

    public void setDeleteTime(Date deleteTime) {
        this.deleteTime = deleteTime;
    }

    public List<Long> getServiceIds() {
        return serviceIds;
    }

    public void setServiceIds(List<Long> serviceIds) {
        this.serviceIds = serviceIds;
    }

    public List<Long> getAccountServiceIds() {
        return accountServiceIds;
    }

    public void setAccountServiceIds(List<Long> accountServiceIds) {
        this.accountServiceIds = accountServiceIds;
    }

    private Map<String, Object> getUpdateUserParams(
            final String address, final String city, final String code, final String country, final String email,
            final String name, final String organization, final String phone, final String state, final String timeZone,
            final String vatId) {
        Map<String, Object> map = new HashMap<>();
        map.put("address", address);
        map.put("city", city);
        map.put("code", code);
        map.put("country", country);
        map.put("email", email);
        map.put("name", name);
        map.put("organization", organization);
        map.put("phone", phone);
        map.put("state", state);
        map.put("timeZone", timeZone);
        map.put("vatId", vatId);
        return map;
    }

    private Map<String, Object> getCreateDeviceGroupParams(final String displayName, final APIDevice.OsType osType) {
        Map<String, Object> map = new HashMap<>();
        map.put("displayName", displayName);
        map.put("osType", osType);
        return map;
    }

    private Map<String, Object> getCreateProjectParams(String name) {
        Map<String, Object> result = new HashMap<>();
        result.put("name", name);
        return result;
    }

    public APIProject createProject(String name) throws APIException {
        return postResource(getProjectsURI(), getCreateProjectParams(name), APIProject.class);
    }

    public void update() throws APIException {
        APIUser user = postResource(selfURI,
                getUpdateUserParams(address, city, code, country, email, name, organization, phone, state, timeZone,
                        vatId), APIUser.class
        );
        clone(user);
    }

    public APITestRun startTestRun(APITestRunConfig config) throws APIException {
        APITestRun apiTestRun = postResource(createUri(selfURI, "/runs"), config, APITestRun.class);
        apiTestRun.setSelfURI(createUri(selfURI, String.format("/projects/%s/runs/%s", apiTestRun.getProjectId(),
                apiTestRun.getId())));
        return apiTestRun;
    }

    public APITestRunConfig validateTestRunConfig(APITestRunConfig config) throws APIException {
        return postResource(createUri(selfURI, "/runs/config"), config, APITestRunConfig.class);
    }

    @JsonIgnore
    public APIUserFile uploadFile(File file) throws APIException {
        return postFile(createUri(selfURI, "/files"), file, null, APIUserFile.class);
    }

    @JsonIgnore
    public APIListResource<APIProject> getProjectsResource() throws APIException {
        return getListResource(getProjectsURI(), APIProject.class);
    }

    @JsonIgnore
    public APIListResource<APIProject> getProjectsResource(Context<APIProject> context) throws APIException {
        return getListResource(getProjectsURI(), context);
    }

    public APIProject getProject(Long id) throws APIException {
        return getResource(getProjectURI(id), APIProject.class).getEntity();
    }

    @JsonIgnore
    public APIListResource<APIDeviceGroup> getDeviceGroupsResource() throws APIException {
        return getListResource(getDeviceGroupsURI(), APIDeviceGroup.class);
    }

    @JsonIgnore
    public APIListResource<APIDeviceGroup> getDeviceGroupsResource(Context<APIDeviceGroup> context)
            throws APIException {
        return getListResource(getDeviceGroupsURI(), context);
    }

    @JsonIgnore
    public APIListResource<APIFramework> getAvailableFrameworksResource(Context<APIFramework> context)
            throws APIException {
        return this.getListResource(createUri(selfURI, "/available-frameworks"), context);
    }

    @JsonIgnore
    public APIDeviceGroup createDeviceGroup(String displayName, APIDevice.OsType osType) throws APIException {
        return postResource(getDeviceGroupsURI(), getCreateDeviceGroupParams(displayName, osType),
                APIDeviceGroup.class);
    }

    @Override
    @JsonIgnore
    protected <T extends APIEntity> void clone(T from) {
        APIUser apiUser = (APIUser) from;
        cloneBase(from);
        this.address = apiUser.address;
        this.city = apiUser.city;
        this.code = apiUser.code;
        this.country = apiUser.country;
        this.email = apiUser.email;
        this.name = apiUser.name;
        this.organization = apiUser.organization;
        this.phone = apiUser.phone;
        this.roles = apiUser.roles;
        this.state = apiUser.state;
        this.timeZone = apiUser.timeZone;
        this.vatId = apiUser.vatId;
        this.createTime = apiUser.createTime;
        this.deleteTime = apiUser.deleteTime;
        this.isMainUser = apiUser.isMainUser;
        this.status = apiUser.status;
        this.lastLoginTime = apiUser.lastLoginTime;
        this.mainUserId = apiUser.mainUserId;
        this.mainUserEmail = apiUser.mainUserEmail;
        this.enabled = apiUser.enabled;
        this.accountId = apiUser.accountId;
        this.apiKey = apiUser.apiKey;
        this.createdById = apiUser.createdById;
        this.createdByEmail = apiUser.createdByEmail;
        this.serviceIds = apiUser.serviceIds;
        this.accountServiceIds = apiUser.accountServiceIds;
    }
}
