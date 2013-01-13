package net.wrap_trap.rajah.test;

import static org.junit.Assert.assertThat;
import net.wrap_trap.rajah.test.matchers.ReplyMatchers;

public class RajahResponse {

	private String response;

	public RajahResponse(String response) {
		super();
		this.response = response;
	}
	
	public void isOK() {
		assertThat(response, ReplyMatchers.isOK());
	}
	
	public void is(int expected) {
		assertThat(response, ReplyMatchers.is(expected));
	}

	public void is(String expected) {
		assertThat(response, ReplyMatchers.is(expected));
	}
	
	public void is(String[] expected) {
		assertThat(response, ReplyMatchers.is(expected));		
	}
}
