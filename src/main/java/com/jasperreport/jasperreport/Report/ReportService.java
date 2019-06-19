package com.jasperreport.jasperreport.Report;

import ar.com.fdvs.dj.core.DynamicJasperHelper;
import ar.com.fdvs.dj.core.layout.ClassicLayoutManager;
import ar.com.fdvs.dj.core.layout.LayoutManager;
import ar.com.fdvs.dj.domain.DynamicReport;
import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.builders.ColumnBuilder;
import ar.com.fdvs.dj.domain.builders.DynamicReportBuilder;
import ar.com.fdvs.dj.domain.constants.HorizontalAlign;
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;
import com.jasperreport.jasperreport.Report.model.VisitGuestEntity;
import com.jasperreport.jasperreport.Report.model.VisitGuetEntityRepository;


import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final String template_path = "C:\\Users\\ashanw\\Desktop\\JasperRepor\\jasperreport\\src\\main\\resources\\guest_list_details.jrxml";

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

    public byte[] getDynamicPdf(List<VisitGuestEntity> visitGuestEntities) throws JRException {
        byte[] bytes = null;
        try {
            ReportService reportService = new ReportService();
            DynamicReport dynamicReport = reportService.createDesign();
            JRDataSource jrDataSource = new JRBeanCollectionDataSource(visitGuestEntities);
            Map<String, Object> visitGuestMap = new HashMap<>();
            JasperReport jasperReport1 = DynamicJasperHelper.generateJasperReport(dynamicReport, getLayoutManager(), visitGuestMap);
            JasperPrint jasperPrint1 = JasperFillManager.fillReport(jasperReport1, visitGuestMap, jrDataSource);
            bytes = JasperExportManager.exportReportToPdf(jasperPrint1);
        } catch (
                Exception e) {
            e.printStackTrace();
        }
        return bytes;
    }

    public DynamicReport createDesign() throws JRException {

        Style detailStyle = new Style();
        Style headerStyle = new Style();

        Style titleStyle = new Style();
        Style subtitleStyle = new Style();
        Style amountStyle = new Style();
        amountStyle.setHorizontalAlign(HorizontalAlign.RIGHT);

        DynamicReportBuilder drb = new DynamicReportBuilder();
        drb.setTitle("MI-GUEST")
                .setSubtitle("Guest Report List")
                .setDetailHeight(15)
                .setMargins(30, 20, 30, 15)
                .setDefaultStyles(titleStyle, subtitleStyle, headerStyle, detailStyle)
                .setColumnsPerPage(1);

        AbstractColumn colName = ColumnBuilder.getNew()
                .setColumnProperty("name", String.class.getName())
                .setTitle("Name").setWidth(85)
                .build();

        AbstractColumn colMobile = ColumnBuilder.getNew()
                .setColumnProperty("mobile", String.class.getName())
                .setTitle("Mobile").setWidth(85)
                .build();

        AbstractColumn identifictionCol = ColumnBuilder.getNew()
                .setColumnProperty("identification", String.class.getName())
                .setTitle("Identification").setWidth(85)
                .build();

        AbstractColumn idTypeCol = ColumnBuilder.getNew()
                .setColumnProperty("idType", String.class.getName())
                .setTitle("ID Type").setWidth(85)
                .build();

        AbstractColumn hostCol = ColumnBuilder.getNew()
                .setColumnProperty("host", String.class.getName())
                .setTitle("Host").setWidth(85)
                .build();

        AbstractColumn hostEmailCol = ColumnBuilder.getNew()
                .setColumnProperty("hostEmail", String.class.getName())
                .setTitle("Host Email").setWidth(85)
                .build();

        drb.addColumn(colName);
        drb.addColumn(colMobile);
        drb.addColumn(identifictionCol);
        drb.addColumn(idTypeCol);
        drb.addColumn(hostCol);
        drb.addColumn(hostEmailCol);

        drb.setUseFullPageWidth(true);
        DynamicReport dr = drb.build();
        return dr;
    }

    protected LayoutManager getLayoutManager() {
        return new ClassicLayoutManager();
    }

}
