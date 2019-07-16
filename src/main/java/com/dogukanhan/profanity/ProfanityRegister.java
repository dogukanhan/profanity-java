package com.dogukanhan.profanity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;

public class ProfanityRegister {

    private Locale[] localeToLoad;
    private static final String FILE_PATH = "/data/";
    private final Map<String, HashSet<String>> map;
    private final HashSet<String> globalBanList;
    private BufferedReader br;
    private static ProfanityRegister instance;

    private ProfanityRegister() {

        this.map = new HashMap<>();
        this.globalBanList = new HashSet<>();

    }

    private String key(Locale locale) {
        String localePath = locale.toString();
        if (localePath.contains("_")) {
            localePath = localePath.split("_")[0];
        }
        return localePath;
    }


    public static void main(String args[]) {
        ProfanityRegister.getRegister().setLocales(new Locale("en_US")).loadLocales();
        System.out.println(ProfanityRegister.getRegister().isProfanity(Locale.ENGLISH, "pussy", false));
    }

    public static synchronized ProfanityRegister getRegister() {

        if (instance == null) {
            instance = new ProfanityRegister();
        }

        return instance;
    }


    private void loadFile(Locale locale) {
        String localePath = key(locale);
        HashSet<String> set = new HashSet<>(globalBanList);
        map.put(localePath, set);
        InputStream file = ProfanityRegister.class
                .getResourceAsStream(FILE_PATH + localePath);
        br = new BufferedReader(new InputStreamReader(file));
        String st;
        try {
            while ((st = br.readLine()) != null) {
                set.add(st);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadLocales() {
        if (localeToLoad != null) {
            for (Locale locale : localeToLoad) {
                loadFile(locale);
            }
        } else
            throw new NullPointerException("Please set which locale files will be loaded");
    }

    public boolean isProfanity(Locale locale, String word, boolean searchFile) {
        HashSet<String> set = map.get(key(locale));
        if (set == null)
            if (searchFile)
                return isProfanityFromFile(locale, word);
            else
                return false;
        if (set.contains(word))
            return true;

        return false;
    }

    public boolean isProfanityFromFile(Locale locale, String word) {
        String localePath = key(locale);

        if (globalBanList.contains(word))
            return true;

        try {
            InputStream file = ProfanityRegister.class
                    .getResourceAsStream(FILE_PATH + localePath);
            if (file != null) {
                br = new BufferedReader(new InputStreamReader(file));
                String st;
                while ((st = br.readLine()) != null) {
                    if (word.equals(st))
                        return true;
                }
                br.close();
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

    public ProfanityRegister setGlobalBanListStream(InputStream globalBanListStream) {
        br = new BufferedReader(new InputStreamReader(globalBanListStream));
        String st;
        try {
            while ((st = br.readLine()) != null) {
                globalBanList.add(st);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }
}
