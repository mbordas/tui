/*
 * Copyright (c) 2012-2024 Smart Grid Energy
 * All Right Reserved
 * http://www.smartgridenergy.fr
 */

package tui.ui.components;

import tui.html.HTMLConstants;
import tui.html.HTMLFetchErrorMessage;
import tui.html.HTMLNode;
import tui.json.JsonMap;

import java.util.HashMap;
import java.util.Map;

public abstract class UIRefreshableComponent extends UIComponent {

	public static final String ATTRIBUTE_SOURCE = "tui-source";
	public static final String JSON_ATTRIBUTE_PARAMETERS = "parameters";
	public static final String HTML_CLASS_PARAMETERS_DIV = "fetch-parameters";
	public static final String HTML_CONTAINER_CLASS = "tui-refreshable-container";

	protected String m_source;
	protected final Map<String, String> m_parameters = new HashMap<>();

	public String getSource() {
		return m_source;
	}

	public void setSource(String source) {
		m_source = source;
	}

	public boolean hasSource() {
		return m_source != null;
	}

	public void addParameter(String key, String value) {
		if(m_source == null) {
			throw new IllegalStateException("Can't add parameter to a component without source.");
		}
		m_parameters.put(key, value);
	}

	public record ContainedElement(HTMLNode container, HTMLNode element) {

		/**
		 * Gives the container node when it exists, or else the element node.
		 */
		public HTMLNode getHigherNode() {
			return container != null ? container : element;
		}
	}

	/**
	 * Creates the standard node structure for a refreshable element. A container 'div' contains two children: an error 'div' and the element.
	 * Attention: when {@code this} has no source defined (thus it could not be refreshed eventually), then the container node is not created.
	 * Be sure to call {@link ContainedElement#getHigherNode()} when you build HTML page to get the right node to be appended.
	 *
	 * @param tagName        The tag name used to create the element.
	 * @param containerClass The HTML class given to the container 'div'
	 * @return A {@link ContainedElement} that points to both container and element.
	 */
	protected ContainedElement createContainedNode(String tagName, String containerClass) {
		if(hasSource()) {
			final HTMLNode container = new HTMLNode("div");
			container.setAttribute("class", containerClass);
			container.addClass(HTML_CONTAINER_CLASS);
			applyCustomStyle(container);
			if(hasSource()) {
				HTMLFetchErrorMessage.addErrorMessageChild(container);
				if(!m_parameters.isEmpty()) {
					appendParameters(container);
				}
			}
			final HTMLNode element = container.createChild(tagName);
			if(hasSource()) {
				element.setAttribute("id", HTMLConstants.toId(getTUID()));
				element.setAttribute(ATTRIBUTE_SOURCE, getSource());
			}
			return new ContainedElement(container, element);
		} else {
			final HTMLNode element = new HTMLNode(tagName);
			applyCustomStyle(element);
			return new ContainedElement(null, element);
		}
	}

	/**
	 * Parameters are embedded in div. A div element should not be inside a textual element like the paragraph. That's why
	 * it is included in the container.
	 */
	private void appendParameters(HTMLNode container) {
		final HTMLNode parametersDiv = container.createChild("div");
		parametersDiv.setClass("fetch-parameters");
		for(Map.Entry<String, String> entry : m_parameters.entrySet()) {
			parametersDiv.createChild("input")
					.setAttribute("type", "hidden")
					.setAttribute("name", entry.getKey())
					.setAttribute("value", entry.getValue());
		}
	}

	protected void appendParameters(JsonMap jsonMap) {
		if(!m_parameters.isEmpty()) {
			final JsonMap parametersMap = jsonMap.createMap(JSON_ATTRIBUTE_PARAMETERS);
			m_parameters.forEach(parametersMap::setAttribute);
		}
	}
}
