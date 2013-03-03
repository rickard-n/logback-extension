package se.svt.logback.access.pattern;

import ch.qos.logback.access.pattern.AccessConverter;
import ch.qos.logback.access.spi.IAccessEvent;
import se.svt.logback.access.spi.RequestStartTimeAccessEvent;

public class ResponseTimeConverter extends AccessConverter {
	private Long currentTime = null;

	@Override
	public String convert(IAccessEvent event) {
		if(event instanceof RequestStartTimeAccessEvent) {

			long responseTime = getCurrentTime() - ((RequestStartTimeAccessEvent)event).getRequestStarted();
			if(responseTime < 0) {
				return IAccessEvent.NA;
			}
			return Long.toString(responseTime);
		} else {
			return IAccessEvent.NA;
		}
	}

	protected void setCurrentTime(Long currentTime) {
		this.currentTime = currentTime;
	}

	private Long getCurrentTime() {
		return currentTime != null ? currentTime : System.currentTimeMillis();
	}
}
