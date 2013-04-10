package se.svt.logback.admin.model;

import java.util.List;

public class LoggersModel {

	private final List<LoggerInfo> loggerInfoList;
	private final String contextPath;
	private final String loggerUrl;
	private final String addLoggerUrl;
	private final String reloadUrl;
	private final String hostName;

	public LoggersModel(List<LoggerInfo> loggerInfoList, String contextPath, String loggerUrl,
						String addLoggerUrl, String reloadUrl, String hostName) {
		this.loggerInfoList = loggerInfoList;
		this.contextPath = contextPath;
		this.loggerUrl = loggerUrl;
		this.addLoggerUrl = addLoggerUrl;
		this.reloadUrl = reloadUrl;
		this.hostName = hostName;
	}

	public List<LoggerInfo> getLoggerInfoList() {
		return loggerInfoList;
	}

	public String getContextPath() {
		return contextPath;
	}

	public String getLoggerUrl() {
		return loggerUrl;
	}

	public String getAddLoggerUrl() {
		return addLoggerUrl;
	}

	public String getReloadUrl() {
		return reloadUrl;
	}

	public String getHostName() {
		return hostName;
	}
}
