package com.jasperreport.jasperreport.Report.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface VisitGuetEntityRepository extends JpaRepository<VisitGuestEntity,Integer>, JpaSpecificationExecutor<VisitGuestEntity> {

}
