/*
 * Copyright (c) 2012-2024 Smart Grid Energy
 * All Right Reserved
 * http://www.smartgridenergy.fr
 */

package tui.test;

import org.openqa.selenium.firefox.FirefoxDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Browser {

	private static final Logger LOG = LoggerFactory.getLogger(Browser.class);

	private final FirefoxDriver m_driver;
	private final String m_host;

	public Browser(int port) {
		m_host = String.format("http://localhost:%d", port);
		m_driver = new FirefoxDriver();
	}

	public void open(String target) {
		if(target.startsWith("/")) {
			m_driver.get(m_host + target);
		} else {
			m_driver.get(m_host + "/" + target);
		}
	}

	public String getTitle() {
		return m_driver.getTitle();
	}

	public void stop() {
		m_driver.quit();
	}
}
