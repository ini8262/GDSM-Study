package selvet;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.junit.Ignore;
import org.junit.Test;

import web.HttpRequest;

public class HttpRequestTest {
	private String testDirectory = "./src/main/resources/";
	
	@Test
	public void request_GET() throws Exception {
		InputStream in = new FileInputStream(new File(testDirectory + "test/Http_GET.txt"));
		HttpRequest request = new HttpRequest(in);
		
		assertEquals("GET", request.getMethod());
		assertEquals("/user/create", request.getPath());
		assertEquals("keep-alive", request.getHeader("Connection"));
		assertEquals("hun", request.getParameter("userId"));
	}
	
	@Test
	public void request_POST() throws Exception {
		InputStream in = new FileInputStream(new File(testDirectory + "test/Http_POST.txt"));
		HttpRequest request = new HttpRequest(in);
		
		assertEquals("POST", request.getMethod());
		assertEquals("/user/create", request.getPath());
		assertEquals("keep-alive", request.getHeader("Connection"));
		assertEquals("hun", request.getParameter("userId"));
		
	}
	
}
