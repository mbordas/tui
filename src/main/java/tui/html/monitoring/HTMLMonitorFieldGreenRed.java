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

package tui.html.monitoring;

import tui.html.HTMLConstants;
import tui.html.HTMLNode;
import tui.ui.components.monitoring.MonitorFieldGreenRed;

public class HTMLMonitorFieldGreenRed {

	public static final String CLASS = "tui-monitor-field tui-monitor-field-greenred";
	public static final String CLASS_LABEL = "tui-monitor-field-label";
	public static final String CLASS_VALUE = "tui-monitor-field-value";

	public static HTMLNode toHTML(MonitorFieldGreenRed field) {
		final HTMLNode result = new HTMLNode("div")
				.setAttribute("id", HTMLConstants.toId(field.getTUID()))
				.setAttribute("class", CLASS)
				.setAttribute("monitor-field-name", field.getName())
				.setAttribute("value", field.getValue().name());
		result.createChild("span").setAttribute("class", CLASS_LABEL).setText(field.getLabel());
		result.createChild("span").setAttribute("class", CLASS_VALUE).setText(field.getText());

		return result;
	}
}
