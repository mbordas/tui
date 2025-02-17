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
import tui.ui.Style;
import tui.ui.components.Page;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class TUIDocumentation {

	private final Collection<Page> m_pages = new ArrayList<>();

	public TUIDocumentation() {
		m_pages.add(new TUIDocsIndex());
		m_pages.add(new TUIDocsOverview());
	}

	void save(File dir) {
		final Style style = new Style();
		for(Page page : m_pages) {
			try {
				save(page, style, dir);
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
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
