## 애플리케이션 컨텍스트 - ContextLoaderListener

`web.xml`에 `DispatcherServlet`을 설정하고 웹 애플리케이션 컨텍스트를 만들었습니다. 웹 애플리케이션 컨텍스트는 서블릿 컨텍스트이며 프리젠테이션 레이어를 담당합니다. 그렇다면 백엔드에 해당하는 서비스와 레포지토리들을 위한 컨텍스트는 어디에 있는 것일까요?  

스프링 MVC에서는 컨텍스트를 두 계층으로 구분하고 있습니다. 웹 애플리케이션 컨텍스트는 앞에서 만든 `DispatcherServlet`으로 생성되지만 그것을 포괄하는 "root" 애플리케이션 컨텍스트를 별도로 가지고 있습니다.  

일반적으로 `DispatcherServlet`을 여러 개 설정할 수 있는데, 이를테면 사용자 HTTP 요청용 디스패처와 API 호출용 디스패처 두 개를 가질 수도 있습니다. 하지만 "root" 애플리케이션 컨텍스트는 한 개이며, 모든 웹 컨텍스트가 공유하게 됩니다(앞으로 "root" 애플리케이션 컨텍스트를 그냥 애플리케이션 컨텍스트라고 하겠습니다).

스프링이 제공하는 `ContextLoaderListener`는 바로 이러한 애플리케이션 컨텍스트를 생성합니다. 이것을 설정하면 디폴트로 /WEB-INF/applicationContext.xml을 찾아서 애플리케이션 컨텍스트를 만듭니다. 보통은 다른 경로를 지정하기 위해 `<context-param>`을 설정합니다. 그래서 `web.xml`에 다음과 같이 작성할 수 있습니다.

```
<context-param>
    <param-name>contextConfigLocation</param-name>
    <param-value>classpath*:framework/spring/**/context-*.xml</param-value>
</context-param>
     
<listener>
    <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
</listener>
```
`ContextLoaderListener`는 `ServletContext` 라이프사이클 동안 발생하는 이벤트를 감지할 수 있는 `ServletContextListener` 인터페이스를 구현하기 때문에 웹 애플리케이션이 시작되거나 종료할 때 애플리케이션 컨텍스트의 빈들을 초기화하거나 정리할 수 있습니다.  

서버가 웹 애플리케이션을 시작하면 `ContextLoaderListener`는 `contextConfigLocation`에 설정된 위치에서 설정 파일들을 로드하여 빈들을 생성합니다. 설정 파일은 그 역할에 따라 나누어 작성할 수 있으므로 파일 네이밍을 정해서 전달했습니다. 예를 들어 아래와 같이 여러 개의 설정 [파일](https://github.com/boyd-dev/MyNewProject/tree/master/src/main/resources/framework/spring)들이 존재할 수 있습니다.

```
context-datasource.xml
context-security.xml
context-transaction.xml
...
```


  [처음](../README.md) | [다음](../05/README.md)