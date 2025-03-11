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
import tui.ui.components.Table;
import tui.ui.components.layout.Panel;

import java.util.List;
import java.util.Map;

public class TUIDocsTables extends Page {

	public static final String PATH = "tables.html";

	public TUIDocsTables() {
		super("TUI Tables", PATH);

		final Section chapter = appendSection("Tables");

		chapter.appendParagraph("The Table object displays string data. Any formating must be done before values are put into a Table.");
		chapter.appendParagraph("Example code:");
		chapter.append(new CodeParagraph("""
				final List<String> columns = List.of("Person", "Hobby", "Age");
						final Table table = new Table("Hobbies", columns);
						table.append(Map.of("Person", "Pierre", "Hobby", "Pétanque", "Age", "42"));
						table.append(Map.of("Person", "Paul", "Hobby", "Rugby", "Age", "35"));
						table.append(Map.of("Person", "Jacques", "Hobby", "Cycling", "Age", "50"));"""));
		chapter.appendParagraph("produces:");

		final List<String> columns = List.of("Person", "Hobby", "Age");
		final Table table = new Table("Hobbies", columns);
		table.append(Map.of("Person", "Pierre", "Hobby", "Pétanque", "Age", "42"));
		table.append(Map.of("Person", "Paul", "Hobby", "Rugby", "Age", "35"));
		table.append(Map.of("Person", "Jacques", "Hobby", "Cycling", "Age", "50"));

		chapter.append(new Panel())
				.setAlign(Panel.Align.CENTER)
				.append(table);

		final Section displayOptions = chapter.createSubSection("Display options");

		displayOptions.append(new CodeParagraph("table.hideHead();"));
		final Table table2 = table.clone();
		table2.hideHead();
		displayOptions.append(new Panel())
				.setAlign(Panel.Align.CENTER)
				.append(table2);

		displayOptions.append(new CodeParagraph("table.hideColumn(\"Age\");"));
		final Table table3 = table.clone();
		table3.hideColumn("Age");
		displayOptions.append(new Panel())
				.setAlign(Panel.Align.CENTER)
				.append(table3);

		displayOptions.append(new CodeParagraph("table.hideTitle();"));
		final Table table4 = table.clone();
		table4.hideTitle();
		displayOptions.append(new Panel())
				.setAlign(Panel.Align.CENTER)
				.append(table4);

	}
}
