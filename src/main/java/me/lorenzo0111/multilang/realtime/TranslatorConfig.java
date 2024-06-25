package me.lorenzo0111.multilang.realtime;

import me.lorenzo0111.multilang.MultiLangPlugin;
import me.lorenzo0111.multilang.api.objects.ITranslator;
import me.lorenzo0111.multilang.api.objects.Locale;
import me.lorenzo0111.multilang.api.objects.LocalizedString;
import me.lorenzo0111.multilang.database.DatabaseManager;
import me.lorenzo0111.multilang.exceptions.ApiException;
import me.lorenzo0111.multilang.utils.RegexChecker;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Pattern;

public class TranslatorConfig {
    private final Map<String, ITranslator> translators = new HashMap<>();
    private final List<Pattern> patterns = new ArrayList<>();
    private Map<String, LocalizedString> cache = new HashMap<>();
    private boolean isEnabled = false;
    private boolean canRegister = false;
    private boolean useCache = false;
    private String inUse = null;
    private boolean translateServer = false;

    public void registerTranslator(String id, ITranslator translator) {
        if (!this.canRegister) {
            throw new ApiException("Cannot register translator after config has been loaded");
        }

        this.translators.put(id.toLowerCase(), translator);
    }


    public void canRegister(boolean canRegister) {
        this.canRegister = canRegister;
    }

    public void setInUse(@NotNull String inUse) {
        if (!translators.containsKey(inUse.toLowerCase())) {
            throw new ApiException("Translator with id " + inUse + " does not exist: " + translators.keySet());
        }

        this.inUse = inUse;
    }

    public boolean isUseCache() {
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

    public void setPatterns(@NotNull List<String> patterns) {
        for (String pattern : patterns) {
            this.patterns.add(Pattern.compile(pattern));
        }
    }

    public void setTranslateServer(boolean translateServer) {
        this.translateServer = translateServer;
    }

    public boolean isTranslateServer() {
        return translateServer;
    }

    public @Nullable String translate(Locale locale, String text) {
        if (inUse == null) {
            return null;
        }

        if (RegexChecker.contains(text, patterns)) {
            return null;
        }

        if (useCache) {
            String cache = tryFromCache(locale, text);
            if (cache != null) {
                return cache;
            }
        }

        String string = this.translators.get(inUse).translate(text, locale.getLocaleID());
        if (useCache && string != null) {
            String newText = ChatColor.stripColor(text);

            if (cache.containsKey(newText)) {
                LocalizedString str = cache.get(newText);
                str.getLocales().put(locale, string);
                cache.remove(newText);
                cache.put(newText, str);
            }

            Map<Locale, String> map = new HashMap<>();
            map.put(locale, string);
            cache.put(newText, new LocalizedString("temp", newText, map));
        }

        return string;
    }

    public @Nullable String tryFromCache(Locale locale, String text) {
        if (!useCache) return null;

        LocalizedString localizedString = cache.get(ChatColor.stripColor(text));
        MultiLangPlugin.getInstance().debug(localizedString);
        if (localizedString != null) {
            String translation = localizedString.getLocales().get(locale);
            if (translation != null) {
                MultiLangPlugin.getInstance().debug("Using cached translation for " + text + " in " + locale.getLocaleID() + ": " + translation);
                return translation;
            }
        }

        return null;
    }

    public boolean testConnection() {
        if (!isEnabled || inUse == null) return true;

        MultiLangPlugin.getInstance().debug("Testing connection with " + inUse + " translator");
        return translators.get(inUse).testConnection();
    }

    public void loadCache(@NotNull DatabaseManager database) {
        database.getCache().whenComplete((data, throwable) -> {
            if (throwable != null || data == null) {
                MultiLangPlugin.getInstance().getLogger().log(Level.WARNING, "Failed to load cache", throwable);
                return;
            }

            this.cache = data;
        });
    }

    public void saveCache(@NotNull DatabaseManager database) {
        database.updateCacheTable(cache);
    }

    public void saveCacheSync(@NotNull DatabaseManager database) {
        database.updateCacheTableSync(cache);
    }

    public void clearCache(@NotNull DatabaseManager database) {
        database.getCacheTable().clear();
    }
}
