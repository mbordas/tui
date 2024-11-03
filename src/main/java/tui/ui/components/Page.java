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
import tui.ui.components.layout.Grid;
import tui.ui.components.layout.Layouts;
import tui.ui.components.layout.VerticalFlow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Page {

	public static final String JSON_TYPE = "page";

	public static final String SESSION_PARAMS_MAP_NAME = "SESSION_PARAMS";

	public enum FetchType {JSON, FORM_DATA}

	public record Resource(boolean isExternal, String contentOrLink) {
	}

	private FetchType m_fetchType = FetchType.JSON;
	private Layouts.Width m_width = Layouts.Width.NORMAL;
	private final List<UIComponent> m_content = new ArrayList<>();
	private UIComponent m_header = null;
	private UIComponent m_footer = null;

	// These parameters will be sent to the backend in every request. They could be used to manage user's session.
	private final Map<String /* name */, String /* value */> m_sessionParameters = new HashMap<>();

	protected final String m_title;
	private String m_source;

	public Page(String title) {
		m_title = title;
	}

	public Page(String title, String source) {
		m_title = title;
		m_source = source;
	}

	public String getTitle() {
		return m_title;
	}

	public void setSource(String source) {
		m_source = source;
	}

	public String getSource() {
		return m_source;
	}

	public void setFetchType(FetchType type) {
		m_fetchType = type;
	}

	public void setSessionParameter(String name, String value) {
		m_sessionParameters.put(name, value);
	}

	public <C extends UIComponent> C setHeader(C header) {
		m_header = header;
		return header;
	}

	public <C extends UIComponent> C setFooter(C footer) {
		m_footer = footer;
		return footer;
	}

	public <C extends UIComponent> C append(C component) {
		m_content.add(component);
		return component;
	}

	public List<UIComponent> getContent() {
		return m_content;
	}

	public Section createSection(String title) {
		final Section result = new Section(title);
		m_content.add(result);
		return result;
	}

	public Page setReadingWidth(Layouts.Width width) {
		assert width != null;
		m_width = width;
		return this;
	}

	public HTMLNode toHTMLNode(Resource cssResource, Resource scriptResource) {
		final HTMLNode result = new HTMLNode("html");
		result.setRoot(true);

		final HTMLNode head = result.createChild("head");
		head.createChild("meta").setAttribute("charset", "utf-8");
		head.createChild("meta").setAttribute("name", "viewport")
				.setAttribute("content", "width=device-width, initial-scale=1");
		head.createChild("title").setText(getTitle());
		if(cssResource != null) {
			if(cssResource.isExternal()) {
				head.createChild("link")
						.setAttribute("rel", "stylesheet")
						.setAttribute("href", cssResource.contentOrLink());
			} else {
				head.createChild("style")
						.setText(cssResource.contentOrLink());
			}
		}
		final HTMLNode script = head.createChild("script")
				.setText(generateSessionParametersInitialization(SESSION_PARAMS_MAP_NAME, m_sessionParameters));
		script.appendText("const FETCH_TYPE='%s'", m_fetchType.name());

		if(scriptResource != null) {
			if(scriptResource.isExternal()) {
				head.createChild("script")
						.setAttribute("type", HTMLConstants.JAVASCRIPT_CONTENT_TYPE)
						.setAttribute("src", scriptResource.contentOrLink())
						.setAttribute("defer", null); // the script is meant to be executed after the document has been parsed
			} else {
				script.appendText(scriptResource.contentOrLink());
			}
		}

		final HTMLNode body = result.createChild("body");
		body.setAttribute("onload", "onload()");

		if(m_header != null) {
			final HTMLNode header = body.createChild("header");
			header.append(m_header.toHTMLNode());
		}

		final HTMLNode main = body.createChild("main");

		final VerticalFlow flow = new VerticalFlow();
		flow.setWidth(m_width);
		flow.appendAll(getContent());
		main.append(flow.toHTMLNode());

		if(m_footer != null) {
			final HTMLNode footer = body.createChild("footer");
			footer.append(m_footer.toHTMLNode());
		}

		if(m_header != null || m_footer != null) {
			body.addClass(Grid.HTML_CLASS);
			body.setStyleProperties(computeBodyStyleProperties());
		}

		return result;
	}

	static String generateSessionParametersInitialization(String mapName, Map<String, String> parameters) {
		final StringBuilder result = new StringBuilder();
		result.append(String.format("const %s={", mapName));
		final List<String> parameterAssignations = new ArrayList<>();
		for(Map.Entry<String, String> entry : parameters.entrySet()) {
			parameterAssignations.add(String.format("%s:'%s'", entry.getKey(), entry.getValue()));
		}
		result.append(String.join(",", parameterAssignations));
		result.append("};");
		return result.toString();
	}

	private Map<String, String> computeBodyStyleProperties() {
		return Map.of("grid-template-columns", "100%",
				"grid-template-rows", "min-content auto min-content");
	}

	public JsonMap toJsonMap() {
		final JsonMap result = new JsonMap(JSON_TYPE);
		result.setAttribute("title", m_title);
		result.createArray("content", m_content, UIComponent::toJsonMap);
		return result;
	}

	public HTMLNode toHTMLNode() {
		return toHTMLNode(null, null);
	}

}
