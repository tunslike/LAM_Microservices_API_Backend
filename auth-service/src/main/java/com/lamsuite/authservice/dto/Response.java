package com.lamsuite.authservice.dto;

import lombok.Data;

@Data
public class Response {
    private Integer ResponseCode;
    private String ResponseMessage;
    private String ExceptionMessage;
}

