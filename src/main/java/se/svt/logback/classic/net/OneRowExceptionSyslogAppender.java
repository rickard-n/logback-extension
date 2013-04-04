package se.svt.logback.classic.net;

import ch.qos.logback.classic.spi.ILoggingEvent;

import java.io.IOException;

public class OneRowExceptionSyslogAppender extends SyslogAppender {
	public static final String EOL = "[EOL]";

	@Override
	protected void append(ILoggingEvent event) {
		if (!isStarted()) {
			return;
		}

		try {
			String msg = getLayout().doLayout(event);
			writeMessage(msg.replaceAll("\n", EOL).trim());
		} catch (IOException ioe) {
			addError("Failed to send diagram to " + getSyslogHost(), ioe);
		}
	}
}