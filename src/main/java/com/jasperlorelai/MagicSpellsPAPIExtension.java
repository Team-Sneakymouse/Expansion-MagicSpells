package com.jasperlorelai;

import org.jetbrains.annotations.NotNull;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.Spell;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.mana.ManaHandler;
import com.nisovin.magicspells.spells.BuffSpell;
import com.nisovin.magicspells.variables.Variable;
import com.nisovin.magicspells.util.managers.VariableManager;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class MagicSpellsPAPIExtension extends PlaceholderExpansion {

	private static final String AUTHOR = "JasperLorelai";
	private static final String IDENTIFIER = "ms";
	private static final String PLUGIN = "MagicSpells";
	private static final String NAME = "magicspells";
	private static final String VERSION = "7.0";
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

	@NotNull
	@Override
	public String getAuthor() {
		return AUTHOR;
	}

	@NotNull
	@Override
	public String getName() {
		return NAME;
	}

	@NotNull
	@Override
	public String getIdentifier() {
		return IDENTIFIER;
	}

	@NotNull
	@Override
	public String getRequiredPlugin() {
		return PLUGIN;
	}

	@NotNull
	@Override
	public String getVersion() {
		return VERSION;
	}

	public String onRequest(OfflinePlayer offlinePlayer, @NotNull String identifier) {
		if (offlinePlayer == null) return null;
		Player player = offlinePlayer.getPlayer();
		String[] args = identifier.split("_");

		Spell spell;
		String value;
		String[] splits;
		String precision = null;

		switch (args[0]) {
			case "variable" -> {
				if (args.length < 2) return null;
				Variable variable;
				VariableManager manager = MagicSpells.getVariableManager();
				if (manager == null) return null;
				switch (args[1]) {
					// %magicspells_variable_max_[varname],(precision)%
					case "max" -> {
						if (args.length < 3) return null;
						identifier = Util.joinArgs(args, 2);
						if (identifier.contains(",")) {
							splits = identifier.split(",");
							identifier = splits[0];
							precision = splits[1];
						}
						identifier = PlaceholderAPI.setBracketPlaceholders(offlinePlayer, identifier);
						variable = manager.getVariable(identifier);
						if (variable == null) return plugin.getName() + ": Player/Global variable '" + identifier + "' wasn't found.";
						value = variable.getMaxValue() + "";
					}

					// %magicspells_variable_min_[varname],(precision)%
					case "min" -> {
						if (args.length < 3) return null;
						identifier = Util.joinArgs(args, 2);
						if (identifier.contains(",")) {
							splits = identifier.split(",");
							identifier = splits[0];
							precision = splits[1];
						}
						identifier = PlaceholderAPI.setBracketPlaceholders(offlinePlayer, identifier);
						variable = manager.getVariable(identifier);
						if (variable == null) return plugin.getName() + ": Player/Global variable '" + identifier + "' wasn't found.";
						value = variable.getMinValue() + "";
					}

					// %magicspells_variable_[varname],(precision)%
					default -> {
						identifier = Util.joinArgs(args, 1);
						if (identifier.contains(",")) {
							splits = identifier.split(",");
							identifier = splits[0];
							precision = splits[1];
						}
						identifier = PlaceholderAPI.setBracketPlaceholders(offlinePlayer, identifier);
						variable = manager.getVariable(identifier);
						if (variable == null) return plugin.getName() + ": Variable '" + identifier + "' wasn't found.";
						value = variable.getStringValue(offlinePlayer.getName());
					}
				}
				return Util.setPrecision(value, precision);
			}

			case "cooldown" -> {
				if (args.length < 2) return null;
				boolean isNow = args[1].equals("now");
				identifier = Util.joinArgs(args, isNow ? 2 : 1);
				if (identifier.contains(",")) {
					splits = identifier.split(",");
					identifier = splits[0];
					precision = splits[1];
				}
				identifier = PlaceholderAPI.setBracketPlaceholders(offlinePlayer, identifier);
				spell = MagicSpells.getSpellByInternalName(identifier);
				if (spell == null) return plugin.getName() + ": Spell '" + identifier + "' wasn't found.";
				return Util.setPrecision("" + (isNow ? spell.getCooldown((LivingEntity) offlinePlayer) : spell.getCooldown()), precision);
			}

			case "charges" -> {
				if (args.length < 2) return null;
				boolean isConsumed = args[1].equals("consumed");
				identifier = Util.joinArgs(args, isConsumed ? 2 : 1);
				identifier = PlaceholderAPI.setBracketPlaceholders(offlinePlayer, identifier);
				spell = MagicSpells.getSpellByInternalName(identifier);
				if (spell == null) return plugin.getName() + ": Spell '" + identifier + "' wasn't found.";
				return (isConsumed ? spell.getCharges((LivingEntity) offlinePlayer) : spell.getCharges()) + "";
			}

			case "mana" -> {
				if (player == null) return null;
				ManaHandler handler = MagicSpells.getManaHandler();
				return "" + (args.length > 1 && args[1].equals("max") ? handler.getMaxMana(player) : handler.getMana(player));
			}

			case "buff" -> {
				if (args.length < 2) return null;
				if (player == null) return null;
				boolean isNow = args[1].equals("now");
				identifier = Util.joinArgs(args, isNow ? 2 : 1);
				if (identifier.contains(",")) {
					splits = identifier.split(",");
					identifier = splits[0];
					precision = splits[1];
				}
				identifier = PlaceholderAPI.setBracketPlaceholders(offlinePlayer, identifier);
				BuffSpell buffSpell = (BuffSpell) MagicSpells.getSpellByInternalName(identifier);
				if (buffSpell == null) return plugin.getName() + ": Buff spell '" + args[0] + "' wasn't found.";
				return Util.setPrecision("" + (isNow ? buffSpell.getDuration(player) : buffSpell.getDuration()), precision);
			}

			case "selectedspell" -> {
				if (player == null) return null;
				spell = MagicSpells.getSpellbook(player).getActiveSpell(player.getInventory().getItemInMainHand());
				if (spell == null) return "";
				return args.length > 1 && args[1].equals("displayed") ? spell.getName() : spell.getInternalName();
			}

			case "int2hex" -> {
				try {
					String text = Util.joinArgs(args, 1);
					splits = text.split(",", 2);
					text = Integer.toHexString(Integer.parseInt(splits[0])) + "";
					int empty = splits.length > 1 ? Integer.parseInt(splits[1]) : 4;
					while (text.length() < empty) {
						//noinspection StringConcatenationInLoop
						text = "0" + text;
					}
					return text;
				} catch (NumberFormatException ignored) {
					return null;
				}
			}

			case "unicode" -> {
				return Util.unescapeUnicode(PlaceholderAPI.setBracketPlaceholders(offlinePlayer, Util.joinArgs(args, 1)));
			}
		}
		return null;
	}

}