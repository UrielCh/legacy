package net.minidev.types;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Language Base format ISO 639
 * 
 * http://ftp.ics.uci.edu/pub/ietf/http/related/iso639.txt
 * 
 * @see http://en.wikipedia.org/w/index.php?title=List_of_ISO_639-1_codes&oldid=133943906
 * @encoding UTF8
 * 
 * @see VLocale
 * 
 * @author Uriel Chemouni
 */
public enum Language {
	aa("aar", "Afar", "Afaraf"),
	ab("abk", "Abkhazian", "Аҧсуа"),
	ae("ave", "Avestan", "avesta"),
	af("afr", "Afrikaans", "Afrikaans"),
	ak("aka", "Akan", "Akan"),
	am("amh", "Amharic", "አማርኛ"),
	an("arg", "Aragonese", "Aragonés"),
	ar("ara", "Arabic", "‫العربية"),
	as("asm", "Assamese", "অসমীয়া"),
	av("ava", "Avaric", "авар мацӀ"),
	ay("aym", "Aymara", "aymar aru"),
	az("aze", "Azerbaijani", "azərbaycan dili"),
	ba("bak", "Bashkir", "башҡорт теле"),
	be("bel", "Belarusian", "Беларуская"),
	bg("bul", "Bulgarian", "български език"),
	bh("bih", "Bihari", "भोजपुरी"),
	bi("bis", "Bislama", "Bislama"),
	bm("bam", "Bambara", "bamanankan"),
	bn("ben", "Bengali", "বাংলা"),
	bo("tib", "Tibetan", "བོད་ཡིག"),
	br("bre", "Breton", "brezhoneg"),
	bs("bos", "Bosnian", "bosanski jezik"),
	ca("cat", "Catalan", "Català"),
	ce("che", "Chechen", "нохчийн мотт"),
	ch("cha", "Chamorro", "Chamoru"),
	co("cos", "Corsican", "corsu"),
	cr("cre", "Cree", "ᓀᐦᐃᔭᐍᐏᐣ"),
	cs("cze", "Czech", "česky"),
	cv("chv", "Chuvash", "чӑваш чӗлхи"),
	cy("wel", "Welsh", "Cymraeg"),
	da("dan", "Danish", "dansk"),
	de("ger", "German", "Deutsch"),
	dv("div", "Divehi", "‫ދިވެހި"),
	dz("dzo", "Dzongkha", "རྫོང་ཁ"),
	ee("ewe", "Ewe", "Ɛʋɛgbɛ"),
	el("gre", "Greek", "Ελληνικά"),
	en("eng", "English", "English"),
	eo("epo", "Esperanto", "Esperanto"),
	es("spa", "Spanish", "español"),
	et("est", "Estonian", "Eesti keel"),
	eu("baq", "Basque", "euskara"),
	fa("per", "Persian", "فارسی"),
	ff("ful", "Fulah", "Fulfulde"),
	fi("fin", "Finnish", "suomen kieli"),
	fj("fij", "Fijian", "vosa Vakaviti"),
	fo("fao", "Faroese", "Føroyskt"),
	fr("fre", "French", "français"),
	fy("fry", "Western Frisian", "Frysk"),
	ga("gle", "Irish", "Gaeilge"),
	gd("gla", "Scottish Gaelic", "Gàidhlig"),
	gl("glg", "Galician", "Galego"),
	gn("grn", "Guaraní", "Avañe'ẽ"),
	gu("guj", "Gujarati", "ગુજરાતી"),
	gv("glv", "Manx", "Ghaelg"),
	ha("hau", "Hausa", "‫هَوُسَ"),
	he("heb", "Hebrew", "‫עברית"),
	hi("hin", "Hindi", "हिन्दी; हिंदी"),
	ho("hmo", "Hiri Motu", "Hiri Motu"),
	hr("scr", "Croatian", "Hrvatski"),
	ht("hat", "Haitian", "Kreyòl ayisyen"),
	hu("hun", "Hungarian", "Magyar"),
	hy("arm", "Armenian", "Հայերեն"),
	hz("her", "Herero", "Otjiherero"),
	ia("ina", "Interlingua", "interlingua"), //  (International Auxiliary Language Association)
	id("ind", "Indonesian", "Bahasa Indonesia"),
	ie("ile", "Interlingue", "Interlingue"),
	ig("ibo", "Igbo", "Igbo"),
	ii("iii", "Sichuan Yi", "ꆇꉙ"),
	ik("ipk", "Inupiaq", "Iñupiaq"),
	io("ido", "Ido", "Ido"),
	is("ice", "Icelandic", "Íslenska"),
	it("ita", "Italian", "Italiano"),
	iu("iku", "Inuktitut", "ᐃᓄᒃᑎᑐᑦ"),
	ja("jpn", "Japanese", "日本語"),
	jv("jav", "Javanese", "basa Jawa"),
	ka("geo", "Georgian", "ქართული"),
	kg("kon", "Kongo", "KiKongo"),
	ki("kik", "Kikuyu", "Gĩkũyũ"),
	kj("kua", "Kuanyama", "Kuanyama"),
	kk("kaz", "Kazakh", "Қазақ тілі"),
	kl("kal", "Kalaallisut", "kalaallisut"),
	km("khm", "Khmer", "ភាសាខ្មែរ"),
	kn("kan", "Kannada", "ಕನ್ನಡ"),
	ko("kor", "Korean", "한국어"),
	kr("kau", "Kanuri", "Kanuri"),
	ks("kas", "Kashmiri", "कश्मीरी"),
	ku("kur", "Kurdish", "Kurdî"),
	kv("kom", "Komi", "коми кыв"),
	kw("cor", "Cornish", "Kernewek"),
	ky("kir", "Kirghiz", "кыргыз тили"),
	la("lat", "Latin", "latine"),
	lb("ltz", "Luxembourgish", "Lëtzebuergesch"),
	lg("lug", "Ganda", "Luganda"),
	li("lim", "Limburgish", "Limburgs"),
	ln("lin", "Lingala", "Lingála"),
	lo("lao", "Lao", "ພາສາລາວ"),
	lt("lit", "Lithuanian", "lietuvių kalba"),
	lv("lav", "Latvian", "latviešu valoda"),
	mg("mlg", "Malagasy", "Malagasy fiteny"),
	mh("mah", "Marshallese", "Kajin M̧ajeļ"),
	mi("mao", "Māori", "te reo Māori"),
	mk("mac", "Macedonian", "македонски јазик"),
	ml("mal", "Malayalam", "മലയാളം"),
	mn("mon", "Mongolian", "Монгол"),
	mo("mol", "Moldavian", "лимба молдовеняскэ"),
	mr("mar", "Marathi", "मराठी"),
	ms("may", "Malay", "bahasa Melayu"),
	mt("mlt", "Maltese", "Malti"),
	my("bur", "Burmese", "ဗမာစာ"),
	na("nau", "Nauru", "Ekakairũ Naoero"),
	nb("nob", "Norwegian Bokmål", "Norsk bokmål"),
	nd("nde", "North Ndebele", "isiNdebele"),
	ne("nep", "Nepali", "नेपाली"),
	ng("ndo", "Ndonga", "Owambo"),
	nl("dut", "Dutch", "Nederlands"),
	nn("nno", "Norwegian Nynorsk", "Norsk nynorsk"),
	no("nor", "Norwegian", "Norsk"),
	nr("nbl", "South Ndebele", "Ndébélé"),
	nv("nav", "Navajo", "Diné bizaad"),
	ny("nya", "Chichewa", "chiCheŵa"),
	oc("oci", "Occitan", "Occitan"),
	oj("oji", "Ojibwa", "ᐊᓂᔑᓈᐯᒧᐎᓐ"),
	om("orm", "Oromo", "Afaan Oromoo"),
	or("ori", "Oriya", "ଓଡ଼ିଆ"),
	os("oss", "Ossetian", "Ирон æвзаг"),
	pa("pan", "Panjabi", "ਪੰਜਾਬੀ"),
	pi("pli", "Pāli", "पािऴ"),
	pl("pol", "Polish", "polski"),
	ps("pus", "Pashto", "‫پښتو"),
	pt("por", "Portuguese", "Português"),
	qu("que", "Quechua", "Runa Simi"),
	rm("roh", "Raeto-Romance", "rumantsch grischun"),
	rn("run", "Kirundi", "kiRundi"),
	ro("rum", "Romanian", "română"),
	ru("rus", "Russian", "русский язык"),
	rw("kin", "Kinyarwanda", "Kinyarwanda"),
	sa("san", "Sanskrit", "संस्कृतम्"),
	sc("srd", "Sardinian", "sardu"),
	sd("snd", "Sindhi", "सिन्धी"),
	se("sme", "Northern Sami", "Davvisámegiella"),
	sg("sag", "Sango", "yângâ tî sängö"),
	si("sin", "Sinhalese", "සිංහල"),
	sk("slo", "Slovak", "slovenčina"),
	sl("slv", "Slovenian", "slovenščina"),
	sm("smo", "Samoan", "gagana fa'a Samoa"),
	sn("sna", "Shona", "chiShona"),
	so("som", "Somali", "Soomaaliga"),
	sq("alb", "Albanian", "Shqip"),
	sr("scc", "Serbian", "српски језик"),
	ss("ssw", "Swati", "SiSwati"),
	st("sot", "Sotho", "seSotho"),
	su("sun", "Sundanese", "Basa Sunda"),
	sv("swe", "Swedish", "Svenska"),
	sw("swa", "Swahili", "Kiswahili"),
	ta("tam", "Tamil", "தமிழ்"),
	te("tel", "Telugu", "తెలుగు"),
	tg("tgk", "Tajik", "тоҷикӣ"),
	th("tha", "Thai", "ไทย"),
	ti("tir", "Tigrinya", "ትግርኛ"),
	tk("tuk", "Turkmen", "Türkmen"),
	tl("tgl", "Tagalog", "Tagalog"),
	tn("tsn", "Tswana", "seTswana"),
	to("ton", "Tonga", "faka Tonga"),
	tr("tur", "Turkish", "Türkçe"),
	ts("tso", "Tsonga", "xiTsonga"),
	tt("tat", "Tatar", "татарча"),
	tw("twi", "Twi", "Twi"),
	ty("tah", "Tahitian", "Reo Mā`ohi"),
	ug("uig", "Uighur", "Uyƣurqə"),
	uk("ukr", "Ukrainian", "українська мова"),
	ur("urd", "Urdu", "‫اردو"),
	uz("uzb", "Uzbek", "O'zbek"),
	ve("ven", "Venda", "tshiVenḓa"),
	vi("vie", "Vietnamese", "Tiếng Việt"),
	vo("vol", "Volapük", "Volapük"),
	wa("wln", "Walloon", "Walon"),
	wo("wol", "Wolof", "Wollof"),
	xh("xho", "Xhosa", "isiXhosa"),
	yi("yid", "Yiddish", "‫ייִדיש"),
	yo("yor", "Yoruba", "Yorùbá"),
	za("zha", "Zhuang", "Saɯ cueŋƅ"),
	zh("chi", "Chinese", "中文"),
	zu("zul", "Zulu", "isiZulu"),
	all("all","all","all"),
	none("none","none","none");

	Language(String iso639_2, String nameEn, String nativeName) {
		this.iso639_2 = iso639_2;
		this.nameEn = nameEn;
		this.nativeName = nativeName;
	}

	String iso639_2;
	String nameEn;
	String nativeName;

	public String getNameEn() {
		return nameEn;
	}

	public String getNativeName() {
		return nativeName;
	}

	private static HashMap<String, Language> map;
	static {
		map = new HashMap<String, Language>();
		for (Language c : Language.values()) {
			map.put(c.name(), c);
		}
		// formerly Alias
		map.put("iw", he);
		map.put("in", id);
		map.put("ji", yi);
	}

	public static Language fromCode(String code) {
		if (code == null)
			return null;
		// strip country
		if (code.length() > 2)
			code = code.substring(0, 2);
		return map.get(code.toLowerCase());
	}

	public static void main(String[] args) throws Exception {
		StringBuilder sb = new StringBuilder("ENUM(");
		int c = 0;
		for (Language l : Language.values()) {
			if (c++ > 0)
				sb.append(", ");
			sb.append("\"").append(l).append("\"");
		}
		sb.append(")");
		System.out.println(sb.toString());
	}
	
	@SuppressWarnings("unused")
	private static void dlLanguage() throws Exception {	
		URL uri = new URL("http://en.wikipedia.org/w/index.php?title=List_of_ISO_639-1_codes&oldid=133943906");
		ByteArrayOutputStream baos = new ByteArrayOutputStream(2000);
		InputStream input = uri.openStream();
		byte[] buf = new byte[1000];
		int bytesRead = input.read(buf);
		while (bytesRead != -1) {
			baos.write(buf, 0, bytesRead);
			bytesRead = input.read(buf);
		}
		input.close();
		Pattern pat;
		pat = Pattern.compile("(?m)<tr>\\s+<td>([a-z]{2})</td>\\s+<td>([a-z]{3})(?:/[a-z]{3})?</td>\\s+[^\n]+\n<td>([^<]+)</td>\\s+<td[^>]*>(?:<span[^>]+>)?([^<]+)</");
		
		String page = baos.toString("UTF8");
		page = page.replaceAll("<a [^>]+>", "");
		page = page.replaceAll("</a>", "");
		Matcher m = pat.matcher(page);
		StringBuilder sb = new StringBuilder();
		int count = 0;
		while (m.find()) {
			count ++;
			String iso639_1 = m.group(1);
			String iso639_2 = m.group(2);
			String name = m.group(3);
			String nativeName = m.group(4);
			String[] splited = nativeName.split(";");
			if (splited.length > 0)
				nativeName = splited[0];
			sb.append("\t").append(iso639_1).append("(\"");
			sb.append(iso639_2).append("\", \"").append(name).append("\"");
			sb.append(", \"").append(nativeName).append("\"),\n");
		}
		// missing:  mk("mac", "Macedonian", ""),
		sb.append("\tall(\"all\",\"all\",\"all\"),\n");
		sb.append("\tnone(\"none\",\"none\",\"none\");");
		System.out.println(sb.toString());
		System.out.println("// total : " + count);
	}
}
