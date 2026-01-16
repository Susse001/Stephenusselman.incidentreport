package com.stephenusselman.incidentservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.openai.client.OpenAIClient;

@SpringBootTest
class IncidentServiceApplicationTests {

	@MockitoBean
	private OpenAIClient openAIClient;

	@Test
	void contextLoads() {
	}

}
