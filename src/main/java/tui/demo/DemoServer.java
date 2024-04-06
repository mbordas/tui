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

import tui.html.HTMLNode;
import tui.http.FormRequest;
import tui.http.TUIServer;
import tui.json.JsonMap;
import tui.json.JsonObject;
import tui.ui.Page;
import tui.ui.TUI;
import tui.ui.Table;
import tui.ui.form.Form;
import tui.ui.form.FormInputString;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DemoServer {

	public static void main(String[] args) throws Exception {

		// Building UI

		final TUI ui = new TUI();

		Page page = new Page("Demo");
		page.createSection("Demo page")
				.createParagraph("Use the form to add row to the table.");
		ui.add(page);

		final String columnVendor = "Vendor";
		final String columnSerialNumber = "Serial number";
		final Table table = new Table("Refreshable table", List.of(columnVendor, columnSerialNumber));
		table.setSource("/demo/table/1");
		page.append(table);

		final Form form = new Form("New element", "/demo/form/1");
		final FormInputString inputVendor = form.createInputString("Vendor", "vendor");
		final FormInputString inputSerialNumber = form.createInputString("Serial number", "serial_number");
		table.connectForRefresh(form);
		page.append(form);

		// Building server with backend web services

		final TUIServer server = new TUIServer(ui);

		// Called when form is submitted
		server.registerWebService(form.getTarget(), (uri, request, response) -> {
			final String serialNumber = FormRequest.getStringField(request, inputSerialNumber.getName());
			final String vendor = FormRequest.getStringField(request, inputVendor.getName());
			Map<String, Object> row = new LinkedHashMap<>();
			row.put(columnVendor, vendor);
			row.put(columnSerialNumber, serialNumber);
			table.append(row);
			return new JsonMap("message").
					setAttribute("value", "form submitted");
		});

		// Called when table is refreshed
		server.registerWebService(table.getSource(), (uri, request, response) -> table.toJsonObject());

		HTMLNode.PRETTY_PRINT = true;
		JsonObject.PRETTY_PRINT = true;
		server.start(80);
	}
}
