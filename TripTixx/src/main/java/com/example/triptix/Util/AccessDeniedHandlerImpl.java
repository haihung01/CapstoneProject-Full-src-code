package com.example.triptix.Util;

import com.example.triptix.DTO.ResponseObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@Component
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {

        // Cấu hình response body tùy chỉnh
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        // Tạo một đối tượng map với thông điệp lỗi
        Map<String, String> responseRs = new HashMap<>();
//        responseRs.put("Error", "Forbidden");
        responseRs.put("Message", "Access Denied. You are NOT allow to do this function.");
//        ResponseObject responseObject =new ResponseObject(false, "Forbidden", responseRs);
        ResponseObject responseObject = ResponseObject.builder()
                .status(false)
                .code(403)
                .message("Forbidden")
                .data(responseRs)
                .build();


        // Ghi response body vào response
        PrintWriter writer = response.getWriter();
        writer.write(new ObjectMapper().writeValueAsString(responseObject));
        writer.flush();
        writer.close();
    }
}
