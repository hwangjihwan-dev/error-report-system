package com.hwang.errorreport.repository;

import com.hwang.errorreport.domain.report.ErrorReport;
import com.hwang.errorreport.domain.report.ReportStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ErrorReportRepository extends JpaRepository<ErrorReport, Long> {
    Page<ErrorReport> findByUserLoginIdOrderByCreatedAtDesc(String loginId, Pageable pageable);

    Page<ErrorReport> findByStatusOrderByCreatedAtDesc(ReportStatus status, Pageable pageable);

    Optional<ErrorReport> findByIdAndUserLoginId(Long id, String loginId);

    List<ErrorReport> findAllByOrderByCreatedAtDesc();

    Page<ErrorReport> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
