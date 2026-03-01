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
import tui.ui.style.LayoutStyleSet;
import tui.utils.TUIUtils;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicLong;

public abstract class UIComponent {

	public static final String HTML_ATTRIBUTE_CUSTOM_TAG = "data-custom-tag";
	public static final String JSON_ATTRIBUTE_CUSTOM_TAG = "customTag";

	private static final AtomicLong m_counter = new AtomicLong(0L);

	private final long m_tuid = newTUID();
	private String m_customTag = null; // Developer's value used to find the component in tests.

	private LayoutStyleSet m_customLayoutStyle = null;

	public abstract HTMLNode toHTMLNode();

	public abstract JsonMap toJsonMap();

	protected HTMLNode toHTMLNode(String name, boolean withTUID) {
		final HTMLNode result = new HTMLNode(name);
		if(withTUID) {
			result.setAttribute("id", HTMLConstants.toId(getTUID()));
		}

		applyCustomStyle(result);
		applyCustomTag(result);

		return result;
	}

	protected JsonMap toJsonMap(String type, boolean withTUID) {
		final JsonMap result = withTUID ? new JsonMap(type, getTUID()) : new JsonMap(type);

		applyCustomStyle(result);
		applyCustomTag(result);

		return result;
	}

	public long getTUID() {
		return m_tuid;
	}

	public void setCustomTag(String customTag) {
		m_customTag = customTag;
	}

	public String getCustomTag() {
		return m_customTag;
	}

	public LayoutStyleSet customStyle() {
		if(m_customLayoutStyle == null) {
			m_customLayoutStyle = new LayoutStyleSet();
		}
		return m_customLayoutStyle;
	}

	protected void applyCustomStyle(HTMLNode htmlNode) {
		if(m_customLayoutStyle != null) {
			m_customLayoutStyle.apply(htmlNode);
		}
	}

	protected void applyCustomTag(HTMLNode htmlNode) {
		if(m_customTag != null) {
			htmlNode.setAttribute(HTML_ATTRIBUTE_CUSTOM_TAG, m_customTag);
		}
	}

	protected void applyCustomStyle(JsonMap jsonMap) {
		if(m_customLayoutStyle != null) {
			m_customLayoutStyle.apply(jsonMap);
		}
	}

	protected void applyCustomTag(JsonMap jsonMap) {
		if(m_customTag != null) {
			jsonMap.setAttribute(JSON_ATTRIBUTE_CUSTOM_TAG, m_customTag);
		}
	}

	public static String getTUIsSeparatedByComa(Collection<? extends UIComponent> components) {
		final Iterator<Long> tuidIterator = components.stream()
				.map(UIComponent::getTUID)
				.iterator();
		return TUIUtils.toTUIDsSeparatedByComa(tuidIterator);
	}

	public static long newTUID() {
		return m_counter.incrementAndGet();
	}

}
