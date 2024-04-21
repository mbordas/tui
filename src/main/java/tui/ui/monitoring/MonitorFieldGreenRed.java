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

package tui.ui.monitoring;

import tui.html.HTMLNode;
import tui.html.monitoring.HTMLMonitorFieldGreenRed;
import tui.json.JsonObject;
import tui.json.monitor.JsonMonitorFieldGreenRed;

public class MonitorFieldGreenRed extends MonitorField {

	public enum Value {
		GREEN, RED, NEUTRAL
	}

	private Value m_value = Value.NEUTRAL;

	public MonitorFieldGreenRed(String name, String label) {
		super(name, label);
	}

	public MonitorFieldGreenRed set(Value value, String text) {
		m_value = value;
		setText(text);
		return this;
	}

	public Value getValue() {
		return m_value;
	}

	@Override
	public HTMLNode toHTMLNode() {
		return HTMLMonitorFieldGreenRed.toHTML(this);
	}

	@Override
	public JsonObject toJsonObject() {
		return JsonMonitorFieldGreenRed.toJson(this);
	}
}
