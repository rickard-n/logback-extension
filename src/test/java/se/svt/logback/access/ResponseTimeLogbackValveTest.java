package se.svt.logback.access;

import org.apache.catalina.connector.Connector;
import org.apache.catalina.connector.Request;
import org.apache.tomcat.util.buf.MessageBytes;
import org.apache.tomcat.util.http.MimeHeaders;
import org.junit.Assert;

import java.util.Enumeration;

public abstract class ResponseTimeLogbackValveTest {

	protected void checkRegexMatch(String s, String regex) {
				Assert.assertTrue("The string [" + s + "] did not match regex [" + regex + "]", s
						.matches(regex));
	}

	private class MockRequest extends Request {
		@Override
		public Enumeration getParameterNames() {
			// To prevent NPE in test. =)
			return null;
		}
	}

	protected Request createRequest(TestCaseRequestObject requestObject) throws Exception {
		Connector connector = new Connector("org.apache.coyote.http11.Http11Protocol");
		Request request = new MockRequest();
		request.setConnector(connector);

		org.apache.coyote.Request coyoteRequest = new org.apache.coyote.Request();
		coyoteRequest.remoteHost().setString(requestObject.getRemoteHost());
		coyoteRequest.remoteAddr().setString(requestObject.getRemoteAddr());
		coyoteRequest.requestURI().setString(requestObject.getRequestUri());
		coyoteRequest.method().setString(requestObject.getMethod());
		coyoteRequest.protocol().setString(requestObject.getProtocol());
		addHeaderToCoyoteRequest(coyoteRequest, "Referer", requestObject.getRefererUrl());
		addHeaderToCoyoteRequest(coyoteRequest, "User-Agent", requestObject.getUserAgent());
		addHeaderToCoyoteRequest(coyoteRequest, "X-Cluster-Client-Ip", requestObject.getClientIp());
		request.setCoyoteRequest(coyoteRequest);
		return request;
	}

	private void addHeaderToCoyoteRequest(org.apache.coyote.Request coyoteRequest, String name, String value) {
		MimeHeaders mimeHeaders = coyoteRequest.getMimeHeaders();
		MessageBytes messageBytes = mimeHeaders.addValue(name);
		messageBytes.setString(value);
	}

	protected class TestCaseRequestObject {
		private final String clientIp;
		private final String refererUrl;
		private final String userAgent;
		private final String remoteHost;
		private final String remoteAddr;
		private final String requestUri;
		private final String method;
		private final String protocol;

		public TestCaseRequestObject(String clientIp, String refererUrl, String userAgent, String remoteHost, String remoteAddr, String requestUri, String method, String protocol) {
			this.clientIp = clientIp;
			this.refererUrl = refererUrl;
			this.userAgent = userAgent;
			this.remoteHost = remoteHost;
			this.remoteAddr = remoteAddr;
			this.requestUri = requestUri;
			this.method = method;
			this.protocol = protocol;
		}

		public String getClientIp() {
			return clientIp;
		}

		public String getRefererUrl() {
			return refererUrl;
		}

		public String getUserAgent() {
			return userAgent;
		}

		public String getRemoteHost() {
			return remoteHost;
		}

		public String getRequestUri() {
			return requestUri;
		}

		public String getMethod() {
			return method;
		}

		public String getProtocol() {
			return protocol;
		}

		public String getRemoteAddr() {
			return remoteAddr;
		}
	}
}
