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

package tui.ui;

import java.awt.*;

public class Style {

	public record GlobalColors(Color text, Color borders, Color action, Color cancel, Color delete,
							   Color neutralState, Color greenState, Color redState) {
	}

	private GlobalColors m_globalColors = new GlobalColors(
			new Color(46, 46, 46), // text
			new Color(180, 180, 180), // borders
			new Color(0, 198, 252), // action
			new Color(222, 222, 222), // cancel
			new Color(252, 40, 3), // delete / rollback
			new Color(230, 230, 230), // neutral state
			new Color(115, 250, 70), // green state
			new Color(252, 40, 3) // red state
	);

	public record TableColors(Color rowHover) {
	}

	private TableColors m_tableStyle = new TableColors(
			new Color(192, 240, 252)
	);

	public Style() {
	}

	public GlobalColors getGlobalColors() {
		return m_globalColors;
	}

	public TableColors getTableStyle() {
		return m_tableStyle;
	}

	public record Padding(int top_px, int right_px, int bottom_px, int left_px) {
	}

}
