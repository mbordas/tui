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

import tui.http.RequestReader;
import tui.http.TUIBackend;
import tui.test.Browser;
import tui.ui.components.Page;
import tui.ui.components.Paragraph;
import tui.ui.components.TablePicker;
import tui.ui.components.form.Search;
import tui.ui.components.layout.Grid;
import tui.ui.components.layout.Layouts;
import tui.utils.TestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NavigationWithTablePicker {

	public static void main(String[] args) throws Exception {
		final List<MailViewer.Email> emails = new ArrayList<>();
		for(int i = 0; i < 30; i++) {
			final String subject = String.format("SUB #%d", (int) (1000.0 * Math.random()));
			emails.add((new MailViewer.Email(String.valueOf(i), String.format("%d days ago", i + 1), subject,
					i + 1 + " " + TestUtils.LOREM_IPSUM)));
		}

		final Page page = new Page("Home", "/index");
		page.setReadingWidth(Layouts.Width.NORMAL);
		page.setHeader(new Paragraph(Layouts.Align.CENTER).appendNormal("Header"));
		page.setFooter(new Paragraph(Layouts.Align.RIGHT).appendNormal("Example footer text"));

		final Search search = new Search("Search in subject", "Subject contains", "subjectContains");
		page.append(search);

		final Grid mailNavigationGrid = new Grid(1, 2).setColumnWidth_px(0, 200);

		final TablePicker mailSelector = new TablePicker("Inbox", List.of("id", "date", "subject"));
		mailSelector.setSource("/email/list");
		search.connectListener(mailSelector);
		mailNavigationGrid.set(0, 0, mailSelector);
		mailSelector.hideColumn("id");
		mailSelector.hideColumn("date");
		mailSelector.hideHead();
		for(MailViewer.Email email : emails) {
			mailSelector.append(Map.of("id", email.id(), "date", email.date(), "subject", email.subject()));
		}

		final Grid mailView = new Grid(2, 1);
		mailNavigationGrid.set(0, 1, mailView);
		mailView.setSource("/email/view");
		mailSelector.connectListener(mailView);

		page.append(mailNavigationGrid);

		final TUIBackend backend = new TUIBackend(8080);
		backend.registerPage(page);

		backend.registerWebService(mailSelector.getSource(), (uri, request, response) -> {
			final RequestReader requestReader = new RequestReader(request);
			final String searched = requestReader.getStringParameter("subjectContains");
			final TablePicker table = new TablePicker(mailSelector.getTitle(), mailSelector.getColumns());
			emails.stream()
					.filter((email) -> email.subject().contains(searched))
					.forEach((email) -> table.append(Map.of("id", email.id(), "date", email.date(), "subject", email.subject())));
			table.hideColumn("id");
			table.hideColumn("date");
			table.hideHead();
			return table.toJsonMap();
		});

		backend.registerWebService(mailView.getSource(), (uri, request, response) -> {
			final RequestReader requestReader = new RequestReader(request);
			final String id = requestReader.getStringParameter("id");
			final MailViewer.Email email = emails.stream().filter((_email) -> _email.id().equals(id)).findAny().get();

			final Grid result = new Grid(2, 1);
			final Grid header = new Grid(3, 1);
			header.set(0, 0, new Paragraph().appendBold("Subject: ").appendNormal(email.subject()));
			header.set(1, 0, new Paragraph().appendBold("Date: ").appendNormal(email.date()));
			header.set(2, 0, new Paragraph().appendBold("Content:"));
			result.set(0, 0, header);
			result.set(1, 0, new Paragraph(email.content())).customStyle().setBorderWidth_px(1);
			return result.toJsonMap();
		});
		backend.start();

		final Browser browser = new Browser(backend.getPort());
		browser.open("/index");
	}
}
