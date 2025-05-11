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

package tui.utils;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tui.ui.components.svg.SVG;
import tui.ui.components.svg.SVGRectangle;

import java.awt.*;
import java.util.List;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;

public class TUIColorsTest {

	private static final Logger LOG = LoggerFactory.getLogger(TUIColorsTest.class);

	@Test
	public void toCSS() {
		assertEquals("#1db0e0", TUIColors.toCSSHex(new Color(29, 176, 224)));
		assertEquals("#00afe0", TUIColors.toCSSHex(new Color(0, 175, 224)));
	}

	@Test
	public void toHSL() {
		check(new TUIColors.ColorHSL(0, 0, 0), TUIColors.toHSL(new Color(0, 0, 0)));
		check(new TUIColors.ColorHSL(0, 0, 100), TUIColors.toHSL(new Color(255, 255, 255)));
		check(new TUIColors.ColorHSL(0, 100, 50), TUIColors.toHSL(new Color(255, 0, 0)));
		check(new TUIColors.ColorHSL(120, 100, 50), TUIColors.toHSL(new Color(0, 255, 0)));
		check(new TUIColors.ColorHSL(240, 100, 50), TUIColors.toHSL(new Color(0, 0, 255)));
		check(new TUIColors.ColorHSL(60, 100, 50), TUIColors.toHSL(new Color(255, 255, 0)));
		check(new TUIColors.ColorHSL(180, 100, 50), TUIColors.toHSL(new Color(0, 255, 255)));
		check(new TUIColors.ColorHSL(300, 100, 50), TUIColors.toHSL(new Color(255, 0, 255)));
		check(new TUIColors.ColorHSL(0, 0, 74), TUIColors.toHSL(new Color(191, 191, 191)));
		check(new TUIColors.ColorHSL(0, 0, 50), TUIColors.toHSL(new Color(128, 128, 128)));
		check(new TUIColors.ColorHSL(0, 100, 25), TUIColors.toHSL(new Color(128, 0, 0)));
	}

	private void check(TUIColors.ColorHSL expected, TUIColors.ColorHSL tested) {
		LOG.debug("expected {}| tested {}", expected, tested);
		assertEquals(expected.hue(), tested.hue());
		assertEquals(expected.saturation(), tested.saturation());
		assertEquals(expected.lightness(), tested.lightness());
	}

	@Test
	public void toRGB() {
		check(new Color(0, 0, 0), new TUIColors.ColorHSL(0, 0, 0).toRGB());
		check(new Color(255, 255, 255), new TUIColors.ColorHSL(0, 0, 100).toRGB());
		check(new Color(255, 0, 0), new TUIColors.ColorHSL(0, 100, 50).toRGB());
		check(new Color(0, 255, 0), new TUIColors.ColorHSL(120, 100, 50).toRGB());
		check(new Color(0, 0, 255), new TUIColors.ColorHSL(240, 100, 50).toRGB());
		check(new Color(255, 255, 0), new TUIColors.ColorHSL(60, 100, 50).toRGB());
		check(new Color(0, 255, 255), new TUIColors.ColorHSL(180, 100, 50).toRGB());
		check(new Color(255, 0, 255), new TUIColors.ColorHSL(300, 100, 50).toRGB());
		check(new Color(191, 191, 191), new TUIColors.ColorHSL(0, 0, 75).toRGB());
		check(new Color(127, 127, 127), new TUIColors.ColorHSL(0, 0, 50).toRGB());
		check(new Color(127, 0, 0), new TUIColors.ColorHSL(0, 100, 25).toRGB());
	}

	private void check(Color expected, Color tested) {
		LOG.debug("expected {}| tested {}", expected, tested);
		assertEquals(expected.getRed(), tested.getRed());
		assertEquals(expected.getGreen(), tested.getGreen());
		assertEquals(expected.getBlue(), tested.getBlue());
	}

	/**
	 * Computing the palette of 3 colors from HSL 200,80,60 to HSL 100,50,90.
	 * Expected hues are: 200 -(+130)-> 330 -(+130)-> 100
	 */
	@Test
	public void palette() {
		final TUIColors.ColorHSL startHSL = new TUIColors.ColorHSL(200, 80, 60);
		final TUIColors.ColorHSL endHSL = new TUIColors.ColorHSL(100, 50, 90);

		final List<TUIColors.ColorHSL> palette = TUIColors.palette(startHSL, endHSL, 3);

		assertEquals(3, palette.size());
		check(startHSL, palette.get(0));
		check(new TUIColors.ColorHSL(330, 65, 75), palette.get(1));
		check(endHSL, palette.get(2));
	}

	/**
	 * Here we construct a {@link SVG} to display computed colors from a single Hue value as follows:
	 * <ul>
	 *     <li>A 2d grid shows Lightness (y-axis) and Saturation (x-axis)</li>
	 *     <li>A row shows a palette computed using a very simple relation between Lightness and Saturation.</li>
	 * </ul>
	 */
	public static void main(String[] args) throws Exception {
		Scanner scanner = new Scanner(System.in);
		System.out.print("\nHue (0 to 360 degrees): ");
		String hueStr = scanner.nextLine();
		int hue = Integer.parseInt(hueStr);

		final SVG svg = new SVG(600, 600);
		long width_px = 30;
		long height_px = 20;
		long x_px = 10;
		long y_px = 10;
		for(int lightness = 100; lightness >= 0; lightness -= 10) {
			for(int saturation = 0; saturation <= 100; saturation += 10) {
				final Color rgb = new TUIColors.ColorHSL(hue, saturation, lightness).toRGB();
				svg.add(new SVGRectangle(x_px, y_px, width_px, height_px)
						.withFillColor(rgb)
						.withStrokeColor(null));
				x_px += width_px + 5;
			}
			x_px = 10;
			y_px += height_px + 5;
		}

		// Palette from same hue
		x_px = 10;
		y_px += height_px + 5;
		for(int lightness = 90; lightness >= 10; lightness -= 10) {
			int saturation = 110 - lightness;
			final Color rgb = new TUIColors.ColorHSL(hue, saturation, lightness).toRGB();
			svg.add(new SVGRectangle(x_px, y_px, width_px, height_px)
					.withFillColor(rgb)
					.withStrokeColor(null));
			x_px += width_px + 5;
		}

		// Palette with different hue
		final TUIColors.ColorHSL colorStart = new TUIColors.ColorHSL(hue, 90, 40);
		int hue2 = (hue + 280) % 360;
		final TUIColors.ColorHSL colorEnd = new TUIColors.ColorHSL(hue2, 90, 40);
		x_px = 10;
		y_px += height_px + 5;
		final List<TUIColors.ColorHSL> palette = TUIColors.palette(colorStart, colorEnd, 8);
		for(TUIColors.ColorHSL color : palette) {
			svg.add(new SVGRectangle(x_px, y_px, width_px, height_px)
					.withFillColor(color.toRGB())
					.withStrokeColor(null));
			x_px += width_px + 5;
		}

		TestUtils.quickShow(svg);
	}
}