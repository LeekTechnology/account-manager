package com.wx.account;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Http11NioProtocol;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@SpringBootApplication
@ComponentScan
@EnableCaching
@EnableAsync
public class WxApplication implements CommandLineRunner ,WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> {

	public static void main(String[] args) {
		SpringApplication.run(WxApplication.class, args);
	}

	@Override
	public void run(String... strings) throws Exception {

	}

	/*配置启动端口*/
	@Override
	public void customize(ConfigurableServletWebServerFactory factory) {
		((TomcatServletWebServerFactory)factory).addConnectorCustomizers(new TomcatConnectorCustomizer() {
			@Override
			public void customize(Connector connector) {
				Http11NioProtocol protocol = (Http11NioProtocol) connector.getProtocolHandler();
				protocol.setPort(9090);
//				protocol.setMaxConnections(200);
//				protocol.setMaxThreads(200);
//				protocol.setSelectorTimeout(3000);
//				protocol.setSessionTimeout(3000);
//				protocol.setConnectionTimeout(3000);
			}
		});
	}
}

