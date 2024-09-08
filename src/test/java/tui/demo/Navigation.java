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

import tui.http.TUIBackend;
import tui.test.Browser;
import tui.ui.UI;
import tui.ui.components.NavLink;
import tui.ui.components.Page;
import tui.ui.components.layout.Grid;

import java.util.List;

public class Navigation {

	static void computeHeader(Page home, List<Page> pages) {

		final Grid header = new Grid(1, 3 + pages.size());

		header.set(0, 0, new NavLink(home.getTitle(), home.getSource()));
		int colIndex = 2;
		home.setHeader(header);
		for(Page page : pages) {
			header.set(0, colIndex++, new NavLink(page.getTitle(), page.getSource()));
			page.setHeader(header);
		}
	}

	public static void main(String[] args) throws Exception {
		final UI ui = new UI();
		ui.setHTTPBackend("localhost", 8080);

		final Page pageHome = new Page("Home", "/index");
		final Page page1 = new Page("Page 1", "/page/1");
		final Page page2 = new Page("Page 2", "/page/2");
		final Page page3 = new Page("Page 3", "/page/3");

		computeHeader(pageHome, List.of(page1, page2, page3));

		ui.add(pageHome);
		ui.add(page1);
		ui.add(page2);
		ui.add(page3);

		final TUIBackend backend = new TUIBackend(ui);
		backend.start();

		final Browser browser = new Browser(backend.getPort());
		browser.open("/index");
	}
}
