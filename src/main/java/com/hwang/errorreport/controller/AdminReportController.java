package com.hwang.errorreport.controller;

import com.hwang.errorreport.service.ErrorReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class AdminReportController {
    private final ErrorReportService errorReportService;

    @GetMapping("/admin/reports")
    public String listReports(Model model){
        model.addAttribute("reports", errorReportService.findAllReports());

        return "admin/reports/list";
    }
}
