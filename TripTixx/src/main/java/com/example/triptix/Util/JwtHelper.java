package com.example.triptix.Util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.triptix.Enum.ObjectStatus;
import com.example.triptix.DTO.ResponseObject;
import com.example.triptix.DTO.UserSystem.ResponseLoginSuccess;
import com.example.triptix.DTO.UserSystem.UserSystemDTOview;
import com.example.triptix.Model.UserSystem;
import com.example.triptix.Repository.UserSystemRepo;
import com.example.triptix.Service.UserSystemService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;
import static org.springframework.http.HttpStatus.FORBIDDEN;

@Component
@Slf4j
public class JwtHelper {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserSystemRepo userRepo;

    @Autowired
    private UserSystemService userService;

    @Autowired
    private Environment env;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * @param password
     * @param username
     * nhận username + password -> check login -> success -> trả về token (access + refresh)
     *                                         -> fail -> trả về lỗi
     * */
    public ResponseEntity<Object> checkLoginToCreateToken(String username, String password) {
        try {
            int timeExpiredToken = env.getProperty("expiredTimeForToken", Integer.class);

            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(username, password);  //nhớ bên UserServiceIMpl ta có overiide lại loadUserByUsername(String username), nó sẽ lấy user info từ username của ta từ Db (encode password nếu có set ở dưới), rồi tạo UsernamePasswordAuthenticationToken từ nó
                                                                                                                                                        //và đồng thời cũng tạo UsernamePasswordAuthenticationToken từ username và password của login này
            Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);    //, so sánh 2 UsernamePasswordAuthenticationToken vs nhau xem nó trùng ko, trùng thì trả về, ko thì quang error AuthenticationException
            if(authentication != null){
                User user = (User)authentication.getPrincipal();
                Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());   //dùng thuật toán HMAC256 để tạo token cùng vs Keywork "secret"
                String access_token = JWT.create()
                        .withSubject(user.getUsername())    //thông tin sẽ đ mã hóa thành token để authen
                        .withExpiresAt(new Date(System.currentTimeMillis() + timeExpiredToken * 60 * 1000))    //10 phút đổi thành mili giây timeExpiredToken
//                    .withIssuer(request.getRequestURI().toString())
                        .withClaim("roles", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                        .sign(algorithm);   //dùng thuật toán ở trên

                UserSystem usersystem= userRepo.findByUserName(username);

                //check status account
                if(usersystem.getStatus().equals(ObjectStatus.DEACTIVE.toString())){
                    ResponseObject responseObject = ResponseObject.builder()
                            .status(false)
                            .code(400)
                            .message("Đăng nhập thất bại, tài khoản của bạn bị vô hiệu hóa")
                            .data(null)
                            .build();
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseObject);
                }

                UserSystemDTOview objDTO = modelMapper.map(usersystem, UserSystemDTOview.class);
                //check nếu là customer thì lấy thêm conin trong ví về (vì chỉ customer mới có wallet)
                if(usersystem.getWallet() != null){
                    objDTO.setCoins(usersystem.getWallet().getBalance());
                }
                objDTO.setBirthdayLong(usersystem.getBirthday().getTime() / 1000);

                ResponseLoginSuccess loginSuccess = new ResponseLoginSuccess();
                loginSuccess.setToken(access_token);
                loginSuccess.setUser(objDTO);

                ResponseObject responseObject = ResponseObject.builder()
                        .status(true)
                        .code(200)
                        .message("Đăng nhập thành công")
                        .data(loginSuccess)
                        .build();

                return ResponseEntity.status(HttpStatus.OK).body(responseObject);
            }else{
                ResponseObject responseObject = new ResponseObject();
                responseObject.setStatus(false);
                responseObject.setMessage("đăng nhập thất bại");
                return ResponseEntity.status(FORBIDDEN).body(responseObject);
            }
        }catch (AuthenticationException ex){
            ResponseObject responseObject = new ResponseObject();
            responseObject.setStatus(false);
            responseObject.setMessage("đăng nhập thất bại: sai tên người dùng hoặc mật khẩu");
            return ResponseEntity.status(FORBIDDEN).body(responseObject);
        }
    }

    public static void main(String[] args) {
        // Tạo một java.sql.Date từ ngày hiện tại
        Date currentDate = new Date(System.currentTimeMillis());

        // Chuyển đổi java.sql.Date thành java.sql.Timestamp
        Timestamp timestamp = new Timestamp(currentDate.getTime());

        // In ra kết quả
        System.out.println("Timestamp: " + timestamp);
    }

    /**
     * @param keyToken (access_token)
     * nhận access_token để valid, giải mã data chứa bên trong sau đó lấy info chi tiết của User từ data đó
     * */
    public ResponseEntity<Object> checkTokenToGetDetailUser(String keyToken){
        if (keyToken != null){   //Bearer ... là từ khóa để thể hiện đấy là 1 token theo quy tắc chung
            try {
                //String token = keyToken.substring("Bearer ".length());   //tách lấy token
                Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());   //nãy dùng thuật toán nào mã hóa thì giờ dùng thuật toán đó giải mã, bao gồm luôn cái keywork"secrect"
                JWTVerifier verifier = JWT.require(algorithm).build();  //tạo jwt verify từ thuật toán
                DecodedJWT decodedJWT = verifier.verify(keyToken);     //giải token ra

                String username = decodedJWT.getSubject();      //nãy lưu usernae ở subject nên lấy ra
                String[] roles = decodedJWT.getClaim("roles").asArray(String.class);    //tượng tự với này, nãy lưu bằng tên gì giờ gọi y chang v "roles
                Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
                stream(roles).forEach(role ->{
                    authorities.add(new SimpleGrantedAuthority(role));
                });

                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(username, null, authorities);   //tạo UsernamePasswordAuthenticationToken từ username và authorities từ token đãi diện cho user
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);  //xác thực cái UsernamePasswordAuthenticationToken vừa tạo ở trên với SecurityContextHolder hay gọi security ra xem nó đc quyền xem trnag nó mún ko

                ResponseValidToken responseValidToken = new ResponseValidToken();
                responseValidToken.setMsg("verify success, welcome " + username);
                responseValidToken.setUser(userRepo.findByUserName(username));

                return ResponseEntity.status(HttpStatus.OK).body(responseValidToken);
            }catch (Exception ex){
                log.info("Error logging in {}", ex.getMessage());
                Map<String, String> error = new HashMap<>();   //vì json thường show kiểu key:value nên type Map là hợp lý
                error.put("error_msg", ex.getMessage());
                ResponseObject responseObject = new ResponseObject();
                responseObject.setStatus(false);
                responseObject.setCode(400);
                responseObject.setMessage("FORBIDDEN");
                responseObject.setData(error);
                return ResponseEntity.status(FORBIDDEN).body(responseObject);
            }
        }else{
            return ResponseEntity.status(FORBIDDEN).body("verify fail.");
        }
    }
}

@Data
class ResponseValidToken{
    private String msg;
    private UserSystem user;
}