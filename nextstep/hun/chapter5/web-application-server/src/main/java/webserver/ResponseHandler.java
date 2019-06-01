package webserver;

import util.HttpRequestUtils;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.ResponseHandler;

public class ResponseHandler {
	private static final Logger log = LoggerFactory.getLogger(ResponseHandler.class);
	
	private String code;
	private String type;
	private String url;
	private String cookie;
	private Map<String, Object> model;
	private int lengthOfBodyContent;
	
	public ResponseHandler(String code, String url) {
		this.code = code;
		this.url = url;
		
		String fileType = HttpRequestUtils.getFileType(url);
		this.type = (fileType.equals("css")) ? "text/css" : "text/html";
	}

	public void response(OutputStream out) throws IOException {
		String fileName = "./webapp" + this.url;
	    File file = new File(fileName);

	    byte [] body = Files.readAllBytes(file.toPath());
    	
        DataOutputStream dos = new DataOutputStream(out);
        //byte[] body = "Hello World".getBytes();
        
        if (this.url.indexOf("list.html") > 0) {
        	String html = new String(body);
        	String listHtml = (String) model.get("list");
        	html = html.replaceAll("<huntag/>", listHtml);
        	
        	System.out.println(html);
        	
        	body = html.getBytes();
        }
        
        //response 정보
        lengthOfBodyContent = body.length;
        
        responseHeader(dos);
        responseBody(dos, body);
	}
	
	private void responseHeader(DataOutputStream dos) {
		if (code.equals("302")) {
			response302Header(dos, url);
		} else {
			response200Header(dos);
		}
    }
	
	private void response302Header(DataOutputStream dos, String url) {
        try {
            dos.writeBytes("HTTP/1.1 302 OK \r\n");
            dos.writeBytes("Set-Cookie: " + cookie + "\r\n");
            dos.writeBytes("Location : " + url + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
	
	private void response200Header(DataOutputStream dos) {
		try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Set-Cookie: " + cookie + "\r\n");
            dos.writeBytes("Content-Type: " + type + ";charset=utf-8\r\n");
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

	public void setCookie(String cookie) {
		this.cookie = cookie;
	}

	public Map<String, Object> getModel() {
		return model;
	}

	public void setModel(Map<String, Object> model) {
		this.model = model;
	}
	
}
