package com.hwang.errorreport.dto.report;

import lombok.Getter;

@Getter
public class ReportDashboardResponse {

    private final long totalCount;
    private final long receivedCount;
    private final long inprogressCount;
    private final long completedCount;
    private final long rejectedCount;
    private final long unansweredCount;
    private final long answeredCount;

    public ReportDashboardResponse(long totalCount,
                                   long receivedCount,
                                   long inprogressCount,
                                   long completedCount,
                                   long rejectedCount,
                                   long unansweredCount,
                                   long answeredCount){
        this.totalCount = totalCount;
        this.receivedCount = receivedCount;
        this.inprogressCount = inprogressCount;
        this.completedCount = completedCount;
        this.rejectedCount = rejectedCount;
        this.unansweredCount = unansweredCount;
        this.answeredCount = answeredCount;
    }

}
