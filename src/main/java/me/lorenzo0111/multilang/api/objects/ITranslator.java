package me.lorenzo0111.multilang.api.objects;

public interface ITranslator {
    default boolean testConnection() {
        return translate("Hello World", "en") != null;
    }
    String translate(String text, String language);
}
