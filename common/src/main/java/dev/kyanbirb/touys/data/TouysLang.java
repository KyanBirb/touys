package dev.kyanbirb.touys.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TouysLang {
    private static final Map<String, String> LANG_MAP = new HashMap<>();

    public static void provideLang(BiConsumer<String, String> consumer) {
        LANG_MAP.forEach(consumer);
        Map<String, String> lang = getLangMap("en_us");
        lang.forEach(consumer);
    }

    public static void add(String key, String value) {
        LANG_MAP.put(key, value);
    }

    public static void addBlock(String id) {
        String key = "block.touys." + id;
        String name = unSnakeCase(id);
        LANG_MAP.put(key, name);
    }

    public static void addItem(String id) {
        String key = "item.touys." + id;
        String name = unSnakeCase(id);
        LANG_MAP.put(key, name);
    }

    public static String unSnakeCase(String string) {
        Stream<String> stream = Arrays.stream(string.split("_"));
        return stream.map(str -> str.substring(0, 1).toUpperCase(Locale.ROOT) + str.substring(1).toLowerCase(Locale.ROOT))
                .collect(Collectors.joining(" "));
    }

    private static Map<String, String> getLangMap(final String lang) {
        final String filepath = "datagen/lang/%s.json".formatted(lang);
        final JsonObject langObject = loadJsonResource(filepath).getAsJsonObject();

        final Map<String, String> langMap = new HashMap<>();
        flattenJson(langMap, langObject, null);
        return langMap;
    }

    private static JsonElement loadJson(InputStream inputStream) {
        try {
            JsonReader reader = new JsonReader(new BufferedReader(new InputStreamReader(inputStream)));
            reader.setLenient(true);
            JsonElement element = Streams.parse(reader);
            reader.close();
            inputStream.close();
            return element;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static JsonElement loadJsonResource(String filepath) {
        return loadJson(ClassLoader.getSystemResourceAsStream(filepath));
    }

    private static void flattenJson(Map<String, String> outputMap, JsonElement element, String currentPath) {
        if(element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
            String string = element.getAsJsonPrimitive().getAsString();
            outputMap.put(currentPath, string);
            return;
        }

        if(element.isJsonObject()) {
            JsonObject object = element.getAsJsonObject();
            for (String key : object.keySet()) {
                JsonElement value = object.get(key);
                String path = currentPath != null ? currentPath + "." + key : key;
                flattenJson(outputMap, value, path);
            }
        } else if(element.isJsonArray() && currentPath != null) {
            JsonArray array = element.getAsJsonArray();
            for (int i = 0; i < array.size(); i++) {
                String path = currentPath + "_" + i;
                flattenJson(outputMap, array.get(i), path);
            }
        }
    }

}
