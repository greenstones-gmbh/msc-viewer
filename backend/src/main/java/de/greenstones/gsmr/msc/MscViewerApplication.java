package de.greenstones.gsmr.msc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 
 */
@SpringBootApplication
@EnableScheduling
public class MscViewerApplication {

	public static void main(String[] args) {
		SpringApplication.run(MscViewerApplication.class, args);
	}

}
