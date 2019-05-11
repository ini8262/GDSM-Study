package webserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.HttpRequestUtils;
import util.IOUtils;
import web.Action;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        //log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
			BufferedReader buffer = new BufferedReader(new InputStreamReader(in,"UTF-8"));
			Map<String, Object> requestMap = null;
			Action action = new Action();
			
			int contentLength = 0;
			String line = null;
			int index = 0;
			while (!"".equals(line = buffer.readLine())) {
				if (line == null) {
					return;
				}
				
				//요청 URL 분석
				if ((index++) == 0) {
					requestMap = getRequestUrlInfo(line);
				}
				
				if (line.indexOf("Content-Length") >= 0) {
					contentLength = getContentLength(line);
				}
				
				if (line.indexOf("Cookie") >= 0) {
					action.setCookies(line.replace("Cookie: ", ""));
				}
				
				//log.debug(line);
			}//while
			
			
			//param
			Map<String, String> parameter = getParameter(buffer, requestMap, contentLength);
			
			//처리
			String url = (String) requestMap.get("url");
			action.setMethod((String) requestMap.get("method"));
			action.transmission(url, parameter, out);
			
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }


    //TODO : 리펙토링
	private Map<String, Object> getRequestUrlInfo(String line) {
    	Map<String, Object> result = new HashMap<>();
    	
    	String[] tempAray = line.split(" ");
    	
    	String url = tempAray[1];
    	String parameter = null;
    	
    	int index = url.indexOf("?");
    	if (index > 0) {
        	parameter = url.substring(index + 1);
        	url = url.substring(0, index);
    	}
		
    	result.put("method", tempAray[0]);
		result.put("url", url);
		result.put("param", HttpRequestUtils.parseQueryString(parameter));
		
		return result;
	}

	
	
	/*private void responseHeaderSetCookie(DataOutputStream dos, Map<String, String> cookie) {
		
		String serializableCookie = cookie.toString();
		
		try {
			dos.writeBytes("Set-Cookie: " + serializableCookie.substring(1, serializableCookie.length() -1) + ";\r\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}*/

	 private Map<String, String> getParameter(BufferedReader buffer, Map<String, Object> requestMap, int contentLength) throws IOException {
		 if ("POST".equals((String) requestMap.get("method"))) {
			 String body = IOUtils.readData(buffer, contentLength);
			return HttpRequestUtils.parseQueryString(body);
		} else {
			return (Map<String, String>) requestMap.get("param");
		}
    }

    private int getContentLength(String line) {
		String CONTENT_LENGTH = "Content-Length: ";
		if (line.indexOf(CONTENT_LENGTH) >= 0) {
			return Integer.parseInt(line.substring(CONTENT_LENGTH.length()));
		}
		
		return 0;
    }
	
    
}
