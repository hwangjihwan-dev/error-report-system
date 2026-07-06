package com.hwang.errorreport.controller;

import com.hwang.errorreport.domain.report.ErrorReport;
import com.hwang.errorreport.domain.report.ReportStatus;
import com.hwang.errorreport.dto.report.ReportAnswerRequest;
import com.hwang.errorreport.service.ErrorReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class AdminReportController {
    private final ErrorReportService errorReportService;

    @GetMapping("/admin/reports")
    public String listReports(@RequestParam(defaultValue = "0") int page,
                              Model model){
        Pageable pageable = PageRequest.of(page,10);

        Page<ErrorReport> reportPage = errorReportService.findAllReports(pageable);

        model.addAttribute("reports", reportPage.getContent());
        model.addAttribute("reportPage", reportPage);

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
        model.addAttribute("statuses", ReportStatus.values());

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
            return "admin/reports/detail";
        }

        errorReportService.answerReport(
                id,
                request.getAnswer(),
                request.getStatus(),
                authentication.getName());

        return "redirect:/admin/reports/"+id;
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
            return "admin/reports/detail";
        }

        errorReportService.updateAnswer(
                id,
                request.getAnswer(),
                request.getStatus(),
                authentication.getName());

        return "redirect:/admin/reports/" + id;
    }
}
