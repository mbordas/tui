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

package tui.ui.components.svg.defs;

import tui.json.JsonArray;
import tui.json.JsonMap;
import tui.ui.components.svg.SVG;
import tui.ui.components.svg.SVGComponent;
import tui.ui.components.svg.SVGLine;
import tui.ui.components.svg.SVGRectangle;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;

public class SVGPatternStripes extends SVGComponent {

	public static final String JSON_TYPE = "pattern";

	private final String m_id;
	private final int m_length;
	private int m_rotate_deg = 45;

	private final Collection<SVGComponent> m_components = new ArrayList<>();

	public SVGPatternStripes(String id, int width, Color stripeColor, int spacing, Color spaceColor) {
		m_id = id;
		m_length = width + spacing;
		final SVGRectangle rectangle = add(new SVGRectangle(0, 0, m_length, m_length));
		rectangle.withStrokeOpacity(0.0);
		rectangle.withFillColor(spaceColor);
		final SVGLine line = add(new SVGLine(m_length / 2, 0, m_length / 2, m_length));
		line.withStrokeWidth(width);
		line.withStrokeColor(stripeColor);
	}

	public String getId() {
		return m_id;
	}

	public <C extends SVGComponent> C add(C component) {
		m_components.add(component);
		return component;
	}

	@Override
	public JsonMap toJsonMap() {
		final JsonMap result = super.toJsonMap(JSON_TYPE);
		result.setAttribute("id", m_id);
		result.setAttribute("patternUnits", "userSpaceOnUse");
		result.setAttribute("width", String.valueOf(m_length));
		result.setAttribute("height", String.valueOf(m_length));
		result.setAttribute("patternTransform", String.format("rotate(%d)", m_rotate_deg));
		final JsonArray components = result.createArray(SVG.JSON_KEY_SUBCOMPONENTS);
		for(SVGComponent component : m_components) {
			final JsonMap jsonMap = component.toJsonMap();
			components.add(jsonMap);
		}
		return result;
	}

}
