## 컨트롤러 메소드의 리턴 타입
컨트롤러 메소드가 리턴하는 주요 타입은 아래와 같은 것이 있습니다.

- @ResponseBody  
`HttpMessageConverter`를 거쳐 응답을 리턴합니다. 뷰가 아닌 데이터만을 리턴할때 사용합니다. 그래서 보통 json 데이터를 리턴할때 적용합니다. 컨트롤러 클래스 레벨에 주면 모든 메소드에 대해 적용됩니다. 

- HttpEntity\<B\>, ResponseEntity\<B\>  
`@ResponseBody`와 거의 같고 단지 HTTP status와 헤더 정보를 더 가지고 있습니다.

- String  
뷰 페이지 이름을 리턴합니다. 뷰에서 렌더링할 데이터, 즉 모델을 만들어줄 필요가 있습니다.

- @ModelAttribute  
인자로 전달되는 `@ModelAttribute`와는 달리 메소드 레벨에 지정합니다. 이렇게 하면 모델에 어떤 속성을 추가됩니다. 보통 모델에서 공통적으로 필요한 속성을 넣을 때 사용할 수 있습니다.

- ModelAndView  
  이름 그대로 모델과 뷰를 하나로 합쳐서 보낼 때 사용합니다. `String`으로 뷰를 리턴하면서 모델을 별도로 만드는 것과 동일합니다.

- void  
아무것도 리턴하지 않는다는 의미보다는 직접 `ServletResponse`의 응답을 생성하겠다는 의미입니다. 예를 들어 아래와 같이 `ServletOutputStream`에 직접 데이터를 넣습니다.

  ```
  @GetMapping(value="/void.do")	
  public void voidMethid(@RequestParam(name = "name", required = false) String v, ServletResponse response) {
		
		try {
			response.setCharacterEncoding("UTF-8");
			response.setContentType("text/html");
			ServletOutputStream out = response.getOutputStream();
			out.print("<h2>Hello, " + v + "</h2>");
			
		} catch (IOException e) {
			e.printStackTrace();
		}		
  }
  ```

예제와 테스트 케이스는 [여기](https://github.com/boyd-dev/demo-mvc/tree/main/example/demo-controller)를 참조하세요.

[처음](../README.md) 