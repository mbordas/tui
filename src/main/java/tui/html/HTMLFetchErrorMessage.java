/*
 * Copyright (c) 2012-2024 Smart Grid Energy
 * All Right Reserved
 * http://www.smartgridenergy.fr
 */

package tui.html;

public class HTMLFetchErrorMessage {

	public static HTMLNode addErrorMessageChild(HTMLNode node) {
		return node.createChild("div")
				.setAttribute("class", "fetch-error-message");
	}
}
