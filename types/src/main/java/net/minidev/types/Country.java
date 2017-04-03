package net.minidev.types;

import java.util.HashMap;

import static net.minidev.types.Language.*;
import static net.minidev.types.SpokenLanguage.*;

/**
 * Country codes in ISO 3166
 * 
 * http://www.davros.org/misc/iso3166.html
 * http://en.wikipedia.org/wiki/List_of_official_languages_by_state
 * 
 * @author Uriel Chemouni
 */
public enum Country {
	AF("AFG", 4, Currency.AFN, "Afghanistan", new SpokenLanguage(fa)), // http://www.kwintessential.co.uk/translation-quote/index.php
	AL("ALB", 8, Currency.ALL, "Albania, People's Socialist Republic of", new SpokenLanguage(sq)), DZ("DZA", 12,
			Currency.DZD, "Algeria, People's Democratic Republic of", SPOKE_ar), AS("ASM", 16, Currency.USD,
			"American Samoa"), AD("AND", 20, Currency.EUR, "Andorra, Principality of", new SpokenLanguage(ca, es, fr,
			pt)), AO("AGO", 24, Currency.AOA, "Angola, Republic of", SPOKE_pt), AI("AIA", 660, Currency.XCD,
			"Anguilla", SPOKE_en), AQ("ATA", 10, Currency.USD, "Antarctica (the territory South of 60 deg S)"), AG(
			"ATG", 28, Currency.XCD, "Antigua and Barbuda", SPOKE_en), AR("ARG", 32, Currency.ARS,
			"Argentina, Argentine Republic"), 
			AM("ARM", 51, Currency.AMD, "Armenia"),
			AW("ABW", 533, Currency.AWG, "Aruba"),
			AU("AUS", 36, Currency.AUD, "Australia, Commonwealth of"),
			AT("AUT", 40, Currency.EUR,	"Austria, Republic of"),
			AZ("AZE", 31, Currency.AZN, "Azerbaijan, Republic of"),
			BS("BHS", 44, Currency.BSD, "Bahamas, Commonwealth of the"),
			BH("BHR", 48, Currency.BHD, "Bahrain, Kingdom of"),
			BD("BGD", 50, Currency.BDT, "Bangladesh, People's Republic of"), BB("BRB", 52, Currency.BBD, "Barbados"), BY(
			"BLR", 112, Currency.BYR, "Belarus"), BE("BEL", 56, Currency.EUR, "Belgium, Kingdom of",
			new SpokenLanguage(fr, nl, de)), BZ("BLZ", 84, Currency.BZD, "Belize"), BJ("BEN", 204, Currency.XOF,
			"Benin (was Dahomey), People's Republic of"), BM("BMU", 60, Currency.BMD, "Bermuda"), BT("BTN", 64,
			Currency.BTN, "Bhutan, Kingdom of"), BO("BOL", 68, Currency.BOB, "Bolivia, Republic of"), BA("BIH", 70,
			Currency.BAM, "Bosnia and Herzegovina"), BW("BWA", 72, Currency.BWP, "Botswana, Republic of"), BV("BVT",
			74, Currency.USD, "Bouvet Island (Bouvetoya)"), BR("BRA", 76, Currency.BRL,
			"Brazil, Federative Republic of", SPOKE_pt), IO("IOT", 86, Currency.USD,
			"British Indian Ocean Territory (Chagos Archipelago)"), VG("VGB", 92, Currency.XCD,
			"British Virgin Islands", SPOKE_en), BN("BRN", 96, Currency.BND, "Brunei Darussalam"), BG("BGR", 100,
			Currency.BGN, "Bulgaria, People's Republic of"), BF("BFA", 854, Currency.XOF,
			"Burkina Faso (was Upper Volta)"), BI("BDI", 108, Currency.BIF, "Burundi, Republic of"), KH("KHM", 116,
			Currency.KHR, "Cambodia, Kingdom of (was Khmer Republic/Kampuchea, Democratic)"), CM("CMR", 120,
			Currency.XAF, "Cameroon, United Republic of"), CA("CAN", 124, Currency.CAD, "Canada", SPOKE_enfr), CV(
			"CPV", 132, Currency.CVE, "Cape Verde, Republic of"), KY("CYM", 136, Currency.KYD, "Cayman Islands"), CF(
			"CAF", 140, Currency.XAF, "Central African Republic"), TD("TCD", 148, Currency.XAF, "Chad, Republic of"), CL(
			"CHL", 152, Currency.CLP, "Chile, Republic of", SPOKE_es), CN("CHN", 156, Currency.CNY,
			"China, People's Republic of"), CX("CXR", 162, Currency.AUD, "Christmas Island"), CC("CCK", 166,
			Currency.AUD, "Cocos (Keeling) Islands"), CO("COL", 170, Currency.COP, "Colombia, Republic of"), KM("COM",
			174, Currency.KMF, "Comoros, Union of the"),

	CD("COD", 180, Currency.CDF, "Congo, Democratic Republic of (was Zaire)", new SpokenLanguage(fr, ln, sw)), CG(
			"COG", 178, Currency.XAF, "Congo, People's Republic of"),

	CK("COK", 184, Currency.NZD, "Cook Islands"), CR("CRI", 188, Currency.CRC, "Costa Rica, Republic of"), CI("CIV",
			384, Currency.XOF, "Cote D'Ivoire, Ivory Coast, Republic of the"), CU("CUB", 192, Currency.CUP,
			"Cuba, Republic of"), CY("CYP", 196, Currency.EUR, "Cyprus, Republic of"), CZ("CZE", 203, Currency.CZK,
			"Czech Republic"), DK("DNK", 208, Currency.DKK, "Denmark, Kingdom of", new SpokenLanguage(da)), DJ("DJI",
			262, Currency.DJF, "Djibouti, Republic of (was French Afars and Issas)"), DM("DMA", 212, Currency.XCD,
			"Dominica, Commonwealth of"), DO("DOM", 214, Currency.DOP, "Dominican Republic"), EC("ECU", 218,
			Currency.USD, "Ecuador, Republic of"), EG("EGY", 818, Currency.EGP, "Egypt, Arab Republic of"), SV("SLV",
			222, Currency.USD, "El Salvador, Republic of"), GQ("GNQ", 226, Currency.XAF,
			"Equatorial Guinea, Republic of"), ER("ERI", 232, Currency.ERN, "Eritrea"), EE("EST", 233, Currency.EEK,
			"Estonia"), ET("ETH", 231, Currency.ETB, "Ethiopia"), FO("FRO", 234, Currency.DKK, "Faeroe Islands"), FK(
			"FLK", 238, Currency.FKP, "Falkland Islands (Malvinas)"), FJ("FJI", 242, Currency.FJD,
			"Fiji, Republic of the Fiji Islands"), FI("FIN", 246, Currency.EUR, "Finland, Republic of"), FR("FRA", 250,
			Currency.EUR, "France, French Republic", SPOKE_fr), GF("GUF", 254, Currency.EUR, "French Guiana"), PF(
			"PYF", 258, Currency.XPF, "French Polynesia"), TF("ATF", 260, Currency.EUR, "French Southern Territories"), GA(
			"GAB", 266, Currency.XAF, "Gabon, Gabonese Republic"), GM("GMB", 270, Currency.GMD,
			"Gambia, Republic of the"), GE("GEO", 268, Currency.GEL, "Georgia"), DE("DEU", 276, Currency.EUR,
			"Germany", SPOKE_de), GH("GHA", 288, Currency.GHS, "Ghana, Republic of"), GI("GIB", 292, Currency.GIP,
			"Gibraltar"), GR("GRC", 300, Currency.EUR, "Greece, Hellenic Republic", SPOKE_el), GL("GRL", 304,
			Currency.DKK, "Greenland"), GD("GRD", 308, Currency.XCD, "Grenada"), GP("GLP", 312, Currency.EUR,
			"Guadaloupe"), GU("GUM", 316, Currency.USD, "Guam"), GT("GTM", 320, Currency.GTQ, "Guatemala, Republic of"), GN(
			"GIN", 324, Currency.GNF, "Guinea, Revolutionary People's Rep'c of"), GW("GNB", 624, Currency.XOF,
			"Guinea-Bissau, Republic of (was Portuguese Guinea)"), GY("GUY", 328, Currency.GYD, "Guyana, Republic of"), HT(
			"HTI", 332, Currency.HTG, "Haiti, Republic of"), HM("HMD", 334, Currency.AUD, "Heard and McDonald Islands"), VA(
			"VAT", 336, Currency.EUR, "Holy See (Vatican City State)"), HN("HND", 340, Currency.HNL,
			"Honduras, Republic of"), HK("HKG", 344, Currency.HKD, "Hong Kong, Special Administrative Region of China"), HR(
			"HRV", 191, Currency.HRK, "Hrvatska (Croatia)"), HU("HUN", 348, Currency.HUF,
			"Hungary, Hungarian People's Republic"), IS("ISL", 352, Currency.ISK, "Iceland, Republic of"), IN("IND",
			356, Currency.INR, "India, Republic of"), ID("IDN", 360, Currency.IDR, "Indonesia, Republic of"), 
			IR("IRN", 364, Currency.IRR, "Iran, Islamic Republic of"),
			IQ("IRQ", 368, Currency.IQD, "Iraq, Republic of"),
			IE("IRL", 372, Currency.EUR, "Ireland", new SpokenLanguage(ga, en)),
			IL("ISR", 376, Currency.ILS, "Israel, State of", SPOKE_he), IT("ITA", 380, Currency.EUR, "Italy, Italian Republic", SPOKE_it), JM("JAM",
			388, Currency.JMD, "Jamaica"), JP("JPN", 392, Currency.JPY, "Japan", new SpokenLanguage(ja)), JO("JOR",
			400, Currency.JOD, "Jordan, Hashemite Kingdom of"), KZ("KAZ", 398, Currency.KZT, "Kazakhstan, Republic of"), KE(
			"KEN", 404, Currency.KES, "Kenya, Republic of"), KI("KIR", 296, Currency.AUD,
			"Kiribati, Republic of (was Gilbert Islands)"), KP("PRK", 408, Currency.KPW,
			"Korea, Democratic People's Republic of"), KR("KOR", 410, Currency.KRW, "Korea, Republic of"), KW("KWT",
			414, Currency.KWD, "Kuwait, State of"), KG("KGZ", 417, Currency.KGS, "Kyrgyz Republic"), LA("LAO", 418,
			Currency.LAK, "Lao People's Democratic Republic"), LV("LVA", 428, Currency.LVL, "Latvia"), LB("LBN", 422,
			Currency.LBP, "Lebanon, Lebanese Republic"), LS("LSO", 426, Currency.LSL, "Lesotho, Kingdom of"), LR("LBR",
			430, Currency.LRD, "Liberia, Republic of"), LY("LBY", 434, Currency.LYD, "Libyan Arab Jamahiriya"), LI(
			"LIE", 438, Currency.CHF, "Liechtenstein, Principality of"), LT("LTU", 440, Currency.LTL, "Lithuania"), LU(
			"LUX", 442, Currency.EUR, "Luxembourg, Grand Duchy of", new SpokenLanguage(de, fr, lb)), MO("MAC", 446,
			Currency.MOP, "Macao, Special Administrative Region of China"), MK("MKD", 807, Currency.MKD,
			"Macedonia, the former Yugoslav Republic of"), MG("MDG", 450, Currency.MGA, "Madagascar, Republic of"), MW(
			"MWI", 454, Currency.MWK, "Malawi, Republic of"), MY("MYS", 458, Currency.MYR, "Malaysia"), MV("MDV", 462,
			Currency.MVR, "Maldives, Republic of"), ML("MLI", 466, Currency.XOF, "Mali, Republic of"), MT("MLT", 470,
			Currency.EUR, "Malta, Republic of"), MH("MHL", 584, Currency.USD, "Marshall Islands"), MQ("MTQ", 474,
			Currency.EUR, "Martinique"), MR("MRT", 478, Currency.MRO, "Mauritania, Islamic Republic of"), MU("MUS",
			480, Currency.MUR, "Mauritius", SPOKE_enfr), YT("MYT", 175, Currency.EUR, "Mayotte"), 
			MX("MEX", 484, Currency.MXN, "Mexico, United Mexican States", SPOKE_es),
			FM("FSM", 583, Currency.USD,
			"Micronesia, Federated States of"), MD("MDA", 498, Currency.MDL, "Moldova, Republic of"), MC("MCO", 492,
			Currency.EUR, "Monaco, Principality of"), MN("MNG", 496, Currency.MNT,
			"Mongolia, Mongolian People's Republic"), MS("MSR", 500, Currency.EUR, "Montserrat"), 
			MA("MAR", 504, Currency.MAD, "Morocco, Kingdom of"), MZ("MOZ", 508, Currency.MZN, "Mozambique, People's Republic of"), MM(
			"MMR", 104, Currency.MMK, "Myanmar (was Burma)"), NA("NAM", 516, Currency.NAD, "Namibia"), NR("NRU", 520,
			Currency.AUD, "Nauru, Republic of"), NP("NPL", 524, Currency.NPR, "Nepal, Kingdom of"), AN("ANT", 530,
			Currency.ANG, "Netherlands Antilles"), NL("NLD", 528, Currency.EUR, "Netherlands, Kingdom of the"), NC(
			"NCL", 540, Currency.XPF, "New Caledonia"), NZ("NZL", 554, Currency.NZD, "New Zealand"), NI("NIC", 558,
			Currency.NIO, "Nicaragua, Republic of"), NE("NER", 562, Currency.XOF, "Niger, Republic of the"), NG("NGA",
			566, Currency.NGN, "Nigeria, Federal Republic of"), NU("NIU", 570, Currency.NZD, "Niue, Republic of"), NF(
			"NFK", 574, Currency.AUD, "Norfolk Island"), MP("MNP", 580, Currency.USD, "Northern Mariana Islands"), NO(
			"NOR", 578, Currency.NOK, "Norway, Kingdom of"), OM("OMN", 512, Currency.OMR,
			"Oman, Sultanate of (was Muscat and Oman)"), PK("PAK", 586, Currency.PKR, "Pakistan, Islamic Republic of"), PW(
			"PLW", 585, Currency.USD, "Palau"), PS("PSE", 275, Currency.USD, "Palestinian Territory, Occupied"), PA(
			"PAN", 591, Currency.PAB, "Panama, Republic of"), PG("PNG", 598, Currency.PGK, "Papua New Guinea"), PY(
			"PRY", 600, Currency.PYG, "Paraguay, Republic of"), PE("PER", 604, Currency.PEN, "Peru, Republic of"), PH(
			"PHL", 608, Currency.PHP, "Philippines, Republic of the"), PN("PCN", 612, Currency.NZD, "Pitcairn Island"), PL(
			"POL", 616, Currency.PLN, "Poland, Polish People's Republic"), PT("PRT", 620, Currency.EUR,
			"Portugal, Portuguese Republic", SPOKE_pt), PR("PRI", 630, Currency.USD, "Puerto Rico"), QA("QAT", 634,
			Currency.QAR, "Qatar, State of"), RE("REU", 638, Currency.EUR, "Reunion", SPOKE_fr), RO("ROU", 642,
			Currency.RON, "Romania, Socialist Republic of"), RU("RUS", 643, Currency.RUB, "Russian Federation"), RW(
			"RWA", 646, Currency.RWF, "Rwanda, Rwandese Republic"), RS("SRB", 688, Currency.RSD, "Serbia"), SH("SHN",
			654, Currency.SHP, "St. Helena"), KN("KNA", 659, Currency.XCD, "St. Kitts and Nevis"), LC("LCA", 662,
			Currency.XCD, "St. Lucia"), PM("SPM", 666, Currency.EUR, "St. Pierre and Miquelon"), VC("VCT", 670,
			Currency.XCD, "St. Vincent and the Grenadines"), WS("WSM", 882, Currency.WST,
			"Samoa, Independent State of (was Western Samoa)"), SM("SMR", 674, Currency.EUR, "San Marino, Republic of"), ST(
			"STP", 678, Currency.STD, "Sao Tome and Principe, Democratic Republic of"), SA("SAU", 682, Currency.SAR,
			"Saudi Arabia, Kingdom of"), SN("SEN", 686, Currency.XOF, "Senegal, Republic of"), CS("SCG", 891,
			Currency.RSD, "Serbia and Montenegro"), SC("SYC", 690, Currency.SCR, "Seychelles, Republic of"), SL("SLE",
			694, Currency.SLL, "Sierra Leone, Republic of"), SG("SGP", 702, Currency.SGD, "Singapore, Republic of"), SK(
			"SVK", 703, Currency.SKK, "Slovakia (Slovak Republic)"), SI("SVN", 705, Currency.EUR, "Slovenia"), SB(
			"SLB", 90, Currency.SBD, "Solomon Islands (was British Solomon Islands)"), SO("SOM", 706, Currency.SOS,
			"Somalia, Somali Republic"), ZA("ZAF", 710, Currency.ZAR, "South Africa, Republic of"), GS("SGS", 239,
			Currency.FKP, "South Georgia and the South Sandwich Islands"), ES("ESP", 724, Currency.EUR,
			"Spain, Spanish State", new SpokenLanguage(es, ca, gl, eu, oc)), LK("LKA", 144, Currency.LKR,
			"Sri Lanka, Democratic Socialist Republic of (was Ceylon)"), SD("SDN", 736, Currency.SDG,
			"Sudan, Democratic Republic of the"), SR("SUR", 740, Currency.SRD, "Suriname, Republic of"), SJ("SJM", 744,
			Currency.USD, "Svalbard & Jan Mayen Islands"), SZ("SWZ", 748, Currency.SZL, "Swaziland, Kingdom of"), SE(
			"SWE", 752, Currency.SEK, "Sweden, Kingdom of"), CH("CHE", 756, Currency.CHF,
			"Switzerland, Swiss Confederation", new SpokenLanguage(de, fr, it, rm)), SY("SYR", 760, Currency.SYP,
			"Syrian Arab Republic"), TW("TWN", 158, Currency.TWD, "Taiwan, Province of China"), TJ("TJK", 762,
			Currency.TJS, "Tajikistan"), TZ("TZA", 834, Currency.TZS, "Tanzania, United Republic of"), TH("THA", 764,
			Currency.THB, "Thailand, Kingdom of"), TL("TLS", 626, Currency.USD, "Timor-Leste, Democratic Republic of"), TG(
			"TGO", 768, Currency.XOF, "Togo, Togolese Republic"), TK("TKL", 772, Currency.NZD,
			"Tokelau (Tokelau Islands)"), TO("TON", 776, Currency.TOP, "Tonga, Kingdom of"), TT("TTO", 780,
			Currency.TTD, "Trinidad and Tobago, Republic of"), TN("TUN", 788, Currency.TND, "Tunisia, Republic of"), TR(
			"TUR", 792, Currency.YTL, "Turkey, Republic of"), TM("TKM", 795, Currency.TMM, "Turkmenistan"), TC("TCA",
			796, Currency.USD, "Turks and Caicos Islands"), TV("TUV", 798, Currency.AUD,
			"Tuvalu (was part of Gilbert & Ellice Islands)"), VI("VIR", 850, Currency.USD, "US Virgin Islands"), UG(
			"UGA", 800, Currency.UGX, "Uganda, Republic of"), UA("UKR", 804, Currency.UAH, "Ukraine"), AE("ARE", 784,
			Currency.AED, "United Arab Emirates (was Trucial States)"), GB("GBR", 826, Currency.GBP,
			"United Kingdom of Great Britain & N. Ireland"), UM("UMI", 581, Currency.USD,
			"United States Minor Outlying Islands"), US("USA", 840, Currency.USD, "United States of America"), UY(
			"URY", 858, Currency.UYU, "Uruguay, Eastern Republic of"), UZ("UZB", 860, Currency.UZS, "Uzbekistan"), VU(
			"VUT", 548, Currency.VUV, "Vanuatu (was New Hebrides)"), VE("VEN", 862, Currency.VEF,
			"Venezuela, Bolivarian Republic of"), VN("VNM", 704, Currency.VND,
			"Viet Nam, Socialist Republic of (was Democratic Republic of & Republic of)"), WF("WLF", 876, Currency.XPF,
			"Wallis and Futuna Islands"), EH("ESH", 732, Currency.USD, "Western Sahara (was Spanish Sahara)"), YE(
			"YEM", 887, Currency.YER, "Yemen"), ZM("ZMB", 894, Currency.BIE, "Zambia, Republic of"), ZW("ZWE", 716,
			Currency.ZWR, "Zimbabwe (was Southern Rhodesia)"), 
			NONE("NONE", 0, Currency.EUR, "none"), ALL("ALL", 0, Currency.EUR, "ALL");

	private Country(String code3, int code, Currency currency, String fullName, SpokenLanguage langs) {
		this.code = code;
		this.currency = currency;
		this.code3 = code3;
		this.fullname = fullName;
		this.langs = langs;
	}

	private Country(String code3, int code, Currency currency, String fullName) {
		this.code = code;
		this.currency = currency;
		this.code3 = code3;
		this.fullname = fullName;
		this.langs = SpokenLanguage.SPOKE_en;
	}

	SpokenLanguage langs;
	String code3;
	int code;
	String fullname;
	Currency currency;

	public Language getMainLanguage() {
		return langs.main();
	}

	public int getCode() {
		return code;
	}

	public String getFullname() {
		return fullname;
	}
	
	private static void alias(String name, Country country) {
		map.put(name.toUpperCase(), country);
	}

	private static HashMap<String, Country> map;
	static {
		// CD colision avec CG Congo
		// KP colision avec KR Korea
		map = new HashMap<String, Country>();
		for (Country c : Country.values()) {
			alias(c.name(), c);
			alias(c.code3, c);
			alias(Integer.toString(c.code), c);
			String s = c.fullname;
			int p = s.indexOf(",");
			if (p > 0)
				s = s.substring(0, p);

			p = s.indexOf("(");
			if (p > 0)
				s = s.substring(0, p);
			s = s.trim();
			alias(s, c);

			if (s.indexOf("St.") > 0)
				alias(s.replace("St.", "saint"), c);

		}
		alias("United States", US);
		alias("United Kingdom", GB);
		// old ISO Code
		alias("FX", FR); // France metropole
		alias("UK", GB); // United Kingdom

		alias("IM", GB); // Isle of Man
		alias("Isle of Man".toUpperCase(), GB); // Isle of Man

		alias("JE", GB); // Jersey
		alias("Jersey", GB); // Jersey

		alias("GG", GB); // Guernsey
		alias("Guernsey", GB); // Guernsey
		alias("Channel Islands", GB); // Guernsey

		alias("ZR", CD); // Zair
		alias("Zaire", CD);
		alias("Democratic Republic of the Congo", CD);

		alias("Vatican", VA);

		alias("AX", FI);
		alias("Aland", FI);

		alias("BU", MM); // Burma
		alias("Burma", MM); // Burma

		alias("SF", FI); // Finland
		alias("TP", TL); // East Timor
		// Yugoslavia HR - CS?
		alias("YU", CS);
		alias("Yugoslavia", CS);
		// alias("Yugoslavia", MK);
		alias("Côte d'Ivoire", CI);
		alias("United Republic of Tanzania", TZ);
		alias("Guadeloupe", GP);
		alias("Falklands", FK);
		alias("Saint Pierre and Miquelon", PM);
		alias("Saint Lucia", LC);
		alias("Saint Kitts and Nevis", KN);
		alias("Saint Vincent and the Grenadines", VC);
		alias("Gaza Strip", PS);
		alias("West Bank", PS);
		alias("Libya", LY);
		alias("Syria", SY);
		alias("Croatia", HR);
		alias("Serbia", CS);
		alias("Montenegro", CS);
		alias("Kosovo", RS);
		alias("Kyrgyzstan", KG);
		alias("Russia", RU);

		alias("NORTH Korea", KP);
		alias("South Korea", KR);

		alias("Brunei", BN);
		alias("Laos", LA);
		alias("Vietnam", VN);
		alias("Pitcairn", PN);

		alias("Macau", MO);
		// FR alias
		alias("Pays-Bas", NL);
		alias("Royaume-Uni", GB);
		alias("Italie", IT);
		alias("Allemagne", DE);
		alias("Belgique", BE);
		alias("Inde", IN);
		alias("Suède", SE);
		alias("Danemark", DK);
		alias("Autriche", AT);
		alias("Pologne", PL);
		alias("Suisse", CH);
		alias("États-Unis", US);
		alias("Tanzanie", TZ);
		alias("République tchèque", CZ);
		alias("Australie", AU);
		alias("Croatie", HR);
		alias("Égypte", EG);
		alias("Nouvelle-Calédonie", NC);
		alias("Roumanie", RO);
		alias("Afrique du Sud", ZA);
		alias("Slovénie", SI);
		alias("Corée du Nord", KP);

		alias("Hongrie", HU);
		alias("Algérie", DZ);
		alias("Norvège", NO);
		alias("Tunisie", TN);
		alias("Bulgarie", BG);
		alias("Arabie saoudite", SA);
		alias("Île Maurice", MU);
		alias("Chypre", CY);
		alias("Brésil", BR);
		alias("Cameroun", CM);
		alias("Congo-Kinshasa", CD);
		alias("Bosnie-Herzégovine", BA);
		alias("Slovaquie", SK);
		alias("Arabie saoudite", SA);
		alias("Cambodge", KH);
		alias("Biélorussie", BY);
		
		alias("Mexique", MX);
		alias("Pérou", PE);
		alias("Maroc", MA);
		alias("Irak", IQ);
		alias("Géorgie", GE);

		
		alias("Arménie", AM);
		alias("Tadjikistan", TJ);
	
	
	}

	public String getCurrencyFormat() {
		if (this == FR)
			return "%v %s";
		else
			return "%s %v";
	}

	public static Country fromCode(String code) {
		if (code == null)
			return null;
		return map.get(code.toUpperCase());
	}
}
