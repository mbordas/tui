/*
 * Copyright (c) 2012-2026 Smart Grid Energy
 * All Right Reserved
 * http://www.smartgridenergy.fr
 */

package tui.json;

public class JsonBoolean extends JsonValue<Boolean> {

	public static final String TYPE = "boolean";

	public JsonBoolean(boolean value) {
		super(TYPE, value);
	}

	@Override
	public String toJson() {
		return getValue().toString();
	}

	@Override
	public void setPrettyPrintDepth(int depth) {

	}
}
