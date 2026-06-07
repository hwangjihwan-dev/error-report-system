package com.hwang.errorreport.controller;

import com.hwang.errorreport.domain.report.ErrorReport;
import com.hwang.errorreport.dto.report.ReportAnswerRequest;
import com.hwang.errorreport.service.ErrorReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AdminReportController {
    private final ErrorReportService errorReportService;

    @GetMapping("/admin/reports")
    public String listReports(Model model){
        model.addAttribute("reports", errorReportService.findAllReports());

        return "admin/reports/list";
    }

    @GetMapping("/admin/reports/{id}")
    public String detailReport(@PathVariable Long id, Model model){
        ErrorReport report = errorReportService.findReportById(id);

        model.addAttribute("report", report);
        model.addAttribute("reportAnswerRequest", new ReportAnswerRequest());

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
            return "admin/reports/detail";
        }

        errorReportService.answerReport(id, request.getAnswer(), authentication.getName());

        return "redirect:/admin/reports/"+id;
    }
}
