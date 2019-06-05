package com.jasperreport.jasperreport.Report;

import com.jasperreport.jasperreport.Report.model.VisitGuestEntity;
import com.jasperreport.jasperreport.Report.model.VisitGuetEntityRepository;

import com.jasperreport.jasperreport.Report.model.visitGuestDTO;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import spec.PredicateBuilder;
import spec.Spec;
import spec.Specifications;

import javax.persistence.criteria.Predicate;
import java.io.*;
import java.time.LocalDate;
import java.util.*;


@Service
public class ReportService {

    @Autowired
    private VisitGuetEntityRepository visitGuetEntityRepository;

    @Autowired
    private ResourceLoader resourceLoader;

    private final String template_path = "C:\\Users\\ashanw\\Desktop\\SprinBoot-test\\jasperreport\\src\\main\\resources\\guest_list.jrxml";
    private static final String logo_path = "C:\\Users\\ashanw\\Desktop\\SprinBoot-test\\jasperreport\\Report_template\\MillenniumIT.jpg";

    @Spec(entity = VisitGuestEntity.class)
    Specification<VisitGuestEntity> specification;


    public List<VisitGuestEntity> getGuestVisitHistory(String id, String status, LocalDate createdFrom, LocalDate createdTo) {

        PredicateBuilder<VisitGuestEntity> predicate = new PredicateBuilder<>(Predicate.BooleanOperator.AND);

        if (status != null && !status.isEmpty()) {
            predicate = predicate.eq("visitStatus", status);
        }
        if (id != null) {

            predicate = predicate.predicate(Specifications.or()
                    .like("name", "%" + id.toLowerCase() + "%")
                    .like("identification", "%" + id.toLowerCase() + "%")
                    .build());
        }

        if (createdFrom != null && createdTo == null) {
            createdTo = createdFrom;
        }

        if (createdFrom == null && createdTo != null) {
            createdFrom = createdTo;
        }

        if (createdFrom != null && createdTo != null) {
            predicate = predicate.between("createdDate", createdFrom, createdTo);
        }

        specification = predicate.build();
        List<VisitGuestEntity> visitGuestEntityList = null;
        try {
            visitGuestEntityList = visitGuetEntityRepository.findAll(specification);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return visitGuestEntityList;
    }

    public byte[] getPdf(List<VisitGuestEntity> visitGuestEntities) throws JRException {

        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        byte[] bytes = null;

        JasperReport jasperReport = JasperCompileManager.compileReport(template_path);
        JRDataSource jrDataSource = new JRBeanCollectionDataSource(visitGuestEntities);

        Map<String, Object> visitGuestMap = new HashMap<>();
        //  visitGuestMap.put("ItemDataSource",jrDataSource);
//        for (VisitGuestEntity visitGuestEntity : visitGuestEntities) {
//            visitGuestMap.put("name", visitGuestEntity.getName());
//            visitGuestMap.put("identification", visitGuestEntity.getIdentification());
//            visitGuestMap.put("idType", visitGuestEntity.getIdType());
//            visitGuestMap.put("host", visitGuestEntity.getHost());
//            visitGuestMap.put("purpose", visitGuestEntity.getPurpose());
//            visitGuestMap.put("in", visitGuestEntity.getIn());
//            visitGuestMap.put("out", visitGuestEntity.getOut());
//            visitGuestMap.put("status", visitGuestEntity.getVisitStatus());
//
//        }

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, visitGuestMap, jrDataSource);

        bytes = JasperExportManager.exportReportToPdf(jasperPrint);

        return bytes;
    }


}
