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
import tui.ui.components.Page;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class JsonParserTest {

	@Test
	public void parseSimpleMap() {
		final JsonMap map = JsonParser.parseMap("{\"key\": \"value\"}");

		assertEquals("value", map.getAttribute("key"));
	}

	@Test
	public void parseMapWithArray() {
		final JsonMap map = JsonParser.parseMap("{\"keys\": [\"v1\",\"v2\"]}");

		final JsonArray array = map.getArray("keys");
		final JsonObject str1 = array.get(0);

		assertTrue(str1 instanceof JsonString);
		assertEquals("v1", ((JsonString) str1).getValue());
	}

	@Test
	public void parseMapWithMap() {
		final String json = """
				{
				  "type" : "formSubmissionResponse",
				  "status" : "ok",
				  "message" : "form submitted",
				  "parameters" : {
				    "pass" : "588827b867e01a041aaf5b2922993b84"
				  }
				}""";

		//
		final JsonMap jsonMap = JsonParser.parseMap(json);
		//

		assertTrue(jsonMap.hasAttribute("status"));
		assertTrue(jsonMap.hasAttribute("message"));
		assertNotNull(jsonMap.getMap("parameters"));

		assertTrue(jsonMap.getMap("parameters").hasAttribute("pass"));
		assertEquals("588827b867e01a041aaf5b2922993b84", jsonMap.getMap("parameters").getAttribute("pass"));
	}

	@Test
	public void parsePageMap() {
		Page page = new Page("My title");
		page.appendSection("My section");

		final String json = page.toJsonMap().toJson();

		//
		final JsonMap jsonMap = JsonParser.parseMap(json);
		//

		assertEquals(Page.JSON_TYPE, jsonMap.getType());
		try {
			jsonMap.getAttribute("type");
			fail();
		} catch(Exception e) {
			assertEquals("Attribute 'type' not found", e.getMessage());
		}
	}

}