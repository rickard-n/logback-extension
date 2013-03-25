package se.svt.logback.access;

import ch.qos.logback.access.spi.AccessContext;
import ch.qos.logback.access.tomcat.TomcatServerAdapter;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.connector.Response;
import org.apache.coyote.http11.filters.ChunkedOutputFilter;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import se.svt.logback.access.spi.RequestStartTimeAccessEventImpl;
import se.svt.logback.access.tomcat.ContentCountingTomcatServerAdapter;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;

import static junit.framework.Assert.assertNull;

public class ResponseTimePatternLayoutTest extends ResponseTimeLogbackValveTest {

	public static final String SIMPLE_LOGGING_PATTERN = "pattern: %D";
	public static final String SIMPLE_MESSAGE_REGEX = "pattern: \\d+\\n";
	public static final String COMPLEX_LOGGING_PATTERN = "%date{yyyy-MM-dd HH:mm:ss.SSS,Europe/Stockholm} %i{X-Cluster-Client-Ip} %clientHost %statusCode %b %D '%requestURL' '%header{Referer}' '%i{User-Agent}'";
	public static final String COMPLEX_MESSAGE_REGEX_PATTERN = "\\d{4}(-\\d{2}){2} \\d{2}(:\\d{2}){2}.\\d{3} %s %s %d %d \\d+ '%s %s %s' '%s' '%s'%n";
	private ResponseTimePatternLayout responseTimePatternLayout;
	public static final String CLIENT_IP = "10.20.101.42";
	public static final String REFERER_URL = "refererUrl";
	public static final String USER_AGENT = "User-Agent";
	public static final String REMOTE_HOST = "svt.se";
	public static final String REQUEST_URI = "/test/url";
	public static final String METHOD = "GET";
	public static final String PROTOCOL = "HTTP/1.1";

	@Before
	public void setUp() throws Exception {
		responseTimePatternLayout = new ResponseTimePatternLayout();
		responseTimePatternLayout.setContext(new AccessContext());
	}

	@Test
	public void shouldPrintResponseTimeFromAccessEvent() throws Exception {
		responseTimePatternLayout.setPattern(SIMPLE_LOGGING_PATTERN);
		responseTimePatternLayout.start();

		String message = responseTimePatternLayout.doLayout(new RequestStartTimeAccessEventImpl(
				new MockHttpServletRequest(), new MockHttpServletResponse(), new TomcatServerAdapter(null, null),
				System.currentTimeMillis()));

		checkRegexMatch(message, SIMPLE_MESSAGE_REGEX);
	}

	@Test
	public void shouldReturnNullIfLayoutPatternIsNotStarted() throws Exception {
		assertNull(responseTimePatternLayout.doLayout(null));
	}

	@Test
	public void shouldReturnMessageOnComplexPatternFormat() throws Exception {
		responseTimePatternLayout.setPattern(COMPLEX_LOGGING_PATTERN);
		responseTimePatternLayout.start();

		MockHttpServletRequest httpRequest = createMockHttpServletRequest();

		MockHttpServletResponse httpResponse = new MockHttpServletResponse();

		final String responseBody = "Test response";
		final int length = responseBody.length();
		TomcatServerAdapter tomcatServerAdapter = createTomcatServerAdapter(responseBody);
		String message = responseTimePatternLayout.doLayout(new RequestStartTimeAccessEventImpl(
				httpRequest, httpResponse, tomcatServerAdapter,
				System.currentTimeMillis()));


		checkRegexMatch(message, String.format(COMPLEX_MESSAGE_REGEX_PATTERN, CLIENT_IP, REMOTE_HOST, HttpServletResponse.SC_OK, length, METHOD, REQUEST_URI, PROTOCOL, REFERER_URL, USER_AGENT));
	}

	private TomcatServerAdapter createTomcatServerAdapter(String responseBody) throws Exception {
		Connector connector = new Connector("org.apache.coyote.http11.Http11Protocol");
		Response response = connector.createResponse();

		org.apache.coyote.Response coyoteResponse = new org.apache.coyote.Response();
		ChunkedOutputFilter outputBuffer = new ChunkedOutputFilter();
		coyoteResponse.setOutputBuffer(outputBuffer);
		response.setCoyoteResponse(coyoteResponse);

		OutputStream outputStream = response.getOutputStream();
		outputStream.write(responseBody.getBytes(), 0, responseBody.length());

		response.setStatus(HttpServletResponse.SC_OK);

		return new ContentCountingTomcatServerAdapter(null, response);
	}

	private MockHttpServletRequest createMockHttpServletRequest() {
		MockHttpServletRequest httpRequest = new MockHttpServletRequest();
		httpRequest.addHeader("X-Cluster-Client-Ip", CLIENT_IP);
		httpRequest.addHeader("Referer", REFERER_URL);
		httpRequest.addHeader("User-Agent", USER_AGENT);
		httpRequest.setRemoteHost(REMOTE_HOST);
		httpRequest.setRequestURI(REQUEST_URI);
		httpRequest.setMethod(METHOD);
		httpRequest.setProtocol(PROTOCOL);
		return httpRequest;
	}
}
