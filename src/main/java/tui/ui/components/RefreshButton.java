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

package tui.ui.components;

import tui.html.HTMLNode;
import tui.json.JsonMap;
import tui.test.components.BadComponentException;

import java.util.ArrayList;
import java.util.Collection;

public class RefreshButton extends UIComponent {

	public static final String HTML_CLASS = "tui-refresh-button";
	public static final String JSON_TYPE = "refreshButton";

	public static final String ATTRIBUTE_REFRESH_LISTENERS = "tui-refresh-listeners";

	private final Collection<UIRefreshableComponent> m_connectedComponents = new ArrayList<>();

	private final String m_label;

	public RefreshButton(String label) {
		m_label = label;
	}

	public String getLabel() {
		return m_label;
	}

	public UIRefreshableComponent connectListener(UIRefreshableComponent component) {
		if(component.getSource() == null) {
			throw new BadComponentException("%s must have a source set for reload events.", UIRefreshableComponent.class.getSimpleName());
		}
		m_connectedComponents.add(component);
		return component;
	}

	@Override
	public HTMLNode toHTMLNode() {
		HTMLNode result = new HTMLNode("button")
				.setAttribute("type", "button")
				.setAttribute("class", HTML_CLASS)
				.setText(m_label);
		if(!m_connectedComponents.isEmpty()) {
			result.setAttribute(ATTRIBUTE_REFRESH_LISTENERS, getTUIsSeparatedByComa(m_connectedComponents));
		}
		return result;
	}

	@Override
	public JsonMap toJsonMap() {
		return null;
	}

}
