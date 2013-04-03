package se.svt.logback.access.tomcat;

import ch.qos.logback.access.spi.IAccessEvent;
import ch.qos.logback.core.spi.AppenderAttachable;
import ch.qos.logback.core.spi.FilterAttachable;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import se.svt.logback.access.spi.FastRequestAccessEventImpl;

import javax.servlet.ServletException;
import java.io.IOException;

public class FastRequestLogbackValve extends StandardLogbackValve
		implements AppenderAttachable<IAccessEvent>,
		FilterAttachable<IAccessEvent> {

	@Override
	public void invoke(Request request, Response response) throws IOException, ServletException {
		IAccessEvent accessEvent = new FastRequestAccessEventImpl(request);
		getAai().appendLoopOnAppenders(accessEvent);
		getNext().invoke(request, response);
	}


}
