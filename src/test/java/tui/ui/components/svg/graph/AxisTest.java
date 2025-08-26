/* Copyright (c) 2025, Mathieu Bordas
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

package tui.ui.components.svg.graph;

import org.junit.Test;
import tui.ui.components.svg.CoordinateTransformation;
import tui.ui.components.svg.SVG;
import tui.ui.components.svg.SVGPoint;
import tui.ui.components.svg.SVGText;
import tui.utils.TestUtils;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.Assert.assertEquals;

public class AxisTest {

	@Test
	public void computeNextY_factor20() {
		Axis.GridFactor gridFactor = new Axis.GridFactor(1, 2); // 20

		assertEquals(20.0, gridFactor.computeNextY(10.0), 0.1);
		assertEquals(0.0, gridFactor.computeNextY(0.0), 0.1);
		assertEquals(0.0, gridFactor.computeNextY(-10.0), 0.1);
		assertEquals(-20.0, gridFactor.computeNextY(-20.0), 0.1);
		assertEquals(-20.0, gridFactor.computeNextY(-25.0), 0.1);
	}

	@Test
	public void computeNextY_factor500() {
		Axis.GridFactor gridFactor = new Axis.GridFactor(2, 5); // 500

		assertEquals(500.0, gridFactor.computeNextY(10.0), 0.1);
		assertEquals(0.0, gridFactor.computeNextY(0.0), 0.1);
		assertEquals(0.0, gridFactor.computeNextY(-10.0), 0.1);
		assertEquals(-1000.0, gridFactor.computeNextY(-1480.0), 0.1);
		assertEquals(2500.0, gridFactor.computeNextY(2480.0), 0.1);
	}

	@Test
	public void computeGridFactor() {
		{ // height 500 px values [0-100], minLabelHeight 50 px -> max label 11, unit factor 10
			final Axis.GridFactor gridFactor = Axis.computeGridFactor(500, new CoordinateTransformation.Range(0.0, 100.0), 50.0);
			assertEquals(1, gridFactor.powerOfTen());
			assertEquals(1, gridFactor.step());
		}

		{ // height 500 px values [5-100], minLabelHeight 50 px -> max label 11, unit factor 10
			final Axis.GridFactor gridFactor = Axis.computeGridFactor(500, new CoordinateTransformation.Range(5.0, 100.0), 50.0);
			assertEquals(1, gridFactor.powerOfTen());
			assertEquals(1, gridFactor.step());
		}

		{ // height 500 px values [0-50], minLabelHeight 50 px -> max label 11, unit factor 5
			final Axis.GridFactor gridFactor = Axis.computeGridFactor(500, new CoordinateTransformation.Range(0.0, 50.0), 50.0);
			assertEquals(0, gridFactor.powerOfTen());
			assertEquals(5, gridFactor.step());
		}

		{ // height 300 px values [0-680], minLabelHeight 50 px -> max label 7, unit factor 100
			final Axis.GridFactor gridFactor = Axis.computeGridFactor(300, new CoordinateTransformation.Range(0.0, 680.0), 50.0);
			assertEquals(2, gridFactor.powerOfTen());
			assertEquals(1, gridFactor.step());
		}

		{ // height 800 px values [0-7], minLabelHeight 30 px -> max label 27, unit factor 0.5
			final Axis.GridFactor gridFactor = Axis.computeGridFactor(300, new CoordinateTransformation.Range(0.0, 680.0), 50.0);
			assertEquals(2, gridFactor.powerOfTen());
			assertEquals(1, gridFactor.step());
		}
	}

	private static final LocalDateTime T0 = LocalDateTime.of(2025, Month.AUGUST, 26, 10, 54, 48);

	/**
	 * Draws x-axis for time periods going from 10s to 18 months
	 */
	public static void main(String[] args) throws Exception {
		final long width_px = 600;
		final long durationLabelWidth_px = 80;
		double durationGrowthFactor = 1.25;

		final Set<Long> durations_s = new TreeSet<>();
		{
			long duration_s = 10; // period = 10s
			while(duration_s < 18 * 30 * 24 * 3600) { // until period = 18 months
				durations_s.add(duration_s);
				duration_s = (long) (duration_s * durationGrowthFactor);
			}
		}

		final SVG svg = new SVG(width_px, 10L * 30 * (durations_s.size() + 1));
		long startY_px = 10;
		for(long duration_s : durations_s) {
			SVGPoint startPoint = new SVGPoint(durationLabelWidth_px, startY_px);
			drawXAxis(svg, duration_s, startPoint, width_px);
			startY_px += 30;
		}

		TestUtils.quickShow(svg);
	}

	private static void drawXAxis(SVG svg, long duration_s, SVGPoint startPoint, long width_px) {
		Axis.drawXAxisWithArrow(svg, new Axis.TimeRange(T0, T0.plusSeconds(duration_s)), startPoint, width_px - 70, 20, Color.BLACK);
		svg.add(new SVGText(startPoint.translate(-10, 0), String.valueOf(duration_s), SVGText.Anchor.END, SVGText.DominantBaseline.MIDDLE));
	}
}