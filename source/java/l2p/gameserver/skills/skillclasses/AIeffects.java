package l2p.gameserver.skills.skillclasses;

import l2p.commons.templates.StatsSet;
import l2p.commons.util.GArray;
import l2p.gameserver.model.L2Character;
import l2p.gameserver.model.L2Skill;

public class AIeffects extends L2Skill
{
	public AIeffects(StatsSet set)
	{
		super(set);
	}

	@Override
	public void useSkill(L2Character activeChar, GArray<L2Character> targets)
	{
		for(L2Character target : targets)
			if(target != null)
				getEffects(activeChar, target, getActivateRate() > 0, false);

		if(isSSPossible())
			activeChar.unChargeShots(isMagic());
	}
}
