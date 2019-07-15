package com.dogukanhan.profanity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

public class ProfanityRegister {

	private Locale[] localeToLoad;
	private static final String FILE_PATH = "/data/";
	private static final String FILE_NAME = "profanity_list_";
	private static final String FILE_EXTENSION = ".txt";
	private final Map<Locale, HashSet<String>> map = new HashMap<Locale, HashSet<String>>();
	private static final String supported = "tr,en";
	private static boolean staticLoad = false;
	private BufferedReader br;
	private static final ProfanityRegister instance = new ProfanityRegister();

	
	public static ProfanityRegister getInstance() {
		return instance;
	}
	
	static {
		URL rs = ProfanityRegister.class.getClassLoader().getResource("./profanity.properties");
		if (rs != null) {
			System.out.println("Configurating ProfanityRegister");
			File file = new File(rs.getFile());
			Properties prop = new Properties();
			try {
				prop.load(new FileReader(file));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			staticLoad = Boolean.parseBoolean((prop.getProperty("profanity.static_load", "false").toString()));
			if (staticLoad) {
				String includes = prop.getProperty("profanity.static_include", "all");
				if ("all".equals(includes)) {
					includes = supported;
				}
				if (includes.contains(",")) {
					String[] listOfIncludes = includes.split(",");
					for (int i = 0; i < listOfIncludes.length; i++) {
						String string = listOfIncludes[i];
						instance.loadFile(new Locale(string));
					}
				}
//			Enumeration<Object> keys = prop.keys();
//			while(keys.hasMoreElements()) {
//				String key = (String) keys.nextElement();
//				if(!"profanity.static_load".equals(key) && key.contains("static_inlude")) {
//				
//					
//				}
//			}

			}
//        String[] files = file.list();
//        for (int i = 0; i < files.length; i++) {
//			String string = files[i];
//			System.out.println(string);
//		}
		}
	}

	private void loadFile(Locale locale) {
		HashSet<String> set = new HashSet<String>();
		map.put(locale, set);
		InputStream file = ProfanityRegister.class
				.getResourceAsStream(FILE_PATH + FILE_NAME + locale.toString() + FILE_EXTENSION);
		br = new BufferedReader(new InputStreamReader(file));
		String st;
		try {
			while ((st = br.readLine()) != null) {
				set.add(st);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void loadLocales() {
		if (localeToLoad != null) {
			for (int i = 0; i < localeToLoad.length; i++) {
				loadFile(localeToLoad[i]);
			}
		} else
			throw new NullPointerException("Please set which locale files will be loaded");
	}

	public boolean isProfanity(Locale locale, String word) {
		HashSet<String> set = map.get(locale);
		if (set == null)
			throw new IllegalArgumentException("Profanity " + locale + " havent loaded");
		return set.contains(word);
	}

	public boolean isProfanityFromFile(Locale locale, String word) {
		try {
			InputStream file = ProfanityRegister.class
					.getResourceAsStream(FILE_PATH + FILE_NAME + locale.toString() + FILE_EXTENSION);
			br = new BufferedReader(new InputStreamReader(file));
			String st;
			while ((st = br.readLine()) != null) {
				if (word.equals(st))
					return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public ProfanityRegister setLocales(Locale... locales) {
		this.localeToLoad = locales;
		return this;
	}

}
