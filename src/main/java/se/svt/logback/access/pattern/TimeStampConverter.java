package se.svt.logback.access.pattern;

import ch.qos.logback.access.pattern.AccessConverter;
import ch.qos.logback.access.spi.IAccessEvent;

public class TimeStampConverter extends AccessConverter {

	@Override
	public String convert(IAccessEvent event) {
		long timeStamp = event.getTimeStamp();
		if(timeStamp < 0) {
			return IAccessEvent.NA;
		}
		return Long.toString(timeStamp);
	}
}