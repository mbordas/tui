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

package tui.ui.components.layout;

import tui.http.TUIBackend;
import tui.test.Browser;
import tui.test.TestWithBackend;
import tui.ui.UI;
import tui.ui.components.Page;
import tui.ui.components.Paragraph;

public class GridTest extends TestWithBackend {

	public static void main(String[] args) throws Exception {
		final UI ui = new UI();
		final Page page = new Page("Home");

		final Grid grid = new Grid(5, 2);

		//		for(int row = 0; row < 5; row++) {
		//			for(int col = 0; col < 2; col++) {
		//				grid.set(row, col, new Paragraph(String.format("%d,%d", row, col)));
		//			}
		//		}

		grid.set(1, 1, new Paragraph("1,1").setAlign(Paragraph.TextAlign.LEFT));
		grid.set(4, 0, new Paragraph("4,0").setAlign(Paragraph.TextAlign.RIGHT));
		page.append(grid);
		ui.add("/index", page);
		ui.setHTTPBackend("localhost", 8080);

		final TUIBackend backend = new TUIBackend(ui);
		backend.start();

		final Browser browser = new Browser(backend.getPort());
		browser.open("/index");
	}
}