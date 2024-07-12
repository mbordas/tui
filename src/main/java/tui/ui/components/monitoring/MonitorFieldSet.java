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

import tui.html.HTMLFetchErrorMessage;
import tui.html.HTMLNode;
import tui.json.JsonMap;
import tui.ui.components.UIComponent;

import java.util.ArrayList;
import java.util.List;

public class MonitorFieldSet extends UIComponent {

	public static final String JSON_TYPE = "monitor_fieldset";
	public static final String HTML_CLASS = "tui-monitor-fieldset";

	private final String m_title;
	private final List<MonitorField> m_fields = new ArrayList<>();
	private String m_source = null;
	private int m_autoRefreshPeriod_s = 5;

	public MonitorFieldSet(String title) {
		m_title = title;
	}

	public void setSource(String source) {
		m_source = source;
	}

	public String getSource() {
		return m_source;
	}

	public void setAutoRefreshPeriod_s(int period_s) {
		m_autoRefreshPeriod_s = period_s;
	}

	public int getAutoRefreshPeriod_s() {
		return m_autoRefreshPeriod_s;
	}

	public String getTitle() {
		return m_title;
	}

	public List<MonitorField> getFields() {
		return m_fields;
	}

	public MonitorFieldGreenRed createFieldGreenRed(String name, String label) {
		final MonitorFieldGreenRed result = new MonitorFieldGreenRed(name, label);
		m_fields.add(result);
		return result;
	}

	@Override
	public HTMLNode toHTMLNode() {
		final HTMLNode result = new HTMLNode("div")
				.setAttribute("class", HTML_CLASS);

		HTMLFetchErrorMessage.addErrorMessageChild(result);

		if(getTitle() != null) {
			result.setAttribute("title", getTitle());
		}
		if(getSource() != null) {
			result.setAttribute("tui-source", getSource())
					.setAttribute("auto-refresh-period_s", getAutoRefreshPeriod_s());
		}

		for(MonitorField field : getFields()) {
			result.addChild(field.toHTMLNode());
		}
		return result;
	}

	@Override
	public JsonMap toJsonMap() {
		final JsonMap result = new JsonMap(JSON_TYPE);
		result.setAttribute("title", m_title);
		result.setAttribute("source", m_source);
		result.setAttribute("auto_refresh_period_s", String.valueOf(m_autoRefreshPeriod_s));
		result.createArray("fields", m_fields, MonitorField::toJsonMap);
		return result;
	}

}
