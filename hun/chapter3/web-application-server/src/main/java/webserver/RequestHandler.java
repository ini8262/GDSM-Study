package webserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.DataBase;
import model.User;
import util.HttpRequestUtils;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {

			//1단계
			BufferedReader buffer = new BufferedReader(new InputStreamReader(in,"UTF-8"));
			String line, url = null;
			String code = "200";
			Map<String, Object> requestMap = null;
			
			String type = "text/html";	
			
			int index = 0;
			while (!"".equals(line = buffer.readLine())) {
				if (line == null) {
					return;
				}
				
				//2단계
				if ((index++) == 0) {
					requestMap = getRequestUrlInfo(line);
					
					url = (String) requestMap.get("url");
					log.error("{}", getRequestFileType(url));
					type = (getRequestFileType(url).equals("css")) ? "text/css" : type;
					log.info(url);		//요청 URL
				}
				
				log.debug(line);
			}
			
			//회원가입
			if (url.indexOf("create") >= 0) {
				User user = new User(
						(String) requestMap.get("userId"),
						(String) requestMap.get("password"),
						(String) requestMap.get("name"),
						(String) requestMap.get("email")
				);
				
				DataBase.addUser(user);
				log.info("회원가입 정보 : {}", user);
				
				url = "/index.html";
				code = "302";
			}
			
			
			//3단계
			String fileName = "./webapp" + url;
		    File file = new File(fileName);

		    byte [] body = Files.readAllBytes(file.toPath());
        	
            DataOutputStream dos = new DataOutputStream(out);
            //byte[] body = "Hello World".getBytes();
            
            
            //response 정보
            Response response = new Response();
            response.setCode(code);
            response.setType(type);
            response.setUrl(url);
            response.setLengthOfBodyContent(body.length);
            
            responseHeader(dos, response);
            responseBody(dos, body);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private Map<String, Object> getRequestUrlInfo(String line) {
    	Map<String, Object> result = new HashMap<>();
    	
    	String[] tempAray = line.split(" ");
    	
    	String url = tempAray[1];
    	String parameter = null;
    	
    	int index = url.indexOf("?");
    	if (index > 0) {
    		log.error("파라미터 존재");
        	parameter = url.substring(index + 1);
        	url = url.substring(0, index);
    	}
		
    	result.put("method", tempAray[0]);
		result.put("url", url);
		result.put("param", HttpRequestUtils.parseQueryString(parameter));
		
		log.info("url 정보 : {}", result);
		
		return result;
	}

	private void responseHeader(DataOutputStream dos, Response response) {
		if (response.getCode().equals("302")) {
			System.out.println(302);
			response302Header(dos, response.getUrl());
		} else {
			System.out.println(200);
			response200Header(dos, response);
		}
    }
	
	private void response302Header(DataOutputStream dos, String url) {
        try {
            dos.writeBytes("HTTP/1.1 302 OK \r\n");
            dos.writeBytes("Location : " + url + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
	
	private void response200Header(DataOutputStream dos, Response response) {
		try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: " + response.getType() + ";charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + response.getLengthOfBodyContent() + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
    
    
    private void urlAnalyze() {
    	//확장자
    	
    }

    //확장자 반환
    private String getRequestFileType(String url) {
    	int index = url.lastIndexOf(".");
    	return url.substring(index + 1);
    }
    
}
