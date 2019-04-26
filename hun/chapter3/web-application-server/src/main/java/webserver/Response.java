package webserver;

public class Response {
	private String code;
	private String type;
	private String url;
	private int lengthOfBodyContent;
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getLengthOfBodyContent() {
		return lengthOfBodyContent;
	}
	public void setLengthOfBodyContent(int lengthOfBodyContent) {
		this.lengthOfBodyContent = lengthOfBodyContent;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
}
