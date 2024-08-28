/*
 * Copyright (c) 2012-2024 Smart Grid Energy
 * All Right Reserved
 * http://www.smartgridenergy.fr
 */

package tui.ui.components;

import tui.html.HTMLConstants;
import tui.html.HTMLFetchErrorMessage;
import tui.html.HTMLNode;

public abstract class UIRefreshableComponent extends UIComponent {

	public static final String ATTRIBUTE_SOURCE = "tui-source";

	protected String m_source;

	public String getSource() {
		return m_source;
	}

	public void setSource(String source) {
		m_source = source;
	}

	public boolean hasSource() {
		return m_source != null;
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
	 * Creates the standard node structure for refreshable element. A container 'div' contains two children: an error 'div' and the element.
	 * Attention: when {@code this} has no source defined (thus it could not be refreshed eventually), then the container node is not created.
	 * Be sure to call {@link ContainedElement#getHigherNode()} when you build HTML page in order to get the right node to be appended.
	 *
	 * @param tagName        The tag name used to create the element.
	 * @param containerClass The HTML class given to the container 'div'
	 * @return A {@link ContainedElement} that points two both container and element.
	 */
	protected ContainedElement createContainedNode(String tagName, String containerClass) {
		if(hasSource()) {
			final HTMLNode container = new HTMLNode("div");
			container.setAttribute("class", containerClass);
			if(hasSource()) {
				HTMLFetchErrorMessage.addErrorMessageChild(container);
			}

			final HTMLNode element = container.createChild(tagName);
			if(hasSource()) {
				element.setAttribute("id", HTMLConstants.toId(getTUID()));
				element.setAttribute(ATTRIBUTE_SOURCE, getSource());
			}
			return new ContainedElement(container, element);
		} else {
			return new ContainedElement(null, new HTMLNode(tagName));
		}
	}
}
