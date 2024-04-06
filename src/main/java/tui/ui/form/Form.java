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

package tui.ui.form;

import tui.html.HTMLForm;
import tui.html.HTMLNode;
import tui.ui.TUIComponent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public class Form extends TUIComponent {

	private final String m_title;
	private final String m_target; // Web service path

	private final Set<FormInputString> m_inputs = new LinkedHashSet<>();
	private final Collection<TUIComponent> m_refreshListeners = new ArrayList<>();

	public Form(String title, String target) {
		m_title = title;
		m_target = target;
	}

	public String getTitle() {
		return m_title;
	}

	public String getTarget() {
		return m_target;
	}

	public Set<FormInputString> getInputs() {
		return m_inputs;
	}

	public Collection<TUIComponent> getRefreshListeners() {
		return m_refreshListeners;
	}

	public FormInputString createInputString(String label, String name) {
		final FormInputString result = new FormInputString(label, name);
		m_inputs.add(result);
		return result;
	}

	/**
	 * Registered listener will be refreshed each time the form will be successfully submitted.
	 */
	public void registerRefreshListener(TUIComponent listener) {
		m_refreshListeners.add(listener);
	}

	@Override
	public HTMLNode toHTMLNode() {
		return HTMLForm.toHTML(this);
	}
}
