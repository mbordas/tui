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

package tui.http;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.openqa.selenium.WebElement;
import tui.test.Browser;
import tui.test.TClient;
import tui.test.components.TComponent;
import tui.test.components.TParagraph;
import tui.ui.components.Page;
import tui.ui.components.Paragraph;
import tui.ui.components.RefreshButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class RequestReaderTest {

	@Test
	public void dayToInputString() throws ParseException {
		final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE);
		final Date date = formatter.parse("05/08/2025");

		assertEquals("2025-08-05", RequestReader.toInputString(date, Locale.FRANCE));
	}

	/**
	 * Arrange: a paragraph is connected to a refresh button, the page has fetch type set to 'FormData'.
	 * Act: click on the button in order to update the text of the paragraph
	 * Assert: the paragraph is well updated. Its new text uses the parameter set on the button and the parameter set in the page.
	 */
	@Test
	public void fetchTypeFormData() throws Exception {
		try(TUIBackend backend = new TUIBackend(8080)) {

			// Arrange
			final Page page = createPageWithRefreshableParagraph("/paragraph");
			page.setFetchType(Page.FetchType.FORM_DATA); // What we want to test
			backend.registerPage(page);
			backend.registerWebService("/paragraph", (uri, request, response) -> {
				final RequestReader reader = new RequestReader(request);
				final String code = reader.getStringParameter("code");
				final String session = reader.getStringParameter("session");
				return new Paragraph("text updated with code=%s and session=%s", code, session).toJsonMap();
			});
			backend.start();

			// Act with TClient
			final TClient client = new TClient(backend.getPort());
			client.open(page.getSource());
			client.getRefreshButton("Refresh").click();

			// Assert with TClient
			assertEquals("text updated with code=FormData and session=session", getParagraph(client).getText());

			try(final Browser browser = new Browser(backend.getPort())) {
				// Act with real browser
				browser.open(page.getSource());
				browser.clickRefreshButton("Refresh");
				final List<WebElement> paragraphs = browser.getParagraphs().stream()
						.filter((paragraph) -> paragraph.getText().length() > 5)
						.toList();

				// Assert with real browser
				assertFalse(paragraphs.isEmpty());
				assertEquals("text updated with code=FormData and session=session", paragraphs.get(0).getText());
			}
		}
	}

	private static @NotNull TParagraph getParagraph(TClient client) {
		final Optional<TComponent> anyParagraph = client.getReachableSubComponents().stream()
				.filter((component) -> component instanceof TParagraph)
				.findAny();
		assertTrue(anyParagraph.isPresent());
		return (TParagraph) anyParagraph.get();
	}

	private static @NotNull Page createPageWithRefreshableParagraph(String paragraphSource) {
		final Page page = new Page("Fetch with type FormData", "/index");
		page.setSessionParameter("session", "session");
		final Paragraph paragraph = page.append(new Paragraph("initial text"));
		paragraph.setSource(paragraphSource);
		final RefreshButton button = page.append(new RefreshButton("Refresh"));
		button.setParameter("code", "FormData");
		button.connectListener(paragraph);
		return page;
	}

	@Test
	public void parsePostMap() {
		final String json = "[[\"Id\",\"002\"],[\"Name\",\"Item-2\"]]";

		//
		final Map<String, String> map = RequestReader.parsePostMap(json);
		//

		assertNotNull(map);
		assertEquals(2, map.size());
		assertEquals("002", map.get("Id"));
		assertEquals("Item-2", map.get("Name"));
	}

	@Test
	public void parseDate() throws ParseException {
		final Date dateWithMinutes = RequestReader.parseDate("2025-03-13T18:11", Locale.FRANCE);
		System.out.println(dateWithMinutes.toString());
		assertEquals(1741885860000L, dateWithMinutes.getTime()); // as French local date

		final Date dateWithSeconds = RequestReader.parseDate("2025-03-13T18:11:35", Locale.FRANCE);
		System.out.println(dateWithSeconds.toString());
		assertEquals(1741885860000L + 35000L, dateWithSeconds.getTime()); // as French local date
	}

}