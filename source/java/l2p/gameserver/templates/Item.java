package l2p.gameserver.templates;

import l2p.commons.templates.StatsSet;

/**
 * Абстрактный контейнер
 */
public class Item
{
	public int id;
	@SuppressWarnings("rawtypes")
	public Enum type;
	public String name;
	public StatsSet set;
	public int currentLevel;
	public L2Item item;
}