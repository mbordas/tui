/*
 * Copyright (c) 2012-2026 Smart Grid Energy
 * All Right Reserved
 * http://www.smartgridenergy.fr
 */

package tui.json;

public class JsonParserNoDependency {

	public static boolean ENABLED = false;

	private final String m_json;
	private int m_index = 0;

	private JsonParserNoDependency(String json) {
		m_json = json.trim();
	}

	private JsonObject parse() {
		skipWhitespace();
		char c = peek();
		if(c == '{') {
			return parseMap();
		}
		if(c == '[') {
			return parseArray();
		}
		throw new RuntimeException("JSON not as expected");
	}

	private JsonMap parseMap() {
		JsonMap map = new JsonMap(null);
		consume('{');
		skipWhitespace();

		if(peek() == '}') {
			consume('}');
			return map;
		}

		while(true) {
			skipWhitespace();
			JsonString key = parseString();
			skipWhitespace();
			consume(':');
			skipWhitespace();
			JsonObject value = parseValue();
			map.setChild(key.getValue(), value);
			skipWhitespace();

			char c = consume(',', '}');
			if(c == '}') {
				break;
			}
		}

		final String type = map.getAttributeOrNull("type");
		if(type != null) {
			map.setType(type);
			map.removeAttribute("type");
		}

		return map;
	}

	private JsonArray parseArray() {
		JsonArray list = new JsonArray();
		consume('[');
		skipWhitespace();

		if(peek() == ']') {
			consume(']');
			return list;
		}

		while(true) {
			skipWhitespace();
			JsonObject value = parseValue();
			list.add(value);
			skipWhitespace();

			char c = consume(',', ']');
			if(c == ']') {
				break;
			}
		}

		return list;
	}

	private JsonObject parseValue() {
		skipWhitespace();
		char c = peek();

		if(c == '"') {
			return parseString();
		}
		if(c == '{') {
			return parseMap();
		}
		if(c == '[') {
			return parseArray();
		}
		if(c == 't' || c == 'f') {
			return parseBoolean();
		}
		if(c == 'n') {
			return parseNull();
		}
		return parseNumber();
	}

	private JsonNull parseNull() {
		if(m_json.startsWith("null", m_index)) {
			m_index += 4;
			return new JsonNull();
		}
		throw new RuntimeException("null is not as expected");
	}

	private JsonBoolean parseBoolean() {
		if(m_json.startsWith("true", m_index)) {
			m_index += 4;
			return new JsonBoolean(true);
		}
		if(m_json.startsWith("false", m_index)) {
			m_index += 5;
			return new JsonBoolean(false);
		}
		throw new RuntimeException("boolean is not as expected");
	}

	private JsonString parseString() {
		consume('"');
		StringBuilder sb = new StringBuilder();

		while(peek() != '"') {
			char c = next();
			if(c == '\\') {
				char escaped = next();
				switch(escaped) {
				case '"':
					sb.append('"');
					break;
				case '\\':
					sb.append('\\');
					break;
				case '/':
					sb.append('/');
					break;
				case 'b':
					sb.append('\b');
					break;
				case 'f':
					sb.append('\f');
					break;
				case 'n':
					sb.append('\n');
					break;
				case 'r':
					sb.append('\r');
					break;
				case 't':
					sb.append('\t');
					break;
				default:
					throw new RuntimeException("Escape character not as expected");
				}
			} else {
				sb.append(c);
			}
		}

		consume('"');
		return new JsonString(sb.toString());
	}

	private JsonValue<?> parseNumber() {
		int start = m_index;
		while(m_index < m_json.length() && "-0123456789.eE".indexOf(m_json.charAt(m_index)) >= 0) {
			m_index++;
		}
		String num = m_json.substring(start, m_index);
		if(num.indexOf('.') >= 0) {
			return new JsonDouble(Double.parseDouble(num));
		}
		return new JsonLong(Long.parseLong(num));
	}

	/**
	 * Gives the current character. Where the index is pointing to.
	 */
	private char peek() {
		return m_json.charAt(m_index);
	}

	/**
	 * Gives the current character and moves the index to the next character.
	 */
	private char next() {
		return m_json.charAt(m_index++);
	}

	private void skipWhitespace() {
		while(m_index < m_json.length() && Character.isWhitespace(m_json.charAt(m_index))) {
			m_index++;
		}
	}

	private void consume(char expected) {
		if(peek() != expected) {
			throw new RuntimeException("Expected character: " + expected);
		}
		m_index++;
	}

	private char consume(char option1, char option2) {
		char c = peek();
		if(c != option1 && c != option2) {
			throw new RuntimeException("Expected character: " + option1 + " or " + option2);
		}
		m_index++;
		return c;
	}

	public static JsonObject parse(String json) {
		final JsonParserNoDependency parser = new JsonParserNoDependency(json);
		return parser.parse();
	}

	public static JsonMap parseMap(String json) {
		if(ENABLED) {
			final JsonParserNoDependency parser = new JsonParserNoDependency(json);
			return parser.parseMap();
		} else {
			return JsonParser.parseMap(json);
		}
	}
}
