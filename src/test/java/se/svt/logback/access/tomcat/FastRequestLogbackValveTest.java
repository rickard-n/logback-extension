package se.svt.logback.access.tomcat;

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class FastRequestLogbackValveTest {

	@Test
	public void shouldCreateALoggingEventAndSendToAllAppenders() throws Exception {
		FastRequestLogbackValve fastRequestLogbackValve = new FastRequestLogbackValve();
		fastRequestLogbackValve.setNext(new DoNothingValve());

		TestAccessAppender appender1 = new TestAccessAppender();
		TestAccessAppender appender2 = new TestAccessAppender();

		fastRequestLogbackValve.addAppender(appender1);
		fastRequestLogbackValve.addAppender(appender2);

		fastRequestLogbackValve.invoke(new Request(), new Response());

		assertNotNull(appender1.getEvent());
		assertNotNull(appender2.getEvent());
	}
}