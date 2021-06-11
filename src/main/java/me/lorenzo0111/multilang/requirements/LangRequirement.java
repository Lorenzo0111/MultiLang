package me.lorenzo0111.multilang.requirements;

import me.lorenzo0111.multilang.MultiLangPlugin;
import me.lorenzo0111.multilang.api.objects.Locale;
import me.lorenzo0111.multilang.api.objects.LocalizedPlayer;
import me.lorenzo0111.rocketplaceholders.creator.conditions.Requirement;
import me.lorenzo0111.rocketplaceholders.creator.conditions.RequirementType;
import org.bukkit.entity.Player;

public class LangRequirement extends Requirement {
    private final Locale locale;
    private final MultiLangPlugin plugin;

    public LangRequirement(MultiLangPlugin plugin, Locale locale) {
        super(null);
        this.locale = locale;
        this.plugin = plugin;
    }

    @Override
    public boolean apply(Player player) {
        return LocalizedPlayer.from(player).getLocale().equals(locale);
    }

    @Override
    public RequirementType getType() {
        return RequirementType.API;
    }

    public Locale getLocale() {
        return locale;
    }
}
