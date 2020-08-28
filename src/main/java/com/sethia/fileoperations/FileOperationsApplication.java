package com.sethia.fileoperations;

import com.sethia.fileoperations.utility.fileStorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({
		fileStorageProperties.class
})
public class FileOperationsApplication {

	public static void main(String[] args) {
		SpringApplication.run(FileOperationsApplication.class, args);
	}
}
