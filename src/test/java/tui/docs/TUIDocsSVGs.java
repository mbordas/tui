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

package tui.docs;

import tui.ui.components.Page;
import tui.ui.components.Section;
import tui.ui.components.svg.SVG;
import tui.ui.components.svg.SVGCircle;
import tui.ui.components.svg.SVGRectangle;
import tui.ui.components.svg.defs.SVGPatternStripes;

import java.awt.*;

public class TUIDocsSVGs extends Page {

	public TUIDocsSVGs() {
		super("SVGs", "svgs.html");

		final Section chapter = appendSection("SVGs");
		chapter.appendParagraph("Graphs are made in TUI by using SVG (Scalable Vector Graphics).");

		final Section sectionDrawing = chapter.createSubSection("Drawing");
		sectionDrawing.appendParagraph(
				"Use the 'add' method of an SVG in order to draw SVG elements. Available elements are classes named SVGxxx. If you know SVGs,"
						+ " the use in TUI should be straightforward for you.");

		// SVG #1 with simple rectangle
		{
			sectionDrawing.append(new CodeParagraph("""
					SVG svg = new SVG(300, 150);
					svg.add(new SVGRectangle(10, 20, 30, 40))
						.withFillColor(Color.GREEN)
						.withFillOpacity(0.5);"""));

			SVG svg = new SVG(300, 150);
			svg.add(new SVGRectangle(10, 20, 30, 40))
					.withFillColor(Color.GREEN)
					.withFillOpacity(0.5);
			sectionDrawing.append(svg);
		}

		final Section sectionStripes = chapter.createSubSection("Stripes");
		sectionStripes.appendParagraph("You can create a SVGPatternStripes in one SVG and use it to fill components.");

		// SVG #2 with circle and stripes
		{
			sectionStripes.append(new CodeParagraph("""
					SVG svg = new SVG(300, 150);
					SVGPatternStripes patternStripes = svg.addStripes(new SVGPatternStripes("stripes", 2, Color.orange, 2, Color.gray));
					SVGCircle circle = new SVGCircle(200, 75, 50);
					circle.withFillPattern(patternStripes);
					svg.add(circle);"""));

			SVG svg = new SVG(300, 150);
			SVGPatternStripes patternStripes = svg.addStripes(new SVGPatternStripes("stripes", 2, Color.orange, 2, Color.gray));
			SVGCircle circle = new SVGCircle(200, 75, 50);
			circle.withFillPattern(patternStripes);
			svg.add(circle);
			sectionStripes.append(svg);

		}

		final Section sectionClicks = chapter.createSubSection("Triggering clicks");
		sectionClicks.appendParagraph(
				"You can catch some clicks in one SVG in order to trigger the refresh of one (or many) component of your page. To do so, you"
						+ " have to add a 'ClickableZone' to your SVG. A clickable zone is defined by an area (where to catch the mouse's clicks)"
						+ " and a set of (key,value) parameters. This optional set of parameters will then be sent to the backend for every components"
						+ " to be refreshed.");

		// SVG #3 with clickable zone
		{
			sectionClicks.append(new CodeParagraph("""
					SVG svg = new SVG(300, 150);
					svg.addClickableZone(new SVGRectangle(50, 50, 20, 20), Map.of("zoneId", "1"));
					svg.connectListener(componentToRefresh);"""));

			sectionClicks.appendParagraph(
					"When the user clicks on the rectangle, the browser will call the backend webservice of 'componentToRefresh'"
							+ " and send additional parameter:");
			sectionClicks.append(new CodeParagraph("zoneId=1"));

			sectionClicks.appendParagraph("The clickable zones are not drawn, but the cursor changes when the mouse is hovering a zone.");
		}
	}

}
