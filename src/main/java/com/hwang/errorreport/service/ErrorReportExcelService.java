package com.hwang.errorreport.service;

import com.hwang.errorreport.domain.report.ErrorReport;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ErrorReportExcelService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm");

    public byte[] createReportExcel(List<ErrorReport> reports){
        try(Workbook workbook = new XSSFWorkbook();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream()){

            Sheet sheet = workbook.createSheet("오류신고 목록");

            createHeader(sheet);
            createBody(sheet, reports);

            for(int i = 0; i <7; i++){
                sheet.autoSizeColumn(i);
            }

            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e){
            throw new IllegalStateException("엑셀 파일 생성 중 오류가 발생했습니다.");
        }
    }

    private void createHeader(Sheet sheet){
        Row header = sheet.createRow(0);

        header.createCell(0).setCellValue("ID");
        header.createCell(1).setCellValue("제목");
        header.createCell(2).setCellValue("작성자");
        header.createCell(3).setCellValue("처리상태");
        header.createCell(4).setCellValue("답변여부");
        header.createCell(5).setCellValue("작성일");
        header.createCell(6).setCellValue("수정일");
    }

    private void createBody(Sheet sheet, List<ErrorReport> reports){
        for(int i = 0; i < reports.size(); i++){
            ErrorReport report = reports.get(i);
            Row row = sheet.createRow(i+1);

            row.createCell(0).setCellValue(report.getId());
            row.createCell(1).setCellValue(report.getTitle());
            row.createCell(2).setCellValue(report.getUser().getLoginId());
            row.createCell(3).setCellValue(report.getStatus().getDescription());
            row.createCell(4).setCellValue(report.hasAnswer() ? "답변완료" : "미답변");
            row.createCell(5).setCellValue(formatDateTime(report.getCreatedAt()));
            row.createCell(6).setCellValue(formatDateTime(report.getUpdatedAt()));
        }
    }

    private String formatDateTime(LocalDateTime dateTime){
        if(dateTime == null){
            return "-";
        }

        return dateTime.format(FORMATTER);
    }
}
