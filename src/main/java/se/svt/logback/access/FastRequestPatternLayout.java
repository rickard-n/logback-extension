package se.svt.logback.access;

import ch.qos.logback.access.pattern.EnsureLineSeparation;
import ch.qos.logback.access.pattern.RemoteHostConverter;
import ch.qos.logback.access.pattern.RequestHeaderConverter;
import ch.qos.logback.access.pattern.RequestURLConverter;
import ch.qos.logback.access.spi.IAccessEvent;
import ch.qos.logback.core.pattern.PatternLayoutBase;
import ch.qos.logback.core.pattern.parser.Parser;
import se.svt.logback.access.pattern.TimeStampConverter;

import java.util.HashMap;
import java.util.Map;

public class FastRequestPatternLayout  extends PatternLayoutBase<IAccessEvent> {
	public static String CLF_PATTERN = "%t %i{X-Cluster-Client-Ip} %h '%requestURL' '%i{Referer}' '%i{User-Agent}'";
	public static final Map<String, String> DEFAULT_CONVERTER_MAP = new HashMap<String, String>();

	static {
		DEFAULT_CONVERTER_MAP.putAll(Parser.DEFAULT_COMPOSITE_CONVERTER_MAP);
		DEFAULT_CONVERTER_MAP.put("h", RemoteHostConverter.class.getName());
		DEFAULT_CONVERTER_MAP.put("t", TimeStampConverter.class.getName());
		DEFAULT_CONVERTER_MAP.put("i", RequestHeaderConverter.class.getName());
		DEFAULT_CONVERTER_MAP.put("requestURL", RequestURLConverter.class.getName());
	}

	public FastRequestPatternLayout() {
		// set a default value for pattern
		setPattern(CLF_PATTERN);
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