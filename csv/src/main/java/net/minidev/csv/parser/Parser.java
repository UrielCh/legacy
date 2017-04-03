package net.minidev.csv.parser;
/**
 * @see ParserSimple
 * @see ParserDumy
 * @see ParserFixedLen
 * @see ParserQuoted
 * @see ParserExcel
 * 
 * @author uriel
 *
 */
public interface Parser {
	public int count(String str);
	public String[] split(String line);
	public String[] splitTo(String str, String[] dest);	
	public int getPriority();
}
