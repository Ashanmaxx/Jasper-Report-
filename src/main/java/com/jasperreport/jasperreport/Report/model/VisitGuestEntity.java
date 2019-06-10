package com.jasperreport.jasperreport.Report.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "visit_history")
public class VisitGuestEntity {

    @Id
    @NotEmpty
    private int id;

    @NotEmpty
    private String name;

    @NotEmpty
    private String mobile;

    @NotEmpty
    private String identification;

    @NotEmpty
    private String idType;

    @NotEmpty
    private String host;

    @NotEmpty
    private String hostEmail;

    @NotEmpty
    private String purpose;

    @NotEmpty
    private String username;


    private String in;

    private String out;

    private Integer visitId;

    @NotEmpty
    private String tokenStatus;

    @NotEmpty
    private String visitStatus;

    @NotEmpty
    private LocalDate createdDate;

}
