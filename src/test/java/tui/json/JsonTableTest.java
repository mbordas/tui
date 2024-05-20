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

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tui.test.components.TTable;
import tui.ui.Table;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class JsonTableTest {

	private static final Logger LOG = LoggerFactory.getLogger(JsonTableTest.class);

	@Test
	public void serialization() {
		final String columnA = "Col A";
		final String columnB = "Col B";
		final Table table = new Table("Table Test Title", List.of(columnA, columnB));
		table.append(row(columnA, "value 1A", columnB, "value 1B"));
		table.append(row(columnA, "value 2A", columnB, "value 2B"));

		final String json = table.toJsonMap().toJson();
		LOG.debug("Table:\n" + json);

		//
		final TTable result = JsonTable.parseJson(json, null);
		//

		assertEquals("Table Test Title", result.getTitle());
		// Checking columns
		assertEquals(columnA, result.getColumns().get(0));
		assertEquals(columnB, result.getColumns().get(1));
		// Checking rows
		assertEquals(2, result.size());
		final List<List<Object>> rows = result.getRows();
		assertEquals("value 1A", rows.get(0).get(0));
		assertEquals("value 1B", rows.get(0).get(1));
		assertEquals("value 2A", rows.get(1).get(0));
		assertEquals("value 2B", rows.get(1).get(1));

	}

	private static Map<String, Object> row(String colA, String valA, String colB, String valB) {
		final Map<String, Object> result = new LinkedHashMap<>();
		result.put(colA, valA);
		result.put(colB, valB);
		return result;
	}

}