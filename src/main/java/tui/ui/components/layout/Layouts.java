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

package tui.ui.components.layout;

public class Layouts {

	public enum Width {
		NORMAL("tui-reading-normal"),
		WIDE("tui-reading-wide"),
		MAX("tui-reading-max");
		private final String m_htmlClass;

		Width(String htmlClass) {
			m_htmlClass = htmlClass;
		}

		public String getHTMLClass() {
			return m_htmlClass;
		}
	}

	public enum Spacing {
		FIT("tui-spacing-fit"),
		COMPACT("tui-spacing-compact"),
		NORMAL("tui-spacing-normal"),
		LARGE("tui-spacing-large");

		private final String m_htmlClass;

		Spacing(String htmlClass) {
			m_htmlClass = htmlClass;
		}

		public String getHTMLClass() {
			return m_htmlClass;
		}
	}

	public enum TextAlign {
		LEFT("tui-align-left"), CENTER("tui-align-center"), RIGHT("tui-align-right"), STRETCH("tui-align-stretch");

		private String m_htmlClass;

		private TextAlign(String htmlClass) {
			m_htmlClass = htmlClass;
		}

		public String getHTMLClass() {
			return m_htmlClass;
		}
	}
}
