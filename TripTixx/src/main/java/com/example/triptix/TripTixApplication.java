package com.example.triptix;

import com.example.triptix.Enum.ObjectStatus;
import com.example.triptix.Enum.ProvinceCityType;
import com.example.triptix.Enum.Role;
import com.example.triptix.DTO.ProvinceCity.DetailProvinceCity;
import com.example.triptix.DTO.ProvinceCity.ListDetailProvinceCity;
import com.example.triptix.Model.ConfigSystem;
import com.example.triptix.Model.ProvinceCity;
import com.example.triptix.Model.SpecialDay;
import com.example.triptix.Model.UserSystem;
import com.example.triptix.Repository.ConfigSystemRepo;
import com.example.triptix.Repository.ProvinceCityRepo;
import com.example.triptix.Repository.SpecialDayRepo;
import com.example.triptix.Repository.UserSystemRepo;
import com.example.triptix.Util.AesEncryptionUtil;
import com.example.triptix.Util.Province.Province;
import com.example.triptix.Util.Province.ProvinceUtil;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
@OpenAPIDefinition(
        info = @Info(
                title = "TripTix",
                description = "Swagger API documentation",
                contact = @Contact(name = "Kha", email = "khaphpdz@gmail.com"),
                version = "ver2023 1.0.1",
                license = @License(name = "MIT", url = "www.google.com")
        )
        , servers = {
        @Server(url = "http://localhost:8080", description = "Local server"),
//                @Server(url = "https://triptix.monoinfinity.net", description = "Production server 1"),
        @Server(url = "http://btbs.ap-southeast-1.elasticbeanstalk.com/", description = "Production server 7"),
        @Server(url = "https://triptixv2.azurewebsites.net/", description = "Production server 8"),
        @Server(url = "https://triptix3.azurewebsites.net/", description = "Production server - BACKUP")
}
)
@SecurityScheme(name = "BTBsSecurityScheme",scheme = "bearer", bearerFormat = "JWT", type = SecuritySchemeType.HTTP, in = SecuritySchemeIn.HEADER)
public class TripTixApplication {

    @Autowired
    private UserSystemRepo userSystemRepo;

    @Autowired
    private ConfigSystemRepo configSystemRepo;

    @Autowired
    private ProvinceCityRepo provinceCityRepo;

    @Autowired
    private SpecialDayRepo specialDayRepo;

    public static void main(String[] args) {
        SpringApplication.run(TripTixApplication.class, args);
    }

    @Bean
    FirebaseMessaging firebaseMessaging() throws IOException {
        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new ClassPathResource("key-firebase.json").getInputStream());
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(googleCredentials).build();
        FirebaseApp firebaseApp = FirebaseApp.initializeApp(options, "my-app"); //my-app là tên thôi, nhớ có nó để có thể gửi đc noti nha
        return FirebaseMessaging.getInstance(firebaseApp);
    }

    @Bean
    public TaskScheduler taskScheduler() {
        final ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(2);
        return scheduler;
    }

    @Bean
    AesEncryptionUtil aesEncryptionUtil(){
        return new AesEncryptionUtil();
    }

    @Bean
    BCryptPasswordEncoder passwordEncoder(){
        return  new BCryptPasswordEncoder();
    }

    @Bean
    CommandLineRunner run() {
        return args -> {
            //default account admin
            String username = "admin";
            UserSystem check = userSystemRepo.findByUserName(username);
            if(check == null){  //chưa tồn tại admin
                UserSystem dto = new UserSystem();
                dto.setAddress("Unknown");
                dto.setBirthday(new java.util.Date());
                dto.setGender("MALE");
                dto.setRole("ROLE_"+ Role.ADMIN.name());
                dto.setEmail("triptixsystem@gmail.com");
                dto.setPhone("123456789");
                dto.setFullName("Admintrator");
                dto.setUserName(username);
                dto.setPassword(aesEncryptionUtil().encrypt("123456"));
                dto.setStatus(ObjectStatus.ACTIVE.name());
                dto.setCreatedDate(new java.util.Date());
                userSystemRepo.save(dto);
                System.out.println("==> admin is created !");
            }

            //default account admin
            username = "guest";
            check = userSystemRepo.findByUserName(username);
            if(check == null){  //chưa tồn tại admin
                UserSystem dto = new UserSystem();
                dto.setAddress("Unknown");
                dto.setBirthday(new java.util.Date());
                dto.setGender("MALE");
                dto.setRole("ROLE_"+ Role.CUSTOMER.name());
                dto.setEmail("guestTripTix@gmail.com");
                dto.setPhone("123456789");
                dto.setFullName("Guest");
                dto.setUserName(username);
                dto.setPassword(aesEncryptionUtil().encrypt("123456"));
                dto.setStatus(ObjectStatus.ACTIVE.name());
                dto.setCreatedDate(new java.util.Date());
                userSystemRepo.save(dto);
                System.out.println("==> guest is created !");
            }

            //default config system
            List<ConfigSystem> configSystemList = configSystemRepo.findAll();
            if (configSystemList.size() == 0) {
                // các dịp lễ lớn (tặng 42 coins)
                configSystemRepo.save(new ConfigSystem(1, "Festival Coins (coins)", 42000));
                //vào ngày sinh nhật (tặng 42 coins)
                configSystemRepo.save(new ConfigSystem(2, "Birthday Coins (coins)", 42000));
                //mốc 1 là 10 ticket đã mua đc xu voucher
                configSystemRepo.save(new ConfigSystem(3, "Milestone Voucher 1 (ticket)", 10));
                //mốc 2 là 40 ticket đã mua đc xu voucher
                configSystemRepo.save(new ConfigSystem(4, "Milestone Voucher 2 (ticket)", 40));
                //mốc 3 là 100 ticket đã mua đc xu voucher
                configSystemRepo.save(new ConfigSystem(5, "Milestone Voucher 3 (ticket)", 100));
                //vượt qua mốc 3 thì cứ 20 ticket đã mua đc xu voucher
                configSystemRepo.save(new ConfigSystem(6, "Cross Milestone (ticket)", 20));
                //thưởng mốc 1 là 14000 coins
                configSystemRepo.save(new ConfigSystem(7, "Coins Milestone Voucher 1 (coins)", 14000));
                //thưởng mốc 2 là 19000 coins
                configSystemRepo.save(new ConfigSystem(8, "Coins Milestone Voucher 2 (coins)", 19000));
                //thưởng mốc 3 là 28000 coins
                configSystemRepo.save(new ConfigSystem(9, "Coins Milestone Voucher 3 (coins)", 28000));
                //vượt qua mốc 3 thì cứ 40000 coins
                configSystemRepo.save(new ConfigSystem(10, "Coins Cross Milestone (coins)", 56000));
                //quy ước đổi tiền từ BANK -> conins trong ví là: 1.000 vnđ  = 1.000 coins
                configSystemRepo.save(new ConfigSystem(11, "Coin Exchange Rate (No use)", 1));
                //quy ước đổi tiền từ coins voucher -> coins trong ví là: 1.000 coins voucher = 1.000 coins
                configSystemRepo.save(new ConfigSystem(12, "Xu Exchange Rate (No use)", 1));
                //24 -> (24h thì dc hoàn 80-85%)
                configSystemRepo.save(new ConfigSystem(13, "Time Refund (hours)", 24));
                //30-> Trước 30ph xe chạy thì không hủy vé được
                configSystemRepo.save(new ConfigSystem(14, "Time Cannot Refund (hours)", 30));
                //15->  15ph trc khi xe chạy thì không đc book vé của chuyến đó
                configSystemRepo.save(new ConfigSystem(15, "Time Cannot Book (minutes)", 15));
//            dto.setNonBookingTimeBeforeStart((short) 30); // (30ph trc khi xe chạy ko đc book)
                //10k -> (nạp ít nhất 10k)
//                configSystemRepo.save(new ConfigSystem(16, "Minimum Deposit", 10000));
                //1 lần book tối đa 5 vé (hay 5 chỗ ngồi)
                configSystemRepo.save(new ConfigSystem(16, "Seat Per Booking (number)", 5));
                //85% -> Hủy vé trước 1 ngày (túc: vé chạy T6, T5 hủy thì hoa 85%) thì hoàn 80-85%
                configSystemRepo.save(new ConfigSystem(17, "Per Cancel Ticket Before Time Cannot Refund (number)", 85));
                //(hoàn bth thì 90-95%) hay T6 xe chạy, ngoại trừ hủy T5 là hona2 85% còn lại hủy trc T% thì hoàn 95%
                configSystemRepo.save(new ConfigSystem(18, "Per Cancel Ticket (number)", 95));
                //(mỗi ngày 3 lần cancel book)
//                configSystemRepo.save(new ConfigSystem(20, "Cancel Booking Per Day", 3));
                System.out.println("==> config system is created !");
            }


            //default psecial day in year
            List<SpecialDay> specialDayList = specialDayRepo.findAll();
            if (specialDayList.size() == 0) {
                specialDayRepo.save(new SpecialDay(1, "01-01", "Tết Dương lịch"));
                specialDayRepo.save(new SpecialDay(2, "03-08", "Ngày Quốc tế Phụ nữ"));
                specialDayRepo.save(new SpecialDay(3, "03-10", "Giỗ tổ Hùng Vương"));
                specialDayRepo.save(new SpecialDay(4, "04-30", "Ngày giải phóng miền Nam"));
                specialDayRepo.save(new SpecialDay(5, "05-01", "Ngày Quốc tế Lao động"));
                specialDayRepo.save(new SpecialDay(6, "08-15", "Tết Trung thu"));
                specialDayRepo.save(new SpecialDay(7, "09-02", "Ngày Quốc Khánh"));
                specialDayRepo.save(new SpecialDay(8, "10-20", "Ngày Phụ nữ Việt Nam"));
                specialDayRepo.save(new SpecialDay(9, "12-24", "Ngày lễ Giáng sinh"));
                System.out.println("==> psecial day in year is created !");
            }

            //import hết các tỉnh - tp vào
            List<ProvinceCity> provinceCityList = provinceCityRepo.findAll();
            ProvinceUtil provinceUtil = new ProvinceUtil();
            if (provinceCityList.size() == 0) { //chưa gì gì, cần import vào
                //import tỉnh
                //lấy list province từ OPEN API và import vào
                RestTemplate restTemplate = new RestTemplate();
                String apiUrl = "https://provinces.open-api.vn/api/p/";
                ProvinceCity provinceCity = null;
                //gọi API
                ResponseEntity<Province[]> response = restTemplate.getForEntity(apiUrl, Province[].class);
                if (response.getStatusCode().is2xxSuccessful()) {
                    Province[] provinces = response.getBody();
                    for (Province item : Arrays.asList(provinces)) {
                        provinceCity = new ProvinceCity();
                        provinceCity.setIdProvince(item.getCode() + "");

                        if(item.getName().contains("Tỉnh")){
                            provinceCity.setType(ProvinceCityType.PROVINCE.name());
                            provinceCity.setName(item.getName().substring(5));
//                            provinceCity.setRegion(provinceUtil.getProvinceVn(item.getName().substring(5)).getRegion());
                        }else{
                            provinceCity.setType(ProvinceCityType.CITY.name());
                            provinceCity.setName(item.getName().substring(10));
//                            provinceCity.setRegion(provinceUtil.getProvinceVn(item.getName().substring(10)).getRegion());
                        }
//                        System.out.println(provinceCity.toString());
                        provinceCityRepo.save(provinceCity);

                        //import thành phố
                        String apiUrlDetailProvince = "https://vapi.vnappmob.com/api/province/district/" + item.getCode();
                        ResponseEntity<ListDetailProvinceCity> responseDetail = restTemplate.getForEntity(apiUrlDetailProvince, ListDetailProvinceCity.class);
                        if (responseDetail.getStatusCode().is2xxSuccessful()) {
                            provinceCity = null;
                            List<DetailProvinceCity> detailProvinces = responseDetail.getBody().getResults();
                            if(detailProvinces.size() != 0){
                                for (DetailProvinceCity itemDetail : detailProvinces) {
                                    if(itemDetail.getDistrict_type().equals("Thành phố")){
                                        provinceCity = new ProvinceCity();
                                        provinceCity.setIdProvince(item.getCode() + "-" +itemDetail.getDistrict_id());
                                        provinceCity.setName("Thành Phố " + itemDetail.getDistrict_name().substring(10));
                                        provinceCity.setType(ProvinceCityType.CITY.name());
                                        provinceCityRepo.save(provinceCity);
                                    }
                                }
                            }
                        }
                    } ;
                    System.out.println("==> 63 province ang city each province created !");
                } else {
                    throw new RuntimeException("Lỗi khi gọi API: " + response.getStatusCode());
                }

            };
        };
    }
}
