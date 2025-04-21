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

package tui.ui.components.form;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tui.test.Browser;
import tui.test.TestWithBackend;
import tui.test.WebServiceSpy;
import tui.ui.components.Page;
import tui.ui.components.Paragraph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SearchTest extends TestWithBackend {

	private static final Logger LOG = LoggerFactory.getLogger(SearchTest.class);

	record Item(long serialNumber, String manufacturer, String code, String label, double price_Euro) {
	}

	/**
	 * Here the user enters 'value1' in search single input then submit. The test checks that the paragraphs refreshed with
	 * the entered value.
	 */
	@Test
	public void refreshWithOneInput() {
		final Page page = new Page("Search", "/search");
		final Search search = page.append(new Search("refreshWithOneInput", "One input", "oneInput"));
		final Paragraph paragraphToBeRefreshed = page.append(new Paragraph());
		paragraphToBeRefreshed.setSource("/paragraph");
		search.connectListener(paragraphToBeRefreshed);

		startBackend(page);
		final WebServiceSpy webServiceSpy = registerWebServiceSpy(paragraphToBeRefreshed);

		final Browser browser = startBrowser();
		browser.open(page.getSource());
		browser.typeSearch(search.getTitle(), "oneInput", "value1");
		browser.submitSearch(search.getTitle());

		assertNotNull(webServiceSpy.getRequestReader());
		assertEquals("value1", webServiceSpy.getRequestReader().getStringParameter("oneInput"));
	}

	@Test
	public void refreshWithAdditionalInputs() {
		final Page page = new Page("Search", "/search");
		final Search search = page.append(new Search("refreshWithAdditionalInputs", "One input", "firstInput"));
		search.createInputDayHHmm("Date", "secondInput");
		final Paragraph paragraphToBeRefreshed = page.append(new Paragraph());
		paragraphToBeRefreshed.setSource("/paragraph");
		search.connectListener(paragraphToBeRefreshed);

		startBackend(page);
		final WebServiceSpy webServiceSpy = registerWebServiceSpy(paragraphToBeRefreshed);

		final Browser browser = startBrowser();
		browser.open(page.getSource());
		browser.typeSearch(search.getTitle(), "firstInput", "value1");
		browser.typeSearch(search.getTitle(), "secondInput", "18/02/2025 22:37");
		browser.submitSearch(search.getTitle());

		assertNotNull(webServiceSpy.getRequestReader());
		assertEquals("value1", webServiceSpy.getRequestReader().getStringParameter("firstInput"));
		assertEquals("2025-02-18T22:37", webServiceSpy.getRequestReader().getStringParameter("secondInput"));
	}

	@Test
	public void supportForRadioButton() {
		final Page page = new Page("Search", "/search");
		final Search search = page.append(new Search("supportForRadioButton", "One input", "firstInput"));
		search.createInputRadio("Radio", "secondInput")
				.addOption("Option 1", "one")
				.addOption("Option 2", "two")
				.addOption("Option 3", "three");
		final Paragraph paragraphToBeRefreshed = page.append(new Paragraph());
		paragraphToBeRefreshed.setSource("/paragraph");
		search.connectListener(paragraphToBeRefreshed);

		startBackend(page);
		final WebServiceSpy webServiceSpy = registerWebServiceSpy(paragraphToBeRefreshed);

		final Browser browser = startBrowser();
		browser.open(page.getSource());
		browser.typeSearch(search.getTitle(), "firstInput", "value1");
		browser.selectSearchRadio(search.getTitle(), "secondInput", "three");
		browser.submitSearch(search.getTitle());

		assertNotNull(webServiceSpy.getRequestReader());
		assertEquals("value1", webServiceSpy.getRequestReader().getStringParameter("firstInput"));
		assertEquals("three", webServiceSpy.getRequestReader().getStringParameter("secondInput"));
	}

	@Ignore
	@Test
	public void displayInColumns() throws InterruptedException {
		final Page page = new Page("Search", "/search");
		final Search search = page.append(new Search("supportForRadioButton", "One input", "firstInput"));
		search.createInputRadio("Radio", "secondInput")
				.addOption("Option 1", "one")
				.addOption("Option 2", "two")
				.addOption("Option 3", "three");
		search.createInputDayHHmm("Time", "time");
		final Paragraph paragraphToBeRefreshed = page.append(new Paragraph());
		paragraphToBeRefreshed.setSource("/paragraph");
		search.connectListener(paragraphToBeRefreshed);

		search.displayLikeForm(3);

		startBackend(page);

		final Browser browser = startBrowser();
		browser.open(page.getSource());
		Thread.sleep(60_000);
	}

}