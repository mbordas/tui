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

package tui.ui.components.layout;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import tui.test.Browser;
import tui.test.TestWithBackend;
import tui.ui.components.Page;
import tui.ui.components.Paragraph;
import tui.ui.components.RefreshButton;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TabbedFlowTest extends TestWithBackend {

	@Test
	public void load() {
		final Page page = new Page("TabbedFlow load", "/index");
		final Panel panel = page.append(new Panel());
		panel.setSource("/panel");
		final RefreshButton refreshButton = page.append(new RefreshButton(("Refresh")));
		refreshButton.connectListener(panel);

		startBackend(page);
		registerWebService(panel.getSource(), (uri, request, response) -> {
			final Panel _panel = new Panel();
			final TabbedFlow tabbedFlow = _panel.append(new TabbedFlow());

			final VerticalFlow flowA = tabbedFlow.createTab("Tab A");
			flowA.append(new Paragraph("Text for tab A."));
			flowA.setWidth(Layouts.Width.MAX);
			flowA.setSpacing(Layouts.Spacing.LARGE);

			final VerticalFlow flowB = tabbedFlow.createTab("Tab B");
			flowB.append(new Paragraph("Text for tab B."));
			flowB.setWidth(Layouts.Width.NORMAL);
			flowB.setSpacing(Layouts.Spacing.FIT);
			return _panel.toJsonMap();
		});

		final Browser browser = startBrowser();
		browser.open(page.getSource());

		browser.clickRefreshButton(refreshButton.getLabel());
		wait_s(0.1);

		final WebElement tabbedFlow = browser.getTabbedFlows().get(0);
		final List<WebElement> tabs = tabbedFlow.findElements(By.className(TabbedFlow.HTML_CLASS_TAB));
		final List<WebElement> buttons = tabbedFlow.findElements(By.className(TabbedFlow.HTML_CLASS_TABLINK));

		assertEquals(2, buttons.size());
		assertEquals(2, tabs.size());

		// Checking active tab
		final Map<String, String> activeTabStyleProperties = Browser.parseStyleProperties(tabs.get(0));
		assertEquals("block", activeTabStyleProperties.get("display"));

		// Checking inactive tab
		final Map<String, String> inactiveTabStyleProperties = Browser.parseStyleProperties(tabs.get(1));
		assertEquals("none", inactiveTabStyleProperties.get("display"));

		// Switching tabs

		final Optional<WebElement> buttonToTabA = buttons.stream().filter((button) -> button.getText().equals("Tab A")).findAny();
		assertTrue(buttonToTabA.isPresent());
		final Optional<WebElement> buttonToTabB = buttons.stream().filter((button) -> button.getText().equals("Tab B")).findAny();
		assertTrue(buttonToTabB.isPresent());

		assertTrue(Browser.getClasses(buttonToTabA.get()).contains(TabbedFlow.HTML_CLASS_TABLINK_ACTIVE));
		assertFalse(Browser.getClasses(buttonToTabB.get()).contains(TabbedFlow.HTML_CLASS_TABLINK_ACTIVE));
		assertTrue(tabs.get(0).isDisplayed());
		assertFalse(tabs.get(1).isDisplayed());

		buttonToTabB.get().click();
		wait_s(0.1);

		assertFalse(Browser.getClasses(buttonToTabA.get()).contains(TabbedFlow.HTML_CLASS_TABLINK_ACTIVE));
		assertTrue(Browser.getClasses(buttonToTabB.get()).contains(TabbedFlow.HTML_CLASS_TABLINK_ACTIVE));
		assertFalse(tabs.get(0).isDisplayed());
		assertTrue(tabs.get(1).isDisplayed());

		buttonToTabA.get().click();
		wait_s(0.1);

		assertTrue(Browser.getClasses(buttonToTabA.get()).contains(TabbedFlow.HTML_CLASS_TABLINK_ACTIVE));
		assertFalse(Browser.getClasses(buttonToTabB.get()).contains(TabbedFlow.HTML_CLASS_TABLINK_ACTIVE));
		assertTrue(tabs.get(0).isDisplayed());
		assertFalse(tabs.get(1).isDisplayed());
	}

}