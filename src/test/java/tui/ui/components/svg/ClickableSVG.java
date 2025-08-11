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
import org.junit.Test;
import org.openqa.selenium.WebElement;
import tui.http.RequestReader;
import tui.http.TUIBackend;
import tui.test.Browser;
import tui.ui.components.Page;
import tui.ui.components.Paragraph;
import tui.ui.components.layout.Panel;

import java.awt.*;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ClickableSVG {

	/**
	 * Here we check with a browser that the SVG can be clicked on a area and trigger a {@link tui.ui.components.UIComponent} refresh
	 * along with parameters defined at the clickable area level.
	 */
	@Test
	public void clickingOnZoneRefreshElementWithZoneParameters() {
		final String parameterName = "zoneId";
		final String parameterValue = "Z-01";
		final SVGPoint whereToClick = new SVGPoint(60, 60);

		final Page page = new Page("Clickable SVG", "/index");
		final Panel panel = createInitialPanel();
		page.append(panel);

		final SVG svg = createSVG(200, 100);

		// Adding a clickable area
		final SVGRectangle clickableZone = svg.add(new SVGRectangle(whereToClick.x() - 10, whereToClick.y() - 10, 20, 20));
		clickableZone.withFillColor(Color.ORANGE); // show the clickable area
		svg.add(new SVGCircle(whereToClick, 2)).withFillColor(Color.black); // Shows where to click

		svg.addClickableZone(clickableZone, Map.of(parameterName, parameterValue)); // Adding parameter to clickable area

		svg.connectListener(panel);

		page.append(svg);

		try(final TUIBackend backend = new TUIBackend(8080)) {
			backend.registerPage(page);
			backend.registerWebService(panel.getSource(), (uri, request, response) -> {
				final Panel result = new Panel();
				final String zoneId = new RequestReader(request).getStringParameter(parameterName);
				result.append(new Paragraph("Updated text, clicked area: " + zoneId));
				return result.toJsonMap();
			});
			backend.start();
			try(final Browser browser = new Browser(backend.getPort())) {
				browser.open(page.getSource());
				final WebElement svgElement = browser.getSVGs().get(0);
				browser.clickInElement(svgElement, whereToClick.x(), whereToClick.y());
				Thread.sleep(10);
				final WebElement paragraphElement = browser.getParagraphsWithText().get(0);

				// We check that all the refresh cycle is correct by checking the new text displayed
				assertEquals("Updated text, clicked area: " + parameterValue, paragraphElement.getText());
			}
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static @NotNull SVG createSVG(int width_px, int height_px) {
		final SVG svg = new SVG(width_px, height_px);
		svg.add(new SVGRectangle(new SVGPoint(0, 0), width_px, height_px))
				.withStrokeColor(Color.BLACK) // Shows SVG border
				.withFillOpacity(0.0);
		return svg;
	}

	private static @NotNull Panel createInitialPanel() {
		final Panel panel = new Panel();
		panel.setSource("/panel");
		panel.append(new Paragraph("initial text"));
		return panel;
	}

}
