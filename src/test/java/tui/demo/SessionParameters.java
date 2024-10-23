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

package tui.demo;

import tui.http.RequestReader;
import tui.http.TUIBackend;
import tui.test.Browser;
import tui.ui.components.Page;
import tui.ui.components.Paragraph;
import tui.ui.components.RefreshButton;

public class SessionParameters {

	public static void main(String[] args) throws Exception {
		final Page page = new Page("Session parameters", "/index");
		page.setSessionParameter("sessionId", "testsessionid");
		final RefreshButton refreshButton = page.append(new RefreshButton("Refresh"));
		final Paragraph paragraph = page.append(new Paragraph());
		paragraph.setSource("/paragraph");
		refreshButton.connectListener(paragraph);

		final TUIBackend backend = new TUIBackend(8080);
		backend.registerPage(page);

		backend.registerWebService(paragraph.getSource(), (uri, request, response) -> {
			final RequestReader reader = new RequestReader(request);
			final String sessionId = reader.getStringParameter("sessionId");
			final Paragraph result = new Paragraph();
			result.appendNormal("Session id sent to backend is: " + sessionId);
			return result.toJsonMap();
		});

		backend.start();
		final Browser browser = new Browser(backend.getPort());
		browser.open(page.getSource());
	}
}
