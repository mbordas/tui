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

package tui.ui.components.svg;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class CoordinatesComputerTest {

	@Test
	public void computeX_px() {
		final int width_px = 600;
		final int height_px = 500;
		final int padding_px = 20;

		final CoordinatesComputer computer = new CoordinatesComputer(width_px, height_px, padding_px,
				new CoordinatesComputer.Range(0.0, 10.0), new CoordinatesComputer.Range(-1.0, 1.0));

		assertEquals(padding_px, computer.getX_px(0.0));
		assertEquals(width_px - padding_px, computer.getX_px(10.0));

		assertEquals(padding_px, computer.getY_px(1.0));
		assertEquals(height_px - padding_px, computer.getY_px(-1.0));
		assertEquals(height_px / 2, computer.getY_px(0.0));
	}

	@Test
	public void computeXWithOverriddenLeftPadding() {
		final CoordinatesComputer computer = new CoordinatesComputer(200, 100, 20,
				new CoordinatesComputer.Range(0.0, 10.0), new CoordinatesComputer.Range(-1.0, 1.0));
		computer.setPaddingLeft_px(30);

		assertEquals(30, computer.getX_px(0.0));
		assertEquals(200 - 20, computer.getX_px(10.0));
	}
}