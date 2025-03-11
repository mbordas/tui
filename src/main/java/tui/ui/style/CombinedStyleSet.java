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

package tui.ui.style;

import java.util.function.BiConsumer;

public class CombinedStyleSet extends StyleSet {

	private LayoutStyleSet m_layout = null;
	private TextStyleSet m_text = null;

	public LayoutStyleSet layout() {
		if(m_layout == null) {
			m_layout = new LayoutStyleSet();
		}
		return m_layout;
	}

	public TextStyleSet text() {
		if(m_text == null) {
			m_text = new TextStyleSet();
		}
		return m_text;
	}

	@Override
	public String toCSS(String selector) {
		final StringBuilder result = new StringBuilder();
		if(m_layout != null) {
			result.append(m_layout.toCSS(selector));
		}
		if(m_text != null) {
			result.append(m_text.toCSS(selector));
			result.append(m_text.toCSS(selector + " *"));
		}
		return result.toString();
	}

	@Override
	<T> void apply(T node, BiConsumer<T, Property> setter) {
		if(m_layout != null) {
			m_layout.apply(node, setter);
		}
		if(m_text != null) {
			m_text.apply(node, setter);
		}
	}
}
