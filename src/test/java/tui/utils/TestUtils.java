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

package tui.utils;

import tui.http.TUIBackend;
import tui.test.Browser;
import tui.ui.components.Page;
import tui.ui.components.UIComponent;
import tui.ui.style.Style;

public class TestUtils {

	public static final String LOREM_IPSUM = """
			Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed non risus. Suspendisse lectus tortor, dignissim sit amet, adipiscing nec, ultricies sed, dolor. Cras elementum ultrices diam. Maecenas ligula massa, varius a, semper congue, euismod non, mi. Proin porttitor, orci nec nonummy molestie, enim est eleifend mi, non fermentum diam nisl sit amet erat. Duis semper. Duis arcu massa, scelerisque vitae, consequat in, pretium a, enim. Pellentesque congue. Ut in risus volutpat libero pharetra tempor. Cras vestibulum bibendum augue. Praesent egestas leo in pede. Praesent blandit odio eu enim. Pellentesque sed dui ut augue blandit sodales. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Aliquam nibh. Mauris ac mauris sed pede pellentesque fermentum. Maecenas adipiscing ante non diam sodales hendrerit.
			
			Ut velit mauris, egestas sed, gravida nec, ornare ut, mi. Aenean ut orci vel massa suscipit pulvinar. Nulla sollicitudin. Fusce varius, ligula non tempus aliquam, nunc turpis ullamcorper nibh, in tempus sapien eros vitae ligula. Pellentesque rhoncus nunc et augue. Integer id felis. Curabitur aliquet pellentesque diam. Integer quis metus vitae elit lobortis egestas. Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Morbi vel erat non mauris convallis vehicula. Nulla et sapien. Integer tortor tellus, aliquam faucibus, convallis id, congue eu, quam. Mauris ullamcorper felis vitae erat. Proin feugiat, augue non elementum posuere, metus purus iaculis lectus, et tristique ligula justo vitae magna.
			Aliquam convallis sollicitudin purus. Praesent aliquam, enim at fermentum mollis, ligula massa adipiscing nisl, ac euismod nibh nisl eu lectus. Fusce vulputate sem at sapien. Vivamus leo. Aliquam euismod libero eu enim. Nulla nec felis sed leo placerat imperdiet. Aenean suscipit nulla in justo. Suspendisse cursus rutrum augue. Nulla tincidunt tincidunt mi. Curabitur iaculis, lorem vel rhoncus faucibus, felis magna fermentum augue, et ultricies lacus lorem varius purus. Curabitur eu amet.""";

	public static final String[] CITIES = { "Pendle", "Laencaster", "Boroughton", "Walden", "MillerVille", "Panshaw", "Ballater",
			"Ilfreycombe", "Wealdstone", "Moonbright", "Tardide", "Kinecardine", "Three Streams", "Hempholme", "Swadlincote", "Porthaethwy",
			"Everton", "Langdale", "Holden", "Hythe", "Lockinge", "Pontypridd", "Coombe", "Coniston", "Taewe", "Wimborne",
			"Everwinter", "Threlkeld", "Cappadocia", "Nantwich", "Eelry", "Ballaeter", "Auchendale", "Worcester", "Auchterarder",
			"Sutton", "Warrington", "Arkmunster", "Bury", "Lhanbryde", "Aempleforth", "Auchenshuggle", "Longdale", "Sudbury", "Hankala",
			"Strongfair", "Middlesborough", "Kilerth", "Shadowfen", "Nuxvar" };

	public static String getRandomCityName() {
		return CITIES[(int) (Math.random() * (CITIES.length - 1))];
	}

	public static void quickShow(UIComponent component) throws Exception {
		quickShow(component, null);
	}

	public static void quickShow(UIComponent component, Style style) throws Exception {
		final TUIBackend backend = new TUIBackend(8000);
		if(style != null) {
			backend.setStyle(style);
		}
		final Page page = new Page("TUI Quick show - " + component.getClass().getSimpleName(), "/quickShow");
		page.append(component);
		backend.registerPage(page);
		backend.start();

		final Browser browser = new Browser(backend.getPort());
		browser.open(page.getSource());
	}
}