package com.hwang.errorreport.dto.report;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportCreateRequest {

    @NotBlank(message = "제목을 입력해주세요.")
    @Size(max = 100, message = "제목은 최대 100자까지 입력할 수 있습니다.")
    private String title;

    @NotBlank(message = "내용을 입력해주세요.")
    private String content;
}
