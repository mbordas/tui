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

import tui.ui.Page;
import tui.ui.TUIComponent;

public class HTMLPage {

	public static HTMLNode toHTML(Page page, String pathToCSS, String pathToScript, String onLoadFunctionCall) {
		final HTMLNode result = new HTMLNode("html");
		result.setRoot(true);

		final HTMLNode head = result.createChild("head");
		head.createChild("meta").setAttribute("charset", "utf-8");
		head.createChild("meta").setAttribute("name", "viewport")
				.setAttribute("content", "width=device-width, initial-scale=1");
		head.createChild("title").setText(page.getTitle());
		if(pathToCSS != null) {
			head.createChild("link")
					.setAttribute("rel", "stylesheet")
					.setAttribute("href", pathToCSS);
		}
		if(pathToScript != null) {
			head.createChild("script")
					.setAttribute("type", HTMLConstants.JAVASCRIPT_CONTENT_TYPE)
					.setAttribute("src", pathToScript)
					.setAttribute("defer", null); // the script is meant to be executed after the document has been parsed
		}

		final HTMLNode body = result.createChild("body");
		if(onLoadFunctionCall != null) {
			body.setAttribute("onload", onLoadFunctionCall);
		}
		final HTMLNode main = body.createChild("main");
		for(TUIComponent component : page.getContent()) {
			main.addChild(component.toHTMLNode());
		}

		return result;
	}

}
