package me.lorenzo0111.multilang.api.objects;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LocalizedString {
    private final String key;
    private final String defaultString;
    private final Map<Locale,String> locales;

    public LocalizedString(String key, String defaultString, Map<Locale, String> locales) {
        this.key = key;
        this.defaultString = defaultString;
        this.locales = locales;
    }

    public LocalizedString(String key, String defaultString) {
        this(key,defaultString,new HashMap<>());
    }

    public String getDefaultString() {
        return defaultString;
    }

    public Map<Locale, String> getLocales() {
        return locales;
    }

    public String getKey() {
        return key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LocalizedString that = (LocalizedString) o;
        return key.equals(that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }
}
