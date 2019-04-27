package com.ksnote.nextstep;


import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StringCalculatorTests {

    private StringCalculator calculator;

    @Before
    public void setup() {
        calculator = new StringCalculator();
    }

    @Test
    public void add_null_또는_빈문자() {
        assertEquals(0, calculator.add(null));
        assertEquals(0, calculator.add(""));
    }

    @Test
    public void add_숫자하나() throws Exception {
        assertEquals(1, calculator.add("1"));
    }

    @Test
    public void add_쉼표구분자() throws Exception {
        assertEquals(3, calculator.add("1,2"));
    }

    @Test
    public void add_쉼표_또는_콜론_구분자() throws Exception {
        assertEquals(6, calculator.add("1,2:3"));
    }

    @Test
    public void add_custom_구분자() throws Exception {
        assertEquals(6, calculator.add("//;\n1;2;3"));
    }

    @Test(expected = RuntimeException.class)
    public void add_negative() throws Exception {
        calculator.add("-1,2,3");
    }


}
