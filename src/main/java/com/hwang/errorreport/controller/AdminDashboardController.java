package com.hwang.errorreport.controller;

import com.hwang.errorreport.dto.report.ReportDashboardResponse;
import com.hwang.errorreport.service.ErrorReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class AdminDashboardController {

    private final ErrorReportService errorReportService;

    @GetMapping("/admin/dashboard")
    public String dashboard(Model model){
        ReportDashboardResponse dashboard = errorReportService.getDashboard();

        model.addAttribute("dashboard", dashboard);

        return "admin/dashboard";
    }

}
