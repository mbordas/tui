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

public class HTMLConstants {

	public static final String RESPONSE_CHARSET = "utf-8";
	public static final String HTML_CONTENT_TYPE = "text/html;charset=" + RESPONSE_CHARSET;
	public static final String JAVASCRIPT_CONTENT_TYPE = "text/javascript";
	public static final String PNG_CONTENT_TYPE = "image/png";
	public static final String JPG_CONTENT_TYPE = "image/jpg";
	public static final String FAVICON_CONTENT_TYPE = "image/x-icon";
	public static final String CSS_CONTENT_TYPE = "text/css";
	public static final String JSON_CONTENT_TYPE = "application/json;charset=" + RESPONSE_CHARSET;

	public static final String ATTRIBUTE_ID = "id";
	public static final String ATTRIBUTE_REFRESH_LISTENERS = "tui-refresh-listeners";

	public static String toId(long tuid) {
		return String.valueOf(tuid);
	}
}
