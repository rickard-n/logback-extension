package org.apache.log4j;

import org.apache.log4j.helpers.NullEnumeration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.slf4j.spi.LocationAwareLogger;

import java.util.Enumeration;

public class Category
{
	private static final String CATEGORY_FQCN = Category.class.getName();
	private String name;
	protected Logger slf4jLogger;
	private LocationAwareLogger locationAwareLogger;
	private static Marker FATAL_MARKER = MarkerFactory.getMarker("FATAL");

	Category(String name) {
		this.name = name;
		this.slf4jLogger = LoggerFactory.getLogger(name);
		if ((this.slf4jLogger instanceof LocationAwareLogger))
			this.locationAwareLogger = ((LocationAwareLogger)this.slf4jLogger);
	}

	public static Category getInstance(Class clazz)
	{
		return Log4jLoggerFactory.getLogger(clazz.getName());
	}

	public static Category getInstance(String name) {
		return Log4jLoggerFactory.getLogger(name);
	}

	public String getName()
	{
		return this.name;
	}

	public Enumeration getAllAppenders() {
		return NullEnumeration.getInstance();
	}

	public Level getEffectiveLevel()
	{
		if (this.slf4jLogger.isTraceEnabled()) {
			return Level.TRACE;
		}
		if (this.slf4jLogger.isDebugEnabled()) {
			return Level.DEBUG;
		}
		if (this.slf4jLogger.isInfoEnabled()) {
			return Level.INFO;
		}
		if (this.slf4jLogger.isWarnEnabled()) {
			return Level.WARN;
		}
		return Level.ERROR;
	}

	public final Level getLevel()
	{
		return null;
	}

	public void setLevel(Level level) {
		// Do nothing. Configure logging levels in property file.
	}

	/** @deprecated */
	public final Level getPriority()
	{
		return null;
	}

	public void setPriority(Priority priority) {
		// Do nothing. Configure logging levels in property file.
	}

	public boolean isDebugEnabled()
	{
		return this.slf4jLogger.isDebugEnabled();
	}

	public boolean isInfoEnabled()
	{
		return this.slf4jLogger.isInfoEnabled();
	}

	public boolean isWarnEnabled()
	{
		return this.slf4jLogger.isWarnEnabled();
	}

	public boolean isErrorEnabled()
	{
		return this.slf4jLogger.isErrorEnabled();
	}

	public boolean isEnabledFor(Priority p)
	{
		switch (p.level) {
			case 5000:
				return this.slf4jLogger.isTraceEnabled();
			case 10000:
				return this.slf4jLogger.isDebugEnabled();
			case 20000:
				return this.slf4jLogger.isInfoEnabled();
			case 30000:
				return this.slf4jLogger.isWarnEnabled();
			case 40000:
				return this.slf4jLogger.isErrorEnabled();
			case 50000:
				return this.slf4jLogger.isErrorEnabled();
		}
		return false;
	}

	void differentiatedLog(Marker marker, String fqcn, int level, Object message, Throwable t)
	{
		String m = convertToString(message);
		if (this.locationAwareLogger != null)
			this.locationAwareLogger.log(marker, fqcn, level, m, null, t);
		else
			switch (level) {
				case 0:
					this.slf4jLogger.trace(marker, m);
					break;
				case 10:
					this.slf4jLogger.debug(marker, m);
					break;
				case 20:
					this.slf4jLogger.info(marker, m);
					break;
				case 30:
					this.slf4jLogger.warn(marker, m);
					break;
				case 40:
					this.slf4jLogger.error(marker, m);
			}
	}

	public void debug(Object message)
	{
		differentiatedLog(null, CATEGORY_FQCN, 10, message, null);
	}

	public void debug(Object message, Throwable t)
	{
		differentiatedLog(null, CATEGORY_FQCN, 10, message, t);
	}

	public void info(Object message)
	{
		differentiatedLog(null, CATEGORY_FQCN, 20, message, null);
	}

	public void info(Object message, Throwable t)
	{
		differentiatedLog(null, CATEGORY_FQCN, 20, message, t);
	}

	public void warn(Object message)
	{
		differentiatedLog(null, CATEGORY_FQCN, 30, message, null);
	}

	public void warn(Object message, Throwable t)
	{
		differentiatedLog(null, CATEGORY_FQCN, 30, message, t);
	}

	public void error(Object message)
	{
		differentiatedLog(null, CATEGORY_FQCN, 40, message, null);
	}

	public void error(Object message, Throwable t)
	{
		differentiatedLog(null, CATEGORY_FQCN, 40, message, t);
	}

	public void fatal(Object message)
	{
		differentiatedLog(FATAL_MARKER, CATEGORY_FQCN, 40, message, null);
	}

	public void fatal(Object message, Throwable t)
	{
		differentiatedLog(FATAL_MARKER, CATEGORY_FQCN, 40, message, t);
	}

	protected void forcedLog(String FQCN, Priority p, Object msg, Throwable t)
	{
		log(FQCN, p, msg, t);
	}

	public void log(String FQCN, Priority p, Object msg, Throwable t)
	{
		int levelInt = priorityToLevelInt(p);
		differentiatedLog(null, FQCN, levelInt, msg, t);
	}

	public void log(Priority p, Object message, Throwable t) {
		int levelInt = priorityToLevelInt(p);
		differentiatedLog(null, CATEGORY_FQCN, levelInt, message, t);
	}

	public void log(Priority p, Object message) {
		int levelInt = priorityToLevelInt(p);
		differentiatedLog(null, CATEGORY_FQCN, levelInt, message, null);
	}

	private int priorityToLevelInt(Priority p) {
		switch (p.level) {
			case 5000:
			case 9900:
				return 0;
			case 10000:
				return 10;
			case 20000:
				return 20;
			case 30000:
				return 30;
			case 40000:
				return 40;
			case 50000:
				return 40;
		}
		throw new IllegalStateException("Unknown Priority " + p);
	}

	protected final String convertToString(Object message)
	{
		if (message == null) {
			return (String)message;
		}
		return message.toString();
	}
}