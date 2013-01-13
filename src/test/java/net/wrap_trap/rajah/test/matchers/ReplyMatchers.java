package net.wrap_trap.rajah.test.matchers;

import net.wrap_trap.rajah.protocol.Reply;

import org.hamcrest.Matcher;

public class ReplyMatchers {

	public static Matcher<String> isOK() {
		return org.hamcrest.CoreMatchers.is("+OK" + Reply.LT);
	}
	
	public static Matcher<String> is(Integer expected) {
		return org.hamcrest.CoreMatchers.is(":" + expected + Reply.LT);
	}

	public static Matcher<String> is(String expected) {
        if (expected != null) {
    		return org.hamcrest.CoreMatchers.is("$" + expected.length() + Reply.LT + expected + Reply.LT);
        } 
        
        return org.hamcrest.CoreMatchers.is("$-1" + Reply.LT);
	}
	
	public static Matcher<String> is(String[] expected) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < expected.length; i++) {
            if (expected[i] != null) {
                sb.append("$" + expected[i].length() + Reply.LT + expected[i] + Reply.LT);
            } else {
                sb.append("$-1" + Reply.LT);
            }
        }
        sb.insert(0, "*" + expected.length + Reply.LT);
        return org.hamcrest.CoreMatchers.is(sb.toString());
	}
}
