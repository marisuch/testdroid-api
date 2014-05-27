package com.testdroid.api.model;

import com.testdroid.api.APIEntity;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Michał Szpruta <michal.szpruta@bitbar.com>
 */
@XmlRootElement
public class APIDeviceSessionStep extends APIEntity {

    private Long deviceSessionId;

    private String failReason;

    private Long finishTimeMS;

    private DeviceSessionStepType type;

    private Long startTimeMS;

    public APIDeviceSessionStep() {
    }

    public APIDeviceSessionStep(
            Long id, Long deviceSessionId, String failReason, Long finishTimeMS, Long startTimeMS,
            DeviceSessionStepType type) {
        super(id);
        this.deviceSessionId = deviceSessionId;
        this.failReason = failReason;
        this.finishTimeMS = finishTimeMS;
        this.startTimeMS = startTimeMS;
        this.type = type;
    }

    public Long getDeviceSessionId() {
        return deviceSessionId;
    }

    public void setDeviceSessionId(Long deviceSessionId) {
        this.deviceSessionId = deviceSessionId;
    }

    public String getFailReason() {
        return failReason;
    }

    public void setFailReason(String failReason) {
        this.failReason = failReason;
    }

    public Long getFinishTimeMS() {
        return finishTimeMS;
    }

    public void setFinishTimeMS(Long finishTimeMS) {
        this.finishTimeMS = finishTimeMS;
    }

    public DeviceSessionStepType getType() {
        return type;
    }

    public void setType(DeviceSessionStepType type) {
        this.type = type;
    }

    public Long getStartTimeMS() {
        return startTimeMS;
    }

    public void setStartTimeMS(Long startTimeMS) {
        this.startTimeMS = startTimeMS;
    }

    @Override protected <T extends APIEntity> void clone(T from) {
        APIDeviceSessionStep apiDeviceSessionStep = (APIDeviceSessionStep) from;
        cloneBase(from);
        this.deviceSessionId = apiDeviceSessionStep.deviceSessionId;
        this.failReason = apiDeviceSessionStep.failReason;
        this.startTimeMS = apiDeviceSessionStep.startTimeMS;
        this.finishTimeMS = apiDeviceSessionStep.finishTimeMS;
        this.type = apiDeviceSessionStep.type;
    }
}
