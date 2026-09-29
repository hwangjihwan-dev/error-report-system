package com.hwang.errorreport.service;

import com.hwang.errorreport.domain.report.ErrorReport;
import com.hwang.errorreport.domain.report.ErrorReportHistory;
import com.hwang.errorreport.domain.report.ReportStatus;
import com.hwang.errorreport.domain.user.User;
import com.hwang.errorreport.dto.report.ReportCreateRequest;
import com.hwang.errorreport.dto.report.ReportDashboardResponse;
import com.hwang.errorreport.dto.report.ReportUpdateRequest;
import com.hwang.errorreport.repository.ErrorReportHistoryRepository;
import com.hwang.errorreport.repository.ErrorReportRepository;
import com.hwang.errorreport.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ErrorReportService {

    private final ErrorReportRepository errorReportRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;
    private final ErrorReportHistoryRepository errorReportHistoryRepository;

    public Long createReport(String loginId, ReportCreateRequest request, MultipartFile file){
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        ErrorReport errorReport = new ErrorReport(
                user,
                request.getTitle(),
                request.getContent()
        );

        FileStorageService.StoredFile storedFile = fileStorageService.storeFile(file);

        if(storedFile != null){
            errorReport.attachFile(
                    storedFile.getOriginalFileName(),
                    storedFile.getStoredFileName(),
                    storedFile.getFilePath(),
                    storedFile.getFileSize()
            );
        }
        ErrorReport savedReport = errorReportRepository.save(errorReport);

        return savedReport.getId();
    }

    @Transactional(readOnly = true)
    public Page<ErrorReport> findMyReports(String loginId, String keyword, Pageable pageable){
        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();

        if(!hasKeyword){
            return errorReportRepository.findByUserLoginIdOrderByCreatedAtDesc(loginId, pageable);
        }
        return errorReportRepository.searchMyReports(
                        loginId,
                        keyword,
                        pageable
                );
    }

    @Transactional(readOnly = true)
    public ErrorReport findMyReport(Long reportId, String loginId){
        return errorReportRepository.findByIdAndUserLoginId(reportId, loginId)
                .orElseThrow(()-> new IllegalArgumentException("오류신고를 찾을 수 없습니다."));
    }

    @Transactional(readOnly = true)
    public ErrorReport findMyReportForEdit(Long reportId, String loginId){
        ErrorReport report = findMyReport(reportId, loginId);

        validateUpdatableReport(report);

        return report;
    }

    @Transactional(readOnly = true)
    public Page<ErrorReport> findAllReports(Pageable pageable){
        return errorReportRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    @Transactional(readOnly = true)
    public Page<ErrorReport> findReports(ReportStatus status, String keyword, Pageable pageable){
        String normalizedKeyword =
                keyword == null || keyword.trim().isEmpty() ? "" : keyword.trim();
        return errorReportRepository.searchReports(
                status,
                normalizedKeyword,
                pageable
        );
    }

    @Transactional(readOnly = true)
    public ErrorReport findReportById(Long id){
        return errorReportRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("존재하지 않는 오류신고입니다."));
    }

    public void answerReport(Long reportId, String answer, ReportStatus status, String adminLoginId) {
        ErrorReport report = getReport(reportId);
        User admin = getAdmin(adminLoginId);

        ReportStatus previousStatus = report.getStatus();

        report.answer(answer, status, admin);

        saveHistory(
                report,
                admin,
                previousStatus,
                status,
                answer
        );
    }

    public void updateAnswer(Long reportId, String answer, ReportStatus status, String adminLoginId){
        ErrorReport report = getReport(reportId);
        User admin = getAdmin(adminLoginId);

        ReportStatus previousStatus = report.getStatus();

        report.updateAnswer(answer, status, admin);

        saveHistory(
                report,
                admin,
                previousStatus,
                status,
                answer
        );
    }

    public void updateMyReport(Long reportId,
                               String loginId,
                               ReportUpdateRequest request,
                               MultipartFile file){

        ErrorReport report = findMyReport(reportId, loginId);

        validateUpdatableReport(report);

        report.update(request.getTitle(), request.getContent());

        if(file != null && !file.isEmpty()){
            String oldFilePath = report.getFilePath();

            FileStorageService.StoredFile storedFile = fileStorageService.storeFile(file);

            report.attachFile(
                    storedFile.getOriginalFileName(),
                    storedFile.getStoredFileName(),
                    storedFile.getFilePath(),
                    storedFile.getFileSize()
            );
            fileStorageService.deleteFile(oldFilePath);
            return;
        }
        if(request.isDeleteAttachment() && report.hasAttachment()){
            String oldFilePath = report.getFilePath();

            report.removeAttachment();

            fileStorageService.deleteFile(oldFilePath);
        }
    }

    public void deleteMyReport(Long reportId, String loginId){
        ErrorReport report = findMyReport(reportId,loginId);

        validateDeletableReport(report);

        if(report.hasAttachment()){
            fileStorageService.deleteFile(report.getFilePath());
        }

        errorReportRepository.delete(report);
    }

    public void rejectReport(Long reportId, String rejectReason, String adminLoginId){
        ErrorReport report = getReport(reportId);

        if(report.hasAnswer()){
            throw new IllegalStateException("이미 답변이 등록된 오류신고는 반려할 수 없습니다.");
        }

        if(report.getStatus() == ReportStatus.REJECTED){
            throw new IllegalStateException("이미 반려된 오류신고입니다.");
        }

        User admin = getAdmin(adminLoginId);

        ReportStatus previousStatus = report.getStatus();

        report.reject(rejectReason, admin);

        saveHistory(
                report,
                admin,
                previousStatus,
                ReportStatus.REJECTED,
                rejectReason
        );
    }

    public List<ErrorReportHistory> findHistories(Long reportId){
        return errorReportHistoryRepository.findByReportIdOrderByCreatedAtDesc(reportId);
    }

    public List<ErrorReport> findReportsForExcel(ReportStatus status, String keyword){
        String normalizedKeyword =
                keyword == null || keyword.trim().isEmpty() ? "" : keyword.trim();

        return errorReportRepository.searchReportsForExcel(
                status,
                normalizedKeyword
        );
    }

    public ReportDashboardResponse getDashboard(){
        long totalCount = errorReportRepository.count();
        long receivedCount = errorReportRepository.countByStatus(ReportStatus.RECEIVED);
        long inProgressCount = errorReportRepository.countByStatus(ReportStatus.IN_PROGRESS);
        long completedCount = errorReportRepository.countByStatus(ReportStatus.COMPLETED);
        long rejectedCount = errorReportRepository.countByStatus(ReportStatus.REJECTED);
        long unansweredCount = errorReportRepository.countByAnswerIsNull();
        long answeredCount = errorReportRepository.countByAnswerIsNotNull();

        return new ReportDashboardResponse(
                totalCount,
                receivedCount,
                inProgressCount,
                completedCount,
                rejectedCount,
                unansweredCount,
                answeredCount
        );
    }

    private ErrorReport getReport(Long reportId){
        return errorReportRepository.findById(reportId)
                .orElseThrow(()-> new IllegalArgumentException("존재하지 않는 오류신고입니다."));
    }

    private User getAdmin(String adminLoginId){
        return userRepository.findByLoginId(adminLoginId)
                .orElseThrow(()-> new IllegalArgumentException("존재하지 않는 관리자입니다."));
    }

    private void saveHistory(ErrorReport report,
                             User admin,
                             ReportStatus previousStatus,
                             ReportStatus newStatus,
                             String comment){
        errorReportHistoryRepository.save(new ErrorReportHistory(
                report,
                admin,
                previousStatus,
                newStatus,
                comment
        ));
    }

    private void validateUpdatableReport(ErrorReport report){
        if(report.hasAnswer()){
            throw new IllegalStateException("관리자 답변이 등록된 오류신고는 수정할 수 없습니다.");
        }

        if(report.isRejected()){
            throw new IllegalStateException("반려된 오류신고는 수정할 수 없습니다.");
        }
    }

    private void validateDeletableReport(ErrorReport report){
        if(report.hasAnswer()){
            throw new IllegalStateException("관리자 답변이 등록된 오류신고는 삭제할 수 없습니다.");
        }

        if(report.isRejected()){
            throw new IllegalStateException("반려된 오류신고는 삭제할 수 없습니다.");
        }
    }
}
