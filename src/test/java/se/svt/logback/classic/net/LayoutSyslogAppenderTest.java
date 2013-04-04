package se.svt.logback.classic.net;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import se.svt.logback.access.MockSyslogServer;
import se.svt.logback.access.RandomUtil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LayoutSyslogAppenderTest {

	public static final String DEFAULT_PREFIX_REGEX = "\\d{4}(-\\d{2}){2} \\d{2}(:\\d{2}){2}\\.\\d{3} &lt;\\w*&gt; Pub:\\w* cn:\\w*";
	public static final String LOG_MSG = "hello";
	private LoggerContext lc = new LoggerContext();
	private OneRowExceptionSyslogAppender sa = new OneRowExceptionSyslogAppender();
	private MockSyslogServer mockServer;
	private String loggerName = OneRowExceptionSyslogAppenderTest.class.getName();
	private Logger logger = lc.getLogger(loggerName);

	@Before
	public void setUp() throws Exception {
		lc.setName("test");
		sa.setContext(lc);
	}

	@After
	public void tearDown() throws Exception {
	}

	public void setMockServerAndConfigure(int expectedCount)
			throws InterruptedException {
		int port = RandomUtil.getRandomServerPort();

		mockServer = new MockSyslogServer(expectedCount, port);
		mockServer.start();
		// give MockSyslogServer head start

		Thread.sleep(100);

		sa.setSyslogHost("localhost");
		sa.setFacility("MAIL");
		sa.setPort(port);
		sa.setSuffixPattern("%d{yyyy-MM-dd HH:mm:ss.SSS,Europe/Stockholm} &lt;%marker&gt; Pub:%X{publication} cn:%contextName %-5level [%thread] \\(%logger\\) %msg");
		sa.start();
		assertTrue(sa.isStarted());

		logger.addAppender(sa);
	}

	@Test
	public void shouldLogSimpleMessageWithPatternLayoutFormat() throws InterruptedException {

		setMockServerAndConfigure(1);
		logger.debug(LOG_MSG);

		// wait max 2 seconds for mock server to finish. However, it should
		// much sooner than that.
		mockServer.join(8000);

		assertTrue(mockServer.isFinished());
		assertEquals(1, mockServer.getMessageList().size());
		String msg = mockServer.getMessageList().get(0);

		String threadName = Thread.currentThread().getName();

		String first = DEFAULT_PREFIX_REGEX;
		checkRegexMatch(msg, first + " DEBUG \\[" + threadName + "] \\(" + loggerName + "\\) "   // NOSONAR
				+ LOG_MSG);

	}

	private void checkRegexMatch(String s, String regex) {
		assertTrue("The string [" + s + "] did not match regex [" + regex + "]", s
				.matches(regex));
	}
}
