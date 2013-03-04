package se.svt.logback.access.pattern;

import ch.qos.logback.access.spi.IAccessEvent;
import ch.qos.logback.core.Layout;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;
import ch.qos.logback.core.pattern.PatternLayoutBase;

import java.io.IOException;

public class PatternWithLayoutEncoder extends LayoutWrappingEncoder<IAccessEvent> {

	String pattern;

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	@Override
	public void start() {
		Layout<IAccessEvent> eventLayout = getLayout();
		eventLayout.setContext(context);
		if(eventLayout instanceof PatternLayoutBase) {
			((PatternLayoutBase)eventLayout).setPattern(getPattern());
		}
		eventLayout.start();
		super.start();
	}

	@Override
	public void doEncode(IAccessEvent event) throws IOException {
		super.doEncode(event);
	}
}
