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
import tui.test.TClient;
import tui.ui.components.List;
import tui.ui.components.NavLink;
import tui.ui.components.Paragraph;
import tui.ui.components.RefreshButton;
import tui.ui.components.Section;
import tui.ui.components.Table;
import tui.ui.components.TablePicker;
import tui.ui.components.form.Form;
import tui.ui.components.form.ModalForm;
import tui.ui.components.form.Search;
import tui.ui.components.layout.Grid;
import tui.ui.components.layout.Panel;
import tui.ui.components.layout.TabbedFlow;
import tui.ui.components.layout.VerticalFlow;

public class TComponentFactory {

	public static TComponent parse(String json, TClient client) {
		final JsonMap map = JsonParser.parseMap(json);
		return switch(map.getType()) {
			case TabbedFlow.TABBED_PANEL_JSON_TYPE -> TTabbedPanel.parse(map, client);
			case Panel.JSON_TYPE -> TPanel.parse(map, client);
			case Paragraph.JSON_TYPE -> TParagraph.parse(map, client);
			case Paragraph.Text.JSON_TYPE -> TParagraph.TText.parse(map, client);
			case Section.JSON_TYPE -> TSection.parse(map, client);
			case Table.JSON_TYPE -> TTable.parse(map, client);
			case TablePicker.JSON_TYPE -> TTablePicker.parse(map, client);
			case Form.JSON_TYPE -> TForm.parse(map, client);
			case ModalForm.JSON_TYPE -> TModalForm.parse(map, client);
			case Search.JSON_TYPE -> TSearch.parse(map, client);
			case VerticalFlow.JSON_TYPE -> TVerticalFlow.parse(map, client);
			case TabbedFlow.JSON_TYPE -> TTabbedFlow.parse(map, client);
			case Grid.JSON_TYPE -> TGrid.parse(map, client);
			case RefreshButton.JSON_TYPE -> TRefreshButton.parse(map, client);
			case NavLink.JSON_TYPE -> TNavLink.parse(map, client);
			case List.JSON_TYPE -> TList.parse(map, client);
			default -> throw new IllegalStateException("Unexpected value: " + map.getType());
		};
	}

	public static TComponent parse(JsonObject json, TClient tClient) {
		return parse(json.toJson(), tClient);
	}

}
