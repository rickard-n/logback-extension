package se.svt.logback.admin;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.util.ContextInitializer;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.joran.spi.JoranException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;
import se.svt.logback.admin.domain.LogLevels;
import se.svt.logback.admin.model.LoggerInfo;
import se.svt.logback.admin.model.LoggersModel;

import javax.servlet.ServletContext;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static ch.qos.logback.core.util.StatusPrinter.printInCaseOfErrorsOrWarnings;


@Controller
public class LoggerController {

	public static final String BASE_URL = "/admin";
	public static final String LOGGER_URL = BASE_URL + "/loggers";
	public static final String ADD_LOGGER_URL = BASE_URL + "/addLogger";
	public static final String RELOAD_URL = BASE_URL + "/reload";
	@Autowired
	private ServletContext servletContext;

	@Value("${escenic.hostname:\"unknown host\"}")
	private String hostName;

	@RequestMapping(value = LOGGER_URL, method = RequestMethod.GET)
	public ModelAndView listLoggers(@RequestParam(value = "showAll", defaultValue = "false") Boolean showAll) {
		final LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		final List<LoggerInfo> loggers = getLoggerInfoList(showAll, lc);

		LoggersModel modelObject = new LoggersModel(loggers, servletContext.getContextPath(),
				LOGGER_URL, ADD_LOGGER_URL, RELOAD_URL, hostName);
		return new ModelAndView("showLoggers", "loggers", modelObject);
	}

	private List<LoggerInfo> getLoggerInfoList(Boolean showAll, LoggerContext lc) {
		final List<LoggerInfo> loggers = new ArrayList<LoggerInfo>();

		for (Logger log : lc.getLoggerList()) {
			if(showAll != null && !showAll) {
				if(log.getLevel() != null || hasAppenders(log)) {
					loggers.add(createLoggerInfo(log));
				}
			} else {
				loggers.add(createLoggerInfo(log));
			}
		}
		return loggers;
	}

	@RequestMapping(value = LOGGER_URL, method = RequestMethod.POST)
	@ResponseStatus( HttpStatus.OK )
	public void setLogStatus(@RequestParam(value = "logger") String logger,
									 @RequestParam(value = "level") String level) {
		final LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		lc.getLogger(logger).setLevel(Level.toLevel(LogLevels.getLogLevelFromString(level).getLogLevel()));
	}

	@RequestMapping(value = ADD_LOGGER_URL, method = RequestMethod.POST)
	public View addLogger(@RequestParam(value = "logger") String logger,
									 @RequestParam(value = "level") String level) {
		final LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		lc.getLogger(logger).setLevel(Level.toLevel(LogLevels.getLogLevelFromString(level).getLogLevel()));
		return new RedirectView(servletContext.getContextPath() + LOGGER_URL);
	}

	@RequestMapping(value = RELOAD_URL, method = RequestMethod.POST)
	public View reloadLevels() {
		LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

		ContextInitializer ci = new ContextInitializer(loggerContext);
		URL url = ci.findURLOfDefaultConfigurationFile(true);

		try {
			JoranConfigurator configurator = new JoranConfigurator();
			configurator.setContext(loggerContext);
			loggerContext.reset();
			configurator.doConfigure(url);
		} catch (JoranException je) {
			// StatusPrinter will handle this
		}
		printInCaseOfErrorsOrWarnings(loggerContext);
		return new RedirectView(servletContext.getContextPath() + LOGGER_URL);
	}

	private LoggerInfo createLoggerInfo(Logger logger) {
		return new LoggerInfo(logger.getName(), LogLevels.getLogLevelFromId(logger.getEffectiveLevel().levelInt));
	}

	public static boolean hasAppenders(ch.qos.logback.classic.Logger logger) {
		Iterator<Appender<ILoggingEvent>> it = logger.iteratorForAppenders();
		return it.hasNext();
	}
}