package com.hwang.errorreport.repository;

import com.hwang.errorreport.domain.report.ErrorReport;
import com.hwang.errorreport.domain.report.ErrorReportHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ErrorReportHistoryRepository extends JpaRepository<ErrorReportHistory, Long> {

    List<ErrorReportHistory> findByReportIdOrderByCreatedAtDesc(Long reportId);

    Long report(ErrorReport report);
}
