package se.svt.logback.access.tomcat;

import ch.qos.logback.access.tomcat.TomcatServerAdapter;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;

/**
 * Overrides logback TomcatServerAdapter getContentLength to read content count the same way as Tomcat AccessValve class.
 */
public class ContentCountingTomcatServerAdapter extends TomcatServerAdapter {

	private final Response response;

	public ContentCountingTomcatServerAdapter(Request tomcatRequest, Response tomcatResponse) {
		super(tomcatRequest, tomcatResponse);
		this.response = tomcatResponse;
	}

	@Override
	public long getContentLength() {
		return response.getContentCountLong();
	}
}
