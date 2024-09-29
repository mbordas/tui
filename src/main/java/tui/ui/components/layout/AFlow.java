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

package tui.ui.components.layout;

import org.jetbrains.annotations.NotNull;
import tui.html.HTMLNode;
import tui.ui.components.UIComponent;

import java.util.ArrayList;
import java.util.List;

public abstract class AFlow extends UIComponent {

	protected Layouts.Width m_width = Layouts.Width.NORMAL;
	protected Layouts.Spacing m_spacing = Layouts.Spacing.NORMAL;

	protected final List<UIComponent> m_content = new ArrayList<>();

	public AFlow setWidth(@NotNull Layouts.Width width) {
		m_width = width;
		return this;
	}

	public AFlow setSpacing(Layouts.Spacing spacing) {
		m_spacing = spacing;
		return this;
	}

	public <C extends UIComponent> C append(C component) {
		m_content.add(component);
		return component;
	}

	public void appendAll(List<UIComponent> components) {
		m_content.addAll(components);
	}

	protected HTMLNode giveCenterReadingProperties(HTMLNode node) {
		switch(m_width) {
		case NORMAL -> node.addClass("tui-reading-normal-area");
		}
		return node;
	}

	protected void giveMarginReadingProperties(HTMLNode node) {
		switch(m_width) {
		case WIDE -> node.addClass("tui-reading-wide-margin");
		}
	}
}
