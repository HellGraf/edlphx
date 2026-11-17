package l2p.gameserver.network.serverpackets;

import l2p.commons.util.GArray;
import l2p.gameserver.instancemanager.PartyRoomManager;
import l2p.gameserver.model.L2ObjectsStorage;
import l2p.gameserver.model.L2Player;
import l2p.gameserver.model.PartyRoom;

/**
 * Format:(ch) d d [dsdddd]
 */
public class ExPartyRoomMember extends L2GameServerPacket
{
	private int _mode;
	private L2Player _leader;
	private GArray<L2Player> _members = null;

	public ExPartyRoomMember(PartyRoom room, L2Player activeChar, int mode)
	{
		_leader = room.getLeader();
		if(_leader == null) return;

		_mode = mode;
		_members = new GArray<L2Player>(room.getMembersSize());
		for(Long storedId : room.getMembers())
		{
			L2Player _member;
			if((_member = L2ObjectsStorage.getAsPlayer(storedId)) != null)
			{
				_members.add(_member);
			}
		}
	}

	@Override
	protected final void writeImpl()
	{
		if(_members == null)
			return;

		writeC(EXTENDED_PACKET);
		writeH(0x08);
		writeD(_mode);
		writeD(_members.size());
		for(L2Player _member : _members)
		{
			writeD(_member.getObjectId());
			writeS(_member.getName());
			writeD(_member.getClassId().ordinal());
			writeD(_member.getLevel());
			writeD(PartyRoomManager.getInstance().getLocation(_member));
			writeD(_member.equals(_leader) ? 0x01 : _member.getParty() != null && _leader.getParty() == _member.getParty() ? 0x02 : 0x00);//1-leader     2-party member    0-not party member
		}
		writeD(0x00); // TODO: Instance datas there is more if that is not 0!
	}
}