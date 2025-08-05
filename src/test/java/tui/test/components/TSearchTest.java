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

package tui.test.components;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import tui.http.TUIBackend;
import tui.test.TClient;
import tui.test.TestWithBackend;
import tui.test.WebServiceSpy;
import tui.ui.components.Page;
import tui.ui.components.form.Search;
import tui.ui.components.layout.Panel;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;

import static org.junit.Assert.assertEquals;

public class TSearchTest extends TestWithBackend {

	@Test
	public void initialValue() throws Exception {
		final LocalDateTime initialValue = LocalDateTime.of(2025, Month.AUGUST, 5, 12, 1);

		final Page page = new Page("TSearch - initialValue", "/page");
		final Search search = page.append(new Search("search", "submit"));
		final Panel panel = page.append(new Panel());
		panel.setSource("/panel");
		search.connectListener(panel);

		search.createInputDayHHmm("Day", "day")
				.setInitialValue(initialValue);

		try(final TUIBackend backend = startBackend(page)) {
			final WebServiceSpy webServiceSpy = registerWebServiceSpy(panel);

			final TClient client = new TClient(backend.getPort());
			client.open(page.getSource());

			final TSearch tSearch = client.getSearch(search.getTitle());
			tSearch.submit();

			final Date sentDate = webServiceSpy.getRequestReader().getDateParameter("day", Locale.getDefault());
			assertEquals(
					// initialValue is built with default locale which is UTC+2 in France
					initialValue.toInstant(ZoneOffset.ofHours(+2)),
					sentDate.toInstant());
		}
	}

	public static String getTitle(WebElement formElement) {
		final WebElement label = formElement.findElement(By.tagName("label"));
		return label.getText();
	}

	public static Collection<WebElement> getFields(WebElement searchElement) {
		return searchElement.findElements(By.tagName("input"));
	}
}
