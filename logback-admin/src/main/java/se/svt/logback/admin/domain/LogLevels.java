package se.svt.logback.admin.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum LogLevels {
	OFF(Integer.MAX_VALUE, "Off"),
	ERROR_INT(40000, "Error"),
	WARN_INT(30000, "Warn"),
	INFO_INT(20000, "Info"),
	DEBUG_INT(10000, "Debug"),
	TRACE(5000, "Trace"),
	ALL(Integer.MIN_VALUE, "All");

	private int logLevel;
	private String logLevelName;

	LogLevels(final int logLevel, final String logLevelName) {
		this.logLevel = logLevel;
		this.logLevelName = logLevelName;
	}

	public int getLogLevel() {
		return this.logLevel;
	}

	public String getLogLevelName() {
		return this.logLevelName;
	}




	public static LogLevels getLogLevelFromString(final String logLevelAsString) {

		for (LogLevels logLevel : LogLevels.values()) {

			if (logLevel.logLevelName.equals(logLevelAsString)) {
				return logLevel;
			}
		}

		throw new IllegalStateException("Loglevel with name " + logLevelAsString + " does not exist.");

	}

	public static LogLevels getLogLevelFromId(final int logLevelAsInt) {

		for (LogLevels logLevel : LogLevels.values()) {

			if (logLevelAsInt == logLevel.logLevel) {
				return logLevel;
			}
		}

		throw new IllegalStateException("Loglevel " + logLevelAsInt + " does not exist.");

	}

	public static List<LogLevels> getNames() {
		return new ArrayList<LogLevels>(Arrays.asList(LogLevels.values()));
	}
}
