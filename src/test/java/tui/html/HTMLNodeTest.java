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

import org.junit.Test;
import tui.ui.StyleSet;

import java.awt.*;

import static org.junit.Assert.assertEquals;

public class HTMLNodeTest {

	@Test
	public void setStyleProperty() {
		HTMLNode.PRETTY_PRINT = false;
		final HTMLNode node = new HTMLNode("div");

		node.setStyleProperty("display", "grid");
		assertEquals("<div style=\"display:grid;\"></div>", node.toHTML());

		node.setStyleProperty("width", "100%");
		assertEquals("<div style=\"display:grid;width:100%;\"></div>", node.toHTML());
	}

	@Test
	public void customStyle() {
		HTMLNode.PRETTY_PRINT = false;
		final HTMLNode node = new HTMLNode("div");

		StyleSet styleset = new StyleSet();
		styleset.setBackgroundColor(Color.WHITE);
		styleset.apply(node);

		assertEquals("<div style=\"background-color:#ffffff;color:#000000;\"></div>", node.toHTML());
	}

}