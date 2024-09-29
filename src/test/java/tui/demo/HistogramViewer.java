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
import tui.ui.components.Page;
import tui.ui.components.Paragraph;
import tui.ui.components.layout.Layouts;
import tui.ui.components.svg.SVG;
import tui.ui.components.svg.SVGRectangle;
import tui.utils.TestUtils;

import java.awt.*;

public class HistogramViewer {

	private static SVG buildSVG(int width_px, int height_px) {
		final SVG result = new SVG(width_px, height_px);

		result.add(new SVGRectangle(0, 0, width_px, height_px).withStrokeColor(Color.BLACK));

		return result;
	}

	public static void main(String[] args) throws Exception {
		final Page page = new Page("Histogram", "/index");
		page.setReadingWidth(Layouts.Width.NORMAL);

		page.append(buildSVG(400, 300));

		page.append(new Paragraph(TestUtils.LOREM_IPSUM).setAlign(Layouts.TextAlign.LEFT));

		final UI ui = new UI();
		ui.setHTTPBackend("http://localhost", 8080);
		ui.add(page);
		final TUIBackend backend = new TUIBackend(ui);
		backend.start();

		final Browser browser = new Browser(backend.getPort());
		browser.open(page.getSource());
	}
}
