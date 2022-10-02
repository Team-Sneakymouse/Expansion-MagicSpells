package com.jasperlorelai;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.OfflinePlayer;

import org.jetbrains.annotations.NotNull;

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
	private static final String VERSION = "1.0.0";

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
		if (offlinePlayer == null || !offlinePlayer.isOnline()) return null;
		Player player = (Player) offlinePlayer;

        String[] splits = identifier.split("_", 2);
		String name = splits[0];
		String args = splits.length > 1 ? splits[1] : null;

		return switch (name) {
			case "variable" -> processVariable(player, args);
			case "cooldown" -> processCooldown(player, args);
			case "charges" -> processCharges(player, args);
			case "mana" -> processMana(player, args);
			case "buff" -> processBuff(player, args);
			case "selectedspell" -> processSelectedSpell(player, args);
			default -> null;
		};
	}

	private String error(String string) {
		return plugin.getName() + ": " + string;
	}

	/*
	 * variable     [varname],(precision)
	 * variable max [varname],(precision)
	 * variable min [varname],(precision)
	 */
	private String processVariable(Player player, String args) {
		if (args == null) return null;
		String[] splits = args.split("_", 2);
		String type = splits[0];
		boolean isMax = type.equals("max");
		boolean isMin = type.equals("min");
		boolean isCur = !isMax && !isMin;

		String varName = isCur ? args : splits[1];
		String precision = null;
		if (varName.contains(",")) {
			splits = varName.split(",", 2);
			varName = splits[0];
			precision = splits[1];
		}

		VariableManager manager = MagicSpells.getVariableManager();
		if (manager == null) return null;
		varName = PlaceholderAPI.setBracketPlaceholders(player, varName);
		Variable variable = manager.getVariable(varName);
		if (variable == null) return error("Variable '" + varName + "' wasn't found.");

		String value;
		if (isCur) value = Util.colorise(variable.getStringValue(player));
		else if (isMax) value = variable.getMaxValue() + "";
		else value = variable.getMinValue() + "";

		return Util.setPrecision(value, precision);
	}

	/*
	 * cooldown     [spellname],(precision)
	 * cooldown now [spellname],(precision)
	 */
	private String processCooldown(Player player, String args) {
		if (args == null) return null;
		String[] splits = args.split("_", 2);
		boolean isNow = splits[0].equals("now");
		String spellName = isNow ? splits[1] : args;
		String precision = null;
		if (spellName.contains(",")) {
			splits = spellName.split(",", 2);
			spellName = splits[0];
			precision = splits[1];
		}

		spellName = PlaceholderAPI.setBracketPlaceholders(player, spellName);
		Spell spell = MagicSpells.getSpellByInternalName(spellName);
		if (spell == null) return error("Spell '" + spellName + "' wasn't found.");

		float cooldown = isNow ? spell.getCooldown(player) : spell.getCooldown();
		return Util.setPrecision(cooldown + "", precision);
	}

	/*
	 * charges          [spellname]
	 * charges consumed [spellname]
	 */
	private String processCharges(Player player, String args) {
		if (args == null) return null;
		String[] splits = args.split("_", 2);
		boolean isConsume = splits[0].equals("consumed");
		String spellName = PlaceholderAPI.setBracketPlaceholders(player, isConsume ? splits[1] : args);

		Spell spell = MagicSpells.getSpellByInternalName(spellName);
		if (spell == null) return error("Spell '" + spellName + "' wasn't found.");
		return (isConsume ? spell.getCharges(player) : spell.getCharges()) + "";
	}

	/*
	 * mana
	 * mana max
	 */
	private String processMana(Player player, String args) {
		boolean isMax = args != null && args.equals("max");
		ManaHandler handler = MagicSpells.getManaHandler();
		return (isMax ? handler.getMaxMana(player) : handler.getMana(player)) + "";
	}

	/*
	 * buff     [spellname],(precision)
	 * buff now [spellname],(precision)
	 */
	private String processBuff(Player player, String args) {
		if (args == null) return null;
		String[] splits = args.split("_", 2);
		boolean isNow = splits[0].equals("now");
		String spellName = isNow ? splits[1] : args;
		String precision = null;
		if (spellName.contains(",")) {
			splits = spellName.split(",", 2);
			spellName = splits[0];
			precision = splits[1];
		}
		spellName = PlaceholderAPI.setBracketPlaceholders(player, spellName);
		Spell spell = MagicSpells.getSpellByInternalName(spellName);
		if (!(spell instanceof BuffSpell buff)) return error("Buff spell '" + spellName + "' not found.");
		float duration = isNow ? buff.getDuration(player) : buff.getDuration();
		return Util.setPrecision(duration + "", precision);
	}

	/*
	 * selectedspell
	 * selectedspell displayed
	 */
	private String processSelectedSpell(Player player, String args) {
		boolean isDis = args != null && args.equals("displayed");
		Spell spell = MagicSpells.getSpellbook(player).getActiveSpell(player.getInventory().getItemInMainHand());

		return spell == null ? "" : (isDis ? Util.colorise(spell.getName()) : spell.getInternalName());
	}

}
