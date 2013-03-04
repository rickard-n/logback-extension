package se.svt.logback.access.tomcat;

import org.apache.catalina.CometEvent;
import org.apache.catalina.Valve;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;

import javax.servlet.ServletException;
import java.io.IOException;

public class DoNothingValve implements Valve {
	@Override
	public String getInfo() {
		return null;
	}

	@Override
	public Valve getNext() {
		return null;
	}

	@Override
	public void setNext(Valve valve) {

	}

	@Override
	public void backgroundProcess() {

	}

	@Override
	public void invoke(Request request, Response response) throws IOException, ServletException {

	}

	@Override
	public void event(Request request, Response response, CometEvent cometEvent) throws IOException, ServletException {

	}
}
