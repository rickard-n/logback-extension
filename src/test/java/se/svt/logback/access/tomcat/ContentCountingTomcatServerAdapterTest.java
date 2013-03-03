package se.svt.logback.access.tomcat;

import org.apache.catalina.connector.Connector;
import org.apache.catalina.connector.Response;
import org.junit.Test;

import java.io.OutputStream;

import static junit.framework.Assert.assertEquals;

public class ContentCountingTomcatServerAdapterTest {

	@Test
	public void shouldReadContentLengthFromResponseOutputStream() throws Exception {
		final String responseBody = "Test response";
		final int length = responseBody.length();

		Response response = new Response();
		response.setConnector(new Connector("org.apache.coyote.http11.Http11Protocol"));
		OutputStream outputStream = response.getOutputStream();
		outputStream.write(responseBody.getBytes(), 0, length);

		ContentCountingTomcatServerAdapter contentCountingTomcatServerAdapter =
				new ContentCountingTomcatServerAdapter(null, response);

		final long contentLength = contentCountingTomcatServerAdapter.getContentLength();
		assertEquals(length, contentLength);
	}
}
