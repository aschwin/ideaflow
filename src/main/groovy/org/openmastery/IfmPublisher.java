/**
 * Copyright 2017 New Iron Group, Inc.
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.gnu.org/licenses/gpl-3.0.en.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openmastery;

import org.openmastery.publisher.core.stub.FixtureTimelineInitializer;
import org.openmastery.publisher.security.AuthorizationFilter;
import org.openmastery.publisher.security.InvocationContext;
import org.openmastery.time.LocalDateTimeService;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

public class IfmPublisher {

	public static void main(String[] args) {

		ConfigurableApplicationContext context = SpringApplication.run(IfmPublisherConfig.class, args);
		FixtureTimelineInitializer fixtureInitializer = context.getBean(FixtureTimelineInitializer.class);
		fixtureInitializer.initialize();
	}

	@Bean
	public InvocationContext authenticationDetails() {
		return new InvocationContext();
	}

	@Bean
	public AuthorizationFilter authorizationFilter() {
		return new AuthorizationFilter();
	}

	@Bean
	public LocalDateTimeService localDateTimeService() {
		return new LocalDateTimeService();
	}

}
