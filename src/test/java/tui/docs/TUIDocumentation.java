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

import tui.html.HTMLNode;
import tui.ui.components.List;
import tui.ui.components.NavLink;
import tui.ui.components.Page;
import tui.ui.components.Paragraph;
import tui.ui.components.Section;
import tui.ui.components.layout.Panel;
import tui.ui.style.Style;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class TUIDocumentation {

	private final java.util.List<Page> m_miscs = new ArrayList<>();
	private final java.util.List<Page> m_components = new ArrayList<>();
	private final java.util.List<Page> m_layouts = new ArrayList<>();

	public TUIDocumentation() {
		m_layouts.add(new TUIDocsPanels());
		m_components.add(new TUIDocsTables());
		m_miscs.add(new TUIDocsFirstSteps());
		m_miscs.add(new TUIDocsUpdatingAPage());
	}

	Page buildIndex() {
		final Page result = new Page("TUI Index", "index.html");

		final Section sectionMisc = result.appendSection("Table of Content");
		for(Page miscPage : m_miscs) {
			sectionMisc.append(new Paragraph().append(new NavLink(miscPage.getTitle(), miscPage.getSource())));
		}

		final Section sectionComponents = sectionMisc.createSubSection("Components");
		final List listComponents = sectionComponents.append(new List(false));
		for(Page componentPage : m_components) {
			listComponents.append(new NavLink(componentPage.getTitle(), componentPage.getSource()));
		}

		final Section sectionLayouts = sectionMisc.createSubSection("Layouts");
		final List listLayouts = sectionLayouts.append(new List(false));
		for(Page layoutPage : m_layouts) {
			listLayouts.append(new NavLink(layoutPage.getTitle(), layoutPage.getSource()));
		}

		return result;
	}

	void save(File dir) {
		final Style style = new Style();
		Collection<Page> pages = new ArrayList<>();
		pages.add(buildIndex());
		pages.addAll(m_miscs);
		pages.addAll(m_components);
		pages.addAll(m_layouts);

		for(Page page : pages) {
			try {
				decorate(page, style);
				save(page, style, dir);
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}

	static void decorate(Page page, Style style) {
		page.setFooter(new Panel());
		style.footer().layout().setHeight_px(50);
	}

	static void save(Page page, Style style, File dir) throws IOException {
		final File file = new File(dir, page.getSource());
		try(FileOutputStream outputStream = new FileOutputStream(file)) {
			final HTMLNode htmlNode = page.toHTMLNode(new Page.Resource(false, style.toCSS()), null);
			byte[] strToBytes = htmlNode.toHTML().getBytes();
			outputStream.write(strToBytes);
		}
		System.out.println("Page saved: " + file.getAbsolutePath());
	}

	public static void main(String[] args) throws IOException {
		final File dir = new File(args[0]);
		final TUIDocumentation tuiDocumentation = new TUIDocumentation();
		tuiDocumentation.save(dir);
	}
}
