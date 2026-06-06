package com.hwang.errorreport.controller;

import com.hwang.errorreport.dto.report.ReportCreateRequest;
import com.hwang.errorreport.service.ErrorReportService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ReportController {

    private final ErrorReportService errorReportService;

    public ReportController(ErrorReportService errorReportService) {
        this.errorReportService = errorReportService;
    }

    @GetMapping("/reports/new")
    public String creatForm(@ModelAttribute ReportCreateRequest reportCreateRequest){
        return "reports/new";
    }

    @PostMapping("/reports")
    public String creatReport(
            @Valid
            @ModelAttribute ReportCreateRequest reportCreateRequest,
            BindingResult bindingResult,
            Authentication authentication
    ){
        if(bindingResult.hasErrors()){
            return "return/new";
        }

        String loginId = authentication.getName();

        Long reportId = errorReportService.createReport(loginId, reportCreateRequest);

        return "redirect:/reports/" + reportId;
    }

    @GetMapping("/reports")
    public String listReports(Authentication authentication, Model model){
        String loginId = authentication.getName();

        model.addAttribute("reports", errorReportService.findMyReports(loginId));

        return "reports/list";
    }

}
