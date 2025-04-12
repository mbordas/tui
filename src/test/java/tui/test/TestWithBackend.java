/* Copyright (c) 2024, Mathieu Bordas
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

1- Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
2- Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
3- Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package tui.test;

import org.jetbrains.annotations.NotNull;
import org.junit.After;
import tui.http.TUIBackend;
import tui.http.TUIWebService;
import tui.ui.components.Page;
import tui.ui.components.UIRefreshableComponent;

import java.io.IOException;
import java.net.ServerSocket;

public class TestWithBackend {

	protected record BackendAndBrowser(TUIBackend backend, Browser browser) {
	}

	protected TUIBackend m_backend;
	protected Browser m_browser;

	@After
	public void after() throws Exception {
		if(m_browser != null) {
			m_browser.close();
		}
		if(m_backend != null) {
			m_backend.stop();
		}
	}

	private void constructBackendWhenNeeded() {
		if(m_backend == null) {
			try {
				m_backend = new TUIBackend(getRandomAvailablePort());
			} catch(IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	protected void startBackend(Page page) {
		try {
			constructBackendWhenNeeded();
			m_backend.registerPage(page);
			m_backend.start();
		} catch(Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	protected BackendAndBrowser startAndBrowse(Page page) {
		startBackend(page);
		startBrowser();
		m_browser.open(page.getSource());
		return new BackendAndBrowser(m_backend, m_browser);
	}

	protected void registerWebService(String path, TUIWebService webservice) {
		constructBackendWhenNeeded();
		m_backend.registerWebService(path, webservice);
	}

	/**
	 * Adds a web service that will return the given component as is, and provides a {@link tui.http.RequestReader} for further assertions.
	 *
	 * @param component Must be refreshable and have its source set.
	 */
	protected WebServiceSpy registerWebServiceSpy(@NotNull UIRefreshableComponent component) {
		final String source = component.getSource();
		assert source != null;
		constructBackendWhenNeeded();
		final WebServiceSpy result = new WebServiceSpy(component);
		m_backend.registerWebService(source, result.buildWebService());
		return result;
	}

	protected TClient startClient() {
		return new TClient("localhost", m_backend.getPort());
	}

	protected Browser startBrowser() {
		m_browser = new Browser(m_backend.getPort());
		return m_browser;
	}

	protected void wait_s(double delay_s) {
		try {
			Thread.sleep((long) (delay_s * 1000.0));
		} catch(InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	protected static int getRandomAvailablePort() throws IOException {
		try(ServerSocket socket = new ServerSocket(0, 50, null)) {
			return socket.getLocalPort();
		}
	}
}
