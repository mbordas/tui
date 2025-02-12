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
import org.openqa.selenium.WebElement;
import tui.test.Browser;
import tui.test.TestWithBackend;
import tui.ui.components.Page;
import tui.ui.components.Paragraph;
import tui.ui.components.RefreshButton;
import tui.utils.TestUtils;

import java.util.Map;

import static org.junit.Assert.assertEquals;

public class VerticalFlowTest extends TestWithBackend {

	@Test
	public void loadMAX() {
		final Page page = new Page("VerticalFlow load", "/index");
		final Panel panel = page.append(new Panel());
		panel.setSource("/panel");
		final RefreshButton refreshButton = page.append(new RefreshButton(("Refresh")));
		refreshButton.connectListener(panel);

		startBackend(page);
		registerWebService(panel.getSource(), (uri, request, response) -> {
			final Panel _panel = new Panel();
			final VerticalFlow verticalFlow = _panel.append(new VerticalFlow());
			verticalFlow.append(new Paragraph(TestUtils.LOREM_IPSUM));
			verticalFlow.setWidth(Layouts.Width.MAX);
			verticalFlow.setSpacing(Layouts.Spacing.LARGE);
			return _panel.toJsonMap();
		});

		final Browser browser = startBrowser();
		browser.open(page.getSource());

		browser.clickRefreshButton(refreshButton.getLabel());
		wait_s(0.1);

		final WebElement verticalFlow = browser.getVerticalFlows().get(0);
		final Map<String, String> styleProperties = Browser.parseStyleProperties(verticalFlow);

		assertEquals("auto", styleProperties.get("grid-template-rows"));
		assertEquals("minmax(20px, 1fr) minmax(65em, 1fr) minmax(20px, 1fr)", styleProperties.get("grid-template-columns"));
		assertEquals("center", styleProperties.get("place-items"));
	}

}