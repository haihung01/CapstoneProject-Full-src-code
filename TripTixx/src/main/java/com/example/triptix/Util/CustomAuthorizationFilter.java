package com.example.triptix.Util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.triptix.DTO.ResponseObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static java.util.Arrays.stream;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
public class CustomAuthorizationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(     request.getServletPath().equals("/usersystem/login/**") ||
                request.getServletPath().equals("/usersystem/register/**") ||
                request.getServletPath().equals("/usersystem/register-2/**") ||

                request.getServletPath().equals("/v3/api-docs/**") ||
                request.getServletPath().equals("/swagger-ui/**") ||

                request.getServletPath().equals("/otp/**") ||

                request.getServletPath().equals("/payment/**") ||
                (request.getMethod().equals(HttpMethod.GET.name()) && request.getServletPath().equals("/province-city")) ||
                (request.getMethod().equals(HttpMethod.GET.name()) && request.getServletPath().equals("/province-city/detail")) ||

                (request.getMethod().equals(HttpMethod.GET.name()) && request.getServletPath().equals("/config-system")) ||
                (request.getMethod().equals(HttpMethod.GET.name()) && request.getServletPath().equals("/config-system/detail")) ||

                (request.getMethod().equals(HttpMethod.GET.name()) && request.getServletPath().equals("/bus-companys")) ||
                (request.getMethod().equals(HttpMethod.GET.name()) && request.getServletPath().equals("/bus-companys/detail")) ||

                (request.getMethod().equals(HttpMethod.POST.name()) && request.getServletPath().equals("/booking/guest")) ||
                (request.getMethod().equals(HttpMethod.GET.name()) && request.getServletPath().equals("/booking/get-tick-type-of-trip")) ||

                (request.getMethod().equals(HttpMethod.GET.name()) && request.getServletPath().equals("/buses")) ||
                (request.getMethod().equals(HttpMethod.GET.name()) && request.getServletPath().equals("/buses/detail")) ||

                (request.getMethod().equals(HttpMethod.GET.name()) && request.getServletPath().equals("/stations")) ||
                (request.getMethod().equals(HttpMethod.GET.name()) && request.getServletPath().equals("/stations/detail")) ||

                (request.getMethod().equals(HttpMethod.GET.name()) && request.getServletPath().equals("/station-timecome")) ||
                (request.getMethod().equals(HttpMethod.GET.name()) && request.getServletPath().equals("/station-timecome/detail")) ||

                (request.getMethod().equals(HttpMethod.GET.name()) && request.getServletPath().equals("/trip-stop")) ||
                (request.getMethod().equals(HttpMethod.GET.name()) && request.getServletPath().equals("/trip-stop/detail")) ||
                (request.getMethod().equals(HttpMethod.GET.name()) && request.getServletPath().equals("/trip-stop/allStationOfTrip")) ||

                request.getServletPath().equals("/time-convert/**") ||

                request.getServletPath().equals("/trips/search/**") ||
                request.getServletPath().equals("/trips/find-seat/**") ||
                request.getServletPath().equals("/trips/detail/**") ||

                (request.getMethod().equals(HttpMethod.GET.name()) && request.getServletPath().equals("/route")) ||
                request.getServletPath().equals("/route/detail/**") ||
                request.getServletPath().equals("/route/recommend-for-customer/**") ||
                request.getServletPath().equals("/route/route-hot/**") ||

                request.getServletPath().equals("/news") ||
                request.getServletPath().equals("/news/detail/**") ||

                request.getServletPath().equals("/privateBTB/**") ||
                request.getServletPath().equals("/privateReservation/**")
        ){
            filterChain.doFilter(request, response);    // Chuyển tiếp yêu cầu và phản hồi tới bộ lọc tiếp theo trong chuỗi bộ lọc của ứng dụng web
                                                        //nchuẩn hơn: cho pass qua vì nó public vs mn và ko cần autho
        }else{
            String authorizationHeader = request.getHeader(AUTHORIZATION); //AUTHORIZATION = "Authorization"
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")){   //Bearer ... là từ khóa để thể hiện đấy là 1 token theo quy tắc chung
                try {
                    String token = authorizationHeader.substring("Bearer ".length());   //tách lấy token
                    Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());   //nãy dùng thuật toán nào mã hóa thì giờ dùng thuật toán đó giải mã, bao gồm luôn cái keywork"secrect"
                    JWTVerifier verifier = JWT.require(algorithm).build();  //tạo jwt verify từ thuật toán
                    DecodedJWT decodedJWT = verifier.verify(token);     //giải token ra

                    String username = decodedJWT.getSubject();      //nãy lưu usernae ở subject nên lấy ra
                    String[] roles = decodedJWT.getClaim("roles").asArray(String.class);    //tượng tự với này, nãy lưu bằng tên gì giờ gọi y chang v "roles
                    Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
                    stream(roles).forEach(role ->{
                        authorities.add(new SimpleGrantedAuthority(role));
                    });

                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(username, null, authorities);   //tạo UsernamePasswordAuthenticationToken từ username và authorities từ token đãi diện cho user
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);  //xác thực cái UsernamePasswordAuthenticationToken vừa tạo ở trên với SecurityContextHolder hay gọi security ra xem nó đc quyền xem trnag nó mún ko
                    filterChain.doFilter(request, response);    // Chuyển tiếp yêu cầu và phản hồi tới bộ lọc tiếp theo trong chuỗi bộ lọc của ứng dụng web

                }catch (Exception ex){
                    log.info("Error logging in {}", ex.getMessage());
                    int statusCodeReturn = 400;
                    response.setHeader("error", ex.getMessage());
                    response.setContentType(APPLICATION_JSON_VALUE);    // APPLICATION_JSON_VALUE = "application/json"
                    if(ex.getMessage().contains("expired")){
                        response.setStatus(UNAUTHORIZED.value());
                        statusCodeReturn = 401;
                    }else{
                        response.setStatus(FORBIDDEN.value());  //FORBIDDEN(403, Series.CLIENT_ERROR, "Forbidden")
                        statusCodeReturn = 403;
                    }
                    //response.sendError(FORBIDDEN.value());

                    Map<String, String> error = new HashMap<>();   //vì json thường show kiểu key:value nên type Map là hợp lý
                    error.put("Message", ex.getMessage());
//                    ResponseObject responseObject =new ResponseObject(false, "Error valid token", error);
                    ResponseObject responseObject = ResponseObject.builder()
                            .status(false)
                            .code(statusCodeReturn)
                            .message("Error valid token")
                            .data(error)
                            .build();

                    new ObjectMapper().writeValue(response.getOutputStream(), responseObject);   //ghi error vào response
                }
            }else{
                filterChain.doFilter(request, response);
            }
        }
    }
}
