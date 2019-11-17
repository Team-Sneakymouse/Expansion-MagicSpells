package com.jasperlorelai;

import java.util.Arrays;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import com.nisovin.magicspells.Spell;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.spells.BuffSpell;

import org.bukkit.inventory.PlayerInventory;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class MagicSpellsPAPIExtension extends PlaceholderExpansion {
    private static final String AUTHOR = "JasperLorelai";
    private static final String IDENTIFIER = "magicspells";
    private static final String PLUGIN = "MagicSpells";
    private static final String VERSION = "2.0";
    private MagicSpells plugin;

    @Override
    public boolean canRegister() {
        return Bukkit.getPluginManager().getPlugin(getRequiredPlugin()) != null;
    }

    @Override
    public boolean register() {
        if(!canRegister()) return false;
        plugin = (MagicSpells) Bukkit.getPluginManager().getPlugin(getRequiredPlugin());
        if(plugin == null) return false;
        return super.register();
    }

    @Override
    public String getAuthor() {
        return AUTHOR;
    }

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public String getRequiredPlugin() {
        return PLUGIN;
    }

    @Override
    public String getVersion() {
        return VERSION;
    }

    public String onRequest(OfflinePlayer player, String identifier) {
        if(player == null) return "";
        String[] args = identifier.split("_");
        identifier = args[0];
        args = Arrays.copyOfRange(args, 1, args.length);
        String value;

        switch(identifier) {
            // %magicspells_variable_[varname]_(precision)%
            case "variable":
                if(args.length < 1) return plugin.getName() + ": Value must be %" + getIdentifier() + "_variable_[varname]_(precision)%";
                if(args[0].isEmpty() || MagicSpells.getVariableManager().getVariable(args[0]) == null) {
                    return plugin.getName() + ": Variable '" + args[0] + "' wasn't found.";
                }
                value = MagicSpells.getVariableManager().getStringValue(args[0], player.getName());
                if(args.length < 2) return value;
                return setPrecision(value, args[1]);

            // %magicspells_cooldown_[spellname]_(precision)%
            case "cooldown":
                if(args.length < 1) return plugin.getName() + ": Value must be %" + getIdentifier() + "_cooldown_[spellname]_(precision)%";
                Spell spell = MagicSpells.getSpellByInternalName(args[0]);
                if(spell == null) return plugin.getName() + ": Spell '" + args[0] + "' wasn't found.";
                value = spell.getCooldown(player.getPlayer()) + "";
                if(args.length < 2) return value;
                return setPrecision(value, args[1]);

            // %magicspells_mana%
            case "mana":
                return MagicSpells.getManaHandler().getMana(player.getPlayer()) + "";

            // %magicspells_maxmana%
            case "maxmana":
                return MagicSpells.getManaHandler().getMaxMana(player.getPlayer()) + "";

            // %magicspells_buff_[spellname]_(precision)%
            case "buff":
                if(args.length < 1) return plugin.getName() + ": Value must be %" + getIdentifier() + "_buff_[spellname]_(precision)%";
                BuffSpell buffSpell = (BuffSpell) MagicSpells.getSpellByInternalName(args[0]);
                if(buffSpell == null) return plugin.getName() + ": Buff spell '" + args[0] + "' wasn't found.";
                value = buffSpell.getDuration(player.getPlayer()) + "";
                if(args.length < 2) return value;
                return setPrecision(value, args[1]);

            // %magicspells_selectedspell%
            case "selectedspell":
                PlayerInventory inv = player.getPlayer().getInventory();
                if(inv == null) return null;
                spell = MagicSpells.getSpellbook(player.getPlayer()).getActiveSpell(inv.getItemInMainHand());
                return spell == null ? "" : spell.getInternalName();
        }
        return null;
    }

    public String setPrecision(String str, String precision) {
        // Return value if value isn't a floating point - can't be scaled.
        float floatValue;
        try {
            floatValue = Float.parseFloat(str);
        }
        catch (NumberFormatException | NullPointerException nfe) {
            return str;
        }

        // Return value if precision isn't a floating point.
        int toScale;
        try {
            toScale = Integer.parseInt(precision);
        }
        catch (NumberFormatException | NullPointerException nfe) {
            return str;
        }

        // Return the scaled value.
        return BigDecimal.valueOf(floatValue).setScale(toScale, RoundingMode.HALF_UP).toString();
    }
}