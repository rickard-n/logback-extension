package se.svt.logback.access.tomcat;

import ch.qos.logback.access.AccessConstants;
import ch.qos.logback.access.spi.AccessEvent;
import ch.qos.logback.access.spi.IAccessEvent;
import ch.qos.logback.access.tomcat.TomcatServerAdapter;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.AppenderAttachable;
import ch.qos.logback.core.spi.AppenderAttachableImpl;
import ch.qos.logback.core.spi.FilterAttachable;
import ch.qos.logback.core.spi.FilterAttachableImpl;
import ch.qos.logback.core.spi.FilterReply;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class StandardLogbackValve extends ExtendableLogbackValve implements Lifecycle, Context,
		AppenderAttachable<IAccessEvent>, FilterAttachable<IAccessEvent> {

	private FilterAttachableImpl<IAccessEvent> fai = new FilterAttachableImpl<IAccessEvent>();
	private AppenderAttachableImpl<IAccessEvent> aai = new AppenderAttachableImpl<IAccessEvent>();
	private boolean alreadySetLogbackStatusManager = false;

	@Override
	public void addAppender(Appender<IAccessEvent> newAppender) {
		aai.addAppender(newAppender);
	}

	@Override
	public Iterator<Appender<IAccessEvent>> iteratorForAppenders() {
		return aai.iteratorForAppenders();
	}

	@Override
	public Appender<IAccessEvent> getAppender(String name) {
		return aai.getAppender(name);
	}

	@Override
	public boolean isAttached(Appender<IAccessEvent> appender) {
		return aai.isAttached(appender);
	}

	@Override
	public void detachAndStopAllAppenders() {
		aai.detachAndStopAllAppenders();
	}

	@Override
	public boolean detachAppender(Appender<IAccessEvent> appender) {
		return aai.detachAppender(appender);
	}

	@Override
	public boolean detachAppender(String name) {
		return aai.detachAppender(name);
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

			getNext().invoke(request, response);

			TomcatServerAdapter adapter = new TomcatServerAdapter(request, response);
			IAccessEvent accessEvent = new AccessEvent(request, response, adapter);

			if (getFilterChainDecision(accessEvent) == FilterReply.DENY) {
				return;
			}

			// TODO better exception handling
			aai.appendLoopOnAppenders(accessEvent);
		} finally {
			request.removeAttribute(AccessConstants.LOGBACK_STATUS_MANAGER_KEY);
		}
	}

	@Override
	public void addFilter(Filter<IAccessEvent> newFilter) {
		fai.addFilter(newFilter);
	}

	@Override
	public void clearAllFilters() {
		fai.clearAllFilters();
	}

	@Override
	public List<Filter<IAccessEvent>> getCopyOfAttachedFiltersList() {
		return fai.getCopyOfAttachedFiltersList();
	}

	@Override
	public FilterReply getFilterChainDecision(IAccessEvent event) {
		return fai.getFilterChainDecision(event);
	}

	protected FilterAttachableImpl<IAccessEvent> getFai() {
		return fai;
	}

	protected AppenderAttachableImpl<IAccessEvent> getAai() {
		return aai;
	}


}