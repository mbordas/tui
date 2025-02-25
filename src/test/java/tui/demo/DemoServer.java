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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tui.html.HTMLNode;
import tui.http.RequestReader;
import tui.http.TUIBackend;
import tui.json.JsonObject;
import tui.test.Browser;
import tui.ui.components.Page;
import tui.ui.components.Section;
import tui.ui.components.Table;
import tui.ui.components.form.Form;
import tui.ui.components.form.FormInputString;
import tui.ui.components.form.ModalForm;
import tui.ui.components.layout.TabbedFlow;
import tui.ui.components.layout.VerticalFlow;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DemoServer {

	private static final Logger LOG = LoggerFactory.getLogger(DemoServer.class);

	public static void main(String[] args) throws Exception {

		final Map<String, String> initialRows = new LinkedHashMap<>();
		for(int i = 0; i < 10; i++) {
			initialRows.put("Vendor #" + i, "SN_" + (i * 1010));
		}

		// Building UI

		Page page = new Page("Demo", "/index");
		final TabbedFlow tabbedFlow = page.append(new TabbedFlow());
		final VerticalFlow panel1 = tabbedFlow.createTab("Table and form");
		panel1.append(new Section("Tables"))
				.appendParagraph("Use the form to add row to the table.");

		final String columnVendor = "Vendor";
		final String columnSerialNumber = "Serial number";
		final Table table = new Table("Refreshable table", List.of(columnVendor, columnSerialNumber));
		for(Map.Entry<String, String> row : initialRows.entrySet()) {
			table.append(Map.of("Vendor", row.getKey(), "Serial number", row.getValue()));
		}
		table.setSource("/demo/table/1");
		panel1.append(table);

		final Form form = new ModalForm("New element", "Add", "/demo/form/1");
		final FormInputString inputVendor = form.createInputString("Vendor", "vendor");
		final FormInputString inputSerialNumber = form.createInputString("Serial number", "serial_number");
		table.connectForRefresh(form);
		panel1.append(form);

		final VerticalFlow panel3 = tabbedFlow.createTab("Modal form");
		final ModalForm modalForm = new ModalForm("Add a new element", "Add", "/modalform/add");
		final FormInputString modalFormFieldName = modalForm.createInputString("Name", "name");
		panel3.append(new Section("Modal form"))
				.appendParagraph("Modal forms use dialog elements.");
		panel3.append(modalForm);

		// Building server with backend web services

		final TUIBackend backend = new TUIBackend(8080);
		backend.registerPage(page);

		// Called when form is submitted
		backend.registerWebService(form.getTarget(), (uri, request, response) -> {
			final RequestReader reader = new RequestReader(request);
			final String serialNumber = reader.getStringParameter(inputSerialNumber.getName());
			final String vendor = reader.getStringParameter(inputVendor.getName());
			Map<String, Object> row = new LinkedHashMap<>();
			row.put(columnVendor, vendor);
			row.put(columnSerialNumber, serialNumber);
			table.append(row);
			return Form.buildSuccessfulSubmissionResponse();
		});

		// Called when table is refreshed
		backend.registerWebService(table.getSource(), (uri, request, response) -> table.toJsonMap());

		backend.registerWebService(modalForm.getTarget(), (uri, request, response) -> {
			final RequestReader reader = new RequestReader(request);
			final String nameValue = reader.getStringParameter(modalFormFieldName.getName());
			LOG.info("Modal form submitted with parameter name=" + nameValue);
			if(nameValue.isEmpty()) {
				Map<String, String> errors = new HashMap<>();
				errors.put(modalFormFieldName.getName(), "Value cannot be empty");
				return Form.buildFailedSubmissionResponse("Errors found in form data", errors);
			} else {
				return Form.buildSuccessfulSubmissionResponse();
			}
		});

		HTMLNode.PRETTY_PRINT = true;
		JsonObject.PRETTY_PRINT = true;
		backend.start();

		final Browser browser = new Browser(backend.getPort());
		browser.open(page.getSource());
	}
}
