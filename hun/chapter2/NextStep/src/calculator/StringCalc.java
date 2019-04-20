package calculator;

public class StringCalc {
	
	private final String CUSTOM_START = "//";
	private final String CUSTOM_END = "\n";
	
	//add함수
	public int add(String text) {
		
		if (text == null || text.trim().isEmpty()) {
			return 0;
		}
		
		String separator = ":,";
		
		if (text.startsWith(CUSTOM_START)) {
			separator += text.substring(CUSTOM_START.length(), CUSTOM_START.length() + 1);
			text = text.substring(
					CUSTOM_START.length() + 1 + CUSTOM_END.length()
					, text.length()
			);
		}
		
		String[] value = text.split("[" + separator + "]");
		return sum(value);
	}
	
	//배열들의  합 반환
	private int sum(String[] value) {
		int sum = 0;
		
		for (String s : value) {
			int num = Integer.valueOf(s);
			if (num < 0) {
				throw new RuntimeException("음수전달~");
			}
			sum += num;
		}
		
		return sum;
	}
}
