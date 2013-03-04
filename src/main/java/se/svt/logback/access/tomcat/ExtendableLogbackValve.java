package se.svt.logback.access.tomcat;

import ch.qos.logback.access.joran.JoranConfigurator;
import ch.qos.logback.core.BasicStatusManager;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.status.InfoStatus;
import ch.qos.logback.core.status.StatusManager;
import ch.qos.logback.core.status.WarnStatus;
import ch.qos.logback.core.util.OptionHelper;
import ch.qos.logback.core.util.StatusPrinter;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;

import javax.servlet.ServletException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class ExtendableLogbackValve extends ValveBase implements Lifecycle, Context {

	public final static String DEFAULT_CONFIG_FILE = "conf" + File.separatorChar
			+ "logback-access.xml";

	private long birthTime = System.currentTimeMillis();
	private Object configurationLock = new Object();

	// Attributes from ContextBase:
	private String name;
	private StatusManager sm = new BasicStatusManager();
	private Map<String, String> propertyMap = new HashMap<String, String>();
	private Map<String, Object> objectMap = new HashMap<String, Object>();

	private String filename;
	private boolean quiet;

	public ExtendableLogbackValve() {
		putObject(CoreConstants.EVALUATOR_MAP, new HashMap());
	}

	public void start() {
		if (filename == null) {
			String tomcatHomeProperty = OptionHelper
					.getSystemProperty("catalina.home");

			filename = tomcatHomeProperty + File.separatorChar + DEFAULT_CONFIG_FILE;
			getStatusManager().add(
					new InfoStatus("filename property not set. Assuming [" + filename
							+ "]", this));
		}
		File configFile = new File(filename);
		if (configFile.exists()) {
			try {
				JoranConfigurator jc = new JoranConfigurator();
				jc.setContext(this);
				jc.doConfigure(filename);
			} catch (JoranException e) {
				// TODO can we do better than printing a stack trace on syserr?
				e.printStackTrace();
			}
		} else {
			getStatusManager().add(
					new WarnStatus("[" + filename + "] does not exist", this));
		}

		if (!quiet) {
			StatusPrinter.print(getStatusManager());
		}

	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public boolean isQuiet() {
		return quiet;
	}

	public void setQuiet(boolean quiet) {
		this.quiet = quiet;
	}

	public abstract void invoke(Request request, Response response) throws IOException, ServletException;

	public void stop() {
	}

	// Methods from ContextBase:
	public StatusManager getStatusManager() {
		return sm;
	}

	public Map<String, String> getPropertyMap() {
		return propertyMap;
	}

	public void putProperty(String key, String val) {
		this.propertyMap.put(key, val);
	}

	public String getProperty(String key) {
		return (String) this.propertyMap.get(key);
	}

	public Map<String, String> getCopyOfPropertyMap() {
		return new HashMap<String, String>(this.propertyMap);
	}

	public Object getObject(String key) {
		return objectMap.get(key);
	}

	public void putObject(String key, Object value) {
		objectMap.put(key, value);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		if (this.name != null) {
			throw new IllegalStateException(
					"LogbackValve has been already given a name");
		}
		this.name = name;
	}

	public long getBirthTime() {
		return birthTime;
	}

	public Object getConfigurationLock() {
		return configurationLock;
	}

	// ====== Methods from catalina Lifecycle =====

	public void addLifecycleListener(LifecycleListener arg0) {
		// dummy NOP implementation
	}

	public LifecycleListener[] findLifecycleListeners() {
		return new LifecycleListener[0];
	}

	public void removeLifecycleListener(LifecycleListener arg0) {
		// dummy NOP implementation
	}
}
