package se.svt.logback.classic.net;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.recovery.RecoveryCoordinator;
import ch.qos.logback.core.util.StatusPrinter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import se.svt.logback.access.MockSyslogServer;
import se.svt.logback.access.RandomUtil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class OneRowExceptionSyslogAppenderTest {
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
		sa.setSuffixPattern("[%thread] %logger %msg ");
		sa.start();
		assertTrue(sa.isStarted());

		logger.addAppender(sa);
	}

	@Test
	public void basic() throws InterruptedException {

		setMockServerAndConfigure(1);
		logger.debug(LOG_MSG);

		// wait max 2 seconds for mock server to finish. However, it should
		// much sooner than that.
		mockServer.join(8000);

		assertTrue(mockServer.isFinished());
		assertEquals(1, mockServer.getMessageList().size());
		String msg = mockServer.getMessageList().get(0);

		String threadName = Thread.currentThread().getName();

		checkRegexMatch(msg, "\\[" + threadName + "\\] " + loggerName + " "   + LOG_MSG); // NOSONAR

	}

	@Test
	public void tException() throws InterruptedException {
		setMockServerAndConfigure(1);

		String exMsg = "just testing";
		Exception ex = new Exception(exMsg);
		logger.debug(LOG_MSG, ex);
		StatusPrinter.print(lc);

		// wait max 2 seconds for mock server to finish. However, it should
		// much sooner than that.
		mockServer.join(8000);
		assertTrue(mockServer.isFinished());

		assertEquals(1, mockServer.getMessageList().size());

		String msg = mockServer.getMessageList().get(0);

		String threadName = Thread.currentThread().getName();
		String regex = "\\[" + threadName + "] " + loggerName
				+ " " + LOG_MSG + " " + ex.getClass().getName() + ": " + exMsg + "\\[EOL]" + ".*" ; // NOSONAR
		checkRegexMatch(msg, regex);
	}

	private void checkRegexMatch(String s, String regex) {
		assertTrue("The string [" + s + "] did not match regex [" + regex + "]", s
				.matches(regex));
	}

	@Test
	public void large() throws InterruptedException {
		setMockServerAndConfigure(1);
		StringBuilder largeBuf = new StringBuilder();
		for (int i = 0; i < 2 * 1024 * 1024; i++) {
			largeBuf.append('a');
		}
		logger.debug(largeBuf.toString());

		logger.debug(LOG_MSG);
		Thread.sleep(RecoveryCoordinator.BACKOFF_COEFFICIENT_MIN+10);
		logger.debug(LOG_MSG);

		mockServer.join(8000);
		assertTrue(mockServer.isFinished());

		// the first message is wasted
		assertEquals(1, mockServer.getMessageList().size());
		String msg = mockServer.getMessageList().get(0);
		String threadName = Thread.currentThread().getName();
		String regex = "\\[" + threadName + "\\] " + loggerName
				+ " " + LOG_MSG;
		checkRegexMatch(msg, regex);
	}
}
