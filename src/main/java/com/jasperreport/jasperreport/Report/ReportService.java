package com.jasperreport.jasperreport.Report;

import com.jasperreport.jasperreport.Report.model.VisitGuestEntity;
import com.jasperreport.jasperreport.Report.model.VisitGuetEntityRepository;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import spec.PredicateBuilder;
import spec.Spec;
import spec.Specifications;

import javax.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.util.*;


@Service
public class ReportService {

    @Autowired
    private VisitGuetEntityRepository visitGuetEntityRepository;

    @Autowired
    private ResourceLoader resourceLoader;

    private final String template_path = "C:\\Users\\ashanw\\Desktop\\JasperRepor\\jasperreport\\src\\main\\resources\\guest_list_details.jrxml";
    // private static final String logo_path = "C:\\Users\\ashanw\\Desktop\\SprinBoot-test\\jasperreport\\Report_template\\MillenniumIT.jpg";

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

//    public byte[] getPdf(List<VisitGuestEntity> visitGuestEntities) throws JRException {
//
//        String recordSize = String.valueOf(visitGuestEntities.size());
//        String created = String.valueOf(LocalDate.now());
//        byte[] bytes;
//
//        JasperReport jasperReport = JasperCompileManager.compileReport(template_path);
//        JRDataSource jrDataSource = new JRBeanCollectionDataSource(visitGuestEntities);
//
//        Map<String, Object> visitGuestMap = new HashMap<>();
//        visitGuestMap.put("recordSize", recordSize);
//        visitGuestMap.put("created", created);
//       // visitGuestMap.put("subReport","C:\\Users\\ashanw\\Desktop\\JasperRepor\\jasperreport\\src\\main\\resources\\Guest_subreport.jrxml");
//        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, visitGuestMap, jrDataSource);
//        bytes = JasperExportManager.exportReportToPdf(jasperPrint);
//        return bytes;
//    }

    public byte[] getPdf(List<VisitGuestEntity> visitGuestEntities) throws JRException {

        String recordSize = String.valueOf(visitGuestEntities.size());
        String created = String.valueOf(LocalDate.now());
        byte[] bytes = null;

        try {
            JasperReport jasperReport = JasperCompileManager.compileReport(template_path);
            JasperReport jasperReport1 = JasperCompileManager.compileReport("C:\\Users\\ashanw\\Desktop\\JasperRepor\\jasperreport\\src\\main\\resources\\details.jrxml");
            JRDataSource jrDataSource = new JRBeanCollectionDataSource(visitGuestEntities);

            Map<String, Object> visitGuestMap = new HashMap<>();
            visitGuestMap.put("recordSize", recordSize);
            visitGuestMap.put("created", created);

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, visitGuestMap, new JREmptyDataSource());
            JasperPrint jasperPrint1 = JasperFillManager.fillReport(jasperReport1, visitGuestMap, jrDataSource);

            for (int j = 0; j < jasperPrint1.getPages().size(); j++) {
                jasperPrint.addPage(jasperPrint1.getPages().get(j));
            }
            bytes = JasperExportManager.exportReportToPdf(jasperPrint);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return bytes;
    }


}
