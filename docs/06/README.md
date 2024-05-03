## 요청 처리
여기서는 HTTP 요청을 처리하는 컨트롤러 매핑과 메소드 인자들에 대해 알아보겠습니다.  

[스프링 MVC](https://docs.spring.io/spring-framework/docs/5.3.32/reference/html/web.html#mvc)는 웹 애플리케이션 프레임워크로 "프론트 컨트롤러 패턴"을 사용합니다. 디스패처 서블릿이 바로 그 역할을 하고 있는데 모든 요청은 디스패처 서블릿을 거쳐서 URL에 매핑된 "핸들러"로 전달됩니다. 여기서 "핸들러"는 보통 컨트롤러의 메소드가 됩니다.

>Spring MVC, as many other web frameworks, is designed around the front controller pattern where a central Servlet, the DispatcherServlet, provides a shared algorithm for request processing, while actual work is performed by configurable delegate components.

과거에는 컨트롤러가 `org.springframework.web.servlet.mvc.Controller` 인터페이스를 구현하는 것이었습니다.

```
public class MyController implements Controller {
    
	@Override
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.getWriter().println("Hello, world");
        return null;
    }
}
```

하지만 스프링 2.5부터 `@Controller`와 `@RequestMapping` 어노테이션이 도입되면서 하나의 컨트롤러가 HTTP요청에 따라 매핑된 메소드를 실행하는 것이 가능해졌습니다(과거에는 이것을 `MultiActionController`로 구현했습니다).  

디스패처 서블릿이 HTTP 요청을 어느 메소드로 보내서 처리할지를 결정하려면 중간에 `HandlerMapping`과 `HandlerAdaptor`라는 것이 필요합니다. 원래는 이러한 `HandlerMapping`, `HandlerAdaptor` 인터페이스를 구현한 빈들을 설정해주는 작업이 필요했지만 스프링 3.0부터는 `<mvc:annotation-driven />` 또는 `@EnableWebMvc`으로 설정되는 "MVC 간소화" 기능으로 자동화되었습니다. 설정하지 않으면 [디폴트 빈](https://github.com/spring-projects/spring-framework/blob/5.3.x/spring-webmvc/src/main/resources/org/springframework/web/servlet/DispatcherServlet.properties)들이 동작하게 됩니다. 

이와 같이 디스패처 서블릿은 요청과 응답 처리를 위해 그 역할을 다른 [여러 빈들](https://docs.spring.io/spring-framework/docs/5.3.32/reference/html/web.html#mvc-servlet-special-bean-types)에게 위임합니다. 

- HandlerMapping
- HandlerAdapter
- HandlerExceptionResolver
- ViewResolver
- LocaleResolver
- ThemeResolver
- MultipartResolver
- FlashMapManager

## @RequestMapping

`RequestMappingHandlerMapping`은 요청 URL에 따라 실행할 메소드를 결정합니다. `@RequestMapping` 어노테이션을 추가한 메소드에 적용됩니다. 속성으로 경로와 HTTP method를 지정할 수 있습니다. method를 지정하지 않으면 모든 HTTP method(GET, POST, HEAD, OPTIONS, PUT, PATCH, DELETE, TRACE)에 대해 매핑됩니다.

```
@RequestMapping(value = {"/home"}, method = {RequestMethod.GET, RequestMethod.POST})
```
HTTP method에 따라 아래와 같은 축약된 어노테이션들을 사용할 수 있습니다.  

- @GetMapping
- @PostMapping
- @PutMapping
- @DeleteMapping
- @PatchMapping

요청 URL 패턴은 두 가지 형태가 있습니다. 

- PathPattern
- AntPathMatcher


## 핸들러 메소드의 인자들  

디스패처 서블릿이 `RequestMappingHandlerMapping`은 메소드를 찾는 역할까지만 하고 실제 실행은 `RequestMappingHandlerAdapter`가 수행합니다. `RequestMappingHandlerAdapter`를 거치면서 메소드로 전달되는 인자들이 만들어지는데, HTTP 요청의 파라미터들과 함께 부가적으로 추가되는 인자들도 있습니다. 이 과정은 `HandlerMethodArgumentResolver`에 의해 수행됩니다. 메소드에 전달되는 인자들의 종류는 [여기에](https://docs.spring.io/spring-framework/docs/5.3.32/reference/html/web.html#mvc-ann-arguments) 잘 정리되어 있습니다.





[처음](../README.md)
