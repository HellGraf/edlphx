package l2p.gameserver.network.clientpackets;

import l2p.gameserver.handler.CommunityBoardHandler;

public class RequestShowBoard extends L2GameClientPacket
{
	@Override
	public void readImpl()
	{
		readD(); //always 1
	}

	@Override
	public void runImpl()
	{
		CommunityBoardHandler.getInstance().useCommand(getClient().getActiveChar(), null);
	}
}