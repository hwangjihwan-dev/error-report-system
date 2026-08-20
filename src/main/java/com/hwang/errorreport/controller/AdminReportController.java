package com.hwang.errorreport.controller;

import com.hwang.errorreport.domain.report.ErrorReport;
import com.hwang.errorreport.domain.report.ReportStatus;
import com.hwang.errorreport.dto.report.ReportAnswerRequest;
import com.hwang.errorreport.dto.report.ReportRejectRequest;
import com.hwang.errorreport.service.ErrorReportService;
import com.hwang.errorreport.service.FileStorageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class AdminReportController {
    private final ErrorReportService errorReportService;
    private final FileStorageService fileStorageService;

    @GetMapping("/admin/reports")
    public String listReports(@RequestParam(defaultValue = "0") int page,
                              @RequestParam(required = false) ReportStatus status,
                              @RequestParam(required = false) String keyword,
                              Model model){
        Pageable pageable = PageRequest.of(page,10);

        Page<ErrorReport> reportPage = errorReportService.findReports(status, keyword, pageable);

        model.addAttribute("reports", reportPage.getContent());
        model.addAttribute("reportPage", reportPage);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("statuses", ReportStatus.values());
        model.addAttribute("keyword", keyword);

        return "admin/reports/list";
    }

    @GetMapping("/admin/reports/{id}")
    public String detailReport(@PathVariable Long id, Model model){
        ErrorReport report = errorReportService.findReportById(id);

        ReportAnswerRequest reportAnswerRequest = new ReportAnswerRequest();
        reportAnswerRequest.setAnswer(report.getAnswer());
        reportAnswerRequest.setStatus(report.getStatus());

        model.addAttribute("report", report);
        model.addAttribute("reportAnswerRequest", reportAnswerRequest);
        model.addAttribute("reportRejectRequest", new ReportRejectRequest());
        model.addAttribute("statuses", answerStatuses());
        model.addAttribute("histories", errorReportService.findHistories(id));

        return "admin/reports/detail";
    }

    @PostMapping("/admin/reports/{id}/answer")
    public String answerReport(
            @PathVariable Long id,
            @Valid @ModelAttribute("reportAnswerRequest") ReportAnswerRequest request,
            BindingResult bindingResult,
            Authentication authentication,
            Model model
    ){
        if(bindingResult.hasErrors()){
            ErrorReport report = errorReportService.findReportById(id);
            model.addAttribute("report", report);
            model.addAttribute("statuses", ReportStatus.values());
            model.addAttribute("histories", errorReportService.findHistories(id));

            return "admin/reports/detail";
        }

        errorReportService.answerReport(
                id,
                request.getAnswer(),
                request.getStatus(),
                authentication.getName());

        return "redirect:/admin/reports/"+id;
    }

    @PostMapping("/admin/reports/{id}/reject")
    public String rejeectReport(@PathVariable Long id,
                                @Valid @ModelAttribute("reportRejectRequest") ReportRejectRequest request,
                                BindingResult bindingResult,
                                Authentication authentication,
                                Model model){
        if(bindingResult.hasErrors()){
            ErrorReport report = errorReportService.findReportById(id);

            ReportAnswerRequest reportAnswerRequest = new ReportAnswerRequest();
            reportAnswerRequest.setAnswer(report.getAnswer());
            reportAnswerRequest.setStatus(report.getStatus());

            model.addAttribute("report", report);
            model.addAttribute("reportAnswerRequest", reportAnswerRequest);
            model.addAttribute("statuses", ReportStatus.values());
            model.addAttribute("histories", errorReportService.findHistories(id));

            return "admin/reports/detail";
        }

        String adminLoginId = authentication.getName();

        errorReportService.rejectReport(
                id,
                request.getRejectReason(),
                adminLoginId
        );

        return "redirect:/admin/reports/" + id;
    }

    @PostMapping("/admin/reports/{id}/answer/edit")
    public String updateAnswer(
            @PathVariable Long id,
            @Valid @ModelAttribute("reportAnswerRequest") ReportAnswerRequest request,
            BindingResult bindingResult,
            Authentication authentication,
            Model model
    ){
        if(bindingResult.hasErrors()){
            ErrorReport report = errorReportService.findReportById(id);
            model.addAttribute("report", report);
            model.addAttribute("statuses", ReportStatus.values());
            model.addAttribute("histories", errorReportService.findHistories(id));

            return "admin/reports/detail";
        }

        errorReportService.updateAnswer(
                id,
                request.getAnswer(),
                request.getStatus(),
                authentication.getName());

        return "redirect:/admin/reports/" + id;
    }

    @GetMapping("/admin/reports/{id}/attachment")
    public ResponseEntity<Resource> downloadAttachment(@PathVariable Long id){
        ErrorReport errorReport = errorReportService.findReportById(id);

        if(!errorReport.hasAttachment()){
            throw new IllegalArgumentException("첨부파일이 없습니다.");
        }

        return fileStorageService.downloadFile(
                errorReport.getFilePath(),
                errorReport.getOriginalFileName()
        );
    }

    private List<ReportStatus> answerStatuses(){
        return List.of(
                ReportStatus.RECEIVED,
                ReportStatus.IN_PROGRESS,
                ReportStatus.COMPLETED
        );
    }
}
