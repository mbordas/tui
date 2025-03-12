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
import tui.ui.style.TextStyleSet;

import java.util.ArrayList;
import java.util.List;

public class Section extends UIComponent {

	public static final String JSON_TYPE = "section";

	public enum DisclosureType {NONE, STARTS_OPENED, STARTS_CLOSED}

	private final String m_title;
	private TextStyleSet m_customStyleHeader = null;
	private DisclosureType m_disclosureType = DisclosureType.NONE;
	private final List<UIComponent> m_content = new ArrayList<>();

	public Section(String title) {
		m_title = title;
	}

	public Section withDisclosureType(DisclosureType type) {
		m_disclosureType = type;
		return this;
	}

	public TextStyleSet customStyleForHeader() {
		if(m_customStyleHeader == null) {
			m_customStyleHeader = new TextStyleSet();
		}
		return m_customStyleHeader;
	}

	public Section createSubSection(String format, Object... args) {
		final Section result = new Section(String.format(format, args));
		m_content.add(result);
		return result;
	}

	public Paragraph appendParagraph(String format, Object... args) {
		final Paragraph result = new Paragraph(String.format(format, args));
		m_content.add(result);
		return result;
	}

	public <C extends UIComponent> C append(C component) {
		m_content.add(component);
		return component;
	}

	public String getTitle() {
		return m_title;
	}

	public List<UIComponent> getContent() {
		return m_content;
	}

	@Override
	public HTMLNode toHTMLNode() {
		return toHTML(this, 1);
	}

	private static HTMLNode toHTML(Section section, int depth) {
		final HTMLNode result = new HTMLNode("section");

		final HTMLNode containerNodeForTitle;
		final HTMLNode containerNodeForContent;
		if(section.m_disclosureType != DisclosureType.NONE) {
			containerNodeForContent = result.createChild("details");
			if(section.m_disclosureType == DisclosureType.STARTS_OPENED) {
				containerNodeForContent.setAttribute("open", null);
			}
			containerNodeForTitle = containerNodeForContent.createChild("summary");
		} else {
			containerNodeForTitle = result;
			containerNodeForContent = result;
		}

		if(section.m_customStyleHeader != null) {
			section.m_customStyleHeader.apply(containerNodeForTitle);
		}
		final HTMLNode header = containerNodeForTitle.createChild("h" + depth).setText(section.getTitle());
		if(section.m_disclosureType != DisclosureType.NONE) {
			header.setStyleProperty("display", "inline");
		}

		for(UIComponent component : section.getContent()) {
			final HTMLNode child;
			if(component instanceof Section _section) {
				child = toHTML(_section, depth + 1);
			} else {
				child = component.toHTMLNode();
			}
			containerNodeForContent.append(child);
		}

		section.applyCustomStyle(result);

		return result;
	}

	@Override
	public JsonMap toJsonMap() {
		final JsonMap result = new JsonMap(JSON_TYPE, getTUID());
		result.setAttribute("title", m_title);
		result.setAttribute("disclosureType", m_disclosureType.name());
		result.createArray("content", m_content, UIComponent::toJsonMap);
		if(m_customStyleHeader != null) {
			m_customStyleHeader.apply(result.createMap("customStyleHeader"));
		}
		return result;
	}

}
