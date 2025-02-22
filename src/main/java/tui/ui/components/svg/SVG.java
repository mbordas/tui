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

package tui.ui.components.svg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tui.html.HTMLConstants;
import tui.html.HTMLNode;
import tui.json.JsonArray;
import tui.json.JsonMap;
import tui.json.JsonObject;
import tui.json.JsonValue;
import tui.ui.components.UIRefreshableComponent;
import tui.ui.components.svg.defs.SVGMarker;
import tui.ui.components.svg.defs.SVGPatternStripes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class SVG extends UIRefreshableComponent {

	private static final Logger LOG = LoggerFactory.getLogger(SVG.class);

	public static final String JSON_TYPE = "svg";
	public static final String JSON_ATTRIBUTE_INNER_TEXT = "innerText";
	public static final String JSON_ATTRIBUTE_TITLE = "title";
	public static final String JSON_KEY_SUBCOMPONENTS = "components";

	public static final String HTML_CLASS_CONTAINER = "tui-container-svg";

	private final List<SVGComponent> m_markers = new ArrayList<>();
	private final List<SVGComponent> m_patterns = new ArrayList<>();
	private final List<SVGComponent> m_components = new ArrayList<>();
	private int m_width_px;
	private int m_height_px;
	private ViewBox m_viewBox = null;

	record ViewBox(long x, long y, long width, long height) {
	}

	public SVG(int width_px, int height_px) {
		m_width_px = width_px;
		m_height_px = height_px;
	}

	public SVGMarker addMarker(SVGMarker marker) {
		m_markers.add(marker);
		return marker;
	}

	public SVGPatternStripes addStripes(SVGPatternStripes stripes) {
		m_patterns.add(stripes);
		return stripes;
	}

	public void add(SVGComponent component) {
		m_components.add(component);
	}

	public void setViewBox(long x, long y, long width, long height) {
		m_viewBox = new ViewBox(x, y, width, height);
	}

	/**
	 * Here we translate a {@link JsonMap} into {@link HTMLNode}, which is the exact same process that should be done at browser side
	 * by the Javascript.
	 */
	@Override
	public HTMLNode toHTMLNode() {
		final ContainedElement containedElement = createContainedNode("svg", HTML_CLASS_CONTAINER);

		final HTMLNode svgElement = containedElement.element();
		final JsonMap jsonMap = toJsonMap();
		for(Map.Entry<String, JsonValue<?>> attribute : jsonMap.getAttributes().entrySet()) {
			if(!attribute.getKey().equals(HTMLConstants.ATTRIBUTE_ID)) {
				svgElement.setAttribute(attribute.getKey(), attribute.getValue().toString());
			}
		}

		for(JsonObject component : jsonMap.getArray(JSON_KEY_SUBCOMPONENTS).getItems()) {
			svgElement.append(toHTMLNode(component));
		}

		return containedElement.getHigherNode();
	}

	static HTMLNode toHTMLNode(JsonObject json) {
		final HTMLNode result = new HTMLNode(json.getType());

		LOG.info("JSON:\n{}", json.toJson());

		if(json instanceof JsonMap map) {
			for(Map.Entry<String, JsonValue<?>> attribute : map.getAttributes().entrySet()) {
				final String key = attribute.getKey();
				if(JSON_ATTRIBUTE_INNER_TEXT.equals(key)) {
					result.setText(attribute.getValue().toString());
				} else if(JSON_ATTRIBUTE_TITLE.equals(key)) {
					result.append(new HTMLNode("title").setText(attribute.getValue().toString()));
				} else {
					result.setAttribute(key, attribute.getValue().toString());
				}
			}
			for(Map.Entry<String, JsonMap> submap : map.getMaps().entrySet()) {
				result.append(toHTMLNode(submap.getValue()));
			}
			for(Map.Entry<String, JsonArray> subarray : map.getArrays().entrySet()) {
				final JsonArray array = subarray.getValue();
				for(JsonObject item : array.getItems()) {
					result.append(toHTMLNode(item));
				}
			}
		} else if(json instanceof JsonArray array) {
			final HTMLNode child = result.createChild(array.getType());
			for(JsonObject item : array.getItems()) {
				child.append(toHTMLNode(item));
			}
		}

		return result;
	}

	@Override
	public JsonMap toJsonMap() {
		final JsonMap result = new JsonMap(JSON_TYPE);
		if(hasSource()) {
			result.setAttribute(HTMLConstants.ATTRIBUTE_ID, HTMLConstants.toId(getTUID()));
			result.setAttribute(ATTRIBUTE_SOURCE, getSource());
		}
		result.setAttribute("width", m_width_px);
		result.setAttribute("height", m_height_px);
		if(m_viewBox != null) {
			result.setAttribute("viewBox", String.format("%d %d %d %d", m_viewBox.x, m_viewBox.y, m_viewBox.width, m_viewBox.height));
		}
		final JsonArray components = result.createArray(JSON_KEY_SUBCOMPONENTS);
		if(!m_markers.isEmpty() || !m_patterns.isEmpty()) {
			components.add(createDefs(m_markers, m_patterns));
		}
		for(SVGComponent component : m_components) {
			components.add(component.toJsonMap());
		}

		applyCustomStyle(result);
		return result;
	}

	private static JsonMap createDefs(Collection<? extends SVGComponent>... reusableCollections) {
		final JsonMap result = new JsonMap("defs");
		final JsonArray components = result.createArray(JSON_KEY_SUBCOMPONENTS);
		for(Collection<? extends SVGComponent> reusableCollection : reusableCollections) {
			for(SVGComponent reusable : reusableCollection) {
				components.add(reusable.toJsonMap());
			}
		}
		return result;
	}
}
