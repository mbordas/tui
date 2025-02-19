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

package tui.ui;

import org.junit.Test;
import org.openqa.selenium.WebElement;
import tui.http.RequestReader;
import tui.test.TestWithBackend;
import tui.ui.components.Page;
import tui.ui.components.Paragraph;
import tui.ui.components.form.Search;

import java.util.Optional;

import static org.junit.Assert.assertEquals;

public class FetchDataTest extends TestWithBackend {

	@Test
	public void cumulativeParameters() {
		final Page page = new Page("Cumulative parameters", "/cumulativeParameters");
		final Search search1 = page.append(new Search("Filter 1", "Apply", "search1"));
		final Search search2 = page.append(new Search("Filter 2", "Apply", "search2"));

		final Paragraph paragraph = page.append(new Paragraph("Response: "));
		paragraph.setSource("/paragraph");
		search1.connectListener(paragraph);
		search2.connectListener(paragraph);

		registerWebService(paragraph.getSource(), (uri, request, response) -> {
			final RequestReader reader = new RequestReader(request);
			final String value1 = reader.getStringParameter("search1", null);
			final String value2 = reader.getStringParameter("search2", null);
			return new Paragraph("Response: 1=%s / 2=%s", value1, value2).toJsonMap();
		});
		startAndBrowse(page);

		// Submitting first filter. Only the first parameter should be read by the backend
		m_browser.typeSearch(search1.getTitle(), "search1", "my value 1");
		m_browser.submitSearch(search1.getTitle());
		assertEquals("Response: 1=my value 1 / 2=null", getParagraphText());

		// Submitting the second filter. Now both parameters should be read by the backend.
		m_browser.typeSearch(search2.getTitle(), "search2", "my value 2");
		m_browser.submitSearch(search2.getTitle());
		assertEquals("Response: 1=my value 1 / 2=my value 2", getParagraphText());
	}

	private String getParagraphText() {
		final Optional<WebElement> anyParagraphElement = m_browser.getParagraphs().stream().filter(
				(element) -> element.getText().startsWith("Response: ")).findAny();
		assert anyParagraphElement.isPresent();
		return anyParagraphElement.get().getText();
	}
}
