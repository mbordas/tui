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

import org.jetbrains.annotations.NotNull;
import tui.http.RequestReader;
import tui.http.TUIBackend;
import tui.test.Browser;
import tui.ui.components.Page;
import tui.ui.components.Paragraph;
import tui.ui.components.layout.Layouts;
import tui.ui.components.svg.graph.Axis;

import java.awt.*;
import java.time.LocalDateTime;

public class SVGResize {

	public static final String PATH_SVG = "/svg";

	public static void main(String[] args) throws Exception {
		final Page page = new Page("SVG auto-resize");
		page.setReadingWidth(Layouts.Width.WIDE); // will auto-resize
		page.setSource("/index");

		page.append(new Paragraph.Text("SVG without width auto-refresh:"));
		page.append(new SVG(200, 100))
				.add(new SVGRectangle(1, 1, 198, 98))
				.withNoFillColor();

		page.append(new Paragraph.Text("SVG with width auto-refresh:"));
		page.append(buildSVG(500, 200));

		try(TUIBackend backend = new TUIBackend()) {
			backend.registerPage(page);

			backend.registerWebService(PATH_SVG, (uri, request, response) -> {
				final RequestReader reader = new RequestReader(request);
				int width_px = (int) reader.getDoubleParameter("_width_px", 50);
				System.out.printf("%d x %d%n", width_px, 50);
				final SVG svg = buildSVG(width_px, 100);

				return svg.toJsonMap();
			});

			backend.start(8080);

			final Browser browser = new Browser(backend.getPort(), true);
			browser.open(page.getSource());
			browser.waitClosedManually();
		}
	}

	private static @NotNull SVG buildSVG(int width_px, int height_px) {
		final SVG svg = new SVG(width_px, height_px);
		svg.setSource(PATH_SVG);

		svg.add(new SVGRectangle(1, 1, width_px - 2, height_px - 2)).withNoFillColor();

		Axis.drawXAxis(svg, new Axis.TimeRange(LocalDateTime.of(2026, 7, 7, 8, 0),
						LocalDateTime.of(2026, 7, 7, 9, 0)),
				new SVGPoint(5, height_px - 20), width_px - 10, 20, Color.BLUE);

		svg.add(new SVGText(new SVGPoint(width_px / 2, 20),
				String.format("%d x %d", width_px, height_px),
				SVGText.Anchor.MIDDLE,
				SVGText.DominantBaseline.MIDDLE));

		//
		svg.refreshOnWidthResize(true);
		//

		return svg;
	}
}
