package com.hwang.errorreport.repository;

import com.hwang.errorreport.domain.report.ErrorReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ErrorReportRepository extends JpaRepository<ErrorReport, Long> {
    List<ErrorReport> findByUserLoginIdOrderByCreatedAtDesc(String loginId);
}
