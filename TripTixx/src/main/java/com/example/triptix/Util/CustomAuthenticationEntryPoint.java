package com.example.triptix.Util;

import com.example.triptix.DTO.ResponseObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value()); // Đặt mã trạng thái lỗi là 401 (Unauthorized)
        response.setContentType("application/json;charset=UTF-8");

        Map<String, String> responseRs = new HashMap<>();
//        responseRs.put("Error", "Unauthorized");
        responseRs.put("Message", "Access denied");
//        ResponseObject responseObject =new ResponseObject(false, "Unauthorized", responseRs);
        ResponseObject responseObject = ResponseObject.builder()
                .status(false)
                .code(401)
                .message("Unauthorized")
                .data(responseRs)
                .build();

        PrintWriter out = response.getWriter();
        // Ghi thông báo lỗi vào body của phản hồi
        //out.write("{\"error\":\"Unauthorized\",\"message\":\"Access denied\"}");
        out.write(new ObjectMapper().writeValueAsString(responseObject));
        out.flush();
        out.close();
    }
}
