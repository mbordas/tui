package tui.http;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TUIWebServiceTest {

	@Test
	public void parsePostMap() {
		final String json = "[[\"Id\",\"002\"],[\"Name\",\"Item-2\"]]";

		//
		final Map<String, String> map = TUIWebService.parsePostMap(json);
		//

		assertNotNull(map);
		assertEquals(2, map.size());
		assertEquals("002", map.get("Id"));
		assertEquals("Item-2", map.get("Name"));
	}

}