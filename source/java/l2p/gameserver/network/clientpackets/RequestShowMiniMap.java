package l2p.gameserver.network.clientpackets;

import l2p.gameserver.model.L2Player;
import l2p.gameserver.network.serverpackets.ShowMiniMap;

public class RequestShowMiniMap extends L2GameClientPacket
{
	@Override
	public void readImpl()
	{}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar != null)
			sendPacket(new ShowMiniMap(activeChar, 1665));
	}
}