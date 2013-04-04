package se.svt.logback.classic.net;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.pattern.SyslogStartConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import ch.qos.logback.classic.util.LevelToSyslogSeverity;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.Layout;

import java.io.IOException;

public class SyslogAppender extends ExtendableSyslogAppender<ILoggingEvent> {
	public static final String SYSLOG_START_KEY = "syslogStart";
	public static final String DEFAULT_SUFFIX_PATTERN = "[%thread] %logger %msg ";

	public Layout<ILoggingEvent> buildLayout(String facilityStr) {
		PatternLayout fullLayout = new PatternLayout();
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

	/*
		   * Convert a level to equivalent syslog severity. Only levels for printing
		   * methods i.e DEBUG, WARN, INFO and ERROR are converted.
		   *
		   * @see ch.qos.logback.core.net.SyslogAppenderBase#getSeverityForEvent(java.lang.Object)
		   */
	@Override
	public int getSeverityForEvent(ILoggingEvent event) {
		return LevelToSyslogSeverity.convert(event);
	}

	@Override
	protected void postProcess(ILoggingEvent event) {
		String prefix = getLayout().doLayout(event);

		IThrowableProxy tp = event.getThrowableProxy();
		while (tp != null) {
			StackTraceElementProxy[] stepArray = tp.getStackTraceElementProxyArray();
			try {
				for (StackTraceElementProxy step : stepArray) {
					StringBuilder sb = new StringBuilder();
					sb.append(prefix).append(CoreConstants.TAB).append(step);
					writeMessage(sb.toString());
				}
			} catch (IOException e) {
				break;
			}
			tp = tp.getCause();
		}
	}
}
