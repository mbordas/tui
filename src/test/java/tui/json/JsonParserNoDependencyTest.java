package tui.json;

import junit.framework.TestCase;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import tui.ui.components.Page;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class JsonParserNoDependencyTest {

	@BeforeClass
	public static void beforeClass() {
		JsonParserNoDependency.ENABLED = true;
	}

	@AfterClass
	public static void afterClass() {
		JsonParserNoDependency.ENABLED = false;
	}

	@Test
	public void parseArrayWithObjects() {
		final String json = """
				[{"a":"b"},10]
				""";

		final JsonObject jsonObject = JsonParserNoDependency.parse(json);
		assertTrue(jsonObject instanceof JsonArray);
		final JsonArray jsonArray = (JsonArray) jsonObject;
		assertTrue(jsonArray.get(0) instanceof JsonMap);
		assertTrue(jsonArray.get(1) instanceof JsonLong);
	}

	@Test
	public void parseSimpleMap() {
		final JsonMap map = JsonParserNoDependency.parseMap("{\"key\": \"value\"}");

		TestCase.assertEquals("value", map.getAttribute("key"));
	}

	@Test
	public void parseMapWithArray() {
		final JsonMap map = JsonParserNoDependency.parseMap("{\"keys\": [\"v1\",\"v2\"]}");

		final JsonArray array = map.getArray("keys");
		final JsonObject str1 = array.get(0);

		assertTrue(str1 instanceof JsonString);
		TestCase.assertEquals("v1", ((JsonString) str1).getValue());
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
		final JsonMap jsonMap = JsonParserNoDependency.parseMap(json);
		//

		assertTrue(jsonMap.hasAttribute("status"));
		assertTrue(jsonMap.hasAttribute("message"));
		assertNotNull(jsonMap.getMap("parameters"));

		assertTrue(jsonMap.getMap("parameters").hasAttribute("pass"));
		TestCase.assertEquals("588827b867e01a041aaf5b2922993b84", jsonMap.getMap("parameters").getAttribute("pass"));
	}

	@Test
	public void parsePageMap() {
		Page page = new Page("My title");
		page.appendSection("My section");

		final String json = page.toJsonMap().toJson();

		//
		final JsonMap jsonMap = JsonParserNoDependency.parseMap(json);
		//

		TestCase.assertEquals(Page.JSON_TYPE, jsonMap.getType());
		try {
			jsonMap.getAttribute("type");
			fail();
		} catch(Throwable e) {
			TestCase.assertEquals("Attribute 'type' not found", e.getMessage());
		}
	}

	@Test
	public void parseMap() {
		final String json = """
				         {
				         "name": "Json Map",
				         "age": 30,
				         "enabled": true,
				         "values": [10, 20.5, 30],
				         "address": {
				             "city": "Paris",
				             "code": 75000
				         }
				     }
				""";

		final JsonMap jsonMap = JsonParserNoDependency.parseMap(json);

		System.out.println(jsonMap.toJson());

		assertEquals("Json Map", jsonMap.getAttribute("name"));
		assertEquals(30L, jsonMap.getLongAttribute("age"));
		assertTrue(jsonMap.getBooleanAttribute("enabled"));

		final JsonArray array = jsonMap.getArray("values");
		assertEquals(3, array.size());
		assertEquals(JsonLong.TYPE, array.get(0).getType());
		assertEquals(10L, (long) ((JsonLong) array.get(0)).getValue());
		assertEquals(JsonDouble.TYPE, array.get(1).getType());
		assertEquals(20.5, ((JsonDouble) array.get(1)).getValue(), 0.001);
	}

	@Test
	public void parseHardContent() {
		final String json = """
				{
				  "meta": {
				    "version": "1.0.0",
				    "description": "JSON de test avec caractères spéciaux: \\" \\\\ / \\b \\f \\n \\r \\t 😀",
				    "multiline": "Ligne 1\\nLigne 2\\nLigne 3 avec tab\\tet retour\\r"
				  },
				
				  "weird_keys": {
				    "": "clé vide",
				    "   ": "clé avec espaces",
				    "\\"quoted\\"": "clé contenant des guillemets",
				    "emoji_😀": "clé unicode",
				    "line\\nbreak": "clé avec retour ligne"
				  },
				
				  "deep_nesting": {
				    "lvl1": {
				      "lvl2": {
				        "lvl3": {
				          "lvl4": {
				            "lvl5": {
				              "value": "Tu es arrivé jusqu'ici"
				            }
				          }
				        }
				      }
				    }
				  },
				
				  "array_mess": [
				    123,
				    null,
				    true,
				    false,
				    "string",
				    ["nested", ["very", ["deep", ["array"]]]],
				    { "obj": { "inside": { "array": "ok" } } }
				  ],
				
				  "special_strings": {
				    "escaped": "Texte avec échappements: \\\\\\" \\\\\\\\ \\\\/ \\\\n \\\\t",
				    "unicode": "雪, 火, 水, 🌍",
				    "json_in_string": "{ \\"fake\\": true, \\"nested\\": [1,2,3] }"
				  },
				
				  "edge_cases": {
				    "float_precision": 0.000000000000123,
				    "negative_zero": -0,
				    "inf_string": "Infinity",
				    "nan_string": "NaN"
				  },
				
				  "circular_reference_simulated": {
				    "self": "#ref:$.circular_reference_simulated"
				  },
				
				  "mixed_types": {
				    "bool_as_string": "true",
				    "null_as_string": "null",
				    "number_as_string": "42",
				    "empty_array": [],
				    "empty_object": {}
				  }
				}
				""";

		//
		final JsonObject jsonObject = JsonParserNoDependency.parse(json);
		//

		assertTrue(jsonObject instanceof JsonMap);

		final JsonMap map = (JsonMap) jsonObject;
		assertEquals("clé unicode", map.getMap("weird_keys").getAttribute("emoji_😀"));
		assertEquals("Ligne 1\nLigne 2\nLigne 3 avec tab\tet retour\r", map.getMap("meta").getAttribute("multiline"));
		assertEquals("雪, 火, 水, 🌍", map.getMap("special_strings").getAttribute("unicode"));
		assertEquals(0.000000000000123, map.getMap("edge_cases").getDoubleAttribute("float_precision"), 0.0);
	}

}