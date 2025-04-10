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
import org.junit.Test;
import tui.json.JsonMap;
import tui.json.JsonParser;
import tui.test.Browser;
import tui.test.TClient;
import tui.test.TestWithBackend;
import tui.ui.components.Page;

import static org.junit.Assert.assertEquals;

public class TPageTest extends TestWithBackend {

	@Test
	public void parseJson() {
		Page page = new Page("My page");
		page.appendSection("My section");
		final String json = page.toJsonMap().toJson();

		JsonMap jsonMap = JsonParser.parseMap(json);
		final TPage result = TPage.parse(jsonMap, null);

		assertEquals("My page", result.getTitle());
	}

	@Test
	public void browse() throws HttpException {
		final Page page = new Page("Home", "/index");
		startBackend(page);

		// TUI
		final TClient client = startClient();
		client.open("/index");
		assertEquals("Home", client.getTitle());

		// Web UI
		final Browser browser = startBrowser();
		browser.open("/index");
		assertEquals("Home", browser.getTitle());
	}
}