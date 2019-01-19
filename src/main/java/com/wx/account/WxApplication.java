package com.wx.account;

import com.wx.account.task.AccessTokenTask;
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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@SpringBootApplication
@ComponentScan
@EnableCaching
@EnableAsync
@EnableScheduling                // 开启定时任务
public class WxApplication implements CommandLineRunner ,WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> {

	public static void main(String[] args) {
		SpringApplication.run(WxApplication.class, args);
	}

	/*配置启动端口*/
	@Override
	public void customize(ConfigurableServletWebServerFactory factory) {
		((TomcatServletWebServerFactory)factory).addConnectorCustomizers(new TomcatConnectorCustomizer() {
			@Override
			public void customize(Connector connector) {
				Http11NioProtocol protocol = (Http11NioProtocol) connector.getProtocolHandler();
				protocol.setPort(80);
//				protocol.setMaxConnections(200);
//				protocol.setMaxThreads(200);
//				protocol.setSelectorTimeout(3000);
//				protocol.setSessionTimeout(3000);
//				protocol.setConnectionTimeout(3000);
			}
		});
	}

	//在项目启动之后执行的方法，order指定顺序
	@Bean
	public AccessTokenTask taskRun(){
		return new AccessTokenTask();
	}

	@Override
	public void run(String... strings) throws Exception {

	}


}

