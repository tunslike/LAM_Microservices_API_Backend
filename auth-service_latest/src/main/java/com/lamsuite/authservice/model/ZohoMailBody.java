package com.lamsuite.authservice.model;

import lombok.Data;

@Data
public class ZohoMailBody {
    private String from;
    private String to;
    private String subject;
    private String htmlbody;
}
