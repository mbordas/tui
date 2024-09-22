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

package tui.ui.components.monitoring;

import org.jetbrains.annotations.NotNull;
import tui.html.HTMLConstants;
import tui.html.HTMLNode;
import tui.json.JsonMap;

public class MonitorFieldGreenRed extends MonitorField implements Comparable<MonitorField> {

	public static final String HTML_CLASS = HTML_CLASS_BASE + " tui-monitor-field-greenred";
	public static final String HTML_CLASS_LABEL = "tui-monitor-field-label";
	public static final String HTML_CLASS_VALUE = "tui-monitor-field-value";

	public static final String JSON_TYPE = "monitor-field-greenred";

	public enum Value {
		GREEN, RED, NEUTRAL
	}

	private Value m_value = Value.NEUTRAL;
	private boolean m_displayLabel = true;

	/**
	 * @param label Note that it will be used for sorting.
	 */
	public MonitorFieldGreenRed(String name, String label) {
		super(name, label);
	}

	public MonitorFieldGreenRed set(Value value, String text) {
		m_value = value;
		setText(text);
		return this;
	}

	public void displayLabel(boolean enable) {
		m_displayLabel = enable;
	}

	public Value getValue() {
		return m_value;
	}

	@Override
	public int compareTo(@NotNull MonitorField other) {
		return getLabel().compareTo(other.getLabel());
	}

	@Override
	public HTMLNode toHTMLNode() {
		final HTMLNode result = new HTMLNode("div")
				.setAttribute("id", HTMLConstants.toId(getTUID()))
				.setAttribute("class", HTML_CLASS)
				.setAttribute("monitor-field-name", getName())
				.setAttribute("value", getValue().name());
		if(m_displayLabel) {
			result.createChild("span").setAttribute("class", HTML_CLASS_LABEL).setText(getLabel());
		}
		result.createChild("span").setAttribute("class", HTML_CLASS_VALUE).setText(getText());

		return result;
	}

	@Override
	public JsonMap toJsonMap() {
		return new JsonMap(JSON_TYPE, getTUID())
				.setAttribute("name", getName())
				.setAttribute("value", getValue().name())
				.setAttribute("text", getText());
	}
}
