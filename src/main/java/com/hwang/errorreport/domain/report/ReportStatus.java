package com.hwang.errorreport.domain.report;

import lombok.Getter;

@Getter
public enum ReportStatus {

    RECEIVED("접수"), //접수
    IN_PROGRESS("처리중"), //처리중
    COMPLETED("완료"), //완료
    REJECTED("반려"); //반려

    private final String description;

    ReportStatus(String description){
        this.description = description;
    }
}
