package com.hwang.errorreport.service;

import com.hwang.errorreport.domain.report.ErrorReport;
import com.hwang.errorreport.domain.user.User;
import com.hwang.errorreport.dto.report.ReportCreateRequest;
import com.hwang.errorreport.repository.ErrorReportRepository;
import com.hwang.errorreport.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.View;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ErrorReportService {

    private final ErrorReportRepository errorReportRepository;
    private final UserRepository userRepository;
    private final View error;

    public Long createReport(String loginId, ReportCreateRequest request){
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        ErrorReport errorReport = new ErrorReport(
                user,
                request.getTitle(),
                request.getContent()
        );

        ErrorReport savedReport = errorReportRepository.save(errorReport);

        return savedReport.getId();
    }

    @Transactional(readOnly = true)
    public List<ErrorReport> findMyReports(String loginId){
        return errorReportRepository.findByUserLoginIdOrderByCreatedAtDesc(loginId);
    }

    @Transactional(readOnly = true)
    public ErrorReport findMyReport(Long reportId, String loginId){
        return errorReportRepository.findByIdAndUserLoginId(reportId, loginId)
                .orElseThrow(()-> new IllegalArgumentException("오류신고를 찾을 수 없습니다."));
    }

    @Transactional(readOnly = true)
    public List<ErrorReport> findAllReports(){
        return errorReportRepository.findAllByOrderByCreatedAtDesc();
    }

}
