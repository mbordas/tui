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

}