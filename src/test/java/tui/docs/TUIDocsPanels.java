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
import tui.ui.components.Paragraph;
import tui.ui.components.RefreshButton;
import tui.ui.components.Section;
import tui.ui.components.layout.Panel;
import tui.utils.TestUtils;

import java.awt.*;
import java.util.List;

public class TUIDocsPanels extends Page {

	public static final Color COLOR_CONTAINER = new Color(209, 22, 255);
	public static final Color COLOR_ELEMENT = new Color(27, 155, 255);

	public TUIDocsPanels() {
		super("Panels", "panels.html");

		final Section chapter = appendSection("Panels");
		chapter.appendParagraph(
				"The Panel is used to organize components from left to right, like the hand writing, or vertically from top to bottom.");

		final Section sectionAlignment = chapter.createSubSection("Alignment");
		sectionAlignment.appendParagraph("""
				Different 'Align' options provide different ways to organize the elements inside the panel. The following examples show
				how components are displayed in a panel depending on the panel's align option.""");
		for(Panel.Align align : Panel.Align.values()) {
			chapter.appendParagraph("Example with ").appendBold("align = " + align);
			Panel panel = newPanel();
			panel.append(newButton("1- button"));
			panel.append(newButton("2- button"));
			panel.append(newParagraph("3- " + TestUtils.LOREM_IPSUM.substring(0, 60)));
			panel.append(newButton("4- button"));
			panel.setAlign(align);
			chapter.append(panel);
		}

		final Section sectionCombiningPanels = chapter.createSubSection("Combining panels");
		sectionCombiningPanels.appendParagraph("A panel can also contains other panels, in order to combine layout structures:");
		for(Panel.Align align : List.of(Panel.Align.STRETCH, Panel.Align.VERTICAL)) {

			sectionCombiningPanels.appendParagraph("With align=%s for main container panel", align.name());

			final Panel mainPanel = sectionCombiningPanels.append(newPanel());
			mainPanel.setAlign(align);

			final Panel panel1 = mainPanel.append(newPanel());
			panel1.setAlign(Panel.Align.LEFT);
			panel1.customStyle().setWidth_px(300);
			panel1.append(newButton("1.1- button"));
			panel1.append(newParagraph("1.2- text"));
			final Panel panel2 = mainPanel.append(newPanel());
			panel2.setAlign(Panel.Align.RIGHT);
			panel2.customStyle().setWidth_px(500);
			panel2.append(newButton("2.1- button"));
			panel2.append(newParagraph("2.2- text"));
		}

	}

	private Panel newPanel() {
		final Panel result = new Panel();
		result.customStyle()
				.setBorderColor(COLOR_CONTAINER)
				.setBorderWidth_px(2)
				.setPadding(2, 2, 2, 2);
		return result;
	}

	private Paragraph newParagraph(String text) {
		final Paragraph result = new Paragraph(text);
		result.customStyle()
				.setBorderColor(COLOR_ELEMENT)
				.setBorderWidth_px(2)
				.setPadding(2, 2, 2, 2);
		return result;
	}

	private RefreshButton newButton(String label) {
		final RefreshButton result = new RefreshButton(label);
		result.customStyle()
				.setBorderColor(COLOR_ELEMENT)
				.setBorderWidth_px(2)
				.setPadding(2, 2, 2, 2);
		return result;
	}
}
