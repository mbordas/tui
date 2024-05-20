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

package tui.json;

import tui.test.components.TTable;
import tui.ui.Table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JsonTable extends JsonMap {

	public static final String JSON_TYPE = "table";

	public static final String ATTRIBUTE_SOURCE = "source";

	public JsonTable(long tuid) {
		super(JSON_TYPE);
	}

	public static JsonMap toJson(Table table) {
		JsonTable result = new JsonTable(table.getTUID());
		result.setAttribute(JsonConstants.ATTRIBUTE_TUID, JsonConstants.toId(table.getTUID()));
		result.setAttribute("title", table.getTitle());

		if(table.getSource() != null) {
			result.setAttribute(ATTRIBUTE_SOURCE, table.getSource());
		}

		final JsonArray thead = result.createArray("thead");
		for(String column : table.getColumns()) {
			thead.add(column);
		}
		final JsonArray tbody = result.createArray("tbody");
		for(List<Object> _row : table.getRows()) {
			final JsonArray row = tbody.createArray();
			for(Object _cell : _row) {
				if(_cell == null) {
					row.add("");
				} else if(_cell instanceof String cellString) {
					row.add(cellString);
				} else {
					throw new JsonException("Unsupported type: %s", _cell.getClass().getCanonicalName());
				}
			}
		}

		return result;
	}

	public static TTable parseJson(String json) {
		final JsonMap map = JsonParser.parseMap(json);
		return parse(map);
	}

	public static TTable parse(JsonMap map) {
		final String title = map.getAttribute("title");
		final long tuid = JsonConstants.readTUID(map);
		final String sourcePath = map.getAttributeOrNull(ATTRIBUTE_SOURCE);

		final JsonArray thead = map.getArray("thead");
		final Collection<String> columns = new ArrayList<>();
		final Iterator<JsonObject> theadIterator = thead.iterator();
		while(theadIterator.hasNext()) {
			final JsonObject columnObject = theadIterator.next();
			if(columnObject instanceof JsonString columnString) {
				columns.add(columnString.getValue());
			} else {
				throw new JsonException("Unexpected json type: %s", columnObject.getClass().getCanonicalName());
			}
		}
		final TTable result = new TTable(tuid, title, columns, sourcePath);

		final JsonArray array = map.getArray("tbody");
		final Iterator<JsonObject> rowIterator = array.iterator();
		while(rowIterator.hasNext()) {
			final JsonObject rowObject = rowIterator.next();
			if(rowObject instanceof JsonArray rowArray) {
				Map<String, Object> row = new LinkedHashMap<>();
				int c = 0;
				for(String column : columns) {
					final JsonObject cellObject = rowArray.get(c++);
					if(cellObject instanceof JsonString cellString) {
						row.put(column, cellString.getValue());
					} else {
						throw new JsonException("Unexpected json type: %s", cellObject.getClass().getCanonicalName());
					}
				}
				result.append(row);
			} else {
				throw new JsonException("Unexpected json type: %s", rowObject.getClass().getCanonicalName());
			}
		}

		return result;
	}
}
