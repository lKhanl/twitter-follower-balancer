package dev.oguzhanercelik.controller.advice;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {NullPointerException.class})
    protected String handleConflict(RuntimeException ex, WebRequest request) {
        if (ex.getMessage().contains("principal") || ex.getMessage().contains("User not found")) {
            return "redirect:/";
        }
        return "";
    }
}