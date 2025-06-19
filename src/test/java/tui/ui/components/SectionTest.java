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

package tui.ui.components;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import tui.test.Browser;
import tui.test.TestWithBackend;
import tui.test.components.TSectionTest;
import tui.ui.components.layout.Panel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class SectionTest extends TestWithBackend {

	record Context(Page page, RefreshButton button, String panelSource) {
	}

	/**
	 * The default page contains a panel which is refreshed by clicking the refresh button.
	 */
	private Context withDefaultPage() {
		final Page page = new Page("SectionTest", "/index");
		final Panel panel = page.append(new Panel());
		panel.setAlign(Panel.Align.VERTICAL_TOP);
		panel.append(new Section("Section 1"))
				.appendParagraph("Paragraph in section 1");
		panel.setSource("/panel");

		final RefreshButton button = page.append(new RefreshButton("Refresh panel with sections"));
		button.connectListener(panel);

		return new Context(page, button, panel.getSource());
	}

	/**
	 * We build a page with one panel and refresh it with new content: 2 sections, each with one paragraph.
	 */
	@Test
	public void refresh() throws InterruptedException {
		final Context context = withDefaultPage();

		registerWebService(context.panelSource, (uri, request, response) -> {
			Panel panel1 = new Panel().setAlign(Panel.Align.VERTICAL_TOP);
			panel1.append(new Section("Section 1 updated"))
					.appendParagraph("Paragraph updated in section 1.");
			panel1.append(new Section("Section 2 updated"))
					.appendParagraph("Paragraph updated in section 2.");
			return panel1.toJsonMap();
		});

		startBackend(context.page);

		final Browser browser = super.startBrowser();
		browser.open(context.page.getSource());

		browser.clickRefreshButton(context.button.getLabel());

		final WebElement section1 = browser.getSection("Section 1 updated");
		assertNotNull(section1);
		assertFalse(section1.findElements(By.tagName("p")).isEmpty());

		final WebElement section2 = browser.getSection("Section 2 updated");
		assertNotNull(section2);
		assertFalse(section2.findElements(By.tagName("p")).isEmpty());
	}

	@Test
	public void depthOnUpdate() {
		final Context context = withDefaultPage();

		registerWebService(context.panelSource, (uri, request, response) -> {
			final Panel panel = new Panel().setAlign(Panel.Align.VERTICAL_TOP);
			panel.append(new Section("Section 1"))
					.appendParagraph("Paragraph updated in section 1");
			final Section section2 = panel.append(new Section("Section 2"));
			section2.appendParagraph("Paragraph updated in section 2");
			section2.createSubSection("Section 2.1")
					.appendParagraph("Paragraph updated in section 2.1");
			return panel.toJsonMap();
		});

		startBackend(context.page);

		final Browser browser = super.startBrowser();
		browser.open(context.page.getSource());

		browser.clickRefreshButton(context.button.getLabel());

		assertEquals(1, TSectionTest.getDepth(browser.getSection("Section 1")));
		assertEquals(1, TSectionTest.getDepth(browser.getSection("Section 2")));
		assertEquals(2, TSectionTest.getDepth(browser.getSection("Section 2.1")));
	}

}