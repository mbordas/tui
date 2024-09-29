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
import tui.ui.UI;
import tui.ui.components.Page;
import tui.ui.components.Paragraph;
import tui.ui.components.TablePicker;
import tui.ui.components.layout.Grid;
import tui.ui.components.layout.Layouts;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MailViewer {

	public record Email(String id, String date, String subject, String content) {
	}

	public static void main(String[] args) throws Exception {

		final List<Email> emails = new ArrayList<>();
		for(int i = 0; i < 10; i++) {
			emails.add((new Email(String.valueOf(i), String.format("%d days ago", i + 1), "Subject " + i, "content ".repeat(i + 1))));
		}

		final UI ui = new UI();
		ui.setHTTPBackend("localhost", 8080);
		final Page page = new Page("Home", "/index");
		page.setReadingWidth(Layouts.Width.NORMAL);
		page.setHeader(new Paragraph("Header").setAlign(Layouts.TextAlign.CENTER));
		page.setFooter(new Paragraph().appendNormal("Example footer text").setAlign(Layouts.TextAlign.RIGHT));
		ui.add(page);

		final TablePicker mailSelector = new TablePicker("Inbox", List.of("id", "date", "subject"));
		mailSelector.hideColumn("id");
		mailSelector.hideHead();
		for(Email email : emails) {
			mailSelector.append(Map.of("id", email.id, "date", email.date, "subject", email.subject));
		}
		page.append(mailSelector);

		final Grid mailView = new Grid(2, 1);
		mailView.setSource("/email/view");
		mailSelector.connectListener(mailView);
		page.append(mailView);

		final TUIBackend backend = new TUIBackend(ui);
		backend.registerWebService(mailView.getSource(), (uri, request, response) -> {
			final RequestReader requestReader = new RequestReader(request);
			final String id = requestReader.getStringParameter("id");
			final Email email = emails.stream().filter((_email) -> _email.id.equals(id)).findAny().get();

			final Grid result = new Grid(2, 1);
			final Grid header = new Grid(3, 1);
			header.set(0, 0, new Paragraph().appendStrong("Subject: ").appendNormal(email.subject));
			header.set(1, 0, new Paragraph().appendStrong("Date: ").appendNormal(email.date));
			header.set(2, 0, new Paragraph().appendStrong("Content:"));
			result.set(0, 0, header);
			result.set(1, 0, new Paragraph(email.content).withBorder(true));
			return result.toJsonMap();
		});
		backend.start();

		final Browser browser = new Browser(backend.getPort());
		browser.open("/index");
	}
}
