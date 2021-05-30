package com.example.demo;

import org.eclipse.jetty.http.HttpCompliance;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.ServerConnector;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Component
	public class DemoWebServerCustomizer implements WebServerFactoryCustomizer<JettyServletWebServerFactory> {

		@Override
		public void customize(JettyServletWebServerFactory factory) {
			factory.addServerCustomizers(server -> {
				for (Connector connector : server.getConnectors()) {
					if (connector instanceof ServerConnector) {
						HttpConnectionFactory connectionFactory = connector
								.getConnectionFactory(HttpConnectionFactory.class);
						connectionFactory.setHttpCompliance(HttpCompliance.LEGACY);
					}
				}
			});
		}
	}

}
