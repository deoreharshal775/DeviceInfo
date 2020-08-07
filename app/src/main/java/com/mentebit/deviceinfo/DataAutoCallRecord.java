package com.mentebit.deviceinfo;

import java.io.Serializable;

public class DataAutoCallRecord implements Serializable {
    String leadName, leadNumber, leadType, leadRealme, leadDateCompare, leadTime, leadRecName, leadDBinsert;

    public DataAutoCallRecord(String leadName, String leadNumber, String leadType, String leadRealme,
                              String leadDateCompare, String leadTime, String leadRecName, String leadDBinsert) {
        this.leadName = leadName;
        this.leadNumber = leadNumber;
        this.leadType = leadType;
        this.leadRealme = leadRealme;
        this.leadDateCompare = leadDateCompare;
        this.leadTime = leadTime;
        this.leadRecName = leadRecName;
        this.leadDBinsert = leadDBinsert;
    }

    public String getLeadName() {
        return leadName;
    }

    public String getLeadNumber() {
        return leadNumber;
    }

    public String getLeadType() {
        return leadType;
    }

    public String getLeadRealme() {
        return leadRealme;
    }

    public String getLeadDateCompare() {
        return leadDateCompare;
    }

    public String getLeadTime() {
        return leadTime;
    }

    public String getLeadRecName() {
        return leadRecName;
    }

    public String getLeadDBinsert() {
        return leadDBinsert;
    }
}
