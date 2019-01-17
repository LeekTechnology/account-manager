package com.wx.account.config.error;

import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 错误代码及错误提示语的数据提取公共类
 */
@Component
public class ErrorCodeProperties {

	private static final String ERROR_MESSAGE_PATH = "classpath:errorMessage";
	private static ErrorCodeProperties errorCodeProperties = new ErrorCodeProperties();
	static ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();

	@PostConstruct
	private static void getErrorProperties() {
		messageSource.setBasename(ERROR_MESSAGE_PATH);
		messageSource.setDefaultEncoding("UTF-8");
		messageSource.setUseCodeAsDefaultMessage(true);
	}

	public static ErrorCodeProperties init() {
		return errorCodeProperties;
	}

	public static String getErrorMessage(String errorCode) {
		getErrorProperties();//临时设置
		String errorMessage = messageSource.getMessage(errorCode, null, null);
		if (errorMessage != null) {
			return errorMessage;
		}
		return "";
	}

	public static String getErrorMessages(String errorCode,Object[] obj) {
		getErrorProperties();//临时设置
		String errorMessage = messageSource.getMessage(errorCode, obj, null);
		if (errorMessage != null) {
			return errorMessage;
		}
		return "";
	}
}
