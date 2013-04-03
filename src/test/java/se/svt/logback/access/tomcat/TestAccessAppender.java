package se.svt.logback.access.tomcat;

import ch.qos.logback.access.spi.IAccessEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.LogbackException;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;
import ch.qos.logback.core.status.Status;

import java.util.List;

public class TestAccessAppender implements Appender<IAccessEvent> {
	public static final String NOT_IMPLEMENTED = "Not implemented.";
	private Context context;
	private String name;
	private IAccessEvent event;

	public IAccessEvent getEvent() {
		return event;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void doAppend(IAccessEvent event) throws LogbackException {
		this.event = event;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void setContext(Context context) {
		this.context = context;
	}

	@Override
	public Context getContext() {
		return context;
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
	public void addFilter(Filter<IAccessEvent> newFilter) {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED);
	}

	@Override
	public void clearAllFilters() {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED);
	}

	@Override
	public List<Filter<IAccessEvent>> getCopyOfAttachedFiltersList() {
		return null;
	}

	@Override
	public FilterReply getFilterChainDecision(IAccessEvent event) {
		return null;
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
		return true;
	}
}