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

package tui.ui.components;

import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class TableDataTest {

	@Test
	public void getPageWithPageNumberOverflow() {
		final TableData data = new TableData(List.of("A"), 4);
		data.append(Map.of("A", "1"));
		data.append(Map.of("A", "2"));
		data.append(Map.of("A", "3"));
		data.append(Map.of("A", "4"));

		// Trying to get page #3 should return page #1 because there is no page #3
		final TableData result = data.getPage(3, 2, 2);

		assertEquals(1, result.m_pageInfo.pageNumber());
	}

	@Test
	public void computeLastPageNumber() {
		assertEquals(1, TableData.computeLastPageNumber(10, 10));
		assertEquals(2, TableData.computeLastPageNumber(11, 10));
		assertEquals(2, TableData.computeLastPageNumber(19, 10));
		assertEquals(2, TableData.computeLastPageNumber(20, 10));
		assertEquals(3, TableData.computeLastPageNumber(21, 10));
	}

}