package com.foo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;
import org.springframework.web.util.pattern.PathPatternParser;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {"com.foo.myapp"})
public class WebConfig implements WebMvcConfigurer {
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/resources/**").addResourceLocations("/resources/static/");
	}
	
//	@Bean
//	public ViewResolver internalResourceVIewResolver() {		
//		InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
//		viewResolver.setViewClass(JstlView.class);
//		viewResolver.setPrefix("/WEB-INF/views/");
//		viewResolver.setSuffix(".jsp");
//		return viewResolver;
//	}
	
	
	
	@Bean
	public SpringResourceTemplateResolver templateResolver() {		
		SpringResourceTemplateResolver tr = new SpringResourceTemplateResolver();
		tr.setPrefix("/WEB-INF/views/");
		tr.setSuffix(".html");
		tr.setTemplateMode("HTML");		
		return tr;
	}
	
	@Bean
	public SpringTemplateEngine templateEngine() {	
		SpringTemplateEngine te = new SpringTemplateEngine();
		te.setTemplateResolver(templateResolver());
		return te;		
	}
	
	@Bean
	public ThymeleafViewResolver viewResolver() {
		
		ThymeleafViewResolver vr = new ThymeleafViewResolver();
		vr.setTemplateEngine(templateEngine());
		vr.setOrder(1);
		vr.setCharacterEncoding("UTF-8");
		return vr;
	}
	
//	@Bean
//    public CommonsMultipartResolver multipartResolver() {
//        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
//        multipartResolver.setMaxUploadSize(2*1024*1024);
//        multipartResolver.setMaxInMemorySize(2*1024*1024);  
//        return multipartResolver;
//    }
	
	@Bean
	public StandardServletMultipartResolver multipartResolver() {
		
		StandardServletMultipartResolver multipartResolover = new StandardServletMultipartResolver();
		multipartResolover.setStrictServletCompliance(true);
		return multipartResolover;
	}
	
	@Override
	public void configurePathMatch(PathMatchConfigurer configurer) {		
		configurer.setPatternParser(new PathPatternParser());
	}

}
