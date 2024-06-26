package com.foo.config;

import javax.servlet.Filter;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletRegistration.Dynamic;

import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class MyWebInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
	
    private static final long MAX_UPLOAD_SIZE = 2 * 1024 * 1024; 
    
	@Override
	protected Class<?>[] getRootConfigClasses() {
		return null;
	}

	@Override
	protected Class<?>[] getServletConfigClasses() {
		return new Class<?>[] {
			WebConfig.class,
			SecurityConfig.class,
			AppConfig.class
			
		};
	}

	@Override
	protected String[] getServletMappings() {
		return new String[] {"/"};
	}
	
	@Override
	protected Filter[] getServletFilters() {
		
		CharacterEncodingFilter cef = new CharacterEncodingFilter();
		cef.setEncoding("UTF-8");
		cef.setForceEncoding(true);
		
		return new Filter[] {new HiddenHttpMethodFilter(), cef};
		
	}
	
	@Override
	protected void customizeRegistration(Dynamic registration) {		
		registration.setMultipartConfig(new MultipartConfigElement(null, 2097152L, MAX_UPLOAD_SIZE*2L, 1024*1024));
	}
	
}
