package tui.ui.components.svg;

import org.junit.Test;
import tui.html.HTMLNode;

import static org.junit.Assert.assertEquals;

public class SVGGroupTest {

	@Test
	public void toHTMLNode() {
		final SVGGroup group = new SVGGroup();
		group.add(new SVGRectangle(1, 2, 3, 4));

		HTMLNode.PRETTY_PRINT = true;
		assertEquals("""
				<g style="display:inline">
				  <rect x="1" y="2" width="3" height="4" rx="0" ry="0" style="display:inline;stroke:#000000;stroke-width:1;stroke-opacity:1.00;fill:#000000;fill-opacity:1.00;"/>
				</g>
				""", SVG.toHTMLNode(group.toJsonMap()).toHTML());
		HTMLNode.PRETTY_PRINT = false;
	}

}