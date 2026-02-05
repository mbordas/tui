/* Copyright (c) 2026, Mathieu Bordas
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

import tui.html.HTMLNode;
import tui.json.JsonMap;

public class ModalPanel extends Panel {

	public static final String HTML_CLASS = "tui-modal-panel";
	public static final String HTML_CLASS_MODAL_PANEL_OPEN_BUTTON = "tui-modal-panel-open-button";
	public static final String HTML_CLASS_CONTAINER = "tui-container-modalpanel";

	public static final String JSON_TYPE = "modalpanel";
	public static final String JSON_OPEN_BUTTON_LABEL = "openButtonLabel";
	public static final String HTML_CLASS_FOOTER = "tui-modalpanel-footer";

	private final String m_openButtonLabel;

	public ModalPanel(Align align, String openButtonLabel) {
		super(align);
		m_openButtonLabel = openButtonLabel;
	}

	public String getOpenButtonLabel() {
		return m_openButtonLabel;
	}

	@Override
	public HTMLNode toHTMLNode() {
		final ContainedElement containedElement = createContainedNode("div", HTML_CLASS_CONTAINER);
		containedElement.element().addClass(HTML_CLASS);

		containedElement.element().createChild("button")
				.setAttribute("class", HTML_CLASS_MODAL_PANEL_OPEN_BUTTON)
				.setText(getOpenButtonLabel());

		final HTMLNode dialog = containedElement.element().createChild("dialog")
				.setAttribute("class", "modal");

		final HTMLNode flowElement = dialog.append(new Panel().toHTMLNode());
		super.fillDivElement(flowElement);

		final HTMLNode footer = dialog.append(new Panel(Align.RIGHT).toHTMLNode());
		footer.addClass(HTML_CLASS_FOOTER);
		footer.createChild("button")
				.addClass("tui-panel-close-button")
				.setAttribute("type", "button")
				.setText("Close");

		return containedElement.getHigherNode();
	}

	@Override
	public JsonMap toJsonMap() {
		final JsonMap result = super.toJsonMap();
		result.setType(JSON_TYPE);
		result.setAttribute(JSON_OPEN_BUTTON_LABEL, m_openButtonLabel);
		return result;
	}

}
