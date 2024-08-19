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

import org.junit.After;
import tui.http.TUIBackend;
import tui.http.TUIWebService;
import tui.ui.UI;
import tui.ui.components.APage;

import java.io.IOException;
import java.net.ServerSocket;

public class TestWithBackend {

	private TUIBackend m_backend;
	private Browser m_browser;

	@After
	public void after() throws Exception {
		if(m_browser != null) {
			m_browser.stop();
		}
		if(m_backend != null) {
			m_backend.stop();
		}
	}

	protected void startBackend(String target, APage page) {
		try {
			final UI ui = new UI();
			ui.add(target, page);
			ui.setHTTPBackend("localhost", getRandomAvailablePort());
			m_backend = new TUIBackend(ui);
			m_backend.start();
		} catch(Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	protected Browser startAndBrowse(APage page) {
		final String target = "/index";
		startBackend(target, page);
		final Browser result = startBrowser();
		result.open(target);
		return result;
	}

	protected void registerWebService(String path, TUIWebService webservice) {
		m_backend.registerWebService(path, webservice);
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
