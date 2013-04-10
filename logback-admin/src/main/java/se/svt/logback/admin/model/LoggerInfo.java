package se.svt.logback.admin.model;

import se.svt.logback.admin.domain.LogLevels;

public class LoggerInfo {

	private String    loggerName;
	private LogLevels logLevel;

	public LoggerInfo(String loggerName, LogLevels logLevel) {
		this.loggerName = loggerName;
		this.logLevel = logLevel;
	}

	public String getLoggerName() {
		return loggerName;
	}

	public LogLevels getLogLevel() {
		return logLevel;
	}
}
