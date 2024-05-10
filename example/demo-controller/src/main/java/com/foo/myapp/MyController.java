package com.foo.myapp;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/test")
@SessionAttributes("myControllerSession")
public class MyController {
		
	
	@RequestMapping(value="/requsetParam.do", method = {RequestMethod.GET, RequestMethod.POST})
    public String requsetParam(@RequestParam(name = "name", required = false) String v, Model model) {
		
		String result = "Hello, " + v; 
		model.addAttribute("result", result);
		return "main";
    }	
		
	@PostMapping(value="/requsetBodyFormData.do")
	public String requsetBodyFormData(@RequestBody(required = false) MultiValueMap<String,String> data, ModelMap model) {
		
		Map<String,String> result = new HashMap<>();
		result.put("result", "Hello, " + data.getFirst("name"));
		
		model.addAttribute("result", result);
		return "main";
    }
	
	@PostMapping(value="/requsetBodyJson.do")
	public String requsetBodyJson(@RequestBody(required = false) TestDto data, ModelMap model) {
		
		String result = "Hello, " + data.getName(); 
		model.addAttribute("result", result);
		return "main";
    }
	
	@PostMapping(value="/httpEntity.do")
	public String httpEntity(HttpEntity<TestDto> data, ModelMap model) {
		
		String result = "Hello, " + data.getBody().getName(); 
		model.addAttribute("result", result);
		return "main";
    }
	
	@PostMapping(value="/redirectAttributes.do")
	public String redirectAttributes(HttpEntity<TestDto> data, RedirectAttributes redirectAttrs, Model model) {
		
		String result = "Hello, " + data.getBody().getName(); 
		model.addAttribute("result", result);
		//redirectAttrs.addFlashAttribute("result", result);
		
		return "redirect:/test/sub.do";
    }
	
	@PostMapping(value="/modelAttribute.do")
	public String modelAttribute(@ModelAttribute TestDto data, Model model) {
		
		String result = "Hello, " + data.getName(); 
		model.addAttribute("result", result);
		
		return "main";
    }
	
	@PostMapping(value="/user.do")
	public String user(@AuthenticationPrincipal User user, Model model) {
		
		String result = "Hello, " + user.getUsername(); 
		model.addAttribute("result", result);
		
		return "main";
    }
	
	@PostMapping(value="/sessionAttribute.do")
	public String sessionAttribute(@SessionAttribute(name = "myAttr") String myAttr, Model model) {
		
		String result = "SessionAttribute=" + myAttr; 
		model.addAttribute("result", result);
		model.addAttribute("myControllerSession", "This is from SESSIONATTRIBUTES");
		
		return "main";
    }
	
	@PostMapping(value="/sessionAttributes.do")
    public String sessionAttributes(@ModelAttribute(name = "myControllerSession") String g) {
		return "main";
    }
	
	
//	@PostMapping(value="/uploadWithFormData")
//    public void upload(MultipartHttpServletRequest multipartReq, HttpServletResponse response) {
//		
//		String name = multipartReq.getParameter("name");
//		MultipartFile file = multipartReq.getFile("upfile");
//		
//		System.out.println(file.getOriginalFilename() + ":" + file.getSize());
//		System.out.println(name);		
//		
//		response.setStatus(HttpServletResponse.SC_OK);
//    }
	
	@PostMapping(value="/uploadWithFormData")
    public void upload(@RequestParam("name") String name, @RequestPart("upfile") MultipartFile file, HttpServletResponse response) {
		
		System.out.println(name);
		System.out.println(file.getOriginalFilename() + ":" + file.getSize());
		
		response.setStatus(HttpServletResponse.SC_OK);
    }
	
	
	

}
