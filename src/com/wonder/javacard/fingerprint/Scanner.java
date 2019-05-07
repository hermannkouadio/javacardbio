/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wonder.javacard.fingerprint;

/**
 *
 * @author kherman
 */
public class Scanner {
    private Long deviceHandle;
    private String deviceType;
    private Integer deviceIndex;
    private String deviceName;
    
    public Scanner(){}
    
    public Scanner(Long handle, String type, Integer index, String name){
        super();
        this.deviceHandle = handle;
        this.deviceType = type;
        this.deviceIndex = index;
        this.deviceName = name;
    }

    public Long getDeviceHandle() {
        return deviceHandle;
    }

    public void setDeviceHandle(Long deviceHandle) {
        this.deviceHandle = deviceHandle;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public Integer getDeviceIndex() {
        return deviceIndex;
    }

    public void setDeviceIndex(Integer deviceIndex) {
        this.deviceIndex = deviceIndex;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    @Override
    public String toString() {
        return "Scanner{" + "deviceHandle=" + deviceHandle + ", deviceType=" + deviceType + ", deviceIndex=" + deviceIndex + ", deviceName=" + deviceName + '}';
    }
}
