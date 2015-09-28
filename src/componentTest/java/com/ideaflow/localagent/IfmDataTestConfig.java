package com.ideaflow.localagent;

import com.ideaflow.localagent.client.EventClient;
import groovyx.net.http.RESTClient;
import java.net.URISyntaxException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class IfmDataTestConfig {

	@Value("${test-server.base_url:http://localhost}")
	private String serverBaseUrl;
	@Value("${test-server.base_url:http://localhost}:${server.port}")
	private String hostUri;

	@Bean
	public EventClient eventClient() {
		return new EventClient(hostUri);
	}

	@Bean
	@Primary
	public RESTClient restClient() throws URISyntaxException {
		RESTClient client = new RESTClient(hostUri);
		return client;
	}

	@Bean
	public RESTClient managementRestClient(@Value("${management.port}") String managementPort) throws URISyntaxException {
		RESTClient client = new RESTClient(serverBaseUrl + ":" + managementPort);
		return client;
	}

}
