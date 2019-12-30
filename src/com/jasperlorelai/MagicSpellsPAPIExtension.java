package com.jasperlorelai;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.Spell;
import com.nisovin.magicspells.spells.BuffSpell;
import com.nisovin.magicspells.variables.GlobalVariable;
import com.nisovin.magicspells.variables.PlayerVariable;
import com.nisovin.magicspells.variables.Variable;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class MagicSpellsPAPIExtension extends PlaceholderExpansion {

    private static final String AUTHOR = "JasperLorelai";
    private static final String IDENTIFIER = "magicspells";
    private static final String PLUGIN = "MagicSpells";
    private static final String VERSION = "3.0";
    private MagicSpells plugin;

    @Override
    public boolean canRegister() {
        return Bukkit.getPluginManager().getPlugin(getRequiredPlugin()) != null;
    }

    @Override
    public boolean register() {
        if (!canRegister()) return false;
        plugin = (MagicSpells) Bukkit.getPluginManager().getPlugin(getRequiredPlugin());
        if (plugin == null) return false;
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

    public String onRequest(OfflinePlayer offlinePlayer, String identifier) {
        if (offlinePlayer == null) return null;
        String[] args = identifier.split("_");

        String value;
        String[] splits;
        String precision = null;

        switch (args[0]) {
            case "variable":
                if (args.length < 3) return null;
                Variable variable;
                switch (args[1]) {
                    // %magicspells_variable_max_[varname],(precision)%
                    case "max":
                        if (args.length < 4) return null;
                        identifier = StringUtils.join(Arrays.copyOfRange(args, 2, args.length), '_');
                        if (identifier.contains(",")) {
                            splits = identifier.split(",");
                            identifier = splits[0];
                            precision = splits[1];
                        }
                        variable = MagicSpells.getVariableManager().getVariable(identifier);
                        if (!(variable instanceof PlayerVariable || variable instanceof GlobalVariable))
                            return plugin.getName() + ": Player/Global variable '" + identifier + "' wasn't found.";
                        value = variable.getMaxValue();
                        break;

                    // %magicspells_variable_min_[varname],(precision)%
                    case "min":
                        if (args.length < 4) return null;
                        identifier = StringUtils.join(Arrays.copyOfRange(args, 2, args.length), '_');
                        if (identifier.contains(",")) {
                            splits = identifier.split(",");
                            identifier = splits[0];
                            precision = splits[1];
                        }
                        variable = MagicSpells.getVariableManager().getVariable(identifier);
                        if (!(variable instanceof PlayerVariable || variable instanceof GlobalVariable))
                            return plugin.getName() + ": Player/Global variable '" + identifier + "' wasn't found.";
                        value = variable.getMinValue();
                        break;

                    // %magicspells_variable_[varname],(precision)%
                    default:
                        identifier = StringUtils.join(Arrays.copyOfRange(args, 1, args.length), '_');
                        if (identifier.contains(",")) {
                            splits = identifier.split(",");
                            identifier = splits[0];
                            precision = splits[1];
                        }
                        variable = MagicSpells.getVariableManager().getVariable(identifier);
                        if (variable == null) return plugin.getName() + ": Variable '" + identifier + "' wasn't found.";
                        value = variable.getStringValue(offlinePlayer.getName());
                        break;
                }
                return precision == null ? value : Util.setPrecision(value, precision);

            case "cooldown":
                Spell spell;
                if (args.length < 2) return null;
                // %magicspells_cooldown_now_[spellname],(precision)%
                if (args[1].equals("now")) identifier = StringUtils.join(Arrays.copyOfRange(args, 2, args.length), '_');
                    // %magicspells_cooldown_[spellname],(precision)%
                else identifier = StringUtils.join(Arrays.copyOfRange(args, 1, args.length), '_');

                if (identifier.contains(",")) {
                    splits = identifier.split(",");
                    identifier = splits[0];
                    precision = splits[1];
                }
                spell = MagicSpells.getSpellByInternalName(identifier);
                if (spell == null) return plugin.getName() + ": Spell '" + identifier + "' wasn't found.";

                if (args[1].equals("now")) value = spell.getCooldown((LivingEntity) offlinePlayer) + "";
                else value = spell.getCooldown() + "";
                return precision == null ? value : Util.setPrecision(value, precision);

            case "mana":
                if (args.length > 1 && args[1].equals("max"))
                    return MagicSpells.getManaHandler().getMaxMana(offlinePlayer.getPlayer()) + "";
                return MagicSpells.getManaHandler().getMana(offlinePlayer.getPlayer()) + "";

            case "buff":
                if (args.length < 2) return null;
                // %magicspells_buff_now_[spellname],(precision)%
                if (args[1].equals("now")) identifier = StringUtils.join(Arrays.copyOfRange(args, 2, args.length), '_');
                    // %magicspells_buff_[spellname],(precision)%
                else identifier = StringUtils.join(Arrays.copyOfRange(args, 1, args.length), '_');

                if (identifier.contains(",")) {
                    splits = identifier.split(",");
                    identifier = splits[0];
                    precision = splits[1];
                }
                BuffSpell buffSpell = (BuffSpell) MagicSpells.getSpellByInternalName(identifier);
                if (buffSpell == null) return plugin.getName() + ": Buff spell '" + args[0] + "' wasn't found.";

                if (args[1].equals("now")) value = buffSpell.getDuration(offlinePlayer.getPlayer()) + "";
                else value = buffSpell.getDuration() + "";
                return precision == null ? value : Util.setPrecision(value, precision);

            // %magicspells_selectedspell%
            case "selectedspell":
                Player player = offlinePlayer.getPlayer();
                if (player == null) return null;
                spell = MagicSpells.getSpellbook(player).getActiveSpell(player.getInventory().getItemInMainHand());
                return spell == null ? "" : spell.getInternalName();
        }
        return null;
    }
}