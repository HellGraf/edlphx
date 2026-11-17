package l2p.gameserver.skills.skillclasses;

import l2p.commons.templates.StatsSet;
import l2p.commons.util.GArray;
import l2p.gameserver.extensions.multilang.CustomMessage;
import l2p.gameserver.model.L2Character;
import l2p.gameserver.model.L2Skill;

public class Default extends L2Skill
{
	public Default(StatsSet set)
	{
		super(set);
	}

	@Override
	public void useSkill(L2Character activeChar, GArray<L2Character> targets)
	{
		activeChar.sendMessage(new CustomMessage("l2p.gameserver.skills.skillclasses.Default.NotImplemented", activeChar).addNumber(getId()).addString("" + getSkillType()));
		activeChar.sendActionFailed();
	}
}
