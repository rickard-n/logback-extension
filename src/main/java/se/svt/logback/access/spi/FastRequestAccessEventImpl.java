package se.svt.logback.access.spi;

import ch.qos.logback.access.pattern.AccessConverter;
import ch.qos.logback.access.spi.IAccessEvent;
import ch.qos.logback.access.spi.ServerAdapter;
import org.apache.catalina.connector.Request;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

public class FastRequestAccessEventImpl implements Serializable, IAccessEvent {

	private final transient Request httpRequest;
	private long timeStamp = 0;

	private String requestURI;
	private String requestURL;
	private String remoteHost;
	private String remoteUser;
	private String remoteAddr;
	private String protocol;
	private String method;
	private String serverName;

	private Map<String, String> requestHeaderMap;
	private Map<String, String[]> requestParameterMap;

	public FastRequestAccessEventImpl(Request request) {
		this.httpRequest = request;
		this.timeStamp = System.currentTimeMillis();
	}

	@Override
	public HttpServletRequest getRequest() {
		return httpRequest;
	}

	@Override
	public HttpServletResponse getResponse() {
		throw new UnsupportedOperationException("Response object not available in fast request logging.");
	}

	@Override
	public long getTimeStamp() {
		return timeStamp;
	}

	public String getRequestURI() {
		if (requestURI == null) {
			if (httpRequest != null) {
				requestURI = httpRequest.getRequestURI();
			} else {
				requestURI = NA;
			}
		}
		return requestURI;
	}

	public String getRequestURL() {
		if (requestURL == null) {
			if (httpRequest != null) {
				StringBuilder buf = new StringBuilder();
				buf.append(httpRequest.getMethod());
				buf.append(AccessConverter.SPACE_CHAR);
				buf.append(httpRequest.getRequestURI());
				final String qStr = httpRequest.getQueryString();
				if (qStr != null) {
					buf.append(AccessConverter.QUESTION_CHAR);
					buf.append(qStr);
				}
				buf.append(AccessConverter.SPACE_CHAR);
				buf.append(httpRequest.getProtocol());
				requestURL = buf.toString();
			} else {
				requestURL = NA;
			}
		}
		return requestURL;
	}

	public String getRemoteHost() {
		if (remoteHost == null) {
			if (httpRequest != null) {
				// the underlying implementation of HttpServletRequest will
				// determine if remote lookup will be performed
				remoteHost = httpRequest.getRemoteHost();
			} else {
				remoteHost = NA;
			}
		}
		return remoteHost;
	}

	public String getRemoteUser() {
		if (remoteUser == null) {
			if (httpRequest != null) {
				remoteUser = httpRequest.getRemoteUser();
			} else {
				remoteUser = NA;
			}
		}
		return remoteUser;
	}

	public String getProtocol() {
		if (protocol == null) {
			if (httpRequest != null) {
				protocol = httpRequest.getProtocol();
			} else {
				protocol = NA;
			}
		}
		return protocol;
	}

	public String getMethod() {
		if (method == null) {
			if (httpRequest != null) {
				method = httpRequest.getMethod();
			} else {
				method = NA;
			}
		}
		return method;
	}

	public String getServerName() {
		if (serverName == null) {
			if (httpRequest != null) {
				serverName = httpRequest.getServerName();
			} else {
				serverName = NA;
			}
		}
		return serverName;
	}

	public String getRemoteAddr() {
		if (remoteAddr == null) {
			if (httpRequest != null) {
				remoteAddr = httpRequest.getRemoteAddr();
			} else {
				remoteAddr = NA;
			}
		}
		return remoteAddr;
	}

	public String getRequestHeader(String key) {
		String result = null;
		String keyAsLowerCase = key.toLowerCase();
		if (requestHeaderMap == null) {
			if (httpRequest != null) {
				buildRequestHeaderMap();
				result = requestHeaderMap.get(keyAsLowerCase);
			}
		} else {
			result = requestHeaderMap.get(keyAsLowerCase);
		}

		if (result != null) {
			return result;
		} else {
			return NA;
		}
	}

	public Enumeration getRequestHeaderNames() {
		// post-serialization
		if (httpRequest == null) {
			Vector<String> list = new Vector<String>(getRequestHeaderMap().keySet());
			return list.elements();
		}
		return httpRequest.getHeaderNames();
	}

	public Map<String, String> getRequestHeaderMap() {
		if (requestHeaderMap == null) {
			buildRequestHeaderMap();
		}
		return requestHeaderMap;
	}

	public void buildRequestHeaderMap() {
		// according to RFC 2616 header names are case insensitive
		// latest versions of Tomcat return header names in lower-case
		requestHeaderMap = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
		Enumeration e = httpRequest.getHeaderNames();
		if (e == null) {
			return;
		}
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			requestHeaderMap.put(key, httpRequest.getHeader(key));
		}
	}

	public void buildRequestParameterMap() {
		requestParameterMap = new HashMap<String, String[]>();
		Enumeration e = httpRequest.getParameterNames();
		if (e == null) {
			return;
		}
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			requestParameterMap.put(key, httpRequest.getParameterValues(key));
		}
	}

	public Map<String, String[]> getRequestParameterMap() {
		if (requestParameterMap == null) {
			buildRequestParameterMap();
		}
		return requestParameterMap;
	}

	public String getAttribute(String key) {
		if (httpRequest != null) {
			Object value = httpRequest.getAttribute(key);
			if (value == null) {
				return NA;
			} else {
				return value.toString();
			}
		} else {
			return NA;
		}
	}

	public String[] getRequestParameter(String key) {
		if (httpRequest != null) {
			String[] value = httpRequest.getParameterValues(key);
			if (value == null) {
				return new String[] { NA };
			} else {
				return value;
			}
		} else {
			return new String[] { NA };
		}
	}

	public String getCookie(String key) {

		if (httpRequest != null) {
			Cookie[] cookieArray = httpRequest.getCookies();
			if (cookieArray == null) {
				return NA;
			}

			for (Cookie cookie : cookieArray) {
				if (key.equals(cookie.getName())) {
					return cookie.getValue();
				}
			}
		}
		return NA;
	}

	@Override
	public long getContentLength() {
		throw new UnsupportedOperationException("Response object not available in fast request logging.");
	}

	@Override
	public int getStatusCode() {
		throw new UnsupportedOperationException("Response object not available in fast request logging.");
	}

	@Override
	public String getRequestContent() {
		throw new UnsupportedOperationException("Response object not available in fast request logging.");
	}

	@Override
	public String getResponseContent() {
		throw new UnsupportedOperationException("Response object not available in fast request logging.");
	}

	@Override
	public int getLocalPort() {
		return 0;
	}

	@Override
	public ServerAdapter getServerAdapter() {
		throw new UnsupportedOperationException("ServerAdapter not available in fast request logging.");
	}

	@Override
	public String getResponseHeader(String key) {
		throw new UnsupportedOperationException("Response object not available in fast request logging.");
	}

	@Override
	public Map<String, String> getResponseHeaderMap() {
		throw new UnsupportedOperationException("Response object not available in fast request logging.");
	}

	@Override
	public List<String> getResponseHeaderNameList() {
		throw new UnsupportedOperationException("Response object not available in fast request logging.");
	}

	public void prepareForDeferredProcessing() {
		buildRequestHeaderMap();
		buildRequestParameterMap();
		getMethod();
		getProtocol();
		getRemoteAddr();
		getRemoteHost();
		getRemoteUser();
		getRequestURI();
		getRequestURL();
		getServerName();
		getTimeStamp();
	}



}
