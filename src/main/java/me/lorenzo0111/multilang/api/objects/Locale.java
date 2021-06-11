package me.lorenzo0111.multilang.api.objects;

import java.util.Objects;

public class Locale {
    private final String name;

    public Locale(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Locale locale = (Locale) o;
        return name.equals(locale.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
