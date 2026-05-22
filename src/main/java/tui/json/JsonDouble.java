/*
 * Copyright (c) 2012-2026 Smart Grid Energy
 * All Right Reserved
 * http://www.smartgridenergy.fr
 */

package tui.json;

public class JsonDouble extends JsonValue<Double> {

	public static final String TYPE = "double";

	public JsonDouble(double value) {
		super(TYPE, value);
	}

	@Override
	public String toJson() {
		return String.format("%f", getValue());
	}

	@Override
	public void setPrettyPrintDepth(int depth) {

	}
}
