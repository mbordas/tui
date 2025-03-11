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

package tui.ui.style;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tui.html.HTMLNode;
import tui.json.JsonMap;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public abstract class StyleSet {

	protected final Map<String, String> m_overriddenProperties = new HashMap<>();

	public void overrideProperty(String name, String value) {
		m_overriddenProperties.put(name, value);
	}

	public String toCSS(String selector) {
		final Map<String, String> styleProperties = new HashMap<>();
		apply(styleProperties, (map, property) -> map.put(property.name, property.value));
		return String.format("%s { %s }", selector, computeStyleAttribute(styleProperties));
	}

	public void apply(HTMLNode node) {
		final BiConsumer<HTMLNode, Property> setter = (htmlNode, property) -> htmlNode.setStyleProperty(property.name, property.value);
		apply(node, setter);
		for(Map.Entry<String, String> overrideEntry : m_overriddenProperties.entrySet()) {
			setStylePropertyIfDefined(node, overrideEntry.getKey(), overrideEntry.getValue(), setter);
		}
	}

	public void apply(JsonMap node) {
		final BiConsumer<JsonMap, Property> setter = (map, property) -> map.setStyleProperty(property.name, property.value);
		apply(node, setter);
		for(Map.Entry<String, String> overrideEntry : m_overriddenProperties.entrySet()) {
			setStylePropertyIfDefined(node, overrideEntry.getKey(), overrideEntry.getValue(), setter);
		}
	}

	record Property(String name, String value) {
	}

	abstract <T> void apply(T node, BiConsumer<T, Property> setter);

	<T> void setStylePropertyIfDefined(@NotNull T node, @NotNull String name, @Nullable String value,
			BiConsumer<T, Property> setter) {
		if(value != null) {
			setter.accept(node, new Property(name, value));
		}
	}

	public static @NotNull String computeStyleAttribute(Map<String, String> properties) {
		final StringBuilder stylePropertiesStr = new StringBuilder();
		for(Map.Entry<String, String> styleProperty : properties.entrySet()) {
			stylePropertiesStr.append(
							styleProperty.getKey()).append(":")
					.append(styleProperty.getValue()).append(";");
		}
		return stylePropertiesStr.toString();
	}

}
