package com.example.triptix.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

@Configuration
public class ReadFileProperties {
	
	@Bean
	public Properties readFile() {
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream("src/main/resources/application.properties"));		//new FileInputStream("tên_file_đọc")
															//bắt buộc 
															//or path cũng đc: src/main/resources/application.properties (tính từ gốc = cấp vs pom.xml hay src)
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return properties;
	}
}	