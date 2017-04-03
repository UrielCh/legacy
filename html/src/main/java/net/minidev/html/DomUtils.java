package net.minidev.html;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.cyberneko.html.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class DomUtils {
	private static Logger log = Logger.getLogger(DomUtils.class.toString());

	/**
	 * @see http://xerces.apache.org/xerces-j/features.html
	 */
	public static Document toDOMTree(String html) {
		DOMParser parser = new DOMParser();
		try {
			parser.setFeature("http://xml.org/sax/features/namespaces", false);
			// parser.setFeature("http://apache.org/xml/features/validation/schema",
			// false);

			InputSource is = new InputSource(new StringReader(html));
			parser.parse(is);
			Document dom = parser.getDocument();
			return dom;
		} catch (Exception e) {
			log.log(Level.WARNING, "Error Parsing HTML", e);
			throw new RuntimeException("can not Parsse HTML Document", e);
		}
	}

	public static String getAttributValue(Node n, String name) {
		if (n == null)
			return null;
		NamedNodeMap att = n.getAttributes();
		if (att == null)
			return null;
		Node n2 = att.getNamedItem(name);
		if (n2 == null)
			return null;
		return n2.getNodeValue();
	}

	public static String getNodeValue(Node n) {
		if (n == null)
			return null;
		return n.getTextContent();
	}

	
	public static NodeList toDOMFragment(String html) {
		DOMParser parser = new DOMParser();
		try {
			parser.setFeature("http://xml.org/sax/features/namespaces", false);
			String fragment = "<frag>" + html + "</frag>";
			InputSource is = new InputSource(new StringReader(fragment));
			parser.parse(is);
			Document dom = parser.getDocument();
			// Node first = dom.getFirstChild();
			Node first = getFirstNodeOfType(dom, "frag");
			NodeList nodes = first.getChildNodes();
			return nodes;
		} catch (Exception e) {
			log.log(Level.WARNING, "Error Parsing HTML", e);
			throw new RuntimeException("can not Parsse HTML Document", e);
		}
	}

	/**
	 * 
	 * @param src
	 * @param nodes
	 * @return the first remplaced Node
	 */
	public static Node replace(Node src, NodeList nodes) {
		Document doc = src.getOwnerDocument();
		Node parent = src.getParentNode();

		ArrayList<Node> copy = new ArrayList<Node>();
		for (int i = 0; i < nodes.getLength(); i++)
			copy.add(nodes.item(i));

		Node sibling = src.getNextSibling();
		parent.removeChild(src);
		Node last = sibling;
		Node first = null;
		if (sibling == null) {
			for (Node n : copy) {
				last = parent.appendChild(doc.adoptNode(n));
				if (first == null)
					first = last;
			}
		} else {
			for (Node n : copy) {
				last = parent.insertBefore(doc.adoptNode(n), sibling);
				if (first == null)
					first = last;
			}
		}
		return first;
	}

	public static List<Node> getNodesOfType(Node node, String... names) {
		ArrayList<Node> list = new ArrayList<Node>();
		NodeIterator iter = new NodeIterator(node);
		NodeIteratorFilter iter2 = new NodeIteratorFilter(iter);
		// iter2.allowClass(names);
		iter2.allowName(names);
		iter2.allowType(Node.ELEMENT_NODE);
		while (iter2.hasNext()) {
			list.add(iter2.next());
		}
		return list;
	}

	public static List<NameValuePair> getHiddenInputs(Node node) {
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		NodeIterator iter = new NodeIterator(node);
		NodeIteratorFilter iter2 = new NodeIteratorFilter(iter);
		iter2.allowName("input");
		iter2.allowAttributValue("type", "hidden");
		iter2.allowType(Node.ELEMENT_NODE);
		while (iter2.hasNext()) {
			Node node2 = iter2.next();
			String value = DomUtils.getAttributValue(node2, "value");
			String name = DomUtils.getAttributValue(node2, "name");
			nvps.add(new BasicNameValuePair(name, value));
		}
		return nvps;
	}

	public static List<Node> getNodesOfClass(Node node, String... classNames) {
		ArrayList<Node> list = new ArrayList<Node>();
		NodeIterator iter = new NodeIterator(node);
		NodeIteratorFilter iter2 = new NodeIteratorFilter(iter);
		iter2.allowClass(classNames);
		iter2.allowType(Node.ELEMENT_NODE);
		while (iter2.hasNext()) {
			list.add(iter2.next());
		}
		return list;
	}

	public static List<Node> getTextNodes(Node node) {
		ArrayList<Node> list = new ArrayList<Node>();
		NodeIterator iter = new NodeIterator(node);
		NodeIteratorFilter iter2 = new NodeIteratorFilter(iter);
		iter2.allowType(Node.TEXT_NODE);
		while (iter2.hasNext())
			list.add(iter2.next());
		return list;
	}

	public static List<Node> getNodes(Node node) {
		ArrayList<Node> list = new ArrayList<Node>();
		NodeIterator iter = new NodeIterator(node);
		NodeIteratorFilter iter2 = new NodeIteratorFilter(iter);
		// iter2.allowType(Node.TEXT_NODE);
		while (iter2.hasNext())
			list.add(iter2.next());
		return list;
	}

	public static List<Node> toList(NodeList nodes) {
		int len = nodes.getLength();
		ArrayList<Node> list = new ArrayList<Node>(len);
		for (int i = 0; i < len; i++) {
			list.add(nodes.item(i));
		}
		return list;
	}

	public static Node getFirstNodeOfType(Node node, String... names) {
		NodeIterator iter = new NodeIterator(node);
		NodeIteratorFilter iter2 = new NodeIteratorFilter(iter);
		iter2.allowName(names);
		iter2.allowType(Node.ELEMENT_NODE);
		while (iter2.hasNext()) {
			return iter2.next();
		}
		return null;
	}

	public static Element getChild(Node node, String name) {
		NodeList list = node.getChildNodes();
		int count = list.getLength();
		for (int i = 0; i < count; i++) {
			Node n = list.item(i);
			if (n.getNodeType() != Node.ELEMENT_NODE)
				continue;
			if (n.getNodeName().equalsIgnoreCase(name))
				return (Element) n;
		}
		return null;
	}

	public static String toText(Node node) {
		try {
			DOMSource source = new DOMSource(node);
			Transformer identity = TransformerFactory.newInstance().newTransformer();
			identity.setOutputProperty(OutputKeys.INDENT, "no");
			identity.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			// identity.setOutputProperty(OutputKeys.METHOD, "text");
			// identity.setOutputProperty(OutputKeys.METHOD, "xml");
			// identity.setOutputProperty(OutputKeys.METHOD, "html");
			// class
			// com.sun.org.apache.xalan.internal.xsltc.trax.TransformerImpl
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			Result result = new StreamResult(baos);
			identity.transform(source, result);
			return new String(baos.toString("UTF-8"));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void removeNodesByType(Node parent, String... types) {
		for (Node n : DomUtils.getNodesOfType(parent, types))
			n.getParentNode().removeChild(n);
	}

	public static int removeNodesById(Document doc, String... ids) {
		int deleted = 0;
		for (String id : ids) {
			Element elm = doc.getElementById(id);
			if (elm != null && elm instanceof Node) {
				elm.getParentNode().removeChild((Node) elm);
				deleted++;
			}
		}
		return deleted;
	}

	public static void deleteNode(Node n) {
		if (n == null)
			return;
		Node parent = n.getParentNode();
		if (parent != null)
			parent.removeChild((Node) n);
	}

	public static void removeCommont(Node parent) {
		NodeIterator iter = new NodeIterator(parent);
		NodeIteratorFilter iter2 = new NodeIteratorFilter(iter);
		iter2.allowType(Node.COMMENT_NODE);
		while (iter2.hasNext()) {
			Node n = iter2.next();
			n.getParentNode().removeChild(n);
		}
	}

	public static boolean removeAttribute(Node node, String... attributs) {
		NamedNodeMap att = node.getAttributes();
		if (att == null)
			return false;
		boolean ret = false;
		for (String attribut : attributs) {
			if (att.getNamedItem(attribut) != null) {
				att.removeNamedItem(attribut);
				ret = true;
			}
		}
		return ret;
	}

	public static int removeNodesByClass(Node parent, String... clazz) {
		int removed = 0;
		ArrayList<Node> toRemove = new ArrayList<Node>();
		NodeIterator iter = new NodeIterator(parent);
		NodeIteratorFilter iter2 = new NodeIteratorFilter(iter);
		iter2.allowType(Node.ELEMENT_NODE);

		TreeSet<String> remClz = new TreeSet<String>();
		Collections.addAll(remClz, clazz);
		while (iter2.hasNext()) {
			Node n = iter2.next();
			Node cls = n.getAttributes().getNamedItem("class");
			if (cls == null)
				continue;
			String value = cls.getNodeValue();
			if (remClz.contains(value))
				toRemove.add(n);
		}
		for (Node n : toRemove) {
			removed++;
			n.getParentNode().removeChild(n);
		}
		return removed;
	}

	/*
	 * public static void toText(Node node, StringBuilder out) { boolean
	 * mustClose = false; String baliseName = null;
	 * 
	 * if (node.getNodeType() == Node.ELEMENT_NODE) { baliseName =
	 * node.getNodeName(); StringBuilder sb = new StringBuilder();
	 * sb.append("<"); sb.append(baliseName.toLowerCase()); NamedNodeMap atts =
	 * node.getAttributes();
	 * 
	 * for (int i = 0; i < atts.getLength(); i++) { sb.append(" "); Node n =
	 * atts.item(i); String attName = n.getNodeName(); String attValue =
	 * n.getNodeValue();
	 * 
	 * sb.append(attName).append('='); char quote = '"'; if
	 * (attValue.indexOf("\"") > 0) quote = '\'';
	 * sb.append(quote).append(attValue).append(quote); } if
	 * (node.getFirstChild() == null) sb.append("/>"); else { sb.append(">");
	 * mustClose = true; } out.append(sb.toString()); } else if
	 * (node.getNodeType() == Node.TEXT_NODE) { String text =
	 * node.getNodeValue(); out.append(text); } else if (node.getNodeType() ==
	 * Node.COMMENT_NODE) { // SKIP } else if (node.getNodeType() ==
	 * Node.DOCUMENT_NODE) { // Balise ROOT Rien a faire. } else if
	 * (node.getNodeType() == Node.DOCUMENT_TYPE_NODE) { DocumentType docType =
	 * (DocumentType) node; out.append("<!DOCTYPE html PUBLIC \"");
	 * out.append(docType.getPublicId()); out.append("\"\r\n      \"");
	 * out.append(docType.getSystemId()); out.append("\">\r\n"); // html PUBLIC
	 * "-//W3C//DTD XHTML 1.0 Transitional//EN" //
	 * "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"> } else { //
	 * org.springframework.util.xml.DomUtils Log.error(DomUtils.class,
	 * "Unknow Node type:" + node.getNodeType()); }
	 * 
	 * Node child = node.getFirstChild(); while (child != null) { toText(child,
	 * out); child = child.getNextSibling(); } if (mustClose) {
	 * out.append("</").append(node.getNodeName().toLowerCase()).append(">"); }
	 * }
	 */

	private static XPath xpath = XPathFactory.newInstance().newXPath();

	public static List<Node> evalXPath(Node node, String xPath) {
		synchronized (xpath) {
			try {
				NodeList nodes = (NodeList) xpath.evaluate(xPath, node, XPathConstants.NODESET);
				int size = nodes.getLength();
				ArrayList<Node> list = new ArrayList<Node>(size);
				for (int i = 0; i < size; i++)
					list.add(nodes.item(i));
				return list;
			} catch (XPathExpressionException e) {
				throw new RuntimeException("Invalid XPath expression:" + xPath);
			}
			// InputSource inputSource = new InputSource("myXMLDocument.xml");
		}
	}

	static class myNodeList implements NodeList {
		private List<Node> list;

		public myNodeList(List<Node> list) {
			this.list = list;
		}

		@Override
		public Node item(int index) {
			return list.get(index);
		}

		@Override
		public int getLength() {
			return list.size();
		}
	}

	static class NodeIteratorFilter implements Iterator<Node> {
		Iterator<Node> parent;
		Set<Integer> types;
		Set<String> names;
		Set<String> classs;

		private boolean done = false;
		private Node cached;

		// public NodeIteratorFilter(Node parent) {
		// this.parent = Arrays.asList(parent).iterator();
		// }

		public NodeIteratorFilter(Iterator<Node> parent) {
			this.parent = parent;
		}

		public void allowType(int type) {
			if (types == null)
				types = new HashSet<Integer>();
			types.add(type);
		}

		String attribName;
		String attribValue;

		public void allowAttributValue(String key, String value) {
			this.attribName = key.toLowerCase();
			this.attribValue = value;
		}

		public void allowName(String... values) {
			if (values == null)
				return;
			if (values.length == 0)
				return;
			if (names == null)
				names = new HashSet<String>();
			for (String n : values)
				names.add(n.toLowerCase());
		}

		public void allowClass(String... values) {
			if (values == null)
				return;
			if (values.length == 0)
				return;
			if (classs == null)
				classs = new HashSet<String>();
			for (String n : values)
				classs.add(n.toLowerCase());
		}

		@Override
		public boolean hasNext() {
			if (cached != null)
				return true;
			if (done)
				return false;
			while (parent.hasNext()) {
				Node n = parent.next();
				Integer type = Integer.valueOf(n.getNodeType());
				if (types != null && !types.contains(type))
					continue;
				if (names != null && !names.contains(n.getNodeName().toLowerCase()))
					continue;

				if (attribName != null) {
					Node node = n.getAttributes().getNamedItem(attribName);
					if (node == null && attribValue != null)
						continue;
					String value = node.getNodeValue().toLowerCase();
					if (!attribValue.equals(value))
						continue;
				}

				if (classs != null) {
					Node node = n.getAttributes().getNamedItem("class");
					if (node == null)
						continue;
					// add Split
					String value = node.getNodeValue().toLowerCase();
					if (!classs.contains(value))
						continue;
				}
				this.cached = n;
				return true;
			}
			done = true;
			return false;
		}

		@Override
		public Node next() {
			if (cached == null) {
				if (!done)
					hasNext();
				if (cached == null)
					throw new NoSuchElementException();
			}
			Node ret = cached;
			cached = null;
			return ret;
		}

		@Override
		public void remove() {
			// Dummy
		}

	}
	/*
	 * static class NodeIterator implements Iterator<Node> { private boolean
	 * done = false; private Node cached;
	 * 
	 * private Stack<NodeList> stack = new Stack<NodeList>(); private
	 * Stack<int[]> stackPos = new Stack<int[]>();
	 * 
	 * public NodeIterator(Node node) { stack.add(node.getChildNodes());
	 * stackPos.add(new int[] { 0 }); cached = node; }
	 * 
	 * @Override public boolean hasNext() { if (cached != null) return true; if
	 * (done) return false;
	 * 
	 * while (true) { if (stack.isEmpty()) { done = true; break; } NodeList peek
	 * = stack.peek(); int[] peekPos = stackPos.peek();
	 * 
	 * if (peek.getLength() > peekPos[0]) { cached = peek.item(peekPos[0]++);
	 * NodeList childes = cached.getChildNodes(); if (childes.getLength() == 0)
	 * ; // FINI else { stack.push(childes); stackPos.push(new int[] { 0 }); }
	 * break; // trouve } else { peek = stack.pop(); peekPos = stackPos.pop(); }
	 * } return !done; }
	 * 
	 * @Override public Node next() { if (cached == null) { if (!done)
	 * hasNext(); if (cached == null) throw new NoSuchElementException(); } Node
	 * ret = cached; cached = null; return ret; }
	 * 
	 * @Override public void remove() { throw new
	 * UnsupportedOperationException(); } }
	 */

}
