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
import org.openqa.selenium.WebElement;
import tui.http.RequestReader;
import tui.test.Browser;
import tui.test.TestWithBackend;
import tui.test.WebServiceSpy;
import tui.ui.components.layout.Panel;
import tui.utils.TUIColors;

import java.awt.*;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;

public class RefreshButtonTest extends TestWithBackend {

	@Test
	public void callBackendWithParameters() {
		final Page page = new Page("RefreshButtonTest", "/index");

		final Paragraph paragraph = page.append(new Paragraph("to be refreshed"));
		paragraph.setSource("/paragraph");
		final RefreshButton button = page.append(new RefreshButton("refresh"));
		button.connectListener(paragraph);
		button.setParameter("param 1", "value 1");
		button.setParameter("param 2", "value 2");

		startBackend(page);
		final WebServiceSpy webServiceSpy = registerWebServiceSpy(paragraph);

		final Browser browser = startBrowser();
		browser.open(page.getSource());
		browser.clickRefreshButton(button.getLabel());

		assertEquals("value 1", webServiceSpy.getRequestReader().getStringParameter("param 1"));
		assertEquals("value 2", webServiceSpy.getRequestReader().getStringParameter("param 2"));
	}

	@Test
	public void createComponentWithStyle() throws InterruptedException {
		final Page page = new Page("RefreshButtonTest", "/index");
		final Panel panel = page.append(new Panel());
		panel.setSource("/panel");

		final RefreshButton button = panel.append(new RefreshButton("refresh"));
		button.connectListener(panel);

		startBackend(page);

		registerWebService(panel.getSource(), (uri, request, response) -> {
			final Panel refreshedPanel = new Panel();
			refreshedPanel.setSource(panel.getSource());
			final RefreshButton refreshedButton = refreshedPanel.append(new RefreshButton("refreshed"));
			refreshedButton.customStyle().setBackgroundColor(Color.RED);
			refreshedButton.customTextStyle().setTextColor(Color.ORANGE);
			refreshedButton.connectListener(refreshedPanel);
			return refreshedPanel.toJsonMap();
		});

		final Browser browser = startBrowser();
		browser.open(page.getSource());
		browser.clickRefreshButton("refresh");

		final WebElement buttonElement = browser.getRefreshButton("refreshed");
		assertEquals(TUIColors.toCSSRGBAsSelenium(Color.ORANGE), buttonElement.getCssValue("color"));
		assertEquals(TUIColors.toCSSRGBAsSelenium(Color.RED), buttonElement.getCssValue("background-color"));
	}

	/**
	 * Here we check that the Javascript correctly a RefreshButton with parameters as set in the Json given by the backend.
	 */
	@Test
	public void createComponentWithParameters() {
		final Page page = new Page("RefreshButtonTest", "/index");
		final Panel panel = page.append(new Panel());
		panel.setSource("/panel");

		// Initially the button has no parameter
		final RefreshButton button = panel.append(new RefreshButton("refresh"));
		button.connectListener(panel);

		startBackend(page);

		final AtomicReference<RequestReader> reader = new AtomicReference<>();
		registerWebService(panel.getSource(), (uri, request, response) -> {
			reader.set(new RequestReader(request));
			final Panel refreshedPanel = new Panel();
			refreshedPanel.setSource(panel.getSource());
			final RefreshButton refreshedButton = refreshedPanel.append(new RefreshButton("refreshed"));
			refreshedButton.setParameter("param 1", "value 1");
			refreshedButton.setParameter("param 2", "value 2");
			refreshedButton.connectListener(refreshedPanel);
			return refreshedPanel.toJsonMap();
		});

		final Browser browser = startBrowser();
		browser.open(page.getSource());

		browser.clickRefreshButton("refresh");

		browser.clickRefreshButton("refreshed");
		assertEquals("value 1", reader.get().getStringParameter("param 1"));
		assertEquals("value 2", reader.get().getStringParameter("param 2"));
	}
}
