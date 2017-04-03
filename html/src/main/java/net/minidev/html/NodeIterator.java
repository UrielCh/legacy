package net.minidev.html;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class NodeIterator implements Iterator<Node> {
	private boolean done = false;
	private Node cached;

	// Document document;
	private Node root;
	private Node current;

	public NodeIterator(Node document) {
		this.root = document;
		current = document;
		cached = document;
		if (document == null)
			done = true;
	}

	@Override
	public boolean hasNext() {
		if (cached != null)
			return true;
		if (done)
			return false;
		Node next = getNext(current);
		setCurrent(next);
		return !done;
	}

	private Node getNext(Node theNode) {
		if (theNode.hasChildNodes()) {
			return theNode.getFirstChild();
		} else {
			return getNextSibling(theNode);
		}
	}

	private Node getNextSibling(Node theNode) {
		Node n = theNode.getNextSibling();
		while (n == null) {
			// Level up
			// if curseur is on ROOT
			if (root.equals(theNode)) // TODO remove ??
				return null;
			theNode = theNode.getParentNode();
			if (theNode == null) // fini
				return null;
			if (root.equals(theNode))
				return null;
			n = theNode.getNextSibling();
		}
		return n;
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

	public void reNext() {
		setCurrent(current);
	}

	public void setCurrent(Node n) {
		current = n;
		cached = n;
		if (n == null)
			done = true;
	}

	@Override
	public void remove() {
		Node theNext = getNextSibling(current);
		Node tmp = current.getParentNode();
		tmp.removeChild(current);
		current = theNext;
		cached = theNext;
		if (cached == null)
			done = true;
	}

	public void replace(Node n) {
		n = current.getOwnerDocument().adoptNode(n);
		Node parent = current.getParentNode();
		parent.replaceChild(n, current);
		current = n;
	}

	public void replace(NodeList nodes) {
		int size = nodes.getLength(); 
		if (size == 0)
			remove();
		else if (size == 1)
			replace(nodes.item(0));
		else
			current = DomUtils.replace(current, nodes);
	}
}
