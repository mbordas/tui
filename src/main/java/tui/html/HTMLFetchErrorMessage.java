/*
 * Copyright (c) 2012-2024 Smart Grid Energy
 * All Right Reserved
 * http://www.smartgridenergy.fr
 */

package tui.html;

public class HTMLFetchErrorMessage {

	public static final String HTML_CLASS_ERROR_ELEMENT = "fetch-error-message";

	/**
	 * Adds a sub-node to the given {@param node}. This element will be managed by the error handling functions of the JS script.
	 */
	public static HTMLNode addErrorMessageChild(HTMLNode node) {
		return node.createChild("div")
				.setAttribute("class", HTML_CLASS_ERROR_ELEMENT);
	}
}
