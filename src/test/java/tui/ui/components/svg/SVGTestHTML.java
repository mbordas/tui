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

package tui.ui.components.svg;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import tui.test.Browser;
import tui.utils.TestUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class SVGTestHTML {

	@Test
	public void notRefreshable() {
		final SVG svg = createSVGToTest();

		TestUtils.assertHTMLProcedure(() -> svg,
				(prefix, componentRootElement) -> {
					assertEquals(prefix, "svg", componentRootElement.getTagName());

					final WebElement svgElement = componentRootElement;

					checkSVGElement(svgElement);
				});
	}

	@Test
	public void refreshableWithSource() {
		final SVG svg = createSVGToTest();
		svg.setSource("/svg");

		TestUtils.assertHTMLProcedure(() -> svg,
				(prefix, componentRootElement) -> {
					assertEquals(prefix, "div", componentRootElement.getTagName());
					assertTrue(prefix, Browser.getClasses(componentRootElement).contains(SVG.HTML_CLASS_CONTAINER));

					final WebElement svgElement = componentRootElement.findElement(By.tagName("svg"));

					checkSVGElement(svgElement);
				});
	}

	private static @NotNull SVG createSVGToTest() {
		final SVG svg = new SVG(200, 100);
		svg.add(new SVGRectangle(0, 0, 200, 100));
		return svg;
	}

	private static void checkSVGElement(WebElement svgElement) {
		final WebElement rectElement = svgElement.findElement(By.tagName("rect"));
		assertNotNull(rectElement);
	}
}
