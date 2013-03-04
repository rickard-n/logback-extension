package se.svt.logback.access.tomcat;

import ch.qos.logback.core.Appender;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.LogbackException;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;
import ch.qos.logback.core.status.Status;
import se.svt.logback.access.spi.RequestStartTimeAccessEvent;

import java.util.List;

public class TestResponseTimeAccessAppender implements Appender<RequestStartTimeAccessEvent> {
	private static final String NOT_IMPLEMENTED = "Not implemented.";
	private RequestStartTimeAccessEvent event;

	public RequestStartTimeAccessEvent getEvent() {
		return event;
	}

	@Override
	public String getName() {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED);
	}

	@Override
	public void doAppend(RequestStartTimeAccessEvent event) throws LogbackException {
		this.event = event;
	}

	@Override
	public void setName(String name) {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED);
	}

	@Override
	public void setContext(Context context) {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED);
	}

	@Override
	public Context getContext() {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED);
	}

	@Override
	public void addStatus(Status status) {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED);
	}

	@Override
	public void addInfo(String msg) {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED);
	}

	@Override
	public void addInfo(String msg, Throwable ex) {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED);
	}

	@Override
	public void addWarn(String msg) {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED);
	}

	@Override
	public void addWarn(String msg, Throwable ex) {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED);
	}

	@Override
	public void addError(String msg) {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED);
	}

	@Override
	public void addError(String msg, Throwable ex) {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED);
	}

	@Override
	public void addFilter(Filter<RequestStartTimeAccessEvent> newFilter) {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED);
	}

	@Override
	public void clearAllFilters() {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED);
	}

	@Override
	public List<Filter<RequestStartTimeAccessEvent>> getCopyOfAttachedFiltersList() {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED);
	}

	@Override
	public FilterReply getFilterChainDecision(RequestStartTimeAccessEvent event) {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED);
	}

	@Override
	public void start() {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED);
	}

	@Override
	public void stop() {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED);
	}

	@Override
	public boolean isStarted() {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED);
	}
}
