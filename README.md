# MagicSpellsPlaceholderAPIExpansion
[**Downloads**](https://github.com/JasperLorelai/MagicSpellsPlaceholderAPIExpansion/releases): ![Releases](https://img.shields.io/github/downloads/JasperLorelai/MagicSpellsPlaceholderAPIExpansion/total.svg)

[PlaceholderAPI](https://www.spigotmc.org/resources/6245/) expansion for the [MagicSpells](https://github.com/TheComputerGeek2/MagicSpells/) plugin.

Drop the downloaded jar file in `./plugins/PlaceholderAPI/expansions`.

## Placeholders:
Arguments in `[]` brackets are required, while arguments in `()` brackets are optional - the placeholders work without them.

### Initial List:
- `%magicspells_variable_[varname]_(precision)%` - Returns MS variable value, string or floating point - the latter can have decimal precision.
- `%magicspells_cooldown_[spellname]_(precision)%` - Returns MS spell cooldown, which can have decimal precision.
- `%magicspells_mana%`
- `%magicspells_maxmana%`

### Added in version `2.0`:
- `%magicspells_buff_[spellname]_(precision)%` - Returns buff spell duration, which can have decimal precision.
- `%magicspells_selectedspell%` - Returns currently selected spell on the held item (cast item).
