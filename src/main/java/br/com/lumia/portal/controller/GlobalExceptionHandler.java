package br.com.lumia.portal.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(AccessDeniedException.class)
    public void handleAccessDeniedException(AccessDeniedException ex) {
        throw ex;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgumentException(IllegalArgumentException ex,
                                                HttpServletRequest request,
                                                RedirectAttributes redirectAttributes) {
        log.warn("Erro de validação ou argumento inválido em {}: {}", request.getRequestURI(), ex.getMessage());
        redirectAttributes.addFlashAttribute("mensagemErro", ex.getMessage());
        String referer = request.getHeader("Referer");
        if (referer != null && !referer.isBlank()) {
            return "redirect:" + referer;
        }
        return "redirect:/";
    }

    @ExceptionHandler(Exception.class)
    public String handleGeneralException(Exception ex, HttpServletRequest request, Model model) {
        log.error("Exceção não tratada na requisição {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        model.addAttribute("status", 500);
        model.addAttribute("error", "Erro Interno do Servidor");
        model.addAttribute("message", "Ocorreu uma instabilidade momentânea no processamento. Por favor, tente novamente.");
        model.addAttribute("path", request.getRequestURI());
        return "error/error";
    }
}
