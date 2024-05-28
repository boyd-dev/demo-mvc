## Validation

컨트롤러가 파라미터를 전달 받아서(`@ModelAttribute` 같은 것으로) 객체에 바인딩을 하는 과정에서 유효성을 검사할 필요가 있습니다. 스프링에서는 컨트롤러 파라미터 유효성 검사에 국한되는 것이 아니라 일반적인 자바 빈 유효성 검사(Bean Validation) 차원에서 이 기능을 제공합니다. 

자바 빈의 유효성을 검사하려면 "Java Bean Validation(Jakarta Bean Validation) API"라는 표준을 구현한 라이브러리를 사용해야 합니다. 이 표준은 JSR 303, 349 현재는 380으로 명명되어 있습니다. 스프링 프레임워크 5는 JSR 349(Bean Validation 1.1)를 지원합니다.

잘 알려진 구현체는 [Hibernate Validator](https://docs.jboss.org/hibernate/validator/5.4/reference/en-US/html_single/)가 있습니다. 아래와 같은 의존성 라이브러리가 필요합니다(스프링 5 기준).

```
implementation 'javax.validation:validation-api:1.1.0.Final'
implementation 'org.hibernate:hibernate-validator:5.4.3.Final'
implementation 'org.glassfish:javax.el:3.0.1-b08'
```

그리고 `LocalValidatorFactoryBean` 타입의 빈을 선언해야 합니다.

```
@Bean
public LocalValidatorFactoryBean validator() {
	return new LocalValidatorFactoryBean();
}
```

POJO에서는 각 필드 또는 접근자 메소드에 제약조건에 해당하는 어노테이션을 추가합니다. 

```
public class TestDto {
	
	@NotNull(message = "name is mandatory")	
	private String name;

	@Range(min = 30, max = 60, message = "invalid age")
	private Integer age;

    // getter and setter...
}
```
기본으로 제공되는 제약조건 어노테이션은 [여기](https://docs.jboss.org/hibernate/validator/5.4/reference/en-US/html_single/#section-declaring-bean-constraints)를 참조하면 되겠습니다.  

이렇게 적용된 제약조건들은 컨트롤러에서 `@Valid` 어노테이션으로 검사를 실행할 수 있습니다. 

```
@PostMapping(value="/test.do")
public String test(@Valid TestDto data, Model model) {

	String result = "Hello, " + data.getName(); 
	model.addAttribute("result", result);
		
	return "main";
}
```

제약조건을 만족하지 않는 경우는 `BindException`이 발생하고 이를 처리해주지 않으면 400(BAD_REQUEST) 에러가 발생합니다. `Errors`, `BindingResult` 인자 또는 해당 컨트롤러 클래스에서 `@ExceptionHandler`을 만들어서 예외 처리를 해줄 수도 있습니다.


컨트롤러에서 발생하는 예외를 처리해줄 수 있는 다른 방법은 `@ControllerAdvice`를 사용하는 것입니다. `@ControllerAdvice`의 예외처리는 모든 컨트롤러에 적용되지 때문에 예외처리를 한군데서 처리할 수 있다는 장점이 있습니다.  스프링에서는 [`ResponseEntityExceptionHandler`](https://docs.spring.io/spring-framework/docs/5.3.32/javadoc-api/org/springframework/web/servlet/mvc/method/annotation/ResponseEntityExceptionHandler.html)이라는 기본 예외 처리 클래스를 제공하기 때문에 이것을 상속해서 예외 클래스를 만들 수 있습니다.  

아래 예제는 단순하게 `BindException`만 처리하는 `@ControllerAdvice`입니다.
```
@ControllerAdvice
public class ControllerExceptionHandler {

	private static final Logger logger = LoggerFactory.getLogger(ControllerExceptionHandler.class);
	
	@ExceptionHandler(value = BindException.class)
    public ResponseEntity<String> handleBindingException(BindException be, HandlerMethod handlerMethod) {
		
		logger.error("BindException - {}", handlerMethod.getMethod().getName());
        
		List<Map<String,String>> results = be.getFieldErrors().stream()
				.map(item -> {
					Map<String, String> message = new HashMap<>();
					message.put("message", item.getDefaultMessage());
					return message;
				}).toList();
			
		ObjectMapper objectMapper = new ObjectMapper();
		String data = "";
		try {
			data = objectMapper.writeValueAsString(results);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
		return ResponseEntity.badRequest()
				.contentType(MediaType.APPLICATION_JSON)
				.body(data);
    }
	
}
```

바인드가 되는 인자는 보통 `@ModelAttribute`거나 `@RequestBody`인데 전자를 처리하는 것은 `ModelAttributeMethodProcessor`이고 후자는 `RequestResponseBodyMethodProcessor`가 됩니다. 둘 다 `@Valid`가 있는 인자를 자동으로 검사합니다. 그런데 제약조건에 맞지 않으면 발생하는 예외가 각각 다릅니다. `ModelAttributeMethodProcessor`는 `BindException`을, `RequestResponseBodyMethodProcessor`는 `MethodArgumentNotValidException`을 던집니다. `MethodArgumentNotValidException`은 `BindException`을 상속받고 있으므로 `BindException` 예외처리만 해도 되겠습니다.
