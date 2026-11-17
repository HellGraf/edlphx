package l2p.gameserver.network.clientpackets;

import l2p.commons.util.Log;
import l2p.gameserver.model.L2Player;
import l2p.gameserver.tables.GmListTable;

public class RequestGmList extends L2GameClientPacket
{
	@Override
	public void readImpl()
	{}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar != null)
		{
			GmListTable.sendListToPlayer(activeChar);
			Log.LogCommand(activeChar, 2, getType(), 1);
		}
	}
}