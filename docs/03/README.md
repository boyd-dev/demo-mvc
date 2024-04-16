이제부터 스프링 MVC 설정을 해보겠습니다. 가능하면 최소한의 구성을 해서 전체적인 구조를 파악하는 것이 목적입니다. 우선 xml 방식을 사용해보겠습니다.

## 라이브러리 설정
스프링 MVC를 쓰기 위해서는 관련 라이브러리를 설치해야 합니다. 자바에서는 사용하려는 JDK에 맞는 라이브러리를 찾는 것이 일반적입니다. 또 이렇게 만든 웹 애플리케이션이 서버에서 제대로 실행되는지 보기 위해 톰캣 서버가 필요합니다.

앞서 그레이들 dependencies에서 필요한 라이브러리들을 추가했습니다. STS에서 Refresh Gradle Project를 하면 그레이들 로컬 디렉토리에 라이브러리들이 다운로드 됩니다. `web.xml`부터 작성해보겠습니다.

## web.xml
서블릿 버전은 웹 애플리케이션의 구성파일(deployment descriptor)인 `web.xml`을 작성할 때 적용합니다. `web.xml`은 톰캣을 다운로드하면 예제 애플리케이션에 있는(webapps\examples\WEB-INF) 것을 복사해서 사용하면 틀림없습니다. 

```
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee 
             http://xmlns.jcp.org/xml/ns/javaee/web-app_4_0.xsd" 
         version="4.0" metadata-complete="false">

  ...
</web-app>
```
여기서 하나 유념할 것은 `metadata-complete` 속성입니다. 이 속성은 제거하거나 `false`로 해야 합니다(디폴트는 false). 서블릿 3부터 도입된 이 속성은 `web.xml`만으로 모든 구성 정보들이 완료될 수 있는지 여부를 나타냅니다. 예를 들어 서블릿과 URL을 매핑할 때 `web.xml`에 그것을 넣을 수 있지만 `@WebServlet`이라는 어노테이션을 사용하면 `web.xml`에 정의하지 않아도 서블릿 등록이 가능합니다. 그런데 `metadata-complete=true`이면 `@WebServlet`이 있는 클래스를 스캔하지 않기 때문에 해당 서블릿을 요청했을 때 오류가 발생하게 됩니다. 이것은 스프링의 `<context:component-scan>`에서도 마찬가지로 `@Controller`와 같은 것들이 스캔되지 않기 때문에 컨트롤러가 동작하지 않습니다.

>This attribute defines whether this deployment descriptor and any web fragments, if any, are complete, or whether the class files available to this module and packaged with this application should be examined for annotations that specify deployment information. Deployment information, in this sense, refers to any information that could have been specified by the deployment descriptor or fragments, but instead is specified as annotations on classes. 

## 웹 애플리케이션 컨텍스트 - DispatcherServlet
스프링의 `ApplicationContext`는 스프링 빈을 만들어주는 "공장(factory)"을 말합니다. 스프링은 객체의 생성을 `ApplicationContext`과 같은 외부에서 담당하게 하고 그것을 역으로 주입받기(dependency injection) 때문에 규모가 큰 애플리케이션을 개발할 때 결합도를 낮출 수 있습니다.  

같은 맥락으로 스프링 MVC에서도 웹 애플리케이션의 프리젠테이션 레이어를 담당하는 빈(bean)을 관리하는 `WebApplicationContext`을 구성하는데, 그것을 해주는 것이 `DispatcherServlet`입니다. 즉 이 서블릿을 등록하면 주어진 빈 설정에 따라 `WebApplicationContext`을 생성합니다. 

빈 설정은 `contextConfigLocation`에 있는 xml 파일을 로드하면서 이루어집니다. 그래서 `web.xml`에서 디스패처 서블릿을 아래와 같이 지정합니다.

```
<servlet>
        <servlet-name>appServlet</servlet-name>
        <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
        <init-param>
            <param-name>contextConfigLocation</param-name>
            <param-value>/WEB-INF/spring/appServlet/servlet-context.xml</param-value>
        </init-param>
        <load-on-startup>1</load-on-startup>
</servlet>
           
<servlet-mapping>
        <servlet-name>appServlet</servlet-name>
        <url-pattern>/*</url-pattern>
</servlet-mapping>
```
`servlet-context.xml`이 바로 빈 설정 파일에 해당합니다. 이름이 "서블릿 컨텍스트"라는 것은 서블릿 객체인 `ServletContext`의 역할을 하기 때문입니다. 디스패처는 HTTP 요청을 받아서 적절한 컨트롤러에 매핑시켜주는 "핸들러 매핑" 기능과 결과를 브라우저에서 볼 수 있도록 가공하는 "뷰 처리기"를 실행합니다.

디스패처 서블릿은 `<servlet-mapping>`의 `<url-pattern>`에 설정된 요청 패턴을 처리합니다. 서블릿 스펙에서는 이러한 URL 패턴 매칭에 대해서 아래와 같은 규칙을 정하고 있습니다.  

>- A string beginning with a "/" character and ending with a "/*" suffix is used for 
path mapping.
>- A string beginning with a "*." prefix is used as an extension mapping.
>- The empty string ("") is a special URL pattern that exactly maps to the 
application's context root, i.e., requests of the form `http://host:port/<context-root>/`. In this case the path info is "/" and the servlet path and context path is empty string ("").
>- A string containing only the "/" character indicates the "default" servlet of the 
application. In this case the servlet path is the request URI minus the context path 
and the path info is null.
>- All other strings are used for exact matches only.  

서블릿의 URL 매핑은 유일해야 합니다. 하나의 URL에 다수의 서블릿이 매핑되면 애플리케이션 배포가 되지 않습니다.

이 예제에서는 "/"로 설정하여 모든 요청이 디스패처 서블릿으로 전달되도록 하겠습니다. 그런데 "/"은 원래 톰캣이 제공하는 디폴트 서블릿으로 매핑되어 있는데 이것을 디스패처 서블릿으로 대체하는 셈이 되기 때문에 원래 디폴트 서블릿이 제공하는 정적인 리소스 요청은 실패하게 됩니다. 그래서 정적인 리소스들에 대한 매핑은 스프링 MVC가 제공하는 `<mvc:resources mapping>`을 이용하기로 하겠습니다.

`servlet-context.xml`의 내용은 아래와 같습니다. xml 방식으로 빈을 정의할 때와 동일하게 작성하면 되겠습니다.

```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:mvc="http://www.springframework.org/schema/mvc"
    xmlns:context="http://www.springframework.org/schema/context"
    xmlns:p="http://www.springframework.org/schema/p"
    xmlns:tx="http://www.springframework.org/schema/tx"
    xsi:schemaLocation="http://www.springframework.org/schema/mvc http://www.springframework.org/schema/mvc/spring-mvc.xsd
        http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd
        http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx.xsd">

    <mvc:annotation-driven />
     
    <context:component-scan base-package="com.foo.myapp">
        <context:include-filter type="annotation" expression="org.springframework.stereotype.Controller"/>
        <context:exclude-filter type="annotation" expression="org.springframework.stereotype.Service"/>
        <context:exclude-filter type="annotation" expression="org.springframework.stereotype.Repository"/>
    </context:component-scan>
     
    <mvc:resources mapping="/resources/**" location="/resources/static/" />
         
    <bean id="templateResolver" class="org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver"
        p:prefix="/WEB-INF/views/"
        p:suffix=".html"
        p:templateMode="HTML"/>
    
    
    <bean id="templateEngine" class="org.thymeleaf.spring5.SpringTemplateEngine">
        <property name="templateResolver">
            <ref bean="templateResolver"/>
        </property>
    </bean>
    
    <bean class="org.thymeleaf.spring5.view.ThymeleafViewResolver">
        <property name="templateEngine">
            <ref bean="templateEngine"/>
        </property>
        <property name="order" value="1"/>
        <property name="characterEncoding" value="UTF-8"/>
    </bean>    

</beans>
```
스프링 MVC가 제공하는 `<mvc:>` 네임스페이스가 사용되었습니다. 웹 컨텍스트에서는 컨트롤러들만 배치해야 하므로 컴포넌트 자동 스캔에서는 `@Controller`만을 포함시킵니다. <b>아직 서비스와 레포지토리들이 없으므로 "root" 컨텍스트 설정은 하지 않았습니다.</b> 하단의 빈들은 뷰를 생성하는 Thymeleaf 설정입니다.

`<mvc:annotation-driven />`은 디스패처가 적절한 컨트롤러를 찾아서 실행할 수 있도록 핸들러 매핑인 `RequestMappingHandlerMapping`과 `RequestMappingHandlerAdapter` 빈을 자동으로 등록시킵니다. 이외에도 스프링 MVC에서 기본적으로 필요한 빈들을 등록하는데 자세한 것은 [여기](https://docs.spring.io/spring-framework/docs/5.3.32/reference/html/web.html#mvc-servlet-special-bean-types)를 참조하기 바랍니다.

이렇게 `DispatcherServlet`만 등록해도 컨트롤러까지 테스트해볼 수 있습니다. 아래와 같이 간단한 컨트롤러를 작성해보면 웹 애플리케이션 컨텍스트의 동작을 확인할 수 있습니다. 

```
@Controller
public class HomeController {	
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		
		logger.info("Welcome home! The client locale is {}.", locale);
		
		Date date = Date.from(Instant.now());
		DateFormat df = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
		String d = df.format(date);

		model.addAttribute("serverTime", d);
		
		return "home";
	}
}
```
이 컨트롤러는 단순히 서버 시간을 model에 넣고 리턴합니다. 스프링 MVC에서 뷰를 담당하는 인터페이스를 "뷰 리졸버(View resolver)"라고 하는데 예제에서는 Thymeleaf를 사용합니다. `org.thymeleaf.spring5.view.ThymeleafViewResolver`는 스프링의 `org.springframework.web.servlet.ViewResolver` 인터페이스를 구현하고 있습니다. 컨트롤러에서 리턴되는 "home"이라는 문자열을 받아서 "/WEB-INF/views/home.html"을 만듭니다. 최종적으로 톰캣은 이 HTML 파일을 응답으로 전송하면 브라우저에 표시됩니다.



[처음](../README.md) | [다음](../04/README.md)