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

import tui.html.HTMLConstants;
import tui.html.HTMLNode;
import tui.json.JsonMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Page extends APage {

	public static final String JSON_TYPE = "page";

	private final List<UIComponent> m_content = new ArrayList<>();

	public Page(String title) {
		super(title);
	}

	public void append(UIComponent component) {
		m_content.add(component);
	}

	public List<UIComponent> getContent() {
		return m_content;
	}

	public Section createSection(String title) {
		final Section result = new Section(title);
		m_content.add(result);
		return result;
	}

	public HTMLNode toHTMLNode(String pathToCSS, String pathToScript, String onLoadFunctionCall) {
		final HTMLNode result = new HTMLNode("html");
		result.setRoot(true);

		final HTMLNode head = result.createChild("head");
		head.createChild("meta").setAttribute("charset", "utf-8");
		head.createChild("meta").setAttribute("name", "viewport")
				.setAttribute("content", "width=device-width, initial-scale=1");
		head.createChild("title").setText(getTitle());
		if(pathToCSS != null) {
			head.createChild("link")
					.setAttribute("rel", "stylesheet")
					.setAttribute("href", pathToCSS);
		}
		if(pathToScript != null) {
			head.createChild("script")
					.setAttribute("type", HTMLConstants.JAVASCRIPT_CONTENT_TYPE)
					.setAttribute("src", pathToScript)
					.setAttribute("defer", null); // the script is meant to be executed after the document has been parsed
		}

		final HTMLNode body = result.createChild("body");
		if(onLoadFunctionCall != null) {
			body.setAttribute("onload", onLoadFunctionCall);
		}
		final HTMLNode main = body.createChild("main");
		for(UIComponent component : getContent()) {
			main.addChild(component.toHTMLNode());
		}

		return result;
	}

	@Override
	public JsonMap toJsonMap() {
		final JsonMap result = new JsonMap(JSON_TYPE, getTUID());
		result.setAttribute("title", m_title);
		result.createArray("content", m_content, UIComponent::toJsonMap);
		return result;
	}

	@Override
	public Collection<UIComponent> getSubComponents() {
		final Collection<UIComponent> result = new ArrayList<>();
		for(UIComponent component : m_content) {
			result.add(component);
			final Collection<UIComponent> subComponents = component.getSubComponents();
			if(subComponents != null) {
				result.addAll(subComponents);
			}
		}
		return result;
	}

	@Override
	public HTMLNode toHTMLNode() {
		return toHTMLNode(null, null, null);
	}

}
