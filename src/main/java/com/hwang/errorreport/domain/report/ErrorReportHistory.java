package com.hwang.errorreport.domain.report;

import com.hwang.errorreport.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorReportHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    //어떤 오류신고의 이력인지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", nullable = false)
    private ErrorReport report;

    //처리한 관리자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private User admin;

    //변경 전 상태
    @Enumerated(EnumType.STRING)
    @Column(name = "previous_status", nullable = false)
    private ReportStatus previousStatus;

    //변경 후 상태
    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false)
    private ReportStatus newStatus;

    //답변 내용 또는 반려 사유
    @Column(columnDefinition = "TEXT")
    private String comment;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public ErrorReportHistory(ErrorReport report,
                              User admin,
                              ReportStatus previousStatus,
                              ReportStatus newStatus,
                              String comment){
        this.report = report;
        this.admin = admin;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.comment = comment;
        this.createdAt = LocalDateTime.now();
    }

}
