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
import tui.http.RequestReader;
import tui.http.TUIBackend;
import tui.test.Browser;
import tui.ui.components.Page;
import tui.ui.components.form.Search;
import tui.ui.components.svg.SVG;
import tui.ui.components.svg.SVGRectangle;

import java.awt.*;
import java.util.List;

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

	static SVG computePaletteSVG(int hueStart, int hueEnd, int saturation, int lightness) {
		final int nbColors = 10;
		final int y_px = 1;
		final int height_px = 30;
		final int width_px = 600 / nbColors;
		final int margin_px = 5;

		final SVG result = new SVG((width_px + margin_px) * nbColors, 100);

		final List<TUIColors.ColorHSL> palette = TUIColors.palette(new TUIColors.ColorHSL(hueStart, saturation, lightness),
				new TUIColors.ColorHSL(hueEnd, saturation, lightness),
				nbColors);

		int x_px = 1;
		for(TUIColors.ColorHSL colorHSL : palette) {
			result.add(new SVGRectangle(x_px, y_px, width_px, height_px))
					.withFillColor(colorHSL.toRGB());
			x_px += width_px + margin_px;
		}

		return result;
	}

	/**
	 * Opens a page that helps to create a palette of colors
	 */
	public static void main(String[] args) throws Exception {
		final int initialHueStart = 0;
		final int initialHueEnd = 360;
		final int initialSaturation = 70;
		final int initialLightness = 80;

		final Page page = new Page("Color palette", "/index");
		final Search search = page.append(new Search("", "Compute palette"));
		search.createInputNumber("Hue start (0 to 360 degrees)", "hueStart").setInitialValue(initialHueStart);
		search.createInputNumber("Hue end (0 to 360 degrees)", "hueEnd").setInitialValue(initialHueEnd);
		search.createInputNumber("Saturation (0 to 100)", "saturation").setInitialValue(initialSaturation);
		search.createInputNumber("Lightness (0 to 100)", "lightness").setInitialValue(initialLightness);

		final SVG svg = page.append(computePaletteSVG(initialHueStart, initialHueEnd, initialSaturation, initialLightness));
		svg.setSource("/svg");
		search.connectListener(svg);

		try(final TUIBackend backend = new TUIBackend(8080)) {
			backend.start();
			backend.registerPage(page);
			backend.registerWebService(svg.getSource(), (uri, request, response) -> {
				final RequestReader reader = new RequestReader(request);
				final int hueStart = reader.getIntParameter("hueStart");
				final int hueEnd = reader.getIntParameter("hueEnd");
				final int saturation = reader.getIntParameter("saturation");
				final int lightness = reader.getIntParameter("lightness");
				return computePaletteSVG(hueStart, hueEnd, saturation, lightness).toJsonMap();
			});

			try(final Browser browser = new Browser(backend.getPort())) {
				browser.open(page.getSource());
				browser.waitClosedManually();
			}
		}
	}
}