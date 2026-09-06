package com.hwang.errorreport.exception;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgumentException(IllegalArgumentException e, Model model){
        model.addAttribute("message", e.getMessage());
        model.addAttribute("status", 400);
        return "error/custom-error";
    }

    @ExceptionHandler(IllegalStateException.class)
    public String handleIllegalStateException(IllegalStateException e, Model model){
        model.addAttribute("message", e.getMessage());
        model.addAttribute("status", 400);
        return "error/custom-error";
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public String handleMethodNotSupported(HttpRequestMethodNotSupportedException e, Model model){
        model.addAttribute("message", "잘못된 요청 방식입니다. 화면의 버튼을 통해 다시 요청해주세요.");
        model.addAttribute("status", 405);
        return "error/custom-error";
    }

    @ExceptionHandler(AccessDeniedException.class)
    public String handleAccessDeniedException(AccessDeniedException e, Model model){
        model.addAttribute("message", "접근 권한이 없습니다.");
        model.addAttribute("status", 403);
        return "error/custom-error";
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxUploadSizeExceeded(MaxUploadSizeExceededException e, Model model){
        model.addAttribute("message", "첨부파일은 20MB 이하만 업로드할 수 있습니다.");
        model.addAttribute("status", 400);
        return "error/custom-error";
    }

    @ExceptionHandler(MultipartException.class)
    public String handleMultipartException(MultipartException e, Model model){
        model.addAttribute("message", "첨부파일 중 오류가 발생했습니다. 파일 크기와 형식을 확인해주세요.");
        model.addAttribute("status", 400);
        return "error/custom-error";
    }
}
