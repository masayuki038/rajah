package net.wrap_trap.rajah.test.utils;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class AssertTask {

	private boolean completed;
	private Runnable task;
	
	public AssertTask(Runnable task) {
		super();
		this.task = task;
	}

	public void assertLater(long delay) throws InterruptedException {
		Timer timer = new Timer("a few second later", false);
        timer.schedule(new TimerTask() {
			@Override
			public void run() {
				task.run();
				completed = true;
			}        	
        }, delay);
        
        TimeUnit.MILLISECONDS.sleep(delay + 1000L);
		assertThat(completed, is(true));
	}
}
