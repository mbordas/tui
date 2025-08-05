/* Copyright (c) 2025, Mathieu Bordas
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

import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import tui.http.TUIBackend;
import tui.test.Browser;
import tui.test.TestWithBackend;
import tui.ui.components.Paragraph;
import tui.ui.components.RefreshButton;
import tui.utils.TestUtils;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class PanelTest extends TestWithBackend {

	@Test
	public void noHorizontalMargin() throws Exception {
		final TestUtils.UpdatablePage updatablePage = TestUtils.createPageWithUpdatablePanel();

		try(final TUIBackend backend = startBackend(updatablePage.page())) {
			try(final Browser browser = startBrowser()) {
				browser.open(updatablePage.page().getSource());

				setUpdatedPanel(updatablePage.panel().getSource(), Panel.Align.VERTICAL_TOP);
				checkMargin(browser, updatablePage.button(), "0px", "0px");

				setUpdatedPanel(updatablePage.panel().getSource(), Panel.Align.VERTICAL_CENTER);
				checkMargin(browser, updatablePage.button(), "0px", "0px");

				setUpdatedPanel(updatablePage.panel().getSource(), Panel.Align.LEFT);
				checkMargin(browser, updatablePage.button(), "20px", "0px");

				setUpdatedPanel(updatablePage.panel().getSource(), Panel.Align.CENTER);
				checkMargin(browser, updatablePage.button(), "20px", "0px");

				setUpdatedPanel(updatablePage.panel().getSource(), Panel.Align.STRETCH);
				checkMargin(browser, updatablePage.button(), "20px", "0px");

				setUpdatedPanel(updatablePage.panel().getSource(), Panel.Align.RIGHT);
				checkMargin(browser, updatablePage.button(), "20px", "0px");
			}
		}
	}

	private static void checkMargin(Browser browser, RefreshButton button,
			String span1MarginRight, String span2MarginRight) {
		browser.clickRefreshButton(button.getLabel());
		final List<WebElement> spans = getSpans(browser);
		assertEquals(span1MarginRight, spans.get(0).getCssValue("margin-right"));
		assertEquals(span2MarginRight, spans.get(1).getCssValue("margin-right"));
	}

	private void setUpdatedPanel(String panelSource, Panel.Align panelAlign) {
		registerWebService(panelSource, (uri, request, response) -> {
			final Panel result = new Panel(panelAlign);
			result.append(new Paragraph.Text("Text 1"));
			result.append(new Paragraph.Text("Text 2"));
			return result.toJsonMap();
		});
	}

	private static @NotNull List<WebElement> getSpans(Browser browser) {
		final List<WebElement> panels = browser.getPanels();
		assertEquals(1, panels.size());
		final WebElement panel = panels.iterator().next();
		return panel.findElements(By.tagName("span"));
	}

}