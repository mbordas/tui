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

package tui.docs;

import tui.ui.components.NavButton;
import tui.ui.components.NavLink;
import tui.ui.components.Page;
import tui.ui.components.Paragraph;
import tui.ui.components.Section;

public class TUIDocsNavButtons extends Page {

	public TUIDocsNavButtons() {
		super("NavButtons", "navbuttons.html");

		final Section chapter = appendSection("NavButtons");
		final Paragraph introductionParagraph = chapter.appendParagraph("The NavButton opens a new page (as does the ");
		introductionParagraph.append(new NavLink("NavLink", "./" + TUIDocsNavLinks.SOURCE));
		introductionParagraph.appendNormal(" ) but with parameters sent to the backend with POST method.");

		chapter.append(new CodeParagraph("""
				NavButton navButton = new NavButton("Go to page with hidden parameters", "/page");
				navButton.setParameter("secret", "XuSUNdk9F8IeGLNXq6bc");"""));

		final NavButton navButton = new NavButton("Go to page with hidden parameters", "/page");
		navButton.setParameter("secret", "XuSUNdk9F8IeGLNXq6bc");
		chapter.appendParagraph("Produces: ").append(navButton);
		chapter.appendParagraph(
				"which does not work here because this documentation is a static page, where components instrumentation (Javascript code) is not enabled.");

		final Section sectionStyle = chapter.createSubSection("Style");
		sectionStyle.appendParagraph("You can customize both its layout and its text:");
		sectionStyle.append(new CodeParagraph("""
				navButton.customStyle() [...]
				navButton.customTextStyle() [...]"""));
	}
}
