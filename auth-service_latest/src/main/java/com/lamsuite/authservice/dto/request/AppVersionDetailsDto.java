package com.lamsuite.authservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AppVersionDetailsDto {

    private String platformId;
    private String platform;
    private String latestVersion;
    private String minimumVersion;
    private Integer buildNo;
}
