package me.lorenzo0111.multilang.realtime;

import me.lorenzo0111.multilang.api.objects.ITranslator;
import me.lorenzo0111.multilang.api.objects.Locale;
import me.lorenzo0111.multilang.database.DatabaseManager;
import me.lorenzo0111.multilang.exceptions.ApiException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class TranslatorConfig {
    private final Map<String, ITranslator> translators = new HashMap<>();
    private final DatabaseManager database;
    private boolean isEnabled = false;
    private boolean canRegister = false;
    private boolean useCache = false;
    private String inUse = null;

    public TranslatorConfig(DatabaseManager database) {
        this.database = database;
    }

    public void registerTranslator(String id, ITranslator translator) {
        if (!this.canRegister) {
            throw new ApiException("Cannot register translator after config has been loaded");
        }

        this.translators.put(id.toLowerCase(), translator);
    }

    public Map<String, ITranslator> getTranslators() {
        return this.translators;
    }

    public @Nullable ITranslator getTranslator(String id) {
        return this.translators.get(id);
    }

    public boolean canRegister() {
        return canRegister;
    }

    public void canRegister(boolean canRegister) {
        this.canRegister = canRegister;
    }

    public String getInUse() {
        return inUse;
    }

    public void setInUse(@NotNull String inUse) {
        if (!translators.containsKey(inUse.toLowerCase())) {
            throw new ApiException("Translator with id " + inUse + " does not exist: " + translators.keySet());
        }

        this.inUse = inUse;
    }

    public boolean useCache() {
        return useCache;
    }

    public void setUseCache(boolean useCache) {
        this.useCache = useCache;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public @Nullable String translate(Locale locale, String text) {
        if (inUse == null) {
            return null;
        }

        return this.translators.get(inUse).translate(text, locale.getLocaleID());
    }

    public boolean testConnection() {
        if (!isEnabled || inUse == null) return true;

        return translators.get(inUse).testConnection();
    }
}
