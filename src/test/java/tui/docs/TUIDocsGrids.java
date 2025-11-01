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
import tui.ui.components.Paragraph;
import tui.ui.components.RefreshButton;
import tui.ui.components.Section;
import tui.ui.components.UIComponent;
import tui.ui.components.layout.Grid;
import tui.ui.components.layout.Layouts;
import tui.ui.components.layout.Panel;
import tui.utils.TestUtils;

import java.awt.*;
import java.util.ArrayList;

public class TUIDocsGrids extends Page {

	public TUIDocsGrids() {
		super("Grids", "grids.html");

		final Section chapter = appendSection("Grid");

		final Section introduction = chapter.createSubSection("Introduction");
		introduction.appendParagraph(
				"The Grid layout organizes components in cells. The advantage of using a Grid instead of a Table is that you can put "
						+ "any TUI component into it, when you must put strings into Table cells. But the way the sizes are managed is"
						+ " also a big difference from the Table layout.");
		introduction.appendParagraph(
				"You can put any component into a cell by using its coordinates (rowIndex, columnIndex). For example the coordinates "
						+ "of a grid 3x4 are:");
		appendGridWithCellCoordinates(chapter);

		final Section managingTheSizes = chapter.createSubSection("Managing the sizes");

		managingTheSizes.appendParagraph(
				"By default every columns have the same width, and every rows have the same height. Both dimensions are determined by"
						+ " the biggest components. Here is an example with a 2x2 grid:");
		appendGridThatShowsDefaultSizes(managingTheSizes);
		managingTheSizes.appendParagraph(
				"As you can see there is some room at the right and at the bottom of the components. This is because the Grid is set "
						+ "with a fixed width that is larger that what the components require. The same way, when a component has a "
						+ "height smaller that the height of its row, then some room appears at its bottom. The layout is computed as "
						+ "follow:");
		managingTheSizes.append(new List(true))
				.append(new Paragraph.Text("Sizes of rows and columns are computed against the minimum sizes of their components."))
				.append(new Paragraph.Text("In each cell, the component is placed on the top left corner."));

		final Section displayingComponentsInRows = chapter.createSubSection("Displaying components in rows");
		displayingComponentsInRows.appendParagraph("The Grid layout may be used to display elements in harmonized rows.");

		displayingComponentsInRows.appendParagraph("In that example, we want to display a list of emails. The goal of the view is to show "
				+ "the dates and the subjects, and two action buttons for each email. Because we want the list to be displayed with well-aligned "
				+ "columns, we use a Grid layout. We set the columns width constraints: the first column will contain the dates and we want it compact, "
				+ "the second column will contain the subjects and we want it as wide as possible, the third column will contain the buttons and we "
				+ "want it compact.");
		displayingComponentsInRows.append(new CodeParagraph("""
				Grid grid = new Grid(emails.size(), 3);
				grid.setColumnWidthMaxContent(0);
				grid.setColumnWidthAuto(1);
				grid.setColumnWidthMaxContent(2);"""));

		displayingComponentsInRows.appendParagraph("First challenge: we want to vertically align the elements of a same row.");
		displayingComponentsInRows.appendParagraph(
				"To do that, each element added into a cell is put into a Panel using " + Panel.Align.VERTICAL_CENTER
						+ ". An other challenge is to get the dates strings not wrapped on two lines. This is "
						+ "achieved using 'disableTextWrap':");
		displayingComponentsInRows.append(new CodeParagraph("""
				Panel result = new Panel(Panel.Align.VERTICAL_CENTER);
				Paragraph.Text text = result.append(new Paragraph.Text(date));
				text.customStyle().setBackgroundColor(Color.LIGHT_GRAY)
				text.customStyle().setBorderRadius_px(2)
				text.customStyle().setPadding(1, 5, 1, 5);
				text.customTextStyle().setTextSize_em(0.8f)
				text.customTextStyle().disableTextWrap();"""));

		displayingComponentsInRows.appendParagraph(
				"You can find the complete code in the Java source of this page in class " + TUIDocsGrids.class.getSimpleName()
						+ " which produces with 10 randomized emails:");
		appendEmailsInRows(displayingComponentsInRows);
	}

	private void appendEmailsInRows(Section displayingComponentsInRows) {
		final ArrayList<Email> emails = new ArrayList<>();
		for(int i = 0; i < 10; i++) {
			emails.add(new Email(String.format("%d/06/2025 %d:00", 19 + i, 13 + i),
					TestUtils.LOREM_IPSUM.substring(0, 10 + (int) (Math.random() * 150.0))));
		}
		final Grid grid = new Grid(emails.size(), 3);
		grid.setColumnWidthMaxContent(0);
		grid.setColumnWidthAuto(1);
		grid.setColumnWidthMaxContent(2);
		int row = 0;
		for(Email email : emails) {
			grid.set(row, 0, createDateBadge(row, email.date));
			grid.set(row, 1, createSubject(row, email.subject));
			grid.set(row, 2, createButtons(row));
			row++;
		}
		displayingComponentsInRows.append(grid);
	}

	private UIComponent createButtons(int row) {
		final Panel result = createCellPanel(row);
		final Panel linePanel = result.append(new Panel(Panel.Align.STRETCH));
		linePanel.append(new RefreshButton("Open"));
		linePanel.append(new RefreshButton("Delete"));
		linePanel.customStyle().setMargin(5, 0, 5, 0);
		return result;
	}

	private UIComponent createSubject(int row, String subject) {
		final Panel result = createCellPanel(row);
		final Paragraph.Text text = result.append(new Paragraph.Text(subject));
		text.customTextStyle().setTextAlign(Layouts.Align.LEFT);
		text.customStyle().setMargin(0, 0, 0, 20);
		return result;
	}

	private UIComponent createDateBadge(int row, String date) {
		final Panel result = createCellPanel(row);
		final Paragraph.Text text = result.append(new Paragraph.Text(date));
		text.customStyle()
				.setBackgroundColor(Color.LIGHT_GRAY)
				.setBorderRadius_px(2)
				.setPadding(1, 5, 1, 5);
		text.customTextStyle()
				.setTextSize_em(0.8f)
				.disableTextWrap();
		return result;
	}

	private Panel createCellPanel(int row) {
		final Panel result = new Panel(Panel.Align.VERTICAL_CENTER);
		result.customStyle().setBorderColor(Color.gray);
		if(row == 0) {
			result.customStyle().setBorderWidth_px(1 /* only on first row */, 0, 1, 0);
		} else {
			result.customStyle().setBorderWidth_px(0, 0, 1, 0);
		}
		return result;
	}

	private record Email(String date, String subject) {
	}

	private static void appendGridThatShowsDefaultSizes(Section section) {
		final int rows = 2;
		final int columns = 2;
		final Grid grid = section.append(new Panel(Panel.Align.CENTER))
				.append(TUIDocsUtils.decorateContainer(new Grid(rows, columns)));
		grid.customStyle()
				.setWidth_px(300);
		grid.set(0, 0, createBlock(70, 110));
		grid.set(0, 1, createBlock(120, 60));
		grid.set(1, 0, createBlock(80, 90));
		grid.set(1, 1, createBlock(100, 130));
	}

	private static Panel createBlock(int width_px, int height_px) {
		final Panel result = TUIDocsUtils.decorateElement(new Panel(Panel.Align.VERTICAL_CENTER));
		result.customStyle()
				.setWidth_px(width_px)
				.setHeight_px(height_px);
		final Paragraph.Text text = result.append(new Paragraph.Text("%d x %d", width_px, height_px));
		text.customStyle()
				.setWidth_percent(100);
		text.customTextStyle()
				.setTextAlign(Layouts.Align.CENTER);
		return result;
	}

	private static void appendGridWithCellCoordinates(Section section) {
		final int rows = 3;
		final int columns = 4;
		final Grid grid = section.append(new Panel(Panel.Align.CENTER))
				.append(new Grid(rows, columns));
		grid.customStyle().setWidth_px(250);
		for(int rowIndex = 0; rowIndex < rows; rowIndex++) {
			for(int columnIndex = 0; columnIndex < columns; columnIndex++) {
				final Paragraph.Text cell = grid.set(rowIndex, columnIndex,
						TUIDocsUtils.decorateElement(new Paragraph.Text("%d,%d", rowIndex, columnIndex)));
				cell.customTextStyle()
						.setTextAlign(Layouts.Align.CENTER);
			}
		}
	}
}
