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

import tui.ui.components.List;
import tui.ui.components.Page;
import tui.ui.components.Section;
import tui.ui.components.layout.Panel;
import tui.ui.components.svg.SVG;
import tui.ui.components.svg.SVGPoint;
import tui.ui.components.svg.SVGRectangle;
import tui.ui.components.svg.SVGText;
import tui.ui.components.svg.defs.SVGPatternStripes;

import java.awt.*;

public class TUIDocsPage extends Page {

	public TUIDocsPage() {
		super("Pages", "pages.html");

		final Section chapter = appendSection("Pages");
		chapter.appendParagraph("The Page is a root element for creating the UI.");

		final Section sectionStructure = chapter.createSubSection("Structure");
		sectionStructure.appendParagraph("A Page has a standard layout as follows:");

		sectionStructure.append(new Panel(Panel.Align.CENTER)).append(buildPageStructure());
		sectionStructure.appendParagraph("where:");
		sectionStructure.append(new List(false)
				.appendText("header and footer are optional.")
				.appendText("margins on both sides of the body are set with Page.setReadingWidth(NORMAL|WIDE|MAX)"));
		sectionStructure.appendParagraph("The body layout is then computed as:");
		sectionStructure.append(new List(false)
				.appendText("NORMAL: the body is as wide as 65em. The margins take all the rest.")
				.appendText("WIDE: the margins are 35 pixels wide, the body takes the rest.")
				.appendText("MAX: there is no margins, the body is as wide as the page."));
		sectionStructure.appendParagraph("Any layout or component added with Page.append(...) goes into the body.");

		final Section sectionSessionParameters = chapter.createSubSection("Session parameters");
		sectionSessionParameters.appendParagraph("""
				When you need all the requests to your backend to have a set of parameters, then you can add them as follows:""");
		sectionSessionParameters.append(new CodeParagraph("""
				page.setSessionParameter("sessionId", "Xvagm0SgrNHcmmKgRpGL");"""));

		final Section sectionFetchingMethod = chapter.createSubSection("Fetching method");
		sectionFetchingMethod.appendParagraph("""
				By default, the requests to your backend are built with parameters in json format. But this can be changed to form-data by calling:""");
		sectionFetchingMethod.append(new CodeParagraph("""
				page.setFetchType(FetchType.FORM_DATA);"""));
	}

	static SVG buildPageStructure() {
		long width_px = 300;
		long height_px = 220;
		long margin_px = 5;
		long headerHeight_px = 40;
		long bodyWidth_px = width_px - 2 * margin_px - 60;
		long footerHeight_px = 40;

		final SVG result = new SVG(width_px, height_px);
		final SVGPatternStripes stripes = result.addStripes(new SVGPatternStripes("stripes", 2, Color.GRAY, 5, Color.WHITE));

		final SVGPoint headerTopLeft = new SVGPoint(margin_px, margin_px);
		final SVGPoint bodyTopLeft = new SVGPoint((width_px - bodyWidth_px) / 2, headerTopLeft.y() + headerHeight_px);
		final SVGPoint footerTopLeft = new SVGPoint(margin_px, height_px - margin_px - footerHeight_px);
		long bodyHeight_px = height_px - 2 * margin_px - footerHeight_px - headerHeight_px;

		// Drawing body margins
		result.add(new SVGRectangle(margin_px, bodyTopLeft.y(), (width_px - bodyWidth_px - 2 * margin_px) / 2, bodyHeight_px)
				.withFillPattern(stripes)
				.withStrokeColor(null));
		result.add(new SVGRectangle(bodyTopLeft.x() + bodyWidth_px, bodyTopLeft.y(), (width_px - bodyWidth_px - 2 * margin_px) / 2,
				bodyHeight_px)
				.withFillPattern(stripes)
				.withStrokeColor(null));

		addTextBox(result, headerTopLeft, width_px - 2 * margin_px, headerHeight_px, "header");
		addTextBox(result, bodyTopLeft, bodyWidth_px, bodyHeight_px, "body");
		addTextBox(result, footerTopLeft, width_px - 2 * margin_px, footerHeight_px, "footer");

		return result;
	}

	static void addTextBox(SVG svg, SVGPoint topLeft, long width_px, long height_px, String text) {
		final SVGRectangle headerRectangle = svg.add(new SVGRectangle(topLeft, width_px, height_px));
		headerRectangle.withStrokeColor(new Color(209, 22, 255))
				.withStrokeWidth(2)
				.withFillColor(null);
		svg.add(new SVGText(headerRectangle.getCenter(), text, SVGText.Anchor.MIDDLE, SVGText.DominantBaseline.MIDDLE));
	}
}
