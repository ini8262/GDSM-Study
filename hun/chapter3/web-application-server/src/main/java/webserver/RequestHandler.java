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
			Map<String, Object> requestMap = null;
			
			int index = 0;
			while (!"".equals(line = buffer.readLine())) {
				if (line == null) {
					return;
				}
				
				//2단계
				if ((index++) == 0) {
					requestMap = getRequestUrlInfo(line);
					
					url = (String) requestMap.get("url");
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
				
				log.info("회원가입 정보 : {}", user);
			}
			
			
			//3단계
			String fileName = "./webapp" + url;
		    File file = new File(fileName);

		    byte [] body = Files.readAllBytes(file.toPath());
        	
            DataOutputStream dos = new DataOutputStream(out);
            //byte[] body = "Hello World".getBytes();
            response200Header(dos, body.length);
            responseBody(dos, body);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private Map<String, Object> getRequestUrlInfo(String line) {
    	Map<String, Object> result = new HashMap<>();
    	
    	String[] tempAray = line.split(" ");
    	String url = tempAray[1];
    	
    	int index = url.indexOf("?");
    	if (index < 0) {
    		result.put("url", url);
    		return result;
    	}
    	
    	log.error("파라미터 존재");
    	String parameter = url.substring(index + 1);
		
		result.put("url", url.substring(0, index));
		result.put("param", HttpRequestUtils.parseQueryString(parameter));
		
		return result;
	}

	private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
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
    
    
}
