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

package tui.test.components;

import org.apache.http.HttpException;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tui.json.JsonArray;
import tui.json.JsonMap;
import tui.json.JsonObject;
import tui.test.TClient;
import tui.test.TestWithBackend;
import tui.ui.components.Panel;
import tui.ui.components.Section;
import tui.ui.components.TabbedPage;

import static org.junit.Assert.assertEquals;

public class TabbedPageTest extends TestWithBackend {

	private static final Logger LOG = LoggerFactory.getLogger(TabbedPageTest.class);

	private TabbedPage m_page;

	@Before
	public void before() {
		m_page = new TabbedPage("Home");
		final Panel tab1 = m_page.createTab("First tab");
		tab1.createSection("Section 1-1");
		final Panel tab2 = m_page.createTab("Second tab");
		tab2.createSection("Section 2-1");
	}

	@Test
	public void json() {
		final JsonMap jsonMap = m_page.toJsonMap();
		JsonObject.PRETTY_PRINT = true;
		LOG.debug(jsonMap.toJson());

		assertEquals(TabbedPage.JSON_TYPE, jsonMap.getType());
		assertEquals("Home", jsonMap.getAttribute("title"));

		final JsonArray content = jsonMap.getArray("content");

		{
			final JsonMap tab1 = content.getMap(0);

			assertEquals(TabbedPage.TABBED_PANEL_JSON_TYPE, tab1.getType());
			assertEquals("First tab", tab1.getAttribute("title"));
			final JsonArray tab1Array = tab1.getArray("content");
			final JsonMap section1 = tab1Array.getMap(0);
			assertEquals(Section.JSON_TYPE, section1.getType());
			assertEquals("Section 1-1", section1.getAttribute("title"));
		}
	}

	@Test
	public void client() throws HttpException {
		startBackend("/index", m_page);

		// TUI
		final TClient client = startClient();
		client.open("/index");
		assertEquals("Home", client.getTitle());
		assertEquals("First tab", client.getTabTitle());
	}

	@Test
	public void browse() {

	}

}
