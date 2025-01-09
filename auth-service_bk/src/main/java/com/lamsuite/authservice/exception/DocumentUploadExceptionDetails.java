package com.lamsuite.authservice.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.Date;

@Data
@AllArgsConstructor
public class DocumentUploadExceptionDetails {
   private Date timestamp;
   private String message;
   private String details;

}
