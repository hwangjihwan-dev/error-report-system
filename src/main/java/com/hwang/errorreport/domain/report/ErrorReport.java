package com.hwang.errorreport.domain.report;

import com.hwang.errorreport.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "error_reports")
@Entity
public class ErrorReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReportStatus status;

    @Column(columnDefinition = "TEXT")
    private String answer;

    @Column(name = "reject_reason", columnDefinition = "TEXT")
    private String rejectReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private User admin;

    @Column(name = "answered_at")
    private LocalDateTime answeredAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "original_file_name", length = 255)
    private String originalFileName;

    @Column(name = "stored_file_name", length = 255)
    private String storedFileName;

    @Column(name = "file_path", length = 500)
    private String filePath;

    @Column(name = "file_size")
    private Long fileSize;

    public ErrorReport(User user, String title, String content){
        this.user = user;
        this.title = title;
        this.content = content;
        this.status = ReportStatus.RECEIVED;
        this.createdAt = LocalDateTime.now();
    }

    public void update(String title, String content){
        this.title = title;
        this.content = content;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean hasAnswer(){
        return this.answer != null && !this.answer.isBlank();
    }

    public void answer(String answer, ReportStatus status, User admin){
        this.answer = answer;
        this.admin = admin;
        this.status = status;
        this.answeredAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void updateAnswer(String answer, ReportStatus status, User admin){
        this.answer = answer;
        this.admin = admin;
        this.status = status;
        this.answeredAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void attachFile(String originalFileName,
                           String storedFileName,
                           String filePath,
                           Long fileSize){

        this.originalFileName = originalFileName;
        this.storedFileName = storedFileName;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean hasAttachment(){
        return this.originalFileName != null && !this.originalFileName.isBlank();
    }

    public void removeAttachment(){
        this.originalFileName = null;
        this.storedFileName = null;
        this.filePath = null;
        this.fileSize = null;
        this.updatedAt = LocalDateTime.now();
    }

    public void reject(String rejectReason, User admin){
        this.answer = null;
        this.rejectReason = rejectReason;
        this.status = ReportStatus.REJECTED;
        this.admin = admin;
        this.answeredAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}
