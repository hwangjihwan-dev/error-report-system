package com.hwang.errorreport.config;

import com.hwang.errorreport.domain.report.ErrorReport;
import com.hwang.errorreport.domain.report.ReportStatus;
import com.hwang.errorreport.domain.user.User;
import com.hwang.errorreport.service.ErrorReportExcelService;
import com.hwang.errorreport.service.ErrorReportService;
import com.hwang.errorreport.service.FileStorageService;
import com.hwang.errorreport.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.View;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
@Import(SecurityConfig.class)
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private ErrorReportService errorReportService;

    @MockitoBean
    private FileStorageService fileStorageService;

    @MockitoBean
    private ErrorReportExcelService errorReportExcelService;
    @Autowired
    private View error;

    @Test
    @DisplayName("일반 사용자는 관리자 페이지에 접근할 수 없다")
    void userCannotAccessAdminPage() throws Exception{
        //when & then
        mockMvc.perform(
                get("/admin/reports")
                        .with(user("user1").roles("USER"))
        )
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("관리자는 관리자 페이지에 접근할 수 있다")
    void adminCanAccessAdminPage() throws Exception{

        //given
        Page<ErrorReport> emptyPage = Page.empty();

        when(errorReportService.findReports(
                any(),
                any(),
                any(Pageable.class)
        )).thenReturn(emptyPage);

        //when & then
        mockMvc.perform(
                get("/admin/reports")
                        .with(user("admin").roles("ADMIN"))
        )
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("비로그인 사용자는 오류신고 목록에 접근하면 로그인 페이지로 이동한다")
    void unauthenticatedUserIsRedirectedToLogin() throws Exception{
        //when & then
        mockMvc.perform(
                get("/reports")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @DisplayName("CSRF 토큰 없이 오류신고 삭제 요청을 하면 접근이 거부된다")
    void deleteReportWithoutCsrfIsForbidden() throws Exception{
        //when & then
        mockMvc.perform(
                post("/reports/1/delete")
                        .with(user("user1").roles("USER"))
        )
                .andExpect(status().isForbidden());

        verifyNoInteractions(errorReportService);
    }

    @Test
    @DisplayName("CSRF 토큰이 있으면 오류신고 삭제 요청을 처리할 수 있다")
    void deleteReportWithCsrfSucceeds() throws Exception{
        //when & then
        mockMvc.perform(
                post("/reports/1/delete")
                        .with(user("user1").roles("USER"))
                        .with(csrf())
        )
                .andExpect(status().is3xxRedirection());

        verify(errorReportService)
                .deleteMyReport(1L,"user1");
    }

    @Test
    @DisplayName("오류신고 삭제 URL을 GET으로 요청하면 405를 반환한다")
    void deleteReportWithGetReturnsMethodNotAllowed() throws Exception{
        //when & then
        mockMvc.perform(
                get("/reports/1/delete")
                        .with(user("user1").roles("USER"))
        )
                .andExpect(status().isMethodNotAllowed());
        verifyNoInteractions(errorReportService);
    }

    @Test
    @DisplayName("일반 사용자는 관리자 답변을 등록할 수 없다")
    void userCannotSubmitAdminAnswer() throws Exception{
        //when & then
        mockMvc.perform(
                post("/admin/reports/1/answer")
                        .with(user("user1").roles("USER"))
                        .with(csrf())
        )
                .andExpect(status().isForbidden());
        verifyNoInteractions(errorReportService);
    }

    @Test
    @DisplayName("일반 사용자는 오류신고를 반려할 수없다")
    void userCannotRejectReport() throws Exception{
        //when & then
        mockMvc.perform(
                post("/admin/reports/1/reject")
                        .with(user("user1").roles("USER"))
                        .with(csrf())
        )
                .andExpect(status().isForbidden());

        verifyNoInteractions(errorReportService);
    }

    @Test
    @DisplayName("관리자는 오류신고를 반려할 수 있다")
    void adminCanRejectReport() throws Exception{
        //when & then
        mockMvc.perform(
                post("/admin/reports/1/reject")
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf())
                        .param("rejectReason", "반려 사유입니다.")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/reports/1"));

        verify(errorReportService)
                .rejectReport(
                        1L,
                        "반려 사유입니다.",
                        "admin"
                );
    }

    @Test
    @DisplayName("반려 사유가 비어 있으면 반려 처리할 수 없다")
    void rejectReportWithBlankReasonFailsValidation() throws Exception{
        //given
        ErrorReport report = new ErrorReport(
                new User("user1", "password", "사용자"),
                "테스트 제목",
                "테스트 내용"
        );

        when(errorReportService.findReportById(1L))
                .thenReturn(report);

        when(errorReportService.findHistories(1L))
                .thenReturn(List.of());

        //when & then
        mockMvc.perform(
                post("/admin/reports/1/reject")
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf())
                        .param("rejectReason","")
        )
                .andExpect(status().isOk());

        verify(errorReportService, never())
                .rejectReport(anyLong(), anyString(), anyString());
    }

    @Test
    @DisplayName("관리자는 오류신고에 답변을 등록할 수 있다")
    void adminCanAnswerReport() throws Exception{
        //when & then
        mockMvc.perform(
                post("/admin/reports/1/answer")
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf())
                        .param("answer", "답변 내용입니다.")
                        .param("status", "COMPLETED")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/reports/1"));

        verify(errorReportService)
                .answerReport(
                        1L,
                        "답변 내용입니다.",
                        ReportStatus.COMPLETED,
                        "admin"
                );
    }

    @Test
    @DisplayName("답변 내용이 비어 있으면 답변을 등록할 수 없다")
    void answerReportWithBlankAnswerFailsValidation() throws Exception{
            //given
        ErrorReport report = new ErrorReport(
                new User("user1", "password", "사용자"),
                "테스트 제목",
                "테스트 내용"
        );

        when(errorReportService.findReportById(1L))
                .thenReturn(report);

        when(errorReportService.findHistories(null))
                .thenReturn(List.of());

        //when & then
        mockMvc.perform(
                post("/admin/reports/1/answer")
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf())
                        .param("answer","")
                        .param("status","COMPLETED")
        )
                .andExpect(status().isOk());

        verify(errorReportService, never())
                .answerReport(
                        anyLong(),
                        anyString(),
                        any(ReportStatus.class),
                        anyString()
                );
    }
}
