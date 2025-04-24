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
import tui.http.TUIBackend;
import tui.test.Browser;
import tui.ui.components.Page;
import tui.ui.components.Paragraph;
import tui.ui.components.form.Search;

import static org.junit.Assert.assertEquals;

public class SpecialCharactersTest {

	@Test
	public void test() throws Exception {
		try(final TUIBackend backend = new TUIBackend(8080)) {

			final Page page = new Page("title", "/index");
			page.setFetchType(Page.FetchType.FORM_DATA);
			final String searchParameterName = "searched";
			final Search search = page.append(new Search("Text", "Search", searchParameterName));
			final Paragraph paragraph = page.append(buildParagraph(null));
			search.connectListener(paragraph);

			backend.registerPage(page);
			backend.registerWebService(paragraph.getSource(),
					(uri, request, response) -> {
						final RequestReader reader = new RequestReader(request);
						final String searched = reader.getStringParameter(searchParameterName);
						return buildParagraph(searched).toJsonMap();
					});

			backend.start();

			try(final Browser browser = new Browser(backend.getPort())) {
				browser.open(page.getSource());

				browser.typeSearch(search.getTitle(), searchParameterName, "éèàâîë");
				browser.submitSearch(search.getTitle());

				final WebElement paragraphElement = browser.getParagraphs().stream()
						.filter((p) -> p.getText().length() > 5)
						.findAny()
						.get();

				assertEquals("Special characters are: éèêëîà. You are searching for éèàâîë", paragraphElement.getText());
			}
		}
	}

	private Paragraph buildParagraph(String searched) {
		final Paragraph result = new Paragraph();
		result.appendNormal("Special characters are: ").appendBold("éèêëîà").appendNormal(".");
		if(searched != null) {
			result.appendNormal(" You are searching for ").appendBold(searched);
		} else {
			result.appendNormal(" Nothing searched yet.");
		}

		result.setSource("/paragraph");
		return result;
	}
}
