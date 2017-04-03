package net.minidev.html;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;

public class HtmlForms extends ArrayList<HtmlForm> {
	private static final long serialVersionUID = 1L;

	public HtmlForms(String html) {
		this(DomUtils.toDOMTree(html));
	}

	public HtmlForms(Node node) {
		List<Node> inputs = DomUtils.getNodesOfType(node, "form");
		for (Node input : inputs) {
			HtmlForm f = new HtmlForm(input);
			this.add(f);
		}
	}

	public HtmlForm getBigestForm() {
		int size = 0;
		HtmlForm f2 = null;

		for (HtmlForm f : this) {
			if (f._values.size() > size) {
				f2 = f;
				size = f._values.size();
			}
		}
		return f2;
	}

	public HtmlForm getFrmByNameOrId0(String name) {
		for (HtmlForm f : this)
			if (name.equals(f._name) || name.equals(f._id))
				return f;
		return null;
	}

	public HtmlForm getFrmWithPassword() {
		for (HtmlForm frm : this)
			if (frm.getPasswordEntry() != null)
				return frm;
		return null;
	}

	public HtmlForm getFrmByNameOrId(String... names) {
		for (String name : names) {
			HtmlForm frm = getFrmByNameOrId0(name);
			if (frm != null)
				return frm;
		}
		return null;
	}

	public HtmlForm getFrmById(String id) {
		for (HtmlForm f : this)
			if (id.equals(f._id))
				return f;
		return null;
	}

	public HtmlForm getFrmByName(String name) {
		for (HtmlForm f : this)
			if (name.equals(f._name))
				return f;
		return null;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("HtmlForms [");
		for (HtmlForm f : this)
			sb.append(f.toString()).append(" ");
		sb.append("]");
		return sb.toString();
	}
}
