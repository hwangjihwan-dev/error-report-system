package com.hwang.errorreport.service;

import com.hwang.errorreport.domain.report.ErrorReport;
import com.hwang.errorreport.domain.report.ErrorReportHistory;
import com.hwang.errorreport.domain.report.ReportStatus;
import com.hwang.errorreport.domain.user.User;
import com.hwang.errorreport.dto.report.ReportUpdateRequest;
import com.hwang.errorreport.repository.ErrorReportHistoryRepository;
import com.hwang.errorreport.repository.ErrorReportRepository;
import com.hwang.errorreport.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ErrorReportServiceTest {

    @Mock
    private ErrorReportRepository errorReportRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private ErrorReportHistoryRepository errorReportHistoryRepository;

    @InjectMocks
    private ErrorReportService errorReportService;

    private User user;
    private User admin;
    private ErrorReport report;

    @BeforeEach
    void setUp(){
        user = new User("user1", "password", "사용자");
        admin = new User("admin", "password", "관리자");

        report = new ErrorReport(
                user,
                "테스트 제목",
                "테스트 내용"
        );
    }

    @Test
    @DisplayName("관리자 답변이 등록된 오류신고는 수정할 수 없다")
    void cannotEditReportWhenAnswered(){
         //given
        report.answer(
                "답변입니다.",
                ReportStatus.COMPLETED,
                admin
        );

        when(errorReportRepository.findByIdAndUserLoginId(1L, "user1"))
                .thenReturn(Optional.of(report));

        //when & then
        assertThatThrownBy(()->
                errorReportService.findMyReportForEdit(1L, "user1")
        )
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("관리자 답변이 등록된 오류신고는 수정할 수 없습니다.");
    }

    @Test
    @DisplayName("반려된 오류신고는 수정할 수 없다")
    void cannotEditRejectedReport(){
        //given
        report.reject(
                "반려 사유",
                admin
        );

        when(errorReportRepository.findByIdAndUserLoginId(1L, "user1"))
                .thenReturn(Optional.of(report));

    //when & then
    assertThatThrownBy(()->
            errorReportService.findMyReportForEdit(1L, "user1")
    )
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("반려된 오류신고는 수정할 수 없습니다.");
    }

    @Test
    @DisplayName("이미 답변이 등록된 오류신고는 반려할 수 없다")
    void cannotRejectAnsweredReport(){
        //given
        report.answer(
                "답변입니다.",
                ReportStatus.COMPLETED,
                admin
        );

        when(errorReportRepository.findById(1L))
                .thenReturn(Optional.of(report));

        //when & then
        assertThatThrownBy(()->
                errorReportService.rejectReport(
                        1L,
                        "반려 사유",
                        "admin"
                )
        )
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 답변이 등록된 오류신고는 반려할 수 없습니다.");

        verify(userRepository, never()).findByLoginId(any());
        verify(errorReportHistoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("답변이 없는 오류신고는 반려할 수 있다")
    void canRejectReportWhenNotAnswered(){
       //given
       when(errorReportRepository.findById(1L))
               .thenReturn(Optional.of(report));

       when(userRepository.findByLoginId("admin"))
               .thenReturn(Optional.of(admin));

       //when & then
        assertThatCode(()->
                errorReportService.rejectReport(
                        1L,
                        "반려 사유입니다.",
                        "admin"
                )
        ).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("오류신고를 반려하면 상태가 REJECTED로 변경된다")
    void rejectReportChangesStatusToRejected(){
        //given
        when(errorReportRepository.findById(1L))
                .thenReturn(Optional.of(report));

        when(userRepository.findByLoginId("admin"))
                .thenReturn(Optional.of(admin));

        //when
        errorReportService.rejectReport(
                1L,
                "반려 사유입니다.",
                "admin"
        );

        //then
        assertThat(report.getStatus())
                .isEqualTo(ReportStatus.REJECTED);
    }

    @Test
    @DisplayName("오류신고를 반려하면 처리 이력이 저장된다")
    void rejectReportSavesHistory(){
        //given
        when(errorReportRepository.findById(1L))
                .thenReturn(Optional.of(report));

        when(userRepository.findByLoginId("admin"))
                .thenReturn(Optional.of(admin));

        ArgumentCaptor<ErrorReportHistory> historyCaptor =
                ArgumentCaptor.forClass(ErrorReportHistory.class);

        //when
        errorReportService.rejectReport(
                1L,
                "반려 사유입니다.",
                "admin"
        );

        //then
        verify(errorReportHistoryRepository, times(1))
                .save(historyCaptor.capture());

        ErrorReportHistory savedHistory = historyCaptor.getValue();

        assertThat(savedHistory.getPreviousStatus())
                .isEqualTo(ReportStatus.RECEIVED);

        assertThat(savedHistory.getNewStatus())
                .isEqualTo(ReportStatus.REJECTED);

        assertThat(savedHistory.getComment())
                .isEqualTo("반려 사유입니다.");

    }

    @Test
    @DisplayName("반려된 오류신고는 삭제할 수 없다")
    void cannotDeleteRejectedReport(){
        //given
        report.reject(
                "반려 사유입니다.",
                admin
        );

        when(errorReportRepository.findByIdAndUserLoginId(1L, "user1"))
                .thenReturn(Optional.of(report));

        //when & then
        assertThatThrownBy(()->
                errorReportService.deleteMyReport(
                        1L,
                        "user1"
                )
        )
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("반려된 오류신고는 삭제할 수 없습니다.");

        verify(errorReportRepository, never()).delete(any(ErrorReport.class));
    }

    @Test
    @DisplayName("답변이 없는 오류신고는 수정할 수 있다")
    void canUpdateReportWhenNotAnswered(){
        //given
        ReportUpdateRequest request = new ReportUpdateRequest();
        request.setTitle("수정된 제목");
        request.setContent("수정된 내용");

        when(errorReportRepository.findByIdAndUserLoginId(1L,"user1"))
                .thenReturn(Optional.of(report));

        //when
        assertThatCode(()->
                errorReportService.updateMyReport(
                        1L,
                        "user1",
                        request,
                        null
                )
        ).doesNotThrowAnyException();

        //then
        assertThat(report.getTitle()).isEqualTo("수정된 제목");
        assertThat(report.getContent()).isEqualTo("수정된 내용");
    }

    @Test
    @DisplayName("답변이 없는 오류신고는 삭제할 수 있다")
    void canDeleteReportWhenNotAnswered(){
        //given
        when(errorReportRepository.findByIdAndUserLoginId(1L,"user1"))
                .thenReturn(Optional.of(report));

        //when
        errorReportService.deleteMyReport(1L,"user1");

        //then
        verify(errorReportRepository, times(1))
                .delete(report);
    }

    @Test
    @DisplayName("관리자가 답변을 등록하면 처리 이력이 저장된다")
    void answerReportSavesHistory(){
        //given
        when(errorReportRepository.findById(1L))
                .thenReturn(Optional.of(report));

        when(userRepository.findByLoginId("admin"))
                .thenReturn(Optional.of(admin));

        ArgumentCaptor<ErrorReportHistory> historyCaptor =
                ArgumentCaptor.forClass(ErrorReportHistory.class);

        //when
        errorReportService.answerReport(
                1L
                ,"답변 내용입니다.",
                ReportStatus.COMPLETED,
                "admin"
        );

        //then
        verify(errorReportHistoryRepository, times(1))
                .save(historyCaptor.capture());

        ErrorReportHistory savedHistory = historyCaptor.getValue();

        assertThat(savedHistory.getPreviousStatus())
                .isEqualTo(ReportStatus.RECEIVED);

        assertThat(savedHistory.getNewStatus())
                .isEqualTo(ReportStatus.COMPLETED);

        assertThat(savedHistory.getComment())
                .isEqualTo("답변 내용입니다.");
    }

    @Test
    @DisplayName("관리자가 답변을 수정하면 처리 이력이 저장된다")
    void updateAnswerSavesHistory(){
        //given
        report.answer(
                "기존 답변",
                ReportStatus.IN_PROGRESS,
                admin
        );

        when(errorReportRepository.findById(1L))
                .thenReturn(Optional.of(report));

        when(userRepository.findByLoginId("admin"))
                .thenReturn(Optional.of(admin));

        ArgumentCaptor<ErrorReportHistory> historyCaptor =
                ArgumentCaptor.forClass(ErrorReportHistory.class);


        //when
        errorReportService.updateAnswer(
                1L,
                "수정된 답변",
                ReportStatus.COMPLETED,
                "admin"
        );

        //then
        verify(errorReportHistoryRepository, times(1))
                .save(historyCaptor.capture());

        ErrorReportHistory savedHistory = historyCaptor.getValue();

        assertThat(savedHistory.getPreviousStatus())
                .isEqualTo(ReportStatus.IN_PROGRESS);

        assertThat(savedHistory.getNewStatus())
                .isEqualTo(ReportStatus.COMPLETED);

        assertThat(savedHistory.getComment())
                .isEqualTo("수정된 답변");
    }
}
