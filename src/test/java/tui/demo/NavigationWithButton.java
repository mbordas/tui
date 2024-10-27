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
import tui.ui.components.NavButton;
import tui.ui.components.Page;
import tui.ui.components.Section;
import tui.ui.components.layout.Layouts;
import tui.ui.components.layout.Panel;

public class NavigationWithButton {

	public static void main(String[] args) throws Exception {
		final Page page = new Page("Navigation with button", "/index");

		final Section summary = page.createSection("How it works");
		summary.createParagraph("The following buttons will reopen this same page. Each of them sends "
				+ "a parameter named 'direction' to the backend when asking for the page.");
		summary.createParagraph("At the backend side, each time the page is requested, the direction is read from"
				+ "parameters and a new paragraph is added with the selected direction.");

		final Section section = page.createSection("Selected directions:");

		final Panel buttons = section.append(new Panel());
		buttons.setAlign(Layouts.TextAlign.CENTER);
		buttons.append(new NavButton("< Previous", page.getSource())
				.setParameter("direction", "previous"));
		buttons.append(new NavButton("Next >", page.getSource())
				.setParameter("direction", "next"));

		final TUIBackend backend = new TUIBackend(8080);
		backend.registerPage(page);

		backend.registerPageService(page.getSource(), (uri, request) -> {
			final RequestReader reader = new RequestReader(request);
			final String direction = reader.getStringParameter("direction");
			if(direction != null) {
				section.createParagraph("selected: " + direction);
			}
			return page;
		});

		backend.start();
		final Browser browser = new Browser(backend.getPort());
		browser.open(page.getSource());
	}
}
