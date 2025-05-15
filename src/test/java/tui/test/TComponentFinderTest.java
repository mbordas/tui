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

package tui.test;

import org.junit.BeforeClass;
import org.junit.Test;
import tui.test.components.TPage;
import tui.test.components.TPanel;
import tui.test.components.TParagraph;
import tui.test.components.TSection;
import tui.ui.components.List;
import tui.ui.components.Page;
import tui.ui.components.Paragraph;
import tui.ui.components.Section;
import tui.ui.components.layout.Panel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class TComponentFinderTest {

	private static TPage PAGE;

	@BeforeClass
	public static void beforeClass() {
		final Page _page = new Page("index");

		final Section sectionA = _page.append(new Section("Section A"));
		sectionA.append(new Paragraph())
				.append(new Paragraph.Text("Lorem ipsum"));

		final Section sectionB = _page.append(new Section("Section B"));
		sectionB.append(new List(true))
				.append(new Paragraph.Text("Text in list in section B"));
		sectionB.append(new Panel())
				.append(new Paragraph.Text("Text in panel in section B"));

		PAGE = TPage.parse(_page.toJsonMap(), null);
	}

	@Test
	public void findingAllInstancesInPage() {
		assertEquals(3, TComponentFinder.ofClass(TParagraph.TText.class, PAGE).findAll().size());
	}

	@Test
	public void findingUniqueInstanceByItsContent() {
		assertNotNull(TComponentFinder.ofClass(TParagraph.TText.class, PAGE)
				.withCondition((text) -> text.getText().contains("ipsum"))
				.getUnique());
	}

	@Test
	public void findingUniqueInstanceByItsParentClass() {
		assertEquals("Text in panel in section B", TComponentFinder.ofClass(TParagraph.TText.class, PAGE)
				.withParentOfClass(TPanel.class)
				.getUnique()
				.getText());
	}

	@Test
	public void findingDirectChildrenOfPage() {
		assertEquals(2, TComponentFinder.ofClass(TSection.class, PAGE).findAll().size());
	}

	@Test
	public void findingUniqueInstanceCrashesWhenMultipleFound() {
		try {
			TComponentFinder.ofClass(TParagraph.TText.class, PAGE).getUnique();
			fail();
		} catch(TestExecutionException e) {
			assertEquals("Too many TText found: 3", e.getMessage());
		}
	}

}