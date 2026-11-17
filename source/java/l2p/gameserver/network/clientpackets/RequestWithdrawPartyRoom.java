package l2p.gameserver.network.clientpackets;

import l2p.gameserver.instancemanager.PartyRoomManager;
import l2p.gameserver.model.L2Player;
import l2p.gameserver.model.PartyRoom;

/**
 * Format (ch) dd
 */
public class RequestWithdrawPartyRoom extends L2GameClientPacket
{
	private int _roomId;

	@Override
	public void readImpl()
	{
		_roomId = readD(); //room id
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		//_log.info("RequestWithdrawPartyRoom from char: " + activeChar.getName());
		//_log.info("Data received: d:" + _roomId + " d:" + _data2);
		PartyRoom room = PartyRoomManager.getInstance().getRoom(_roomId);
		if(room.getLeader() == null || room.getLeader().equals(activeChar))
			PartyRoomManager.getInstance().removeRoom(_roomId);
		else
			PartyRoomManager.getInstance().getRoom(_roomId).removeMember(activeChar, false);
	}
}