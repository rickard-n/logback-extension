package se.svt.logback.access.spi;

import ch.qos.logback.access.spi.AccessEvent;
import ch.qos.logback.access.spi.ServerAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RequestStartTimeAccessEventImpl extends AccessEvent implements RequestStartTimeAccessEvent {
	private long requestStarted = SENTINEL;

	public RequestStartTimeAccessEventImpl(HttpServletRequest httpRequest, HttpServletResponse httpResponse, ServerAdapter adapter, long requestStarted) {
		super(httpRequest, httpResponse, adapter);
		this.requestStarted = requestStarted;
	}

	@Override
	public long getRequestStarted() {
		return requestStarted;
	}

	@Override
	public void prepareForDeferredProcessing() {
		super.prepareForDeferredProcessing();
	}
}
