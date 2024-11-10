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
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import tui.test.Browser;
import tui.test.TestWithBackend;
import tui.ui.components.layout.Panel;

import static org.junit.Assert.assertEquals;

public class NavButtonTest extends TestWithBackend {

	@Test
	public void load() {
		final Page page2 = new Page("Page 2", "/page/2");
		final Page page3 = new Page("Page 3", "/page/3");

		final Page page1 = new Page("Page 1", "/page/1");
		final Panel panel = page1.append(new Panel());
		{
			panel.setSource("/page/1/panel");
			final NavButton navButton = panel.append(new NavButton("Navigate to page 2", page2.getSource()));
			navButton.setParameter("customParameter", "NavButtonTest 2");
		}
		page1.append(new RefreshButton("Refresh")).connectListener(panel);

		startBackend(page1);

		// Panel update
		registerWebService(panel.getSource(), (uri, request, response) -> {
			final Panel panel1 = new Panel();
			final NavButton _navButton = panel1.append(new NavButton("Navigate to page 3", page3.getSource()));
			_navButton.setParameter("customParameter", "NavButtonTest 2");
			return panel1.toJsonMap();
		});
		// Pages
		m_backend.registerPage(page2);
		m_backend.registerPage(page3);

		final Browser browser = startBrowser();
		browser.open(page1.getSource());

		// Initial NavButton
		{
			final WebElement navButton = browser.getNavButtons().get(0);
			assertEquals(m_backend.sourceToURI(page2.getSource()), navButton.getAttribute("action"));
		}

		browser.clickRefreshButton("Refresh");
		wait_s(2.0);

		// Updated NavButton
		{
			final WebElement navButton = browser.getNavButtons().get(0);
			assertEquals(m_backend.sourceToURI(page3.getSource()), navButton.getAttribute("action"));

			final WebElement button = navButton.findElement(By.tagName("button"));
			assertEquals("Navigate to page 3", button.getText());

			button.click();
			wait_s(0.1);

			assertEquals(page3.getTitle(), browser.getTitle());
		}
	}
}