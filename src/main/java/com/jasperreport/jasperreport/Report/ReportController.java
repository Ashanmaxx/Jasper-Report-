package com.jasperreport.jasperreport.Report;

import com.jasperreport.jasperreport.Report.model.VisitGuestEntity;
import net.sf.jasperreports.engine.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

@RestController
public class ReportController {

    @Autowired
    private ReportService reportService;

    @RequestMapping(value = "/pdf", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> getPdf(@RequestParam(name = "q", required = false) String id, @RequestParam(name = "status", required = false) String status, @RequestParam(name = "createdFrom", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate createdFrom,
                                         @RequestParam(name = "createdTo", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate createdTo) throws IOException, JRException {


        List<VisitGuestEntity> visitGuestEntities = reportService.getGuestVisitHistory(id, status, createdFrom, createdTo);

        // ByteArrayInputStream excelByteArrayInputStream = reportService.getPdf(visitGuestEntities);
        byte[] bytes = reportService.getDynamicPdf(visitGuestEntities);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=visit" + LocalDate.now() + ".pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/pdf"))
                .body(bytes);

    }

}
