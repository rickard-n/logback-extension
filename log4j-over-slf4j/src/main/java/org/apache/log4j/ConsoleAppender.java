package org.apache.log4j;

public class ConsoleAppender extends ch.qos.logback.core.ConsoleAppender {
	public ConsoleAppender()
	{
	}

	public ConsoleAppender(Layout layout)
	{
		this(layout, "System.out");
	}

	public ConsoleAppender(Layout layout, String target)
	{
		setTarget(target);
	}

	public final void setFollow(boolean newValue)
	{
		//
	}

	public final boolean getFollow()
	{
		return false;
	}

	public void activateOptions() {}
}
