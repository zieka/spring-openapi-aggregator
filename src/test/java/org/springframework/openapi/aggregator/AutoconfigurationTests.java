/*
 * Copyright 2023 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.openapi.aggregator;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.springdoc.core.configuration.SpringDocConfiguration;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springdoc.core.service.OpenAPIService;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.test.context.runner.ReactiveWebApplicationContextRunner;

public class AutoconfigurationTests {

	@Test
	public void withoutSpringdoc() {
		ApplicationContextRunner contextRunner = new ApplicationContextRunner()
			.withConfiguration(AutoConfigurations.of(OpenApiAggregatorConfiguration.class))
			.withBean(OpenApiAggregatorSpecs.class, () -> new OpenApiAggregatorSpecs())
			.withClassLoader(new FilteredClassLoader(OpenAPIService.class));
		contextRunner.run(context -> {
			assertThat(context.getBeanNamesForType(OpenApiAggregator.class)).isNotEmpty();
			assertThat(context.getBeanNamesForType(AggregatorEndpoint.class)).isEmpty();
		});
	}

	@Test
	public void withSpringdoc() {
		ReactiveWebApplicationContextRunner contextRunner = new ReactiveWebApplicationContextRunner()
			.withConfiguration(AutoConfigurations.of(OpenApiAggregatorConfiguration.class, SpringDocConfiguration.class,
					SpringDocConfigProperties.class, WebFluxAutoConfiguration.class))
			.withBean(OpenApiAggregatorSpecs.class, () -> new OpenApiAggregatorSpecs());
		contextRunner.run(context -> {
			assertThat(context.getBeanNamesForType(OpenApiAggregator.class)).isNotEmpty();
			assertThat(context.getBeanNamesForType(OpenAPIService.class)).isNotEmpty();
			assertThat(context.getBean(OpenAPIService.class).build(Locale.US)).isNotNull();
		});
	}

	@Test
	public void plainWebApp() {
		ReactiveWebApplicationContextRunner contextRunner = new ReactiveWebApplicationContextRunner()
			.withConfiguration(
					AutoConfigurations.of(OpenApiAggregatorConfiguration.class, WebFluxAutoConfiguration.class))
			.withBean(OpenApiAggregatorSpecs.class, () -> new OpenApiAggregatorSpecs())
			.withClassLoader(new FilteredClassLoader(OpenAPIService.class));
		contextRunner.run(context -> {
			assertThat(context.getBeanNamesForType(OpenApiAggregator.class)).isNotEmpty();
			assertThat(context.getBeanNamesForType(AggregatorEndpoint.class)).isNotEmpty();
			assertThat(context.getBeanNamesForType(OpenAPIService.class)).isEmpty();
		});

	}

}
