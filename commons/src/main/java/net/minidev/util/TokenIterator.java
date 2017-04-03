package net.minidev.util;

/**
 * tokenise un text en extayant les variable empris entre des séparateur.
 * 
 * @author uriel
 *
 */
public class TokenIterator {
	private int p;
	String prevText;
	String theBody;
	String variable;
	boolean mustCallNext;

	final String TOKEN_START;
	final String TOKEN_STOP;

	public TokenIterator(String TOKEN_START, String TOKEN_STOP) {
		this.TOKEN_START = TOKEN_START;
		this.TOKEN_STOP = TOKEN_STOP;
	}

	public void reset(String text) {
		this.theBody = text;
		this.mustCallNext = true;
		this.prevText = "";
		this.variable = null;
		this.p = 0;
		hasNext();
	}

	public boolean hasNext() {
		if (mustCallNext)
			iNext();
		mustCallNext = false;
		return prevText != null;// p != -1;
	}

	public String nextText() {
		mustCallNext = true;
		return prevText;
	}

	public String nextVariable() {
		mustCallNext = true;
		return variable;
	}

	private void iNext() {
		variable = null;
		prevText = null;
		if (p == -1)
			return;
		int p2 = theBody.indexOf(TOKEN_START, p);
		if (p2 == -1) {
			prevText = theBody.substring(p, theBody.length());
			p = -1;
			return;
		}
		int p3 = theBody.indexOf(TOKEN_STOP, p2 + TOKEN_START.length());
		if (p3 == -1) {
			prevText = theBody.substring(p, theBody.length());
			p = -1;
			return;
		}
		prevText = theBody.substring(p, p2);
		variable = theBody.substring(p2 + TOKEN_START.length(), p3);
		p = p3 + TOKEN_STOP.length();
	}
}
