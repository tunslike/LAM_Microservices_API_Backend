package com.lamsuite.authservice.dto;

import lombok.Data;

@Data
public class KYCStatus {
    private Integer bio_data;
    private Integer emp_data;
    private Integer nok_data;
    private Integer doc_data;
}
