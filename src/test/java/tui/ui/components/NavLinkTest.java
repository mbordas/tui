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

package tui.ui.components;

import org.junit.Test;
import org.openqa.selenium.WebElement;
import tui.test.Browser;
import tui.test.TestWithBackend;
import tui.ui.components.layout.Panel;

import static org.junit.Assert.assertEquals;

public class NavLinkTest extends TestWithBackend {

	@Test
	public void load() {
		final Page page = new Page("NavLink load", "/index");
		final Panel panel = page.append(new Panel());
		panel.setSource("/panel");
		panel.append(new NavLink("link 1", "/link/1"));
		final RefreshButton refreshButton = page.append(new RefreshButton("Refresh"));
		refreshButton.connectListener(panel);

		startBackend(page);

		registerWebService(panel.getSource(), (uri, request, response) -> {
			final Panel _panel = new Panel();
			_panel.append(new NavLink("link 2", "/link/2"));
			return _panel.toJsonMap();
		});

		final Browser browser = startBrowser();
		browser.open(page.getSource());

		browser.clickRefreshButton(refreshButton.getLabel());
		wait_s(0.1);

		final WebElement navLink = browser.getNavLinks().get(0);
		assertEquals(m_backend.sourceToURI("/link/2"), navLink.getAttribute("href"));
	}
}