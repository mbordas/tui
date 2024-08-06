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

package tui.ui.components.svg;

import org.junit.Test;
import tui.html.HTMLNode;
import tui.http.FormRequest;
import tui.http.TUIBackend;
import tui.test.Browser;
import tui.ui.UI;
import tui.ui.components.Page;
import tui.ui.components.form.Form;

import java.awt.*;

import static org.junit.Assert.assertEquals;

public class SVGTest {

	@Test
	public void oneRectangle() {
		final SVG svg = new SVG(20, 20);
		svg.add(new SVGRectangle(5, 10, 15, 20));

		HTMLNode.PRETTY_PRINT = true;
		assertEquals("""
				<svg>
				  <rect x="5" y="10" width="15" height="20" rx="0" ry="0" style="stroke:#000000;stroke-width:1;stroke-opacity:1.00;fill:#000000;fill-opacity:1.00;"/>
				</svg>
				""", svg.toHTMLNode().toHTML());
	}

	public static void main(String[] args) throws Exception {
		final UI ui = new UI();
		final Page page = new Page("Home");

		final Form form = new Form("New rectangle", "/addRectangle");
		form.createInputNumber("x", "x");
		form.createInputNumber("y", "y");
		form.createInputNumber("width", "width");
		form.createInputNumber("height", "height");

		final SVG svg = new SVG(200, 200);
		svg.setSource("/getSVG");
		form.registerRefreshListener(svg);
		page.append(form);

		svg.add(new SVGRectangle(5, 5, 150, 150)
				.withCornerRadius(10, 20)
				.withStrokeColor(Color.RED)
				.withStrokeWidth(3)
				.withStrokeDashArray(5, 2)
				.withFillColor(Color.RED));

		svg.add(new SVGRectangle(30, 30, 150, 150)
				.withStrokeColor(Color.BLACK)
				.withStrokeOpacity(0.5)
				.withStrokeWidth(3)
				.withStrokeDashArray(5, 2)
				.withFillColor(Color.BLUE)
				.withFillOpacity(0.5));
		page.append(svg);

		ui.add("/index", page);
		ui.setHTTPBackend("localhost", 8080);

		final TUIBackend backend = new TUIBackend(ui);

		backend.registerWebService(form.getTarget(), (uri, request, response) -> {
			final int x = FormRequest.getIntegerField(request, "x");
			final int y = FormRequest.getIntegerField(request, "y");
			final int width = FormRequest.getIntegerField(request, "width");
			final int height = FormRequest.getIntegerField(request, "height");
			svg.add(new SVGRectangle(x, y, width, height).withFillColor(Color.ORANGE).withFillOpacity(0.5));
			return Form.getSuccessfulSubmissionResponse();
		});

		backend.registerWebService(svg.getSource(), (uri, request, response) -> svg.toJsonMap());

		backend.start();

		final Browser browser = new Browser(backend.getPort());
		browser.open("/index");
	}

}