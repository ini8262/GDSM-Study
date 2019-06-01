package web;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import util.HttpRequestUtils;

public class HttpRequest {
	private String method;
	private String path;
	private Map<String, String> header;
	private Map<String, String> parameter;

	public HttpRequest(InputStream in) {
		header = new HashMap<String, String>();
		Scanner sc = new Scanner(in);
		
		String request = sc.nextLine();
		//method 정보
		setMethod(request.substring(0, request.indexOf(" ")));
		
		//path 정보
		int index = request.contains("?") ? request.indexOf("?") : request.lastIndexOf(" ");
		setPath(request.substring(request.indexOf(" ") + 1, index));
		
		//header 정보
		while (sc.hasNext()) {
			String line = sc.nextLine();
			setRequestInfo(line);
			
			if (line.isEmpty()) {
				break;
			}
		}
		
		//parameter 정보
		String queryString = isPostMethod() ? sc.nextLine() : request.substring(request.indexOf("?") + 1, request.lastIndexOf(" "));
		setParameter(queryString);
		
	}
	
	private boolean isPostMethod() {
		return "POST".equals(this.method);
	}
	
	private void setRequestInfo(String line) {
		if(line.contains(":")) {
			String temp[] = line.split(":");
			header.put(temp[0].trim(), temp[1].trim());
		} 
	}
	
	private void setParameter(String queryString) {
		parameter = HttpRequestUtils.parseQueryString(queryString);
	}

	public void setMethod(String method) {
		this.method = method;
	}
	
	public String getHeader(String string) {
		return header.get(string);
	}

	public String getParameter(String string) {
		return parameter.get(string);
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	public String getMethod() {
		return this.method;
	}

	public String getPath() {
		return this.path;
	}

}
