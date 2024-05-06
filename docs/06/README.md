## 요청 처리
이번 문서에는 HTTP 요청을 처리하는 컨트롤러 매핑과 메소드 인자들에 대해 알아보겠습니다.  

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

하지만 스프링 2.5부터 `@Controller`와 `@RequestMapping` 어노테이션이 도입되면서 하나의 컨트롤러가 HTTP요청에 따라 매핑된 메소드를 실행하는 것이 가능해졌습니다(오래전에는 이것을 `MultiActionController`로 구현했습니다).  

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

`RequestMappingHandlerMapping`은 디폴트로 적용되는 핸들러 매핑 구현체입니다. `@RequestMapping` 어노테이션을 사용하면 `RequestMappingHandlerMapping`에 의해 실행할 메소드를 결정합니다. 속성으로 경로 `path`와 HTTP `method`를 지정할 수 있습니다. `method`를 지정하지 않으면 모든 HTTP method(GET, POST, HEAD, OPTIONS, PUT, PATCH, DELETE, TRACE)에 대해 매핑됩니다.

```
@RequestMapping(path = {"/home"}, method = {RequestMethod.GET, RequestMethod.POST})
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


## 컨트롤러 메소드의 인자들  

디스패처 서블릿은 `RequestMappingHandlerMapping`을 사용하여 메소드를 찾고 실행은 `RequestMappingHandlerAdapter`에 위임합니다. `RequestMappingHandlerAdapter`를 거치면서 메소드로 전달되는 인자들이 만들어지는데, 화면에서 전달된 파라미터들과 함께 부가적으로 추가되는 인자들도 있습니다. 이 과정은 `HandlerMethodArgumentResolver`에 의해 수행됩니다. 메소드에 전달되는 인자들의 종류는 [여기에](https://docs.spring.io/spring-framework/docs/5.3.32/reference/html/web.html#mvc-ann-arguments) 잘 정리되어 있습니다.

컨트롤러 메소드가 받는 주요 인자들은 아래와 같습니다.

- WebRequest, NativeWebRequest  
  >Generic interface for a web request. Mainly intended for generic web request interceptors, giving them access to general request metadata, not for actual handling of the request.

  서블릿 컨테이너가 생성하는 request가 아닌 request입니다. 보통은 서블릿 컨테이너가 재생성하는 HttpServletRequest를 사용하기 때문에 직접 쓸 일이 없습니다.

- HttpServletRequest, HttpServletResponse, MultipartHttpServletRequest, HttpSession  
서블릿에서 사용되는 request, response, session에 해당하는 것으로 HttpSession은 request.getSession(true)와 마찬가지로 세션을 <b>항상 생성</b>합니다.  
`MultipartHttpServletRequest`을 사용하려면 아래와 같은 빈이 등록되어 있어야 합니다(`commons-fileupload` 라이브러리 필요).
  ```
  @Bean
  public CommonsMultipartResolver multipartResolver() {
      CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
      multipartResolver.setMaxUploadSize(2*1024*1024);
      multipartResolver.setMaxInMemorySize(2*1024*1024);  
      return multipartResolver;
  }	
  ```

- PushBuilder  

- Principal  
현재 로그인 등을 통해 인증된 사용자 정보를 가진 객체로 예를 들어 스프링 시큐리티의 경우 `org.springframework.security.core.Authentication` 타입이 전달됩니다.

- Locale, TimeZone, ZoneId  
요청의 로케일, 시간대


- @PathVariable  
URL경로 형태로 전달되는 템플릿 파라미터를 받을 때 사용(주로 API 호출의 경우).
  ```
  @GetMapping("/owners/{ownerId}/pets/{petId}")
  public Pet findPet(@PathVariable Long ownerId, @PathVariable Long petId) {
      // ...
  }
  ```

- @RequestParam  
가장 간단하게 사용할 수 있는 어노테이션으로 폼데이터(multipart/form-data 포함)나 querystring으로 넘어오는 값들을 받을 때 사용합니다. 항목이 많은 경우는 `@RequestBody`나 `@ModelAttribute`를 사용하는 것이 좋습니다.
  ```
  @RequestParam(name = "phoneNumber", required = false) String v,
  ```
  `required=false`이면 해당 이름의 파라미터가 없어도 에러가 나지 않습니다.

- @RequestHeader  
request 헤더에 있는 값이 필요할 때 사용.

- @CookieValue  
요청에 포함된 쿠키 값을 참조할 때 사용.
  ```
  @GetMapping("/home")
  public void handle(@CookieValue("JSESSIONID") String cookie) { 
      //...
  }
  ```

- @RequestBody  
HTTP request 메시지의 body로 전달되는 데이터를 받을 때 사용합니다. `POST`로 전송되는 폼데이터를 받을 수도 있지만 보통은 json 데이터, `Content-type: application/json`인 경우에 사용하게 됩니다. json 데이터의 경우는 해당 필드를 가진 DTO 객체에 자동으로 값을 넣어주게 됩니다.
  ```
  {"name": "Patti"}

  public class TestDto {
	
	   private String name;
	
	   public String getName() {
		   return name;
	   }
  }
  
  @RequestBody(required = false) TestDto data
  data.getName(); //Patti
  ```

- HttpEntity\<B\>  
@RequestBody와 거의 동일. request의 헤더와 바디를 모두 가진 객체.
  >HttpEntity is more or less identical to using @RequestBody but is based on a container object that exposes request headers and body. 

- @RequestPart  
`multipart/form-data`를 받을 때 사용할 수 있습니다. `multipart/form-data`로 전달되는 request는 다음과 같이 하나 이상의 "part"로 구분됩니다. 예를 들어 아래와 같은 폼 데이터를 전송한 경우
  ```
  <form method="post" th:action="@{'/upload'}" enctype="multipart/form-data">
        <input type="text" name="name" value="Tony"/>
        <br/>
        <input type="file" name="upfile" />    
        <br/>
        <input type="button" onclick="fn_submit()" value="Submit"/>        
  </form>
  ```
  실제 request의 바디는 다음과 같이 표시됩니다. 
  ```
  ------WebKitFormBoundaryQnZA9QsTSge8Wuki
  Content-Disposition: form-data; name="name"

  Tony
  ------WebKitFormBoundaryQnZA9QsTSge8Wuki
  Content-Disposition: form-data; name="upfile"; filename=""
  Content-Type: application/octet-stream

  ------WebKitFormBoundaryQnZA9QsTSge8Wuki--
  ```
  메소드에서는 아래와 같이 받을 수 있습니다.
  ```
  @RequestPart(name = "name", required = false) String name,
  @RequestPart(name = "upfile", required = false) MultipartFile file
  ```
  위의 경우는 파일과 함께 폼데이터를(key=value 형태) `String`으로 단순하게 받았지만 보통은 json으로 보내면 json의 각 필드 값을 DTO 객체에 자동으로 넣어주기 때문에 json과 파일을 같이 전송하는 경우 유용하게 사용할 수 있습니다.  

  `@RequestPart`는 서블릿 3.0부터 도입된 `@MultipartConfig` 설정이 선행되어야 정상적으로 동작합니다. `web.xml`이나 `AbstractAnnotationConfigDispatcherServletInitializer` 클래스에 다음을 추가해야 합니다.
  ```
  web.xml
  <multipart-config>
	   <max-file-size>2097152</max-file-size>
	   <max-request-size>4194304</max-request-size>
  </multipart-config>

  AbstractAnnotationConfigDispatcherServletInitializer
  @Override
  protected void customizeRegistration(Dynamic registration) {		
		 registration.setMultipartConfig(new MultipartConfigElement(null, 2097152L, 4194304L, 1024*1024));
  }
  ```
  여기서 유념할 것은 `@MultipartConfig`은 서블릿 컨테이너 레벨에서 파일 전송을 지원하는 것입니다. 이와 함께 서블릿 3.0 기반으로 `multipart/form-data`를 파싱하는 [`StandardServletMultipartResolver`](https://docs.spring.io/spring-framework/docs/5.3.32/javadoc-api/org/springframework/web/multipart/support/StandardServletMultipartResolver.html)를 웹 컨텍스트에 함께 설정할 수 있습니다. `MultipartHttpServletRequest`을 처리할 때 `StandardServletMultipartResolver`만 설정하면 처리되지 않으므로 `@MultipartConfig`와 함께 설정해야 합니다.

  ```
  @Bean
  public StandardServletMultipartResolver multipartResolver() {
		
		StandardServletMultipartResolver multipartResolover = new StandardServletMultipartResolver();
		multipartResolover.setStrictServletCompliance(true);
		return multipartResolover;
  }
  ```
  앞서 말한 것처럼 `commons-fileupload` 기반의 `multipartResolver`를 설정할 수도 있습니다. 이 경우에는 `@MultipartConfig` 설정이 필요없습니다.

- Map, Model, ModelMap

- RedirectAttributes

- @ModelAttribute

- Errors, BindingResult

- @SessionAttribute

컨트롤러 메소드가 리턴하는 주요 타입은 아래와 같은 것이 있습니다.

- @ResponseBody

- HttpEntity\<B\>, ResponseEntity\<B\>

- HttpHeaders

- String

- @ModelAttribute

- ModelAndView

- void



[처음](../README.md)
