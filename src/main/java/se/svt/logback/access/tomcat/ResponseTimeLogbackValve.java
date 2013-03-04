package se.svt.logback.access.tomcat;

import ch.qos.logback.access.AccessConstants;
import ch.qos.logback.access.spi.ServerAdapter;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.AppenderAttachable;
import ch.qos.logback.core.spi.AppenderAttachableImpl;
import ch.qos.logback.core.spi.FilterAttachable;
import ch.qos.logback.core.spi.FilterAttachableImpl;
import ch.qos.logback.core.spi.FilterReply;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import se.svt.logback.access.spi.RequestStartTimeAccessEvent;
import se.svt.logback.access.spi.RequestStartTimeAccessEventImpl;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class ResponseTimeLogbackValve extends ExtendableLogbackValve implements AppenderAttachable<RequestStartTimeAccessEvent>,
		FilterAttachable<RequestStartTimeAccessEvent> {
	private FilterAttachableImpl<RequestStartTimeAccessEvent> fai = new FilterAttachableImpl<RequestStartTimeAccessEvent>();
	private AppenderAttachableImpl<RequestStartTimeAccessEvent> aai = new AppenderAttachableImpl<RequestStartTimeAccessEvent>();
	private boolean alreadySetLogbackStatusManager = false;
	private Long currentTime=null;

	@Override
	public void addAppender(Appender<RequestStartTimeAccessEvent> newAppender) {
		aai.addAppender(newAppender);
	}

	@Override
	public Iterator<Appender<RequestStartTimeAccessEvent>> iteratorForAppenders() {
		return aai.iteratorForAppenders();
	}

	@Override
	public Appender<RequestStartTimeAccessEvent> getAppender(String name) {
		return aai.getAppender(name);
	}

	@Override
	public boolean isAttached(Appender<RequestStartTimeAccessEvent> appender) {
		return aai.isAttached(appender);
	}

	@Override
	public void detachAndStopAllAppenders() {
		aai.detachAndStopAllAppenders();
	}

	@Override
	public boolean detachAppender(Appender<RequestStartTimeAccessEvent> appender) {
		return aai.detachAppender(appender);
	}

	@Override
	public boolean detachAppender(String name) {
		return aai.detachAppender(name);
	}

	@Override
	public void addFilter(Filter<RequestStartTimeAccessEvent> newFilter) {
		fai.addFilter(newFilter);
	}

	@Override
	public void clearAllFilters() {
		fai.clearAllFilters();
	}

	@Override
	public List<Filter<RequestStartTimeAccessEvent>> getCopyOfAttachedFiltersList() {
		return fai.getCopyOfAttachedFiltersList();
	}

	@Override
	public FilterReply getFilterChainDecision(RequestStartTimeAccessEvent event) {
		return fai.getFilterChainDecision(event);
	}

	@Override
	public void invoke(Request request, Response response) throws IOException, ServletException {
		try {

			if (!alreadySetLogbackStatusManager) {
				alreadySetLogbackStatusManager = true;
				org.apache.catalina.Context tomcatContext = request.getContext();
				if (tomcatContext != null) {
					ServletContext sc = tomcatContext.getServletContext();
					if (sc != null) {
						sc.setAttribute(AccessConstants.LOGBACK_STATUS_MANAGER_KEY,
								getStatusManager());
					}
				}
			}
			long requestStarted = getCurrentTime();
			getNext().invoke(request, response);
			ServerAdapter adapter = new ContentCountingTomcatServerAdapter(request, response);
			RequestStartTimeAccessEvent accessEvent = new RequestStartTimeAccessEventImpl(request, response, adapter, requestStarted);

			if (getFilterChainDecision(accessEvent) == FilterReply.DENY) {
				return;
			}
			// TODO better exception handling
			int appended = aai.appendLoopOnAppenders(accessEvent);
		} finally {
			request.removeAttribute(AccessConstants.LOGBACK_STATUS_MANAGER_KEY);
		}
	}

	private long getCurrentTime() {
		if(currentTime != null) {
			return currentTime;
		}
		return System.currentTimeMillis();
	}

	protected void setCurrentTimeForTest(long currentTime) {
		this.currentTime = currentTime;
	}
}
