package se.svt.logback.access;

import ch.qos.logback.access.PatternLayout;
import ch.qos.logback.access.pattern.EnsureLineSeparation;
import ch.qos.logback.access.spi.IAccessEvent;
import ch.qos.logback.core.pattern.PatternLayoutBase;
import se.svt.logback.access.pattern.ResponseTimeConverter;

import java.util.HashMap;
import java.util.Map;

public class ResponseTimePatternLayout extends PatternLayoutBase<IAccessEvent> {
	public static final Map<String, String> DEFAULT_CONVERTER_MAP = new HashMap<String, String>();

	static {
		DEFAULT_CONVERTER_MAP.putAll(PatternLayout.defaultConverterMap);
		DEFAULT_CONVERTER_MAP.put("D", ResponseTimeConverter.class.getName());
	}

	public ResponseTimePatternLayout() {
		// set a default value for pattern
		setPattern(PatternLayout.CLF_PATTERN);
		// by default postCompileProcessor the is an EnsureLineSeparation instance
		this.postCompileProcessor = new EnsureLineSeparation();
	}

	@Override
	public Map<String, String> getDefaultConverterMap() {
		return DEFAULT_CONVERTER_MAP;
	}

	@Override
	public String doLayout(IAccessEvent event) {
		if (!isStarted()) {
			return null;
		}
		return writeLoopOnConverters(event);
	}
}
