package me.lorenzo0111.multilang.realtime;

import com.gtranslate.Translator;
import me.lorenzo0111.multilang.api.objects.ITranslator;
import me.lorenzo0111.multilang.utils.RegexChecker;
import org.bukkit.ChatColor;

public class GoogleTranslator implements ITranslator {

    @Override
    public String translate(String text, String language) {
        if (RegexChecker.isUrl(text)) {
            return text;
        }

        try {
            Translator translator = Translator.getInstance();
            return translator.translate(text, translator.detect(ChatColor.stripColor(text)), language);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
