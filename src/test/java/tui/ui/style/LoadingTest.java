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

package tui.ui.style;

import org.junit.Test;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tui.http.TUIBackend;
import tui.test.Browser;
import tui.test.TestWithBackend;
import tui.ui.components.Paragraph;
import tui.ui.components.layout.Panel;
import tui.utils.TestUtils;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LoadingTest extends TestWithBackend {

	private static final Logger LOG = LoggerFactory.getLogger(LoadingTest.class);

	private static final long delayForLoadingToBeDisplayed_ms = 300;
	private static final long delayForBackendToRespond_ms = 600;

	@Test
	public void cssClassLoadingIsSetDuringBackendCall() throws Exception {
		final TestUtils.UpdatablePage updatablePage = TestUtils.createPageWithUpdatablePanel();
		updatablePage.panel().append(new Paragraph.Text("Initial text"));

		try(final TUIBackend backend = startBackend(updatablePage.page())) {
			registerWebService(updatablePage.panel().getSource(), (uri, request, response) -> {
				try {
					Thread.sleep(delayForBackendToRespond_ms);
				} catch(InterruptedException e) {
					throw new RuntimeException(e);
				}
				final Panel result = new Panel();
				result.append(new Paragraph.Text("Refreshed text"));
				return result.toJsonMap();
			});

			try(final Browser browser = startBrowser()) {
				browser.open(updatablePage.page().getSource());
				browser.clickRefreshButton(updatablePage.button().getLabel());
				Thread.sleep(delayForLoadingToBeDisplayed_ms);
				assertTrue(getClasses(browser.getPanels().get(0)).contains("loading"));
				Thread.sleep(delayForBackendToRespond_ms);
				assertFalse(getClasses(browser.getPanels().get(0)).contains("loading"));
			}
		}
	}

	@Test
	public void cssClassLoadingIsSetDuringBackendCallOnError() throws Exception {
		final TestUtils.UpdatablePage updatablePage = TestUtils.createPageWithUpdatablePanel();
		updatablePage.panel().append(new Paragraph.Text("Initial text"));

		try(final TUIBackend backend = startBackend(updatablePage.page())) {
			registerWebService(updatablePage.panel().getSource(), (uri, request, response) -> {
				try {
					Thread.sleep(delayForBackendToRespond_ms);
				} catch(InterruptedException e) {
					throw new RuntimeException(e);
				}
				throw new RuntimeException("Backend error");
			});

			try(final Browser browser = startBrowser()) {
				browser.open(updatablePage.page().getSource());
				browser.clickRefreshButton(updatablePage.button().getLabel());
				Thread.sleep(delayForLoadingToBeDisplayed_ms);
				assertTrue(getClasses(browser.getPanels().get(0)).contains("loading"));
				Thread.sleep(delayForBackendToRespond_ms);
				assertFalse(getClasses(browser.getPanels().get(0)).contains("loading"));
			}
		}
	}

	private static List<String> getClasses(WebElement element) {
		final String classes = element.getAttribute("class");
		LOG.debug("Element classes: {}", classes);
		return Arrays.asList(classes.split(" "));
	}

}
