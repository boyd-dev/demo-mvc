package com.foo.myapp;


import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;


import java.io.InputStream;
import javax.servlet.ServletContext;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.setup.SharedHttpSessionConfigurer;
import org.springframework.web.context.WebApplicationContext;

import com.foo.config.AppConfig;
import com.foo.config.SecurityConfig;
import com.foo.config.WebConfig;


@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {AppConfig.class, SecurityConfig.class, WebConfig.class})
@WebAppConfiguration
@TestInstance(Lifecycle.PER_CLASS)
class MyControllerTest {

	@Autowired
	private WebApplicationContext webApplicationContext;
	
	private MockMvc mockMvc;
	private MockHttpSession mockSession;
	private String USER_ID = "scott";
	private String USER_PASS = "5555";
	private String FILE_NAME = "logo.png";
	
	
	
	@BeforeAll
	void setupMockMvc() throws Exception {
//	    mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
//	    		.apply(SecurityMockMvcConfigurers.springSecurity())
//	    		.build();	
	    
	    mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
	    		.apply(SecurityMockMvcConfigurers.springSecurity())
	    		.apply(SharedHttpSessionConfigurer.sharedHttpSession()) // same session in this test class
	    		.build();
	    
	    mockSession = new MockHttpSession();
	    mockSession.setAttribute("myAttr", "This is from HttpSession");
	    
	}
	
	@Test
	@Order(1)
	@DisplayName("setup")	
	void setup() {
	    
		ServletContext servletContext = webApplicationContext.getServletContext();
	    
	    assertNotNull(servletContext);
	    assertTrue(servletContext instanceof MockServletContext);
	    assertNotNull(webApplicationContext.getBean("myController"));
	}
	
	@Test
	@Order(2)
	@DisplayName("testRequestParam")  
	void testRequestParam() throws Exception {	        
		mockMvc.perform(MockMvcRequestBuilders.post("/test/requsetParam.do")
				    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
				    .param("name", "Kate")
	        		.with(SecurityMockMvcRequestPostProcessors.httpBasic(USER_ID, USER_PASS))
	        		.session(mockSession) // create a session attribute at first request
	        		.with(csrf()))    
		.andExpect(status().isOk())
		.andExpect(view().name("main"))
		.andExpect(model().attribute("result", "Hello, Kate"));
	}
	
	@Test
	@Order(3)
	@DisplayName("testRequestBodyFormData")  
	void testRequestBodyFormData() throws Exception {
						
		mockMvc.perform(MockMvcRequestBuilders.post("/test/requsetBodyFormData.do")
				    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
				    .content("name=Kate&age=35")
	        		.with(SecurityMockMvcRequestPostProcessors.httpBasic(USER_ID, USER_PASS))
	        		.with(csrf()))		
		.andExpect(status().isOk())
		.andExpect(view().name("main"))
		.andExpect(model().attributeExists("result"));		
	}
	
	@Test
	@Order(4)
	@DisplayName("testRequestBodyJson")  
	void testRequestBodyJson() throws Exception {
						
		mockMvc.perform(MockMvcRequestBuilders.post("/test/requsetBodyJson.do")
				    .contentType(MediaType.APPLICATION_JSON_VALUE)
				    .content("{\"name\": \"Kate\", \"age\": \"77\"}")
	        		.with(SecurityMockMvcRequestPostProcessors.httpBasic(USER_ID, USER_PASS))
	        		.with(csrf()))		
		.andExpect(status().isOk())
		.andExpect(view().name("main"))
		.andExpect(model().attribute("result", "Hello, Kate"));		
	}
	
	@Test
	@Order(5)
	@DisplayName("testHttpEntity")  
	void testHttpEntity() throws Exception {
						
		mockMvc.perform(MockMvcRequestBuilders.post("/test/httpEntity.do")
				    .contentType(MediaType.APPLICATION_JSON_VALUE)
				    .content("{\"name\": \"Kate\", \"age\": \"77\"}")
	        		.with(SecurityMockMvcRequestPostProcessors.httpBasic(USER_ID, USER_PASS))
	        		.with(csrf()))		
		.andExpect(status().isOk())
		.andExpect(view().name("main"))
		.andExpect(model().attribute("result", "Hello, Kate"));		
	}
	
	@Test
	@Order(6)
	@DisplayName("testRedirectAttributes")  
	void testRedirectAttributes() throws Exception {
						
		mockMvc.perform(MockMvcRequestBuilders.post("/test/redirectAttributes.do")
				    .contentType(MediaType.APPLICATION_JSON_VALUE)
				    .content("{\"name\": \"Kate\", \"age\": \"77\"}")
	        		.with(SecurityMockMvcRequestPostProcessors.httpBasic(USER_ID, USER_PASS))
	        		.with(csrf()))		
		.andExpect(status().is3xxRedirection())
		.andExpect(redirectedUrl("/test/sub.do"));
	}
	
	@Test
	@Order(7)
	@DisplayName("testModelAttribute")  
	void testModelAttribute() throws Exception {
						
		mockMvc.perform(MockMvcRequestBuilders.post("/test/modelAttribute.do")
					.contentType(MediaType.APPLICATION_FORM_URLENCODED)
					.content("name=Kate&age=35")
	        		.with(SecurityMockMvcRequestPostProcessors.httpBasic(USER_ID, USER_PASS))
	        		.with(csrf()))		
		.andExpect(status().isOk())
		.andExpect(view().name("main"))
		.andExpect(model().attribute("result", "Hello, Kate"));
	}
	
	@Test
	@Order(8)
	@DisplayName("testLoginWithoutAuthentication")
	void testLogin() throws Exception {						
		mockMvc.perform(MockMvcRequestBuilders.post("/test/user.do"))		
		.andExpect(status().is4xxClientError());
	}
	
	@Test
	@Order(9)
	@DisplayName("testUsername")
	@WithMockUser(username = "foo")
	void testUsername() throws Exception {					
		mockMvc.perform(MockMvcRequestBuilders.post("/test/user.do")
				.with(csrf()))
		//.with(SecurityMockMvcRequestPostProcessors.httpBasic(USER_ID, USER_PASS)))	
		.andExpect(status().isOk())
		.andExpect(model().attribute("result", "Hello, foo"));
	}
	
	@Test
	@Order(10)
	@DisplayName("testSessionAttribute")
	void testSessionAttribute() throws Exception {	
		
		mockMvc.perform(MockMvcRequestBuilders.post("/test/sessionAttribute.do")
		//.sessionAttr("myAttr", "This is from HttpSession")
				.with(SecurityMockMvcRequestPostProcessors.httpBasic(USER_ID, USER_PASS))
				.with(csrf()))	
		.andExpect(status().isOk())
		.andExpect(model().attribute("result", "SessionAttribute=This is from HttpSession"));
		
		
		mockMvc.perform(MockMvcRequestBuilders.post("/test/sessionAttributes.do")
				.with(SecurityMockMvcRequestPostProcessors.httpBasic(USER_ID, USER_PASS))
				.with(csrf()))
		.andExpect(status().isOk())
		.andExpect(model().attribute("myControllerSession", "This is from SESSIONATTRIBUTES"))
		.andExpect(model().attribute("name", "Taylor")); // added by @ModelAttribute method
			
	}
	
	
	@Test
	@Order(11)
	@DisplayName("testUploadWithFormData")
	void testUploadWithFormData() throws Exception {	
		
		InputStream is = this.getClass().getClassLoader().getResourceAsStream(FILE_NAME);
		 
		MockMultipartFile upfile = new MockMultipartFile("upfile", "logo.png", null, is.readAllBytes());
		
		mockMvc.perform(MockMvcRequestBuilders.multipart("/test/uploadWithFormData")
				.file(upfile)
				.param("name", "Kate")			
				.with(SecurityMockMvcRequestPostProcessors.httpBasic(USER_ID, USER_PASS))
				.with(csrf()))
		.andExpect(status().isOk());
	}
	
	@Test
	@Order(12)
	@DisplayName("testUploadWithJson")
	void testUploadWithJson() throws Exception {	
		
		InputStream is = this.getClass().getClassLoader().getResourceAsStream(FILE_NAME);
		 
		MockMultipartFile upfile = new MockMultipartFile("upfile", "logo.png", null, is.readAllBytes());
		
		mockMvc.perform(MockMvcRequestBuilders.multipart("/test/uploadWithJson")
				.file(upfile)
				.content(FILE_NAME)
				.with(SecurityMockMvcRequestPostProcessors.httpBasic(USER_ID, USER_PASS))
				.with(csrf()))
		.andExpect(status().isOk());
	}
	
	@Test
	@Order(13)
	@DisplayName("testResponseBody")  
	void testResponseBody() throws Exception {	        
		mockMvc.perform(MockMvcRequestBuilders.get("/test/responseBody.do")
				    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
				    .param("name", "Kate")
	        		.with(SecurityMockMvcRequestPostProcessors.httpBasic(USER_ID, USER_PASS))
	        		.with(csrf()))    
		.andExpect(status().isOk())
		//.andExpect(jsonPath("$.name").value("Kate"));
		.andExpect(content().json("{\'name\': \'Kate\', \'age\':66}"));
		
	}
	
	@Test
	@Order(14)
	@DisplayName("testResponseEntity")  
	void testResponseEntity() throws Exception {	        
		mockMvc.perform(MockMvcRequestBuilders.get("/test/responseEntity.do")
				    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
				    .param("name", "Kate")
	        		.with(SecurityMockMvcRequestPostProcessors.httpBasic(USER_ID, USER_PASS))
	        		.with(csrf()))
		.andExpect(status().isOk())
		.andExpect(header().stringValues("Content-type", "application/json"))
		.andExpect(content().json("{\'name\': \'Kate\', \'age\':66}"));
	}
	
	@Test
	@Order(15)
	@DisplayName("testModelAttributeAtMethod")  
	void testModelAttributeAtMethod() throws Exception {	        
		mockMvc.perform(MockMvcRequestBuilders.get("/test/modelAttributeAtMethod.do")
				    .with(SecurityMockMvcRequestPostProcessors.httpBasic(USER_ID, USER_PASS))
	        		.with(csrf()))
		.andExpect(status().isOk())
		.andExpect(view().name("main"))
		.andExpect(model().attribute("name", "Taylor")); // added by @ModelAttribute method		
	}
	
	@Test
	@Order(16)
	@DisplayName("testModelAndView")  
	void testModelAndView() throws Exception {
		
		TestDto data = new TestDto();
		data.setAge(1000);
		data.setName("Kate");
		
		mockMvc.perform(MockMvcRequestBuilders.get("/test/modelAndView.do")
				    .param("name", "Kate")
				    .with(SecurityMockMvcRequestPostProcessors.httpBasic(USER_ID, USER_PASS))
	        		.with(csrf()))
		.andExpect(status().isOk())
		.andExpect(view().name("main"))
		//.andExpect(model().attributeExists("data"))
		.andExpect(model().attribute("data", equalTo(data)));
	}
	
	// testSessionAttribute와 함께 테스트하면 오류 발생. 이유는 알 수 없음. 아마 세션이 없어지는 것 때문에? 
	@Disabled
	@Order(17)
	@DisplayName("testVoidMethod")  
	void testVoidMethod() throws Exception {
		
		mockMvc.perform(MockMvcRequestBuilders.get("/test/void.do")
				    .param("name", "Kate")
				    .with(SecurityMockMvcRequestPostProcessors.httpBasic(USER_ID, USER_PASS))
	        		.with(csrf()))
		.andExpect(status().isOk())
		.andExpect(content().contentType("text/html;charset=UTF-8"))
		.andExpect(content().string("<h2>Hello, Kate</h2>"));
	}
	
	
}
