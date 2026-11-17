package l2p.gameserver.tables;

import javolution.util.FastMap;
import l2p.commons.util.GArray;
import l2p.gameserver.model.base.L2EnchantSkillLearn;

public abstract class EnchantTable
{
	public static FastMap<Integer, GArray<L2EnchantSkillLearn>> _enchant = new FastMap<Integer, GArray<L2EnchantSkillLearn>>().setShared(true);
}