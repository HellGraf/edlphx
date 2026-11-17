package l2p.gameserver.handler.interfaces;

import l2p.gameserver.model.L2Player;

public interface ICommunityBoardHandler
{
	public void useCommunityCommand(L2Player player, String command, String args);

	public String[] getCommunityCommandList();
}
