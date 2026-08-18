package com.hwang.errorreport.dto.report;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportRejectRequest {

    @NotBlank(message = "반려 사유를 입력해주세요.")
    private String rejectReason;
}
