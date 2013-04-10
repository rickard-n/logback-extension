package se.svt.logback.classic.pattern;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import org.apache.catalina.loader.WebappClassLoader;

public class WebappNamePatternConverter extends ClassicConverter {

	@Override
	public String convert(ILoggingEvent event) {
		String webappName = "-";
		ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
		if(contextClassLoader instanceof WebappClassLoader) {
			webappName = ((WebappClassLoader)contextClassLoader).getContextName();
		}
		return webappName;
	}
}
