/* Copyright (c) 2025, Mathieu Bordas
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

package tui.ui.components.svg;

import org.jetbrains.annotations.NotNull;
import tui.json.JsonArray;
import tui.json.JsonMap;

import java.util.ArrayList;
import java.util.List;

public class SVGGroup extends SVGComponent {

	public static final String JSON_TYPE = "g";

	private final List<SVGComponent> m_components = new ArrayList<>();

	public <C extends SVGComponent> C add(@NotNull C component) {
		m_components.add(component);
		return component;
	}

	@Override
	public JsonMap toJsonMap() {
		final JsonMap result = super.toJsonMap(JSON_TYPE);
		final JsonArray componentsArray = result.createArray(SVG.JSON_KEY_SUBCOMPONENTS);
		for(SVGComponent component : m_components) {
			componentsArray.add(component.toJsonMap());
		}

		// We only put the 'display' style in order to show or hide on demand.
		// Any other style property, which would be inherited, is unexpected in an SVG group.
		result.setAttribute("style", String.format("display:%s", m_display));
		return result;
	}
}
