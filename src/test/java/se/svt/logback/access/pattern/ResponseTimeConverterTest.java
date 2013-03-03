package se.svt.logback.access.pattern;

import ch.qos.logback.access.spi.AccessEvent;
import ch.qos.logback.access.spi.IAccessEvent;
import org.junit.Before;
import org.junit.Test;
import se.svt.logback.access.spi.RequestStartTimeAccessEventImpl;

import static junit.framework.Assert.assertEquals;

public class ResponseTimeConverterTest {

	private ResponseTimeConverter responseTimeConverter;

	@Before
	public void setUp() throws Exception {
		responseTimeConverter = new ResponseTimeConverter();
	}

	@Test
	public void shouldReturnResponseTimeIfItIsARequestStartTimeAccessEvent() throws Exception {
		IAccessEvent accessEvent = new RequestStartTimeAccessEventImpl(null, null, null, 0l);

		long currentTime = 10l;
		responseTimeConverter.setCurrentTime(currentTime);

		String responseTime = responseTimeConverter.convert(accessEvent);

		assertEquals(Long.toString(currentTime), responseTime);
	}

	@Test
	public void shouldReturnNotAvailableIfResponseTimeIsLessThanZero() throws Exception {
		IAccessEvent accessEvent = new RequestStartTimeAccessEventImpl(null, null, null, 1l);

		long currentTime = 0l;
		responseTimeConverter.setCurrentTime(currentTime);

		String responseTime = responseTimeConverter.convert(accessEvent);

		assertEquals(AccessEvent.NA, responseTime);
	}

	@Test
	public void shouldReturnNotAvailableIfNotAnRequestStartTimeAccessEvent() throws Exception {
		IAccessEvent accessEvent = new AccessEvent(null, null, null);

		String responseTime = responseTimeConverter.convert(accessEvent);
		assertEquals(AccessEvent.NA, responseTime);
	}
}
