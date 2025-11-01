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
import tui.ui.components.layout.Grid;
import tui.ui.components.layout.Panel;
import tui.utils.TestUtils;

import java.awt.*;
import java.util.List;

public class TUIDocsPanels extends Page {

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
			if(!align.name().startsWith("VERTICAL")) {
				sectionAlignment.appendParagraph("Example with ").appendBold("align = " + align);
				Panel panel = TUIDocsUtils.decorateContainer(new Panel());
				panel.append(TUIDocsUtils.decorateElement(new RefreshButton("1- button")));
				panel.append(TUIDocsUtils.decorateElement(new RefreshButton("2- button")));
				panel.append(TUIDocsUtils.decorateElement(new Paragraph("3- " + TestUtils.LOREM_IPSUM.substring(0, 60))));
				panel.append(TUIDocsUtils.decorateElement(new RefreshButton("4- button")));
				panel.setAlign(align);
				sectionAlignment.append(panel);
			}
		}

		final Section verticalAlignment = sectionAlignment.createSubSection("Vertical alignment");
		verticalAlignment.appendParagraph(
				"The VERTICAL_x alignment values differ when the height of the panel is bigger than the heights of the components"
						+ " it contains:");
		for(Panel.Align align : Panel.Align.values()) {
			if(align.name().startsWith("VERTICAL")) {
				verticalAlignment.appendParagraph("Example with ").appendBold("align = " + align);
				Panel panel = TUIDocsUtils.decorateContainer(new Panel());
				panel.customStyle().setHeight_px(100);
				panel.append(TUIDocsUtils.decorateElement(new RefreshButton("1- button")));
				panel.append(TUIDocsUtils.decorateElement(new Paragraph("3- " + TestUtils.LOREM_IPSUM.substring(0, 60))));
				panel.setAlign(align);
				verticalAlignment.append(panel);
			}
		}

		final Section sectionCombiningPanels = chapter.createSubSection("Combining panels");
		sectionCombiningPanels.appendParagraph("A panel can also contains other panels, in order to combine layout structures:");
		for(Panel.Align align : List.of(Panel.Align.STRETCH, Panel.Align.VERTICAL_CENTER)) {

			sectionCombiningPanels.appendParagraph("With align=%s for main container panel", align.name());

			final Panel mainPanel = sectionCombiningPanels.append(TUIDocsUtils.decorateContainer(new Panel()));
			mainPanel.setAlign(align);

			final Panel panel1 = mainPanel.append(TUIDocsUtils.decorateContainer(new Panel()));
			panel1.setAlign(Panel.Align.LEFT);
			panel1.customStyle().setWidth_px(300);
			panel1.append(TUIDocsUtils.decorateElement(new RefreshButton("1.1- button")));
			panel1.append(TUIDocsUtils.decorateElement(new Paragraph("1.2- text")));
			final Panel panel2 = mainPanel.append(TUIDocsUtils.decorateContainer(new Panel()));
			panel2.setAlign(Panel.Align.RIGHT);
			panel2.customStyle().setWidth_px(500);
			panel2.append(TUIDocsUtils.decorateElement(new RefreshButton("2.1- button")));
			panel2.append(TUIDocsUtils.decorateElement(new Paragraph("2.2- text")));
		}

		final Section sectionPanelsInGrid = chapter.createSubSection("Panels in Grid");
		sectionPanelsInGrid.appendParagraph(
				"When inside a grid cell, and as long as you don't set a margin, the Panel is the size of the cell. The following code shows"
						+ " how panels fit with the columns sizing configuration of a Grid:");
		sectionPanelsInGrid.append(new CodeParagraph("""
				Grid grid = sectionPanelsInGrid.append(new Grid(1, 3));
						grid.setColumnWidth_px(0, 200);
						grid.setColumnWidthMaxContent(1);
						grid.setColumnWidthAuto(2);
				
						grid.set(0, 0, decoratedText("Column width = 200px"));
						grid.set(0, 1, decoratedText("Column width = max content"));
						grid.set(0, 2, decoratedText("Column width = auto"));"""));
		sectionPanelsInGrid.appendParagraph("The grid has red borders, the panels have purple borders, and panels have blue borders.");

		final Grid grid = sectionPanelsInGrid.append(new Grid(1, 3));
		grid.setColumnWidth_px(0, 200);
		grid.setColumnWidthMaxContent(1);
		grid.setColumnWidthAuto(2);

		TUIDocsUtils.decorateContainer(grid, Color.RED);
		grid.set(0, 0, decoratedText("Column width = 200px"));
		grid.set(0, 1, decoratedText("Column width = max content"));
		grid.set(0, 2, decoratedText("Column width = auto"));
	}

	static Panel decoratedText(String label) {
		final Panel result = TUIDocsUtils.decorateContainer(new Panel(Panel.Align.CENTER));
		final Paragraph.Text text = result.append(new Paragraph.Text(label));
		TUIDocsUtils.decorateElement(text);
		return result;
	}

}
