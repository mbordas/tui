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

package tui.ui.components.layout;

import tui.html.HTMLConstants;
import tui.html.HTMLNode;
import tui.json.JsonMap;
import tui.ui.components.UIComponent;

import java.util.LinkedHashMap;
import java.util.Map;

public class TabbedFlow extends UIComponent {

	public static final String TABBED_PANEL_JSON_TYPE = "tab";

	public static final String HTML_CLASS_TABNAV = "tui-tabnav";
	public static final String HTML_CLASS_TABLINK = "tui-tablink";
	public static final String HTML_CLASS_TABLINK_ACTIVE = "tui-tablink-active";

	private final Map<String /* title */, VerticalFlow> m_content = new LinkedHashMap<>();

	public VerticalFlow createTab(String label) {
		final VerticalFlow result = new VerticalFlow();
		result.setWidth(Layouts.Width.MAX);
		m_content.put(label, result);
		return result;
	}

	@Override
	public HTMLNode toHTMLNode() {
		final HTMLNode result = new HTMLNode("div");
		final HTMLNode tabsNav = result.createChild("div")
				.setAttribute("class", HTML_CLASS_TABNAV);

		int index = 1;
		for(Map.Entry<String, VerticalFlow> tabEntry : m_content.entrySet()) {
			final String label = tabEntry.getKey();
			final VerticalFlow flow = tabEntry.getValue();

			final String onClickCode = String.format("selectTab('%s', this)", HTMLConstants.toId(flow.getTUID()));
			tabsNav.createChild("button")
					.setAttribute("class", index == 1 ? HTML_CLASS_TABLINK + " " + HTML_CLASS_TABLINK_ACTIVE : HTML_CLASS_TABLINK)
					.setAttribute("onclick", onClickCode)
					.setText(label);

			final HTMLNode flowNode = flow.toHTMLNode();
			flowNode.setAttribute("id", HTMLConstants.toId(flow.getTUID())); // Needed by the function selectTab
			flowNode.setStyleProperty("display", index == 1 ? "block" : "none");
			flowNode.setStyleProperty("width", "100%");
			flowNode.addClass("tui-tab");
			result.addChild(flowNode);

			index++;
		}
		return result;
	}

	@Override
	public JsonMap toJsonMap() {
		return null;
	}

}
