package l2p.gameserver.network.clientpackets;

import l2p.commons.util.Log;
import l2p.gameserver.model.L2Player;

public class RequestPetitionCancel extends L2GameClientPacket
{
	//private String _text;

	@Override
	public void readImpl()
	{
		//_text = readS(4096);
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		Log.LogPetition(activeChar, 0, "Petition Cancel" );
	}
}