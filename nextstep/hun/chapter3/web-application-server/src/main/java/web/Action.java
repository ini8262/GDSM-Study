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
import util.IOUtils;
import webserver.ResponseHandler;

public class Action {
	private static final Logger log = LoggerFactory.getLogger(Action.class);
	
	Map<String, String> responseParam = new HashMap<String, String>(); 
	Map<String, Object> returnMap = new HashMap<String, Object>(); 
	
	
	public void transmission(String url, Map<String, String> param, OutputStream out) throws IOException {
		log.debug("요청 URL ====>   {}", url);
		
		if (url.indexOf("create") >= 0) {
			create(param);
		} else if (url.indexOf("login") >= 0) {
			login(param);
		} else if (url.indexOf("list") >= 0) {
			list(param);
		} else {
			defaultAction(url);
		}
		
		//클라이언트에게 응답
		ResponseHandler responseHandler = new ResponseHandler(responseParam.get("code"), responseParam.get("url"));
		//byte[] body = getBody(url);
		
		//responseHandler.response(out, body);
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
	private void create(Map<String, String> param) {
		User user = new User(param);
		
		log.debug("회원가입 정보 ====>   {}", user);

		//회원가입
		DataBase.addUser(user);
		
		//회원가입후 리다이렉트
		setResponseParam("/index.html", "302");
	}		
	
	//로그인
	private void login(Map<String, String> param) {
		
		//조회
		User user =  DataBase.findUserById(param.get("userId"));
		
		boolean logined = false;
		
		if (user == null) {
			log.debug("로그인 실패");
			setResponseParam("/user/login_failed.html", "200");
			
		} else {
			log.debug("로그인 성공!! 로그인 정보 ====>   {}", user);
			logined = true;
			setResponseParam("/index.html", "999");
		}
	}
	
	private void list(Map<String, String> param) {
		Collection<User> list = DataBase.findAll();
		returnMap.put("list", list);
	}
	
}
