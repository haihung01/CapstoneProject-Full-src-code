package com.example.triptix.Service.Impl;

import com.example.triptix.DTO.ResponseObject;
import com.example.triptix.Service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class RedisServiceImpl implements RedisService {
    public static final int TIME_TO_LIVE = 2;
    @Autowired
    private Environment env;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public ResponseObject<?> saveOTPToCacheRedis(String key, int otp) {
        try {
            int TimeToLive = Integer.parseInt(env.getProperty("time_minutes_expired_otp"));

            redisTemplate.opsForValue().set(key, otp);
            redisTemplate.expire(key, TimeToLive, TimeUnit.MINUTES);   //set key = phone, value = otp và có expired là ? minutes
            String msg = "Save otp of " + key + " : " + otp + " to redis cache with time to live is " + TimeToLive + " minute";
            System.out.println(msg);
            return ResponseObject.builder().status(true).message("save OTP success").data(msg).build();

        } catch (Exception ex) {
            return ResponseObject.builder().status(false).message("error").data(ex.getMessage()).build();
        }
    }

    @Override
    public ResponseObject<?> saveValueToCacheRedis(String key, String value) {
        try {
            int TimeToLive = TIME_TO_LIVE;
            redisTemplate.opsForValue().set(key, value);
            redisTemplate.expire(key, TimeToLive, TimeUnit.MINUTES);
            return ResponseObject.builder().status(true).message("save Key-value success").build();

        } catch (Exception ex) {
            return ResponseObject.builder().status(false).message("error").data(ex.getMessage()).build();
        }
    }

    @Override
    public ResponseObject<?> checkOTP(String phone, int otp) {
        try {
            int otp1 = (Integer) redisTemplate.opsForValue().get(phone);
            if (otp1 == otp) {
                //xóa opt đi nếu co thời gian
                redisTemplate.opsForValue().getAndDelete(phone);
                return ResponseObject.builder().status(true).message("OK, correct OTP !!").build();
            } else {
                return ResponseObject.builder().status(false).message("Wrong OTP !!").build();
            }
        } catch (NullPointerException ex) {
            return ResponseObject.builder().status(false).message("error").data("your OTP is expired !!").build();
        } catch (Exception ex) {
            return ResponseObject.builder().status(false).message("error").data(ex.getMessage()).build();
        }
    }

    @Override
    public ResponseObject<?> deleteValueOfKey(String key) {
        try {
            redisTemplate.opsForValue().getAndDelete(key);
            return ResponseObject.builder().status(true).message("delete success").build();
        } catch (Exception ex) {
            return ResponseObject.builder().status(false).message("error").data(ex.getMessage()).build();
        }
    }

    @Override
    public String getValueOfKeyAndDeleteItFromCache(String codeBooking) {
        try {
            return (String) redisTemplate.opsForValue().getAndDelete(codeBooking);
        } catch (Exception ex) {
            return "error";
        }
    }

    @Override
    public String getValueOfKey(String key) {
        try {
            return (String) redisTemplate.opsForValue().get(key);
        } catch (Exception ex) {
            return "error";
        }
    }

    @Override
    public boolean antiBookedSameTime(List<String> seatName, int idTrip) {
        try {
            ValueOperations<String, String> valueOps = redisTemplate.opsForValue();
            if (valueOps == null) {
                System.out.println("valueOps is null");
            }
            if (seatName.size() > 0) {
                String key = "";
                for (String seatname : seatName) {
                    key = idTrip + " " + seatname;
                    System.out.println("key: " + key);
                    if (valueOps.setIfAbsent(key, "booked")) {
                        System.out.println("add booked key: " + key + " success");
                        redisTemplate.expire(key, TIME_TO_LIVE - 1, TimeUnit.MINUTES); //dùng để reset sau 1 phút để tránh lưu quá nhiều data, trùng
                    } else {
                        System.out.println("booked key: " + key + " fail");
                        return false;
                    }
                }
                System.out.println("=> booked key: " + key + " success");
                return true;
            } else {
                return false;
            }
        } catch (Exception ex) {
            System.out.println("error anti same booked" + ex.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteAntiBookedSameTime(List<String> seatName, int idTrip) {
        try {
            ValueOperations<String, String> valueOps = redisTemplate.opsForValue();
            if (valueOps == null) {
                System.out.println("valueOps is null");
            }
            if (seatName.size() > 0) {
                String key = "";
                for (String seatname : seatName) {
                    key = idTrip + " " + seatname;
                    System.out.println("delete booked key: " + getValueOfKeyAndDeleteItFromCache(key) + " success");
                }
                return true;
            } else {
                return false;
            }
        } catch (Exception ex) {
            System.out.println("error delete anti same booked" + ex.getMessage());
            return false;
        }
    }

    public static void main(String[] args) {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName("redis-18682.c292.ap-southeast-1-1.ec2.cloud.redislabs.com");   //CONFIG SERVER
        configuration.setPort(18682);
        configuration.setUsername("default");
        configuration.setPassword("tRXnMPW8B43zuAOpvebtcmRhxgbu1bQ0");

        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(new JedisConnectionFactory(configuration));
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new JdkSerializationRedisSerializer());
        template.setValueSerializer(new JdkSerializationRedisSerializer());
        template.setEnableTransactionSupport(true);
        template.afterPropertiesSet();

        String seat = "A1";
//        redisTemplate.opsForValue().set(seat, "booked");
        try {
            if (template.opsForValue().setIfAbsent("key", "booked")) {
                System.out.println("OK");
            } else {
                System.out.println("Not OK");
            }
        } catch (Exception ex) {
            System.out.println("error: " + ex.getMessage());
        }
    }
}