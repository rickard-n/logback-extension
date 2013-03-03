package se.svt.logback.access.spi;

import ch.qos.logback.access.spi.IAccessEvent;

/**
 * AccessEvent holding start time of request. To enable logging of response time.
 */
public interface RequestStartTimeAccessEvent extends IAccessEvent {
	long getRequestStarted();
}
