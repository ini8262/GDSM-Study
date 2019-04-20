package calculator;


import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class StringCalcTest {
	
	private StringCalc calc;
	
	@Before
	public void setup() {
		calc = new StringCalc();
	}
	
	@Test
	public void 공백_테스트() {
		assertEquals(0, calc.add(null));
		assertEquals(0, calc.add(""));
		assertEquals(0, calc.add(" "));
	}
	
	@Test
	public void 기본_테스트() {
		assertEquals(1, calc.add("1"));
		assertEquals(3, calc.add("1:2"));
		assertEquals(6, calc.add("1,2,3"));
	}
	
	@Test
	public void 커스텀_테스트() {
		assertEquals(6, calc.add("//;\n1;2;3"));
		assertEquals(6, calc.add("//;\n1:2;3"));
		assertEquals(6, calc.add("//A\n1A2A3"));
	}
	
	@Test(expected = RuntimeException.class)
	public void 음수_테스트() {
		assertEquals(6, calc.add("-1,2,3"));
	}
	
}
