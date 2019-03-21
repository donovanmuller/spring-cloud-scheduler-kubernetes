/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.scheduler.spi.kubernetes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.scheduler.spi.junit.AbstractExternalResourceTestSupport;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;

/**
 * JUnit {@link org.junit.Rule} that detects the fact that a Kubernetes installation is
 * available.
 *
 * @author Chris Schaefer
 */
public class KubernetesTestSupport extends AbstractExternalResourceTestSupport<KubernetesClient> {
	private ConfigurableApplicationContext context;

	protected KubernetesTestSupport() {
		super("KUBERNETES");
	}

	@Override
	protected void cleanupResource() throws Exception {
		context.close();
	}

	@Override
	protected void obtainResource() throws Exception {
		context = new SpringApplicationBuilder(Config.class).web(false).run();
		resource = context.getBean(KubernetesClient.class);
		resource.namespaces().list();
	}

	@Configuration
	@EnableAutoConfiguration
	@EnableConfigurationProperties(KubernetesSchedulerProperties.class)
	public static class Config {
		@Autowired
		private KubernetesSchedulerProperties kubernetesSchedulerProperties;

		@Bean
		public KubernetesClient kubernetesClient() {
			return new DefaultKubernetesClient().inNamespace(kubernetesSchedulerProperties.getNamespace());
		}
	}
}
