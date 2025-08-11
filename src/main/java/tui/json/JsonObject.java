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

package tui.json;

public abstract class JsonObject {

	public static boolean PRETTY_PRINT = false;

	public static final String KEY_TYPE = "type";

	protected String m_type;
	protected int m_prettyPrintDepth = 0;

	public abstract String toJson();

	public abstract void setPrettyPrintDepth(int depth);

	public JsonObject(String type) {
		m_type = type;
	}

	public void setType(String type) {
		m_type = type;
	}

	public String getType() {
		return m_type;
	}

	protected StringBuilder prettyPrintTab(StringBuilder builder, int relativeDepth) {
		if(PRETTY_PRINT) {
			builder.append("  ".repeat(Math.max(0, m_prettyPrintDepth + relativeDepth)));
		}
		return builder;
	}

	protected void endOfTag(StringBuilder result) {
		if(PRETTY_PRINT) {
			result.append("\n");
		}
	}

}
