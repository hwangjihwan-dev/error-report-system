package com.hwang.errorreport.controller;

import com.hwang.errorreport.domain.report.ErrorReport;
import com.hwang.errorreport.dto.report.ReportCreateRequest;
import com.hwang.errorreport.dto.report.ReportUpdateRequest;
import com.hwang.errorreport.service.ErrorReportService;
import com.hwang.errorreport.service.FileStorageService;
import jakarta.validation.Valid;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.View;

@Controller
public class ReportController {

    private final ErrorReportService errorReportService;
    private final View error;
    private final FileStorageService fileStorageService;

    public ReportController(
            ErrorReportService errorReportService,
            View error,
            FileStorageService fileStorageService) {
        this.errorReportService = errorReportService;
        this.error = error;
        this.fileStorageService = fileStorageService;
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
            @RequestParam(required = false) MultipartFile file,
            Authentication authentication
    ){
        if(bindingResult.hasErrors()){
            return "reports/new";
        }

        String loginId = authentication.getName();

        Long reportId = errorReportService.createReport(loginId, reportCreateRequest, file);

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

/*    첨부파일 추가 전
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
    }*/
    @PostMapping("/reports/{id}/edit")
    public String updateReport(@PathVariable Long id,
                               @Valid @ModelAttribute("reportUpdateRequest") ReportUpdateRequest request,
                               BindingResult bindingResult,
                               @RequestParam(required = false) MultipartFile file,
                               Authentication authentication,
                               Model model){
        if(bindingResult.hasErrors()){
            String loginId = authentication.getName();
            ErrorReport report = errorReportService.findMyReportForEdit(id, loginId);

            model.addAttribute("report", report);

            return "reports/edit";
        }

        String loginId = authentication.getName();

        errorReportService.updateMyReport(id, loginId, request, file);

        return "redirect:/reports/"+id;
    }

    @PostMapping("/reports/{id}/delete")
    public String deleteReport(@PathVariable Long id,
                               Authentication authentication){

        String loginId = authentication.getName();

        errorReportService.deleteMyReport(id, loginId);

        return "redirect:/reports";
    }

    @GetMapping("/reports/{id}/attachment")
    public ResponseEntity<Resource> downloadAttachment(@PathVariable Long id,
                                                       Authentication authentication){
        String loginId = authentication.getName();

        ErrorReport report = errorReportService.findMyReport(id, loginId);

        if(!report.hasAttachment()){
            throw new IllegalStateException("첨부파일이 없습니다.");
        }

        return fileStorageService.downloadFile(
                report.getFilePath(),
                report.getOriginalFileName()
        );
    }
}
