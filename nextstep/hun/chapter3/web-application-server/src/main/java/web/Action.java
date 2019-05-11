package web;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.DataBase;
import model.User;
import util.HttpRequestUtils;
import webserver.ResponseHandler;

public class Action {
	private static final Logger log = LoggerFactory.getLogger(Action.class);
	
	Map<String, String> responseParam = new HashMap<String, String>(); 
	Map<String, Object> model = new HashMap<String, Object>(); 
	
	private String cookies;
	private String method;

	public void transmission(String url, Map<String, String> parameter, OutputStream out) throws IOException {
		log.debug("요청 URL ====>   {}", url);
		
		if (url.indexOf("create") >= 0) {
			create(parameter);
		} else if (url.indexOf("login") >= 0) {
			login(parameter);
		} else if (url.indexOf("logout") >= 0) {
			logout(parameter);
		} else if (url.indexOf("list") >= 0) {
			list(parameter);
		} else {
			defaultAction(url);
		}
		
		//클라이언트에게 응답
		ResponseHandler responseHandler = new ResponseHandler(responseParam.get("code"), responseParam.get("url"));
		
		responseHandler.setCookie(cookies);
		responseHandler.setModel(model);
		responseHandler.response(out);
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
	
	private void logout(Map<String, String> parameter) {
		cookies = "logined=;userId=";
		setResponseParam("/user/login.html", "200");
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
		
		//회원 리스트 조회
		Collection<User> list = DataBase.findAll();
		
		//html생성
		StringBuilder userHtml = new StringBuilder();
		
		int rownum = 0;
		for (User user : list) {
			log.info("list ====> {}", user);
			userHtml.append("<tr>");
			userHtml.append("<th scope=\"row\">").append(++rownum).append("</th>");
			userHtml.append("<td>").append(user.getUserId()).append("</td>");
			userHtml.append("<td>").append(user.getName()).append("</td>");
			userHtml.append("<td>").append(user.getEmail()).append("</td>");
			userHtml.append("<td><a href=\"#\" class=\"btn btn-success\" role=\"button\">수정</a></td>");
            userHtml.append("</tr>");
		}
		model.put("list", userHtml.toString());
		
		setResponseParam("/user/list.html", "200");
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
