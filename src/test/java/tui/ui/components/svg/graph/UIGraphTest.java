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

package tui.ui.components.svg.graph;

import tui.http.TUIBackend;
import tui.test.Browser;
import tui.ui.components.Page;

import java.awt.*;

public class UIGraphTest {

	public static void main(String[] args) throws Exception {
		final UIGraph graph = new UIGraph()
				.withSerieColor(Color.BLUE)
				.withAxisColor(Color.DARK_GRAY);
		graph.addPoint(-1.0, -0.05);
		graph.addPoint(-0.8, 0.0, "-0.8 , 0.0");
		graph.addPoint(-0.5, 1.3);
		graph.addPoint(0.0, 1.0);
		graph.addPoint(1.0, 0.5);
		graph.addPoint(2.0, 0.0);

		for(int i = -3; i < 3; i++) {
			graph.addXLabel(1.0 * i, Integer.valueOf(i).toString());
			graph.addYLabel(1.0 * i, Integer.valueOf(i).toString());
		}

		final Page page = new Page("Graph", "/index");

		page.append(graph.toSVG(600, 400));
		final TUIBackend backend = new TUIBackend(8080);
		backend.registerPage(page);
		backend.start();
		final Browser browser = new Browser(backend.getPort());
		browser.open("/index");
	}
}
