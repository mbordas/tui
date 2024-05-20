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

package tui.test;

import org.junit.After;
import org.junit.Test;
import tui.http.FormRequest;
import tui.http.TUIBackend;
import tui.test.components.TForm;
import tui.test.components.TTable;
import tui.ui.UI;
import tui.ui.components.Page;
import tui.ui.components.Table;
import tui.ui.components.form.Form;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestClientTest {

	private TUIBackend m_backend;

	@After
	public void before() throws Exception {
		if(m_backend != null) {
			m_backend.stop();
		}
	}

	/**
	 * The tested page is composed of a table and a form. The form is used to add a row to the table.
	 */
	@Test
	public void addANewRowWithForm() throws Exception {
		final String endPointGetTable = "/table/get";
		final String endPointAppendTable = "/table/append";

		final String columnName = "Name";
		final String parameterName = "name";

		// Building frontend

		final UI ui = new UI();
		ui.setHTTPBackend("localhost", 90);
		{
			final Page page = new Page("Test case");
			ui.add(page);
			final Table uiTable = new Table("My table", List.of(columnName));
			uiTable.setSource(endPointGetTable);
			page.append(uiTable);

			final Form form = new Form("My form", endPointAppendTable);
			form.createInputString(columnName, parameterName);
			page.append(form);
			uiTable.connectForRefresh(form);
		}

		// Building backend

		{
			m_backend = new TUIBackend(ui);
			final Table dataTable = new Table("My table", List.of(columnName));
			dataTable.append(Map.of(columnName, "John Doe"));
			m_backend.registerWebService(endPointGetTable, (uri, request, response) -> dataTable.toJsonMap());
			m_backend.registerWebService(endPointAppendTable, (uri, request, response) -> {
				final String nameValue = FormRequest.getStringField(request, parameterName);
				assert nameValue != null;
				dataTable.append(Map.of(columnName, nameValue));
				return Form.getSuccessfulSubmissionResponse();
			});
			m_backend.start();
		}

		// Testing with TestClient

		{
			final TestClient client = new TestClient(ui.getHTTPHost(), ui.getHTTPPort());

			client.open("index");

			final TTable myTable = client.getTable("My table");
			final TForm myForm = client.getForm("My form");

			assertTrue(myTable.isEmpty());
			myForm.enterInput(parameterName, "My Name");
			myForm.submit(); // Sends form data then refreshes table

			assertFalse(myTable.isEmpty());
			assertTrue(myTable.anyCellMatch(columnName, "John Doe")); // initial row
			assertTrue(myTable.anyCellMatch(columnName, "My Name")); // row created with submitted form
		}
	}

}