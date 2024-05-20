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

package tui.ui;

import tui.html.HTMLNode;
import tui.html.HTMLSection;
import tui.json.JsonMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Section extends TUIComponent {

	public static final String JSON_TYPE = "section";

	private final String m_title;

	private final List<TUIComponent> m_content = new ArrayList<>();

	public Section(String title) {
		m_title = title;
	}

	public Section createSubSection(String title) {
		final Section result = new Section(title);
		m_content.add(result);
		return result;
	}

	public Paragraph createParagraph(String text) {
		final Paragraph result = new Paragraph(text);
		m_content.add(result);
		return result;
	}

	public String getTitle() {
		return m_title;
	}

	public List<TUIComponent> getContent() {
		return m_content;
	}

	@Override
	public Collection<TUIComponent> getSubComponents() {
		final Collection<TUIComponent> result = new ArrayList<>();
		for(TUIComponent component : m_content) {
			result.add(component);
			final Collection<TUIComponent> subComponents = component.getSubComponents();
			if(subComponents != null) {
				result.addAll(subComponents);
			}
		}
		return result;
	}

	@Override
	public HTMLNode toHTMLNode() {
		return HTMLSection.toHTML(this, 1);
	}

	@Override
	public JsonMap toJsonMap() {
		final JsonMap result = new JsonMap(JSON_TYPE, getTUID());
		result.setAttribute("title", m_title);
		result.createArray("content", m_content, TUIComponent::toJsonMap);
		return result;
	}

}
