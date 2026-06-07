package com.hwang.errorreport.dto.report;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportAnswerRequest {

    @NotBlank(message = "답변 내용을 입력해주세요.")
    private String answer;
}
