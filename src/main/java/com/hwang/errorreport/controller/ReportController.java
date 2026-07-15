package com.hwang.errorreport.controller;

import com.hwang.errorreport.domain.report.ErrorReport;
import com.hwang.errorreport.dto.report.ReportCreateRequest;
import com.hwang.errorreport.dto.report.ReportUpdateRequest;
import com.hwang.errorreport.service.ErrorReportService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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
            return "reports/new";
        }

        String loginId = authentication.getName();

        Long reportId = errorReportService.createReport(loginId, reportCreateRequest);

        return "redirect:/reports/" + reportId;
    }

    @GetMapping("/reports")
    public String listReports(@RequestParam(defaultValue = "0") int page,
                              @RequestParam(required = false) String keyword,
                              Authentication authentication,
                              Model model){
        String loginId = authentication.getName();

        Pageable pageable = PageRequest.of(page,10);

        Page<ErrorReport> reportPage = errorReportService.findMyReports(loginId, keyword, pageable);

        model.addAttribute("reports", reportPage.getContent());
        model.addAttribute("reportPage", reportPage);
        model.addAttribute("keyword", keyword);

        return "reports/list";
    }

    @GetMapping("/reports/{id}")
    public String detailReport(
            @PathVariable Long id,
            Authentication authentication,
            Model model
    ){
        String loginId = authentication.getName();
        model.addAttribute("report", errorReportService.findMyReport(id, loginId));

        return "reports/detail";
    }

    @GetMapping("/reports/{id}/edit")
    public String editForm(@PathVariable Long id,
                           Authentication authentication,
                           Model model){
        String loginId = authentication.getName();

        ErrorReport report = errorReportService.findMyReportForEdit(id, loginId);

        ReportUpdateRequest reportUpdateRequest = new ReportUpdateRequest();
        reportUpdateRequest.setTitle(report.getTitle());
        reportUpdateRequest.setContent(report.getContent());

        model.addAttribute("report", report);
        model.addAttribute("reportUpdateRequest", reportUpdateRequest);

        return "reports/edit";
    }

    @PostMapping("/reports/{id}/edit")
    public String updateReport(@PathVariable Long id,
                               @Valid @ModelAttribute("reportUpdateRequest") ReportUpdateRequest request,
                               BindingResult bindingResult,
                               Authentication authentication,
                               Model model){
        if(bindingResult.hasErrors()){
            String loginId = authentication.getName();
            ErrorReport report = errorReportService.findMyReportForEdit(id, loginId);

            model.addAttribute("report", report);

            return "reports/edit";
        }

        String loginId = authentication.getName();

        errorReportService.updateMyReport(id, loginId, request);

        return "redirect:/reports/"+id;
    }

}
