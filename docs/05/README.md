## WebApplicationInitializer 

서블릿 3.0 이상부터 스프링은 web.xml을 자바 클래스로 대체할 수 있도록 `WebApplicationInitializer`라는 인터페이스를 제공합니다.
JavaConfig와 함께 사용하여 모든 설정을 "프로그램적으로(programmatically)" [구성](https://docs.spring.io/spring-framework/docs/5.3.32/reference/html/web.html#mvc-container-config)할 수 있게 되었습니다. 

스프링은 `WebApplicationInitializer` 구현체인 `AbstractAnnotationConfigDispatcherServletInitializer`을 제공하고 있으므로 이것을 상속하여 간단하게 자바 클래스 설정으로 변경할 수 있습니다. 아래 코드는 `web.xml`을 제거하고 대체하는 자바 클래스입니다.

```
public class MyWebInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

	@Override
	protected Class<?>[] getRootConfigClasses() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Class<?>[] getServletConfigClasses() {
		return new Class<?>[] {
			WebConfig.class
		};
	}

	@Override
	protected String[] getServletMappings() {
		return new String[] {"/"};
	}
	
	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {		
		
		CharacterEncodingFilter cef = new CharacterEncodingFilter();
		cef.setEncoding("UTF-8");
		cef.setForceEncoding(true);
		FilterRegistration.Dynamic ce = servletContext.addFilter("characterEncodingFilter", cef);
		ce.addMappingForUrlPatterns(null, true, "*.do");
		
		super.onStartup(servletContext);
	}
}
```
`getRootConfigClasses`는 애플리케이션 컨텍스트에 해당하는 구성 클래스를 리턴하면 되고 `getServletConfigClasses`는 웹 애플리케이션 컨텍스트를 설정하는 클래스를 리턴하면 되겠습니다. 스프링 [API 문서](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/servlet/support/AbstractAnnotationConfigDispatcherServletInitializer.html)에는 이렇게 전달 받은 구성 클래스들을 사용하여 각각의 컨텍스트를 생성한다고 설명하고 있습니다.

```
@Nullable  
protected WebApplicationContext createRootApplicationContext()  
```
>Create the "root" application context to be provided to the ContextLoaderListener.
The returned context is delegated to ContextLoaderListener(WebApplicationContext) and will be established as the parent context for any DispatcherServlet application contexts. 

```
protected WebApplicationContext createServletApplicationContext()  
```
>Create a servlet application context to be provided to the DispatcherServlet.
The returned context is delegated to Spring's DispatcherServlet(WebApplicationContext).

`getServletMappings`은 `DispatcherServlet`이 처리하는 URL 패턴 매핑입니다. `CharacterEncodingFilter` 필터도 `onStartup` 메소드를 오버라이드하여 추가되었습니다.

`servlet-context.xml`을 제거하고 대체하는 클래스는 `WebConfig`라는 이름으로 만들었습니다. `@EnableWebMvc`은 `<mvc:annotation-driven/>`에 해당하는 어노테이션입니다.

```
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {"com.foo.myapp"}, 
        includeFilters = {@ComponentScan.Filter(type=FilterType.ANNOTATION, value=Controller.class)}, 
        excludeFilters = {
        		@ComponentScan.Filter(type=FilterType.ANNOTATION, value=Service.class),
        		@ComponentScan.Filter(type=FilterType.ANNOTATION, value=Repository.class)
        })
public class WebConfig implements WebMvcConfigurer {
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/resources/**").addResourceLocations("/resources/static/");
	}
	
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

}
```

이렇게 작성된 자바 구성 클래스들은 서블릿 컨테이너(서블릿 버전 3이상)가 구동될 때 자동으로 부트스트랩되어 적용됩니다.  

스프링 MVC의 구조를 가장 간단한 형태로 알아보았습니다. 이 예제에서는 서비스와 레포지토리는 없지만 
기존의 백엔드를 그대로 옮기고 애플리케이션 컨텍스트를 설정하면 결국 웹 애플리케이션으로 구성할 수 있을 것입니다. 물론 화면 작업은 필요하겠지만.


[처음](../README.md) | [다음](../06/README.md)
