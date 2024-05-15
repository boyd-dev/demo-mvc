## 스프링 MVC

이 글은 자바 웹 애플리케이션을 만들 때 사용하는 스프링 MVC 대해 간단히 알아보려는 목적으로 작성되었습니다. 여기서는 스프링 부트를 사용하지 않고 스프링 프레임워크 5를 사용합니다. 예제의 소스는 [여기](https://github.com/boyd-dev/demo-mvc/tree/main/example)를 보면 되겠습니다.

개발 도구로 STS 4.20을 사용하는데, 알려진 것처럼 STS 4부터는 "legacy" 스프링 MVC 프로젝트를 생성할 수 없기 때문에 수동으로("from scratch"😁) 그레이들 프로젝트를 만들었습니다.  

서블릿 컨테이너로 사용하는 톰캣의 경우 10은 서블릿 스펙 6을 구현하면서 기존 라이브러리와 잘 맞지 않는 부분이 있어서 서블릿 4(JSP 2.3)를 구현한 톰캣 9를 사용하였습니다. 톰캣-서블릿-JDK 버전의 관계는 [여기](https://tomcat.apache.org/whichversion.html)를 참조하면 될 것 같습니다.

예제 환경은 다음과 같습니다.

- JDK 17
- Spring MVC 5.3.32
- Servlet 4
- Thymeleaf 3
- Tomcat 9.0.87
- Hibernate 5.3.36.Final
- MySQL 8.0
- Junit 5.9.3
- Gradle 8.6
- IDE - STS 4.20.1

목차

1. [개요](01/README.md)
2. [스프링 MVC 프로젝트(STS)](02/README.md)
3. [스프링 MVC 설정](03/README.md)
4. [애플리케이션 컨텍스트](04/README.md)
5. [WebApplicationInitializer](05/README.md)
6. [요청처리](06/README.md)
7. [응답처리](07/README.md)

참고  
[Spring MVC docs 5.3.32](https://docs.spring.io/spring-framework/docs/5.3.32/reference/html/web.html#mvc)
