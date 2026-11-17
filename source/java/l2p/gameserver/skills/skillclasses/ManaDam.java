package l2p.gameserver.skills.skillclasses;

import l2p.commons.templates.StatsSet;
import l2p.commons.util.GArray;
import l2p.gameserver.model.L2Character;
import l2p.gameserver.model.L2Skill;
import l2p.gameserver.skills.Formulas;

public class ManaDam extends L2Skill
{
	public ManaDam(StatsSet set)
	{
		super(set);
	}

	@Override
	public void useSkill(L2Character activeChar, GArray<L2Character> targets)
	{
		int sps = isSSPossible() ? activeChar.getChargedSpiritShot() : 0;

		for(L2Character target : targets)
			if(target != null)
			{
				if(target.isDead())
					continue;

				if(getPower() > 0) // Если == 0 значит скилл "отключен"
				{
					if(target.checkReflectSkill(activeChar, this))
						target = activeChar;

					double damage = Formulas.calcMagicDam(activeChar, target, this, sps);

					target.reduceCurrentMp(damage, activeChar);
				}

				getEffects(activeChar, target, getActivateRate() > 0, false);
			}

		if(isSSPossible())
			activeChar.unChargeShots(isMagic());
	}
}