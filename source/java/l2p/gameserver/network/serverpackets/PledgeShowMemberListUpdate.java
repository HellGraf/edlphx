package l2p.gameserver.network.serverpackets;

import l2p.gameserver.model.L2ClanMember;
import l2p.gameserver.model.L2Player;

public class PledgeShowMemberListUpdate extends L2GameServerPacket
{
	private String _name;
	private int _lvl;
	private int _classId;
	private int _sex;
	private int _race;
	private int _objectId;
	private int _pledgeType;
	private int _isApprentice = 0;
	private boolean _isOnline;

	public PledgeShowMemberListUpdate(final L2Player player)
	{
		_isOnline = player.isOnline();
		_name = player.getName();
		_lvl = player.getLevel();
		_classId = player.getClassId().getId();
		_sex = player.getSex();
		_objectId = player.getObjectId();
		_race = player.getRace().ordinal();
		_pledgeType = player.getPledgeType();
		if(player.getClan() != null && player.getClan().getClanMember(_objectId) != null)
			_isApprentice = player.getClan().getClanMember(_objectId).hasSponsor() ? 1 : 0;
	}

	public PledgeShowMemberListUpdate(final L2ClanMember cm)
	{
		_isOnline = cm.isOnline();
		_name = cm.getName();
		_lvl = cm.getLevel();
		_classId = cm.getClassId();
		_sex = cm.getSex();
		_objectId = cm.getObjectId();
		_race = cm.getRace();
		_pledgeType = cm.getPledgeType();
		_isApprentice = cm.hasSponsor() ? 1 : 0;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x5b);
		writeS(_name);
		writeD(_lvl);
		writeD(_classId);
		writeD(_sex);
		writeD(_race); // 1=online 0=offline
		if(_isOnline)
		{
			writeD(_objectId);
			writeD(_pledgeType);
		}
		else
		{
			writeD(0);
			writeD(0);
		}
		writeD(_isApprentice); // does a clan member have a sponsor
	}
}