package com.gobernacionSIT.sit_backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "app.security.jwt-secret=test-secret-which-is-long-enough-for-hmac-sha256")
class SitBackendApplicationTests {

	@Test
	void contextLoads() {
	}

}
