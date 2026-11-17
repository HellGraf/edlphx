package l2p.gameserver.network.clientpackets;

import l2p.gameserver.model.L2Effect;
import l2p.gameserver.model.L2Player;
import l2p.gameserver.model.L2Skill.SkillType;

public class RequestDispel extends L2GameClientPacket
{
	private int _objectId, _id, _level;

	@Override
	protected void readImpl() throws Exception
	{
		_objectId = readD();
		_id = readD();
		_level = readD();
	}

	@Override
	protected void runImpl() throws Exception
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null || activeChar.getObjectId() != _objectId /* && activeChar.getPet() == null */)
			return;

//		Creature target = activeChar;
//		if(activeChar.getObjectId() != _objectId)
//			target = activeChar.getPet();
		
		for(L2Effect e : activeChar.getEffectList().getAllEffects())
			if(e.getDisplayId() == _id && e.getDisplayLevel() == _level)
				if(activeChar.isGM() || !e.isOffensive() && !e.getSkill().isMusic() && e.getSkill().getSkillType() != SkillType.TRANSFORMATION)
					e.exit();
				else
					return;
	}
}