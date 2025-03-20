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
import tui.ui.components.TablePicker;
import tui.ui.components.layout.Panel;

import java.util.List;
import java.util.Map;

public class TUIDocsTablePicker extends Page {

	public TUIDocsTablePicker() {
		super("TablePickers", "tablepickers.html");

		final Section chapter = appendSection("Table pickers");

		chapter.appendParagraph("The user can click on the rows of a TablePicker in order to refresh other components of the page.");

		final Section subSection = chapter.createSubSection("Parameters sent to backend");
		subSection.appendParagraph("""
				You can refer to the documentation of tables to find how to create TablePickers. You just need to connect it to other components.""");
		subSection.appendParagraph("""
				When the user clicks on a row, any connected components will be refreshed. The calls to the backend contain as many parameters 
				as there is columns in the table (hidden or not). Each parameter is named after the column, and its value is the one corresponding 
				to the clicked row.""");

		subSection.appendParagraph("Lets see an example:");
		final TablePicker tablePicker = new TablePicker("Example TablePicker", List.of("id", "name", "age"));
		tablePicker.append(Map.of("id", "1", "name", "Pierre", "age", "39"));
		tablePicker.append(Map.of("id", "2", "name", "Paul", "age", "42"));
		tablePicker.append(Map.of("id", "3", "name", "Jacques", "age", "24"));
		subSection.append(new Panel(Panel.Align.CENTER)).append(tablePicker);

		subSection.appendParagraph(
				"When the user clicks on the third row, the following parameters are sent to the backend for each component "
						+ "to be refreshed:");
		subSection.append(new CodeParagraph("""
				id: 3
				name: Jacques
				age: 24"""));
		subSection.appendParagraph("Note that parameters are sent even for hidden rows.");
	}
}
