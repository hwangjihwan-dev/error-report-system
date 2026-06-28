package com.hwang.errorreport.dto.report;

import com.hwang.errorreport.domain.report.ReportStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportAnswerRequest {

    @NotBlank(message = "답변 내용을 입력해주세요.")
    private String answer;

    @NotNull(message = "처리상태를 선택해주세요.")
    private ReportStatus status;
}
