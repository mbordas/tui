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

import tui.ui.components.NavLink;
import tui.ui.components.Page;
import tui.ui.components.Section;

public class TUIDocsNavLinks extends Page {

	public static final String SOURCE = "navlinks.html";

	public TUIDocsNavLinks() {
		super("NavLinks", "navlinks.html");

		final Section chapter = appendSection("NavLinks");
		chapter.appendParagraph("The NavLink corresponds to the common hyperlink");

		chapter.append(new CodeParagraph("""
				chapter.append(new NavLink("Back to TUI documentation TOC", "https://mbordas.github.io/tui/"));"""));
		chapter.appendParagraph("Produces: ")
				.append(new NavLink("Back to TUI documentation TOC", "https://mbordas.github.io/tui/"));

		final Section sectionStyle = chapter.createSubSection("Style");
		sectionStyle.appendParagraph("You can customize both its layout and its text:");
		sectionStyle.append(new CodeParagraph("""
				navLink.customStyle() [...]
				navLink.customTextStyle() [...]"""));
	}
}
