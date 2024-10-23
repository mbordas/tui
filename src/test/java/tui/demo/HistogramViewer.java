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
import tui.ui.components.Page;
import tui.ui.components.Paragraph;
import tui.ui.components.layout.Layouts;
import tui.ui.components.svg.CoordinatesComputer;
import tui.ui.components.svg.SVG;
import tui.ui.components.svg.SVGPath;
import tui.ui.components.svg.SVGText;
import tui.ui.components.svg.defs.SVGMarker;
import tui.utils.TestUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class HistogramViewer {

	private static SVG buildSVG(int width_px, int height_px, List<Double> values, int padding_px) {
		assert !values.isEmpty();
		final SVG result = new SVG(width_px, height_px);

		final CoordinatesComputer.Range range_x = new CoordinatesComputer.Range(0.0, 1.0 * values.size());
		final CoordinatesComputer.Range range_y = CoordinatesComputer.getRange(values);
		final CoordinatesComputer computer = new CoordinatesComputer(width_px, height_px, padding_px, range_x, range_y);

		final SVGMarker arrow = new SVGMarker("arrow", 10, 8)
				.withRef(0, 4);
		arrow.add(new SVGPath(0, 1)
				.lineAbsolute(9, 4)
				.lineAbsolute(0, 7)
				.lineAbsolute(0, 1)
				.withStrokeWidth(1)
				.withFillColor(Color.BLACK));
		result.addMarker(arrow);

		final SVGPath xAxis = new SVGPath(0, computer.getY_px(0.0))
				.lineAbsolute(width_px - padding_px / 2, computer.getY_px(0.0));
		xAxis.withStrokeColor(Color.BLACK).withFillOpacity(0.0);
		xAxis.withMarkerAtEnd(arrow);
		final SVGPath yAxis = new SVGPath(computer.getX_px(0.0), height_px)
				.lineAbsolute(computer.getX_px(0.0), padding_px / 2);
		yAxis.withStrokeColor(Color.BLACK).withFillOpacity(0.0);
		yAxis.withMarkerAtEnd(arrow);

		final SVGPath area = new SVGPath(computer.getX_px(0.0), computer.getY_px(0.0));

		double firstValue = values.get(0);
		int prevX_px = computer.getX_px(1.0);
		int prevY_px = computer.getY_px(firstValue);
		area.lineAbsolute(computer.getX_px(0.0), prevY_px);

		final SVGPath curve = new SVGPath(computer.getX_px(0.0), prevY_px);
		curve.lineAbsolute(computer.getX_px(1.0), prevY_px);
		area.lineAbsolute(prevX_px, prevY_px);

		for(int i = 1; i < values.size(); i++) {
			final double value = values.get(i);
			int newX_px = computer.getX_px(1.0 * (i + 1));
			int newY_px = computer.getY_px(value);

			curve.lineAbsolute(prevX_px, newY_px);
			curve.lineAbsolute(newX_px, newY_px);

			area.lineAbsolute(prevX_px, newY_px);
			area.lineAbsolute(newX_px, newY_px);

			prevX_px = newX_px;
		}
		area.lineAbsolute(computer.getX_px(1.0 * values.size()), computer.getY_px(0.0));

		curve.withStrokeColor(Color.BLUE).withStrokeWidth(2).withFillOpacity(0.0);
		area.withStrokeOpacity(0.0).withFillColor(Color.LIGHT_GRAY);

		result.add(area);
		result.add(xAxis);
		result.add(yAxis);
		result.add(curve);

		result.add(new SVGText(computer.getX_px(0.0), computer.getY_px(0.0), "Origin", SVGText.Anchor.MIDDLE));

		return result;
	}

	public static void main(String[] args) throws Exception {
		final Page page = new Page("Histogram", "/index");
		page.setReadingWidth(Layouts.Width.NORMAL);

		final List<Double> values = new ArrayList<>();
		for(int i = 0; i < 50; i++) {
			values.add(-20.0 + Math.random() * 100.0);
		}
		page.append(buildSVG(600, 500, values, 20));

		page.append(new Paragraph(TestUtils.LOREM_IPSUM).setAlign(Layouts.TextAlign.LEFT));

		final TUIBackend backend = new TUIBackend(8080);
		backend.registerPage(page);
		backend.start();

		final Browser browser = new Browser(backend.getPort());
		browser.open(page.getSource());
	}
}
