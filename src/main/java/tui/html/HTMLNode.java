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

package tui.html;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class HTMLNode {

	public static boolean PRETTY_PRINT = false;

	private boolean m_isRoot = false;
	private final String m_name;
	private final Map<String, String> m_attributes = new LinkedHashMap<>();
	private final Map<String, String> m_styleProperties = new TreeMap<>();
	private final List<HTMLNode> m_children = new ArrayList<>();
	private StringBuilder m_text = new StringBuilder();
	private int m_prettyPrintDepth = 0;

	public HTMLNode(String name) {
		m_name = name;
	}

	public String getName() {
		return m_name;
	}

	public void setRoot(boolean isRoot) {
		m_isRoot = isRoot;
	}

	public void setPrettyPrintDepth(int depth) {
		m_prettyPrintDepth = depth;
		for(HTMLNode child : m_children) {
			child.setPrettyPrintDepth(depth + 1);
		}
	}

	public HTMLNode setClass(String className) {
		return setAttribute("class", className);
	}

	public void addClass(String className) {
		if(m_attributes.containsKey("class")) {
			m_attributes.put("class", m_attributes.get("class") + " " + className);
		} else {
			setClass(className);
		}
	}

	public HTMLNode setStyleProperty(String key, String value) {
		m_styleProperties.put(key, value);
		return this;
	}

	public HTMLNode setStyleProperties(Map<String, String> properties) {
		m_styleProperties.putAll(properties);
		return this;
	}

	public HTMLNode setAttribute(String name, String value) {
		m_attributes.put(name, value);
		return this;
	}

	public HTMLNode setAttribute(String name, int value) {
		m_attributes.put(name, String.valueOf(value));
		return this;
	}

	public HTMLNode setAttribute(String name, long value) {
		m_attributes.put(name, String.valueOf(value));
		return this;
	}

	public HTMLNode setText(String text) {
		m_text = new StringBuilder();
		m_text.append(text);
		return this;
	}

	public HTMLNode createChild(String name) {
		final HTMLNode result = new HTMLNode(name);
		if("div".equals(name)) {
			result.setText(" "); // Will force to create a closing tag in DOM
		}
		return addChild(result);
	}

	public HTMLNode addChild(HTMLNode node) {
		m_children.add(node);
		node.setPrettyPrintDepth(m_prettyPrintDepth + 1);
		return node;
	}

	public HTMLNode getDescendant(String path) {
		final int indexOfEndOfFirstWord = path.indexOf("/");
		final String name;
		if(indexOfEndOfFirstWord >= 0) {
			name = path.substring(0, indexOfEndOfFirstWord);
		} else {
			name = path;
		}
		for(HTMLNode child : m_children) {
			if(child.m_name.equals(name)) {
				return child;
			}
		}

		return null;
	}

	public String toHTML() {
		final StringBuilder result = new StringBuilder();

		if(m_isRoot) {
			result.append("<!DOCTYPE html><?xml version='1.0' encoding='UTF-8'?>");
			endOfTag(result);
		}

		prettyPrintTab(result).append("<").append(m_name);
		for(Map.Entry<String, String> attribute : m_attributes.entrySet()) {
			result.append(" ").append(attribute.getKey());
			if(attribute.getValue() != null) {
				result.append("=\"").append(attribute.getValue()).append("\"");
			}
		}

		if(!m_styleProperties.isEmpty()) {
			final StringBuilder stylePropertiesStr = new StringBuilder();
			for(Map.Entry<String, String> styleProperty : m_styleProperties.entrySet()) {
				stylePropertiesStr.append(
								styleProperty.getKey()).append(": ")
						.append(styleProperty.getValue()).append(";");
			}
			result.append(" style");
			result.append("=\"").append(stylePropertiesStr).append("\"");
		}

		if(m_text.isEmpty()
				&& m_children.isEmpty()
				&& !"script".equals(m_name) // script node must have both opening and closing xml tags https://www.w3.org/TR/xhtml1/#h-4.8
		) {
			// Empty node
			result.append("/>");
			endOfTag(result);
		} else {
			// Node with content
			result.append(">"); // ending node's opening tag

			if(!m_text.isEmpty()) {
				result.append(m_text.toString());
			} else {
				endOfTag(result);
			}

			for(HTMLNode child : m_children) {
				result.append(child.toHTML());
			}

			if(m_text.isEmpty()) {
				prettyPrintTab(result);
			}
			result.append("</").append(m_name).append(">");
			endOfTag(result);
		}
		return result.toString();
	}

	private StringBuilder prettyPrintTab(StringBuilder builder) {
		if(PRETTY_PRINT) {
			builder.append("  ".repeat(Math.max(0, m_prettyPrintDepth)));
		}
		return builder;
	}

	public static void endOfTag(StringBuilder builder) {
		if(PRETTY_PRINT) {
			builder.append("\n");
		}
	}

}
