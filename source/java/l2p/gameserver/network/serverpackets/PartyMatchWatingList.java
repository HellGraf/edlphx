package l2p.gameserver.network.serverpackets;

import java.util.Collection;

import l2p.commons.util.GArray;
import l2p.gameserver.instancemanager.PartyRoomManager;
import l2p.gameserver.model.L2Player;
import l2p.gameserver.model.PartyRoom;

/**
 * Format:(c) dddddds
 */
public class PartyMatchWatingList extends L2GameServerPacket
{
	private Collection<PartyRoom> _rooms;

	public PartyMatchWatingList(L2Player player)
	{
		this(player.getPartyMatchingRegion(), player.getPartyMatchingLevels(), 1, player);
	}

	public PartyMatchWatingList(int region, int lvlRst, int page, L2Player activeChar)
	{
		int first = (page - 1) * 64;
		int firstNot = page * 64;
		_rooms = new GArray<PartyRoom>();

		int i = 0;
		GArray<PartyRoom> temp = PartyRoomManager.getInstance().getRooms(region, lvlRst, activeChar);
		for(PartyRoom room : temp)
		{
			if(i < first || i >= firstNot)
				continue;
			_rooms.add(room);
			i++;
		}
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x9c);
		
		int size = _rooms.size();		
		writeD(size > 0 ? 0x01 : 0x00); // unknown
		
		writeD(_rooms.size()); // room count

		for(PartyRoom room : _rooms)
		{
			writeD(room.getId()); //room id
			writeS(room.getTitle()); // room name
			writeD(room.getLocation()); //Location (смотерть список ниже)
			writeD(room.getMinLevel()); //min level
			writeD(room.getMaxLevel()); //max level
			writeD(room.getMembersSize()); //members count
			writeD(room.getMaxMembers()); //max members count
			writeS(room.getLeader() == null ? "None" : room.getLeader().getName()); //leader name
			writeD(room.getMembersSize());
			for (long id : room.getMembers())
			{
				/*
				L2Player member = null; //TODO: getCharByID 
				if (member != null)
				{
					writeD(member.getClassId().getId());
					writeS(member.getName());
				}
				else
				*/
				{
					writeD(0x00);
					writeS("undone " + id);//writeS("Not Found");
				}
			}
		}

		/*	Talking Island - 1
			Gludio - 2
			Dark Elven Ter. - 3
			Elven Territory - 4
			Dion - 5
			Giran - 6
			Neutral Zone - 7
			Schuttgart - 9
			Oren - 10
			Hunters Village - 11
			Innadril - 12
			Aden - 13
			Rune - 14
			Goddard - 15 */
	}
}