package com.example.demo;

import com.example.demo.models.DBSuite;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class DemoApplicationTests extends DBSuite {

	@Test
	void contextLoads() {
	}

}
