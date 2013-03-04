package se.svt.logback.access.tomcat;

import com.google.common.io.Files;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.coyote.http11.filters.ChunkedOutputFilter;
import org.apache.tomcat.util.buf.MessageBytes;
import org.apache.tomcat.util.http.MimeHeaders;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import se.svt.logback.access.ResponseTimePatternLayoutTest;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ResponseTimeLogbackValveTest {
	private static final String CLIENT_IP = "10.20.101.42";
	private static final String REFERER_URL = "refererUrl";
	private static final String USER_AGENT = "User-Agent";
	private static final String REMOTE_HOST = "svt.se";
	private static final String REQUEST_URI = "/test/url";
	private static final String METHOD = "GET";
	private static final String PROTOCOL = "HTTP/1.1";

	private ResponseTimeLogbackValve responseTimeLogbackValve;

	@Before
	public void setUp() throws Exception {
		responseTimeLogbackValve = new ResponseTimeLogbackValve();
	}

	@Test
	public void shouldCreateALoggingEventAndSendToAllAppenders() throws Exception {
		responseTimeLogbackValve.setNext(new DoNothingValve());
		responseTimeLogbackValve.setCurrentTimeForTest(1000);

		TestResponseTimeAccessAppender appender1 = new TestResponseTimeAccessAppender();
		TestResponseTimeAccessAppender appender2 = new TestResponseTimeAccessAppender();

		responseTimeLogbackValve.addAppender(appender1);
		responseTimeLogbackValve.addAppender(appender2);

		responseTimeLogbackValve.invoke(new Request(), new Response());

		assertNotNull(appender1.getEvent());
		assertEquals(1000, appender1.getEvent().getRequestStarted());
		assertNotNull(appender2.getEvent());
		assertEquals(1000, appender2.getEvent().getRequestStarted());
	}

	@Test
	public void shouldLogRequestToFile() throws Exception {
		responseTimeLogbackValve.setNext(new DoNothingValve());
		URL systemResource = ClassLoader.getSystemResource("response-time-logback-valve-test.xml");
		responseTimeLogbackValve.setFilename(systemResource.getPath());
		responseTimeLogbackValve.start();

		Request request = createRequest();

		String responseBody = "Test response body";
		int length = responseBody.length();
		Response response = createResponse(responseBody);

		responseTimeLogbackValve.invoke(request, response);

		File file = new File("/tmp/access.log");
		assertTrue("Access log file does not exists.", file.exists());
		List<String> logRows = Files.readLines(file, Charset.forName("UTF-8"));
		assertEquals(1, logRows.size());
		checkRegexMatch(logRows.get(0).concat("\n"), String.format(ResponseTimePatternLayoutTest.COMPLEX_MESSAGE_REGEX_PATTERN, CLIENT_IP, REMOTE_HOST, HttpServletResponse.SC_OK, length, METHOD, REQUEST_URI, PROTOCOL, REFERER_URL, USER_AGENT));
	}

	@Test
	public void shouldWriteMultipleLogRequestToFile() throws Exception {
		responseTimeLogbackValve.setNext(new DoNothingValve());
		URL systemResource = ClassLoader.getSystemResource("response-time-logback-valve-test.xml");
		responseTimeLogbackValve.setFilename(systemResource.getPath());
		responseTimeLogbackValve.start();

		Request request = createRequest();

		String responseBody = "Test response body";
		int length = responseBody.length();
		Response response = createResponse(responseBody);

		responseTimeLogbackValve.invoke(request, response);
		responseTimeLogbackValve.invoke(request, response);
		responseTimeLogbackValve.invoke(request, response);
		responseTimeLogbackValve.invoke(request, response);
		responseTimeLogbackValve.invoke(request, response);

		File file = new File("/tmp/access.log");
		assertTrue("Access log file does not exists.", file.exists());
		List<String> logRows = Files.readLines(file, Charset.forName("UTF-8"));
		assertEquals(5, logRows.size());
		for(String row : logRows) {
			checkRegexMatch(row.concat("\n"), String.format(ResponseTimePatternLayoutTest.COMPLEX_MESSAGE_REGEX_PATTERN, CLIENT_IP, REMOTE_HOST, HttpServletResponse.SC_OK, length, METHOD, REQUEST_URI, PROTOCOL, REFERER_URL, USER_AGENT));
		}
	}

	private Request createRequest() throws Exception {
		Connector connector = new Connector("org.apache.coyote.http11.Http11Protocol");
		Request request = new MockRequest();
		request.setConnector(connector);

		org.apache.coyote.Request coyoteRequest = new org.apache.coyote.Request();
		coyoteRequest.remoteHost().setString(REMOTE_HOST);
		coyoteRequest.remoteAddr().setString(REMOTE_HOST);
		coyoteRequest.requestURI().setString(REQUEST_URI);
		coyoteRequest.method().setString(METHOD);
		coyoteRequest.protocol().setString(PROTOCOL);
		addHeaderToCoyoteRequest(coyoteRequest, "Referer", REFERER_URL);
		addHeaderToCoyoteRequest(coyoteRequest, "User-Agent", USER_AGENT);
		addHeaderToCoyoteRequest(coyoteRequest, "X-Cluster-Client-Ip", CLIENT_IP);
		request.setCoyoteRequest(coyoteRequest);
		return request;
	}

	private void addHeaderToCoyoteRequest(org.apache.coyote.Request coyoteRequest, String name, String value) {
		MimeHeaders mimeHeaders = coyoteRequest.getMimeHeaders();
		MessageBytes messageBytes = mimeHeaders.addValue(name);
		messageBytes.setString(value);
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

	private void checkRegexMatch(String s, String regex) {
		Assert.assertTrue("The string [" + s + "] did not match regex [" + regex + "]", s
				.matches(regex));
	}

	@After
	public void tearDown() throws Exception {
		File file = new File("/tmp/access.log");
		if(file.exists()){
			file.delete();
		}

	}

	private class MockRequest extends Request {
		@Override
		public Enumeration getParameterNames() {
			// To prevent NPE in test
			return null;
		}
	}
}
