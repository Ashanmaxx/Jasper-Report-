package com.jasperreport.jasperreport.Report.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class visitGuestDTO {
    private String name;
    private String identification;
    private String idType;
    private String host;
    private String purpose;
    private LocalDateTime in;
    private LocalDateTime out;
    private String visitStatus;
}
