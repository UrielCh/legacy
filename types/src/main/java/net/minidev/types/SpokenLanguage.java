package net.minidev.types;

import static net.minidev.types.Language.*;

public class SpokenLanguage {
	public static SpokenLanguage SPOKE_enfr = new SpokenLanguage(en, fr);
	public static SpokenLanguage SPOKE_en = new SpokenLanguage(en);
	public static SpokenLanguage SPOKE_fr = new SpokenLanguage(fr);
	public static SpokenLanguage SPOKE_it = new SpokenLanguage(it);
	public static SpokenLanguage SPOKE_es = new SpokenLanguage(es);
	public static SpokenLanguage SPOKE_de = new SpokenLanguage(de);
	public static SpokenLanguage SPOKE_pt = new SpokenLanguage(pt);
	public static SpokenLanguage SPOKE_ar = new SpokenLanguage(ar);
	public static SpokenLanguage SPOKE_jp = new SpokenLanguage(ja);
	public static SpokenLanguage SPOKE_he = new SpokenLanguage(he);
	public static SpokenLanguage SPOKE_el = new SpokenLanguage(el);

	Language[] langs;

	public SpokenLanguage(Language... ls) {
		this.langs = ls;
	}

	public Language main() {
		return langs[0];
	}
	
	public int getLangCount() {
		return langs.length;
	}

}
