package se.svt.logback.access.net;

import org.apache.catalina.connector.Connector;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.coyote.http11.filters.ChunkedOutputFilter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import se.svt.logback.access.MockSyslogServer;
import se.svt.logback.access.RandomUtil;
import se.svt.logback.access.ResponseTimeLogbackValveTest;
import se.svt.logback.access.ResponseTimePatternLayoutTest;
import se.svt.logback.access.tomcat.DoNothingValve;
import se.svt.logback.access.tomcat.ResponseTimeLogbackValve;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AccessSyslogAppenderTest extends ResponseTimeLogbackValveTest {
	private static final String CLIENT_IP = "10.20.101.42";
	private static final String REFERER_URL = "refererUrl";
	private static final String USER_AGENT = "User-Agent";
	private static final String REMOTE_HOST = "svt.se";
	private static final String REQUEST_URI = "/test/url";
	private static final String METHOD = "GET";
	private static final String PROTOCOL = "HTTP/1.1";


	private MockSyslogServer mockSyslogServer;
	private ResponseTimeLogbackValve responseTimeLogbackValve;

	@Before
	public void setUp() throws Exception {
		responseTimeLogbackValve = new ResponseTimeLogbackValve();
	}

	@After
	public void tearDown() throws Exception {

	}

	@Test
	public void shouldLogSimpleMessageWithPatternLayoutFormat() throws Exception {
		setMockServerAndConfigure(1);

		responseTimeLogbackValve.setNext(new DoNothingValve());
		URL systemResource = ClassLoader.getSystemResource("response-time-syslog-logback-valve.xml");
		responseTimeLogbackValve.setFilename(systemResource.getPath());
		responseTimeLogbackValve.start();

		Request request = createRequest(new TestCaseRequestObject(
				CLIENT_IP,
				REFERER_URL,
				USER_AGENT,
				REMOTE_HOST,
				REMOTE_HOST,
				REQUEST_URI,
				METHOD,
				PROTOCOL
		));

		String responseBody = "Test response body";
		int length = responseBody.length();
		Response response = createResponse(responseBody);

		responseTimeLogbackValve.invoke(request, response);

		mockSyslogServer.join(8000);

		assertTrue(mockSyslogServer.isFinished());
		assertEquals(1, mockSyslogServer.getMessageList().size());
		String msg = mockSyslogServer.getMessageList().get(0);

		final String expected = String.format(ResponseTimePatternLayoutTest.COMPLEX_MESSAGE_REGEX_PATTERN,
				CLIENT_IP, REMOTE_HOST, HttpServletResponse.SC_OK, length, METHOD, REQUEST_URI, PROTOCOL, REFERER_URL, USER_AGENT
				);
		checkRegexMatch(msg, expected);

	}

	private Response createResponse(String responseBody) throws Exception {
		Connector connector = new Connector("org.apache.coyote.http11.Http11Protocol");
		Response response = connector.createResponse();

		org.apache.coyote.Response coyoteResponse = new org.apache.coyote.Response();
		ChunkedOutputFilter outputBuffer = new ChunkedOutputFilter();
		coyoteResponse.setOutputBuffer(outputBuffer);
		response.setCoyoteResponse(coyoteResponse);

		OutputStream outputStream = response.getOutputStream();
		outputStream.write(responseBody.getBytes(), 0, responseBody.length());

		response.setStatus(HttpServletResponse.SC_OK);

		return response;
	}

	public void setMockServerAndConfigure(int expectedCount)
			throws InterruptedException {
		int port = RandomUtil.getRandomServerPort();

		mockSyslogServer = new MockSyslogServer(expectedCount, 1234);
		mockSyslogServer.start();
		// give MockSyslogServer head start

		Thread.sleep(100);
	}
}
