package se.svt.logback.access.net;

import ch.qos.logback.access.spi.IAccessEvent;
import ch.qos.logback.classic.pattern.SyslogStartConverter;
import ch.qos.logback.core.Layout;
import se.svt.logback.access.ResponseTimePatternLayout;
import se.svt.logback.classic.net.ExtendableSyslogAppender;

import java.io.IOException;

public class AccessSyslogAppender extends ExtendableSyslogAppender<IAccessEvent> {
	public static final String SYSLOG_START_KEY = "syslogStart";
	public static final String DEFAULT_SUFFIX_PATTERN = "[%thread] %logger %msg";
	public static final int INFO_SEVERITY = 7;

	@Override
	public Layout<IAccessEvent> buildLayout(String facilityStr) {
		ResponseTimePatternLayout fullLayout = new ResponseTimePatternLayout();
		fullLayout.getInstanceConverterMap().put(SYSLOG_START_KEY,
				SyslogStartConverter.class.getName());

		String suffixPattern = getSuffixPattern();
		if (getSuffixPattern() == null) {
			suffixPattern = DEFAULT_SUFFIX_PATTERN;
			setSuffixPattern(DEFAULT_SUFFIX_PATTERN);
		}

		fullLayout.setPattern(suffixPattern);
		fullLayout.setContext(getContext());
		fullLayout.start();
		return fullLayout;
	}

	@Override
	public int getSeverityForEvent(IAccessEvent event) {
		return INFO_SEVERITY;
	}

	@Override
	protected void append(IAccessEvent event) {
		if (!isStarted()) {
			return;
		}

		try {
			String msg = getLayout().doLayout(event);
			writeMessage(msg);
		} catch (IOException ioe) {
			addError("Failed to send diagram to " + getSyslogHost(), ioe);
		}
	}
}
