package com.lamsuite.authservice.model;

import lombok.Data;

@Data
public class AppVersion {
    private String platformId;
    private String platform;
    private String latestVersion;
    private String minimumVersion;
    private Integer buildNo;
    private String dateCreated;
    private String dateUpdated;
    private Integer status;
}
