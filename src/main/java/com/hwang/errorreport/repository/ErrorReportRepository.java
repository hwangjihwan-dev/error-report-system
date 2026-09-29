package com.hwang.errorreport.repository;

import com.hwang.errorreport.domain.report.ErrorReport;
import com.hwang.errorreport.domain.report.ReportStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

import java.util.List;
import java.util.Optional;

public interface ErrorReportRepository extends JpaRepository<ErrorReport, Long> {
    Page<ErrorReport> findByUserLoginIdOrderByCreatedAtDesc(String loginId, Pageable pageable);

    Page<ErrorReport> findByStatusOrderByCreatedAtDesc(ReportStatus status, Pageable pageable);

    Optional<ErrorReport> findByIdAndUserLoginId(Long id, String loginId);

    List<ErrorReport> findAllByOrderByCreatedAtDesc();

    List<ErrorReport> findByStatusOrderByCreatedAtDesc(ReportStatus status);

    Page<ErrorReport> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @Query("""
        SELECT r
        FROM ErrorReport r
        WHERE r.user.loginId = :loginId
            AND (
                LOWER(r.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(r.content) LIKE LOWER(CONCAT('%', :keyword, '%'))
            )
        ORDER BY r.createdAt DESC
        """)
    Page<ErrorReport> searchMyReports(@Param("loginId") String loginId,
                                      @Param("keyword") String keyword,
                                      Pageable pageable);

    @Query("""
            SELECT r
            FROM ErrorReport r
            WHERE (:status IS NULL OR r.status = :status)
                AND (
                    :keyword = ''
                    OR LOWER(r.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(r.content) LIKE LOWER(CONCAT('%', :keyword, '%'))
                )
                ORDER BY r.createdAt DESC
            """)
    Page<ErrorReport> searchReports(@Param("status") ReportStatus status,
                                    @Param("keyword") String keyword,
                                    Pageable pageable);

    @Query("""
            SELECT r
            FROM ErrorReport r
            WHERE (:status is NULL OR r.status = :status)
                AND (
                    :keyword = ''
                    OR LOWER(r.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(r.content) LIKE LOWER(CONCAT('%', :keyword, '%'))
                )
            ORDER BY r.createdAt DESC
    """)
    List<ErrorReport> searchReportsForExcel(@Param("status") ReportStatus status,
                                            @Param("keyword") String keyword);

    long countByStatus(ReportStatus status);

    long countByAnswerIsNull();

    long countByAnswerIsNotNull();
}
