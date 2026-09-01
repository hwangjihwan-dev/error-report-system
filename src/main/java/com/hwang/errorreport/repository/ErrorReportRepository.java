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

    List<ErrorReport> findByStatusOrderByCreatedAtDesc(ReportStatus status);

    List<ErrorReport> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseOrderByCreatedAtDesc(
            String titleKeyword,
            String contentKeyword
    );

    List<ErrorReport> findByStatusAndTitleContainingIgnoreCaseOrStatusAndContentContainingIgnoreCaseOrderByCreatedAtDesc(
            ReportStatus titleStatus,
            String titleKeyword,
            ReportStatus contentStatus,
            String contentKeyword
    );

    Page<ErrorReport> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<ErrorReport> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseOrderByCreatedAtDesc(
            String titleKeyword,
            String contentKeyword,
            Pageable pageable
    );

    Page<ErrorReport> findByStatusAndTitleContainingIgnoreCaseOrStatusAndContentContainingIgnoreCaseOrderByCreatedAtDesc(
            ReportStatus titleStatus,
            String titleKeyword,
            ReportStatus contentStatus,
            String contentKeyword,
            Pageable pageable
    );

    Page<ErrorReport> findByUserLoginIdAndTitleContainingIgnoreCaseOrUserLoginIdAndContentContainingIgnoreCaseOrderByCreatedAtDesc(
            String titleLoginId,
            String titleKeyword,
            String contentLoginId,
            String contentKeyword,
            Pageable pageable
    );
}
