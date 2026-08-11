package com.hwang.errorreport.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public String hadnleIllegalArgumentException(IllegalArgumentException e, Model model){
        model.addAttribute("message", e.getMessage());
        return "error/custom-error";
    }

    @ExceptionHandler(IllegalStateException.class)
    public String handleIllegalStateException(IllegalStateException e, Model model){
        model.addAttribute("message", e.getMessage());
        return "error/custom-error";
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxUploadSizeExceeded(MaxUploadSizeExceededException e, Model model){
        model.addAttribute("message", "첨부파일은 20MB 이하만 업로드할 수 있습니다.");
        return "error/custom-error";
    }

    @ExceptionHandler(MultipartException.class)
    public String handleMultipartException(MultipartException e, Model model){
        model.addAttribute("message", "첨부파일 중 오류가 발생했습니다. 파일 크기와 형식을 확인해주세요.");
        return "error/custom-error";
    }
}
