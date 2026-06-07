package com.hwang.errorreport.controller;

import com.hwang.errorreport.domain.report.ErrorReport;
import com.hwang.errorreport.service.ErrorReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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

        return "admin/reports/detail";
    }
}
