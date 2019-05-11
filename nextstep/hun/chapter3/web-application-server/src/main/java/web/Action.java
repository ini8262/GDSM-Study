package web;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.DataBase;
import model.User;
import util.HttpRequestUtils;
import util.IOUtils;
import webserver.ResponseHandler;

public class Action {
	private static final Logger log = LoggerFactory.getLogger(Action.class);
	
	Map<String, String> responseParam = new HashMap<String, String>(); 
	Map<String, Object> returnMap = new HashMap<String, Object>(); 
	
	private String cookies;
	private String method;

	public void transmission(String url, Map<String, String> parameter, OutputStream out) throws IOException {
		log.debug("요청 URL ====>   {}", url);
		
		if (url.indexOf("create") >= 0) {
			create(parameter);
		} else if (url.indexOf("login") >= 0) {
			login(parameter);
		} else if (url.indexOf("list") >= 0) {
			list(parameter);
		} else {
			defaultAction(url);
		}
		
		//클라이언트에게 응답
		ResponseHandler responseHandler = new ResponseHandler(responseParam.get("code"), responseParam.get("url"));
		//byte[] body = getBody(url);
		
		//responseHandler.response(out, body);
		responseHandler.setCookie(cookies);
		responseHandler.response(out);
	}
	
	
	private byte[] getBody(String url) throws IOException {
		
		//임시적인것
		if (url.indexOf("list") >= 0) {
			Collection<User> colec = (Collection<User>) returnMap.get("list");
			StringBuilder sb = new StringBuilder();
			for (User user : colec) {
				sb.append(user.toString());
			}
			
			return String.valueOf(sb).getBytes();
		}
		
		String fileName = "./webapp" + url;
	    File file = new File(fileName);

	    return Files.readAllBytes(file.toPath());
	}
	

	private void setResponseParam(String url, String code) {
		responseParam.put("url", url);
		responseParam.put("code", code);
	}
	
	private void defaultAction(String url) {
		setResponseParam(url, "200");
	}
	
	//회원가입
	private void create(Map<String, String> parameter) {
		User user = new User(parameter);
		
		log.debug("회원가입 정보 ====>   {}", user);

		//회원가입
		DataBase.addUser(user);
		
		//회원가입후 리다이렉트
		setResponseParam("/index.html", "302");
	}		
	
	//로그인
	private void login(Map<String, String> parameter) {
		
		if (method.equals("GET")) {
			setResponseParam("/user/login.html", "200");
			return;
		}
		
		//POST
		if (loginCheck(parameter)) {
			log.warn("로그인 성공");
			cookies = "logined=true";
			setResponseParam("/index.html", "302");
		} else {
			log.warn("로그인 실패");
			cookies = "logined=false";
			setResponseParam("/user/login_failed.html", "200");
		}
	}
	
	private boolean loginCheck(Map<String, String> parameter) {
		//조회
		User user =  DataBase.findUserById(parameter.get("userId"));
		
		if (user != null) {
			return user.getPassword().equals(parameter.get("password"));
		}
		
		return false;
	}


	private void list(Map<String, String> parameter) {
		
		//로그인유무 판단
		if(isLogin() == false) {
			setResponseParam("/login.html", "200");
			return;
		}
		
		Collection<User> list = DataBase.findAll();
		
		//html생성
		StringBuilder userHtml = new StringBuilder();
		for (User user : list) {
			log.info("list ====> {}", user);
			userHtml.append(user);
			
			//TODO : 해야함
			
			userHtml.append("<tr>");
			userHtml.append("<th scope=\"row\">1</th> <td>" + user.getUserId() + "</td> <td>자바지기</td> <td>javajigi@sample.net</td><td><a href=\"#\" class=\"btn btn-success\" role=\"button\">수정</a></td>");
            userHtml.append("/<tr>");
		}
		
		
		
		
		
		//회원가입후 리다이렉트
		setResponseParam("/user/list.html", "200");
		
		returnMap.put("list", list);
	}


	private boolean isLogin() {
		Map<String, String> map = HttpRequestUtils.parseCookies(cookies);
		return Boolean.parseBoolean(map.get("logined"));
	}

	public void setMethod(String method) {
		this.method = method;
	}
	
	public String getCookies() {
		return cookies;
	}
	public void setCookies(String cookies) {
		this.cookies = cookies;
	}
	
}
