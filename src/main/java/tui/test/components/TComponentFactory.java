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

package tui.test.components;

import tui.json.JsonMap;
import tui.json.JsonObject;
import tui.json.JsonParser;
import tui.json.JsonTable;
import tui.test.TestClient;
import tui.ui.components.Page;
import tui.ui.components.Section;
import tui.ui.components.form.Form;

public class TComponentFactory {

	public static TComponent parse(String json, TestClient testClient) {
		final JsonMap map = JsonParser.parseMap(json);
		return switch(map.getType()) {
			case Page.JSON_TYPE -> TPage.parse(map, testClient);
			case Section.JSON_TYPE -> TSection.parse(map, testClient);
			case JsonTable.JSON_TYPE -> JsonTable.parse(map, testClient);
			case Form.JSON_TYPE -> TForm.parse(map, testClient);
			default -> throw new IllegalStateException("Unexpected value: " + map.getType());
		};
	}

	public static TComponent parse(JsonObject json, TestClient testClient) {
		return parse(json.toJson(), testClient);
	}

}
