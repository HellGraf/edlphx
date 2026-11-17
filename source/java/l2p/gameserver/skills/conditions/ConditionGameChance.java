package l2p.gameserver.skills.conditions;

import l2p.commons.util.Rnd;
import l2p.gameserver.skills.Env;

public class ConditionGameChance extends Condition
{
	private final int _chance;

	ConditionGameChance(int chance)
	{
		_chance = chance;
	}

	@Override
	protected boolean testImpl(Env env)
	{
		return Rnd.chance(_chance);
	}
}
