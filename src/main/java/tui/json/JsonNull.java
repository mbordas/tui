/*
 * Copyright (c) 2012-2026 Smart Grid Energy
 * All Right Reserved
 * http://www.smartgridenergy.fr
 */

package tui.json;

public class JsonNull extends JsonValue {

	public static final String TYPE = "null";

	public JsonNull() {
		super(TYPE, null);
	}

	@Override
	public String toJson() {
		return "null";
	}

	@Override
	public void setPrettyPrintDepth(int depth) {

	}
}
