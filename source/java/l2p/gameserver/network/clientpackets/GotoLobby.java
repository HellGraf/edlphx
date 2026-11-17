package l2p.gameserver.network.clientpackets;

import l2p.gameserver.network.serverpackets.CharacterSelectionInfo;

public class GotoLobby extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{}

	@Override
	protected void runImpl()
	{
		CharacterSelectionInfo cl = new CharacterSelectionInfo(getClient().getLoginName(), getClient().getSessionId().playOkID1);
		sendPacket(cl);
	}
}