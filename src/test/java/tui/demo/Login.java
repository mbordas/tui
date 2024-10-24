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

package tui.demo;

import tui.http.RequestReader;
import tui.http.TUIBackend;
import tui.test.Browser;
import tui.ui.components.Page;
import tui.ui.components.Paragraph;
import tui.ui.components.form.Form;
import tui.ui.components.layout.Layouts;
import tui.ui.components.layout.VerticalFlow;

import java.util.Map;

/**
 * This demo show how to create a login page.
 */
public class Login {

	public static void main(String[] args) throws Exception {

		// The 'login' page contains a simple login/password form
		final Page loginPage = new Page("Login", "/login");
		final VerticalFlow vFlow = loginPage.append(new VerticalFlow());
		vFlow.setWidth(Layouts.Width.NORMAL);
		final Form loginForm = vFlow.append(new Form("Login", "/login/validate")); // This web service will validate the login form
		loginForm.createInputString("Identifier", "login").setPlaceHolder("your login");
		loginForm.createInputPassword("Password", "password");
		loginForm.opensPage("/session"); // When the form is successfully submitted, this page will open
		loginForm.customStyleWidth_px(400);
		loginForm.customStylePadding(300, 0, 0, 0);

		final TUIBackend backend = new TUIBackend(8080);

		// Login page
		backend.registerPage(loginPage);

		// Login form validation
		backend.registerWebService(loginForm.getTarget(),
				(uri, request, response) -> {
					final RequestReader reader = new RequestReader(request);
					final String login = reader.getStringParameter("login");
					final String password = reader.getStringParameter("password");
					if("password".equals(password)) {
						// Responds successfully to the form and gives a parameter (name,value) to send when opening the page.
						return Form.buildSuccessfulSubmissionResponse(Map.of("sessionId", "testsesionid", "login", login));
					} else {
						// Example of regular failure in form validation
						return Form.buildFailedSubmissionResponse("Password is needed.", Map.of("password", "Must be 'password' ;-)"));
					}
				});

		// Page to open when login form is validated
		backend.registerPageService("/session", (uri, request) -> {
			final RequestReader reader = new RequestReader(request);
			final String sessionId = reader.getStringParameter("sessionId");
			final String login = reader.getStringParameter("login");
			final Page result = new Page("session");
			result.setSessionParameter("sessionId", sessionId);
			result.append(new Paragraph("You are logged as " + login));
			return result;
		});

		backend.start();

		final Browser browser = new Browser(backend.getPort());
		browser.open(loginPage.getSource());
	}
}
