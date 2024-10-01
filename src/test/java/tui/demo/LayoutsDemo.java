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

package tui.demo;

import tui.http.TUIBackend;
import tui.test.Browser;
import tui.ui.UI;
import tui.ui.components.Page;
import tui.ui.components.Paragraph;
import tui.ui.components.RefreshButton;
import tui.ui.components.Table;
import tui.ui.components.layout.HorizontalFlow;
import tui.ui.components.layout.Layouts;
import tui.ui.components.layout.TabbedFlow;
import tui.ui.components.layout.VerticalFlow;
import tui.utils.TestUtils;

import java.util.List;
import java.util.Map;

public class LayoutsDemo {

	private static Table createTable() {
		final Table result = new Table("Cities", List.of("City", "Code", "Inhabitants"));
		for(int i = 0; i < 10; i++) {
			final String city = TestUtils.CITIES[i];
			final int code = (int) (Math.random() * 90000);
			final long inhabitants = (long) (10_000L + Math.random() * 1_000_000L);
			result.append(Map.of("City", city, "Code", String.valueOf(code), "Inhabitants", String.valueOf(inhabitants)));
		}
		return result;
	}

	private static void createTab(TabbedFlow tabbedFlow, tui.ui.components.layout.Layouts.Width width,
			tui.ui.components.layout.Layouts.Spacing spacing) {
		final VerticalFlow tabFlow = tabbedFlow.createTab(String.format("%s %s", width.name(), spacing.name()));
		tabFlow.setWidth(width);
		tabFlow.setSpacing(spacing);

		tabFlow.append(new Paragraph(TestUtils.LOREM_IPSUM));
		final HorizontalFlow buttons = tabFlow.append(new HorizontalFlow());
		buttons.setSpacing(spacing);
		buttons.setAlign(Layouts.TextAlign.LEFT);
		buttons.append(new RefreshButton("Button 1"));
		buttons.append(new RefreshButton("Button 2"));
		buttons.append(new RefreshButton("Button 3"));
		tabFlow.append(new Paragraph(TestUtils.LOREM_IPSUM).setAlign(tui.ui.components.layout.Layouts.TextAlign.RIGHT));

		tabFlow.appendUnitedBlock(createTable(),
				new Paragraph("My legend here").setAlign(tui.ui.components.layout.Layouts.TextAlign.CENTER));

		tabFlow.append(new Paragraph(TestUtils.LOREM_IPSUM).setAlign(tui.ui.components.layout.Layouts.TextAlign.STRETCH));
	}

	public static void main(String[] args) throws Exception {

		final Page page = new Page("Layouts", "/index");

		final TabbedFlow tabbedFlow = page.append(new TabbedFlow());

		createTab(tabbedFlow, tui.ui.components.layout.Layouts.Width.MAX, tui.ui.components.layout.Layouts.Spacing.FIT);
		createTab(tabbedFlow, tui.ui.components.layout.Layouts.Width.WIDE, tui.ui.components.layout.Layouts.Spacing.FIT);
		createTab(tabbedFlow, tui.ui.components.layout.Layouts.Width.NORMAL, tui.ui.components.layout.Layouts.Spacing.COMPACT);
		createTab(tabbedFlow, tui.ui.components.layout.Layouts.Width.NORMAL, tui.ui.components.layout.Layouts.Spacing.NORMAL);
		createTab(tabbedFlow, tui.ui.components.layout.Layouts.Width.NORMAL, tui.ui.components.layout.Layouts.Spacing.LARGE);

		final UI ui = new UI();
		ui.add(page);
		ui.setHTTPBackend("http://localhost", 8080);
		final TUIBackend backend = new TUIBackend(ui);
		backend.start();

		final Browser browser = new Browser(backend.getPort());
		browser.open(page.getSource());
	}

}
