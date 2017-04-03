package net.minidev.html;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Pattern;

import net.minidev.html.HtmlForm.FormEntry;
import net.minidev.net.Navigator;

import org.w3c.dom.Node;

public class HtmlForm implements Iterable<FormEntry> {
	public String _id;
	public String _name;
	public String _method;
	public String _action;
	LinkedHashMap<String, FormEntry> _values;

	public String[] toParams() {
		ArrayList<String> lst = new ArrayList<String>();
		for (FormEntry e : _values.values()) {
			if (e.name == null)
				continue;
			if (e.is(EntryType.submit)) {
				if (e.clicked)
					lst.add(e.name + "=" + e.value);
				continue;
			}
			if (e.type == EntryType.image) {
				if (e.x != null)
					lst.add(e.name + ".x=" + e.x);
				if (e.y != null)
					lst.add(e.name + ".y=" + e.y);
				continue;
			}
			if (e.type == EntryType.checkbox) {
				if (e.value == null)
					continue;
				if (e.value.equalsIgnoreCase(""))
					continue;
				if (e.value.equalsIgnoreCase("false"))
					continue;
				if (e.value != null)
					lst.add(e.name + "=" + e.value);
				else
					lst.add(e.name);
				continue;
			}
			lst.add(e.name + "=" + e.value);
		}
		return lst.toArray(new String[lst.size()]);
	}

	@Override
	public Iterator<FormEntry> iterator() {
		return _values.values().iterator();
	}

	public String getActionUrl(String refererUrl) {
		String actionUrl;
		if (_action == null)
			actionUrl = refererUrl;
		else
			try {
				URI actionURI;
				if (refererUrl != null) {
					actionURI = new URI(refererUrl);
					actionURI = actionURI.resolve(_action);
				} else {
					actionURI = new URI(_action);
				}
				actionUrl = actionURI.toString();
			} catch (Exception e) {
				actionUrl = _action;
			}
		return actionUrl;
	}

	public String submit(Navigator navi, String refererUrl) throws IOException {
		return call(navi, refererUrl);
	}

	public String call(Navigator navi, String refererUrl) throws IOException {
		// enctype = "application/x-www-form-urlencoded"
		String actionUrl = getActionUrl(refererUrl);
		String params[] = toParams();
		String body = null;

		if (refererUrl != null)
			navi.setReferer(refererUrl);
		if (isGET())
			body = navi.doGet(actionUrl, params);
		else
			body = navi.doPost(actionUrl, params);
		return body;
	}

	public boolean isGET() {
		if (_method == null)
			return true;
		return _method.equalsIgnoreCase("GET");
	}

	public boolean isPOST() {
		if (_method == null)
			return false;
		return _method.equalsIgnoreCase("POST");
	}

	public URI getActionUrl(URI srcUri) {
		return srcUri.resolve(_action);
	}

	public int size() {
		return _values.size();
	}

	public FormEntry getEntryByPosition(int pos) {
		for (FormEntry e : _values.values())
			if (e.position == pos)
				return e;
		return null;
	}

	public FormEntry getEntryByName(String name) {
		return _values.get(name);
	}

	public FormEntry remove(String name) {
		return _values.remove(name);
	}

	public FormEntry add(String name, String value) {
		FormEntry f = new FormEntry();
		f.position = _values.size();
		f.name = name;
		f.value = value;
		f.type = EntryType.text;
		return _values.put(name, f);
	}

	public FormEntry getEntryById(String id) {
		for (FormEntry ent : _values.values())
			if (id.equals(ent.id))
				return ent;
		return null;
	}

	public FormEntry getEntryById(Pattern pat) {
		for (FormEntry ent : _values.values())
			if (pat.matcher(ent.id).matches())
				return ent;
		return null;
	}

	public FormEntry getEntryByName(Pattern pat) {
		for (FormEntry ent : _values.values()) {
			if (ent.name == null)
				continue;
			if (pat.matcher(ent.name).matches())
				return ent;
		}
		return null;
	}

	/**
	 * for image input
	 */
	public boolean clicByNameOrId(String name, int x, int y) {
		if (clicByName(name, x, y))
			return true;
		return clicById(name, x, y);
	}

	public boolean setByNameOrId(String name, String value) {
		if (setByName(name, value))
			return true;
		return setById(name, value);
	}

	public boolean setByNameOrId(Pattern pat, String value) {
		if (setByName(pat, value))
			return true;
		return setById(pat, value);
	}

	public boolean setByName(String name, String value) {
		FormEntry f = getEntryByName(name);
		if (f == null)
			return false;
		f.value = value;
		return true;
	}

	public boolean setByName(Pattern pat, String value) {
		FormEntry f = getEntryByName(pat);
		if (f == null)
			return false;
		f.value = value;
		return true;
	}

	public boolean setById(String id, String value) {
		FormEntry f = getEntryById(id);
		if (f == null)
			return false;
		f.value = value;
		return true;
	}

	public boolean clicByName(String name, int x, int y) {
		FormEntry f = getEntryByName(name);
		if (f == null)
			return false;
		if (f.is(EntryType.image) || f.is(EntryType.submit)) {
			f.clic(x, y);
			return true;
		}
		return false;
	}

	public boolean clicByName(Pattern pat, int x, int y) {
		FormEntry f = getEntryByName(pat);
		if (f == null)
			return false;
		if (f.is(EntryType.image) || f.is(EntryType.submit)) {
			f.clic(x, y);
			return true;
		}
		return false;
	}

	public boolean clicById(String id, int x, int y) {
		FormEntry f = getEntryById(id);
		if (f == null)
			return false;
		if (f.is(EntryType.image) || f.is(EntryType.submit)) {
			f.clic(x, y);
			return true;
		}
		return false;
	}

	public boolean clicById(Pattern id, int x, int y) {
		FormEntry f = getEntryById(id);
		if (f == null)
			return false;
		if (f.is(EntryType.submit) || f.is(EntryType.image)) {
			f.clic(x, y);
			return true;
		}
		return false;
	}

	public boolean setById(Pattern pat, String value) {
		FormEntry f = getEntryById(pat);
		if (f == null)
			return false;
		f.value = value;
		return true;
	}

	public FormEntry getPasswordEntry() {
		for (FormEntry f : _values.values()) {
			if (!f.is(EntryType.password))
				continue;
			return f;
		}
		return null;
	}

	public FormEntry getFirstEntryOfType(EntryType type) {
		for (FormEntry f : _values.values())
			if (f.is(type))
				return f;
		return null;
	}

	/**
	 * fill the first value having type = password
	 */
	public boolean setPassword(String value) {
		FormEntry f = getFirstEntryOfType(EntryType.password);
		if (f == null)
			return false;
		f.value = value;
		return true;
	}

	/**
	 * fill the first value having type = password
	 */
	public boolean setText(String value) {
		FormEntry f = getFirstEntryOfType(EntryType.text);
		if (f == null)
			return false;
		f.value = value;
		return true;
	}

	/**
	 * fill the first value having type = email
	 */
	public boolean setEmail(String value) {
		FormEntry f = getFirstEntryOfType(EntryType.email);
		if (f == null)
			return false;
		f.value = value;
		return true;
	}

	public Collection<FormEntry> getEntrys() {
		return _values.values();
	}

	public HtmlForm(Node form) {
		this._values = new LinkedHashMap<String, FormEntry>();
		this._id = DomUtils.getAttributValue(form, "id");
		this._name = DomUtils.getAttributValue(form, "name");
		this._method = DomUtils.getAttributValue(form, "method");
		this._action = DomUtils.getAttributValue(form, "action");
		List<Node> inputs = DomUtils.getNodesOfType(form, "input", "select");
		for (Node input : inputs) {
			FormEntry e = new FormEntry();
			String type = DomUtils.getAttributValue(input, "type");
			String name = DomUtils.getAttributValue(input, "name");
			String id = DomUtils.getAttributValue(input, "id");
			if (name == null)
				name = id;
			e.name = name;
			e.id = id;
			String nodeName = input.getNodeName();
			String value;
			value = DomUtils.getAttributValue(input, "value");

			if (value == null)
				value = "";

			if (nodeName.equalsIgnoreCase("select")) {
				for (Node option : DomUtils.getNodesOfType(input, "option")) {
					if (option.getAttributes().getNamedItem("selected") != null) {
						value = DomUtils.getAttributValue(option, "value");
						break;
					}
				}
				e.type = EntryType.select;
			} else if ("hidden".equals(type)) {
				e.type = EntryType.hidden;
			} else if ("submit".equals(type)) {
				e.type = EntryType.submit;
			} else if ("tel".equals(type)) {
				e.type = EntryType.tel;
			} else if ("password".equals(type)) {
				e.type = EntryType.password;
			} else if ("email".equals(type)) {
				e.type = EntryType.email;
			} else if ("text".equals(type)) {
				e.type = EntryType.text;
			} else if ("button".equals(type)) {
				e.type = EntryType.button;
			} else if ("radio".equals(type)) {
				e.type = EntryType.radio;
			} else if ("image".equals(type)) {
				e.type = EntryType.image;
			} else if ("checkbox".equals(type)) {
				e.type = EntryType.checkbox;
				value = null;
			} else {
				System.out.println("HtmlForm: not supported input type:" + type + " in HtmlFom.java");
			}
			e.value = value;
			e.position = _values.size();
			_values.put(name, e);
		}
	}

	public class FormEntry {
		public int position;
		public EntryType type;
		public boolean clicked = false;
		public String name;
		public String value;
		/**
		 * for image only
		 */
		public Integer x;
		public Integer y;
		public String id;

		public FormEntry() {
		}

		public boolean is(EntryType type) {
			return this.type == type;
		}

		public boolean clic(int x, int y) {
			if (type != EntryType.image) {
				this.clicked = true;
				return true;
			}
			this.x = x;
			this.y = y;
			return true;
		}

		public void setChecked(boolean checker) {
			if (checker)
				this.value = "true";
			else
				this.value = "false";
		}

		public String toString() {
			if (type == EntryType.checkbox)
				return type + ":" + name + "=" + (clicked ? "on" : "off");
			else
				return type + ":" + name + "=" + value;
		}
	}

	public static enum EntryType {
		email, hidden, text, checkbox, select, submit, password, radio, button, image, tel
	}

	public String toString() {
		return "frmName:" + _name + " frmId:" + _id + " Action:" + _action + " (NbField:" + _values.size() + ")";
	}

}
