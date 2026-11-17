package l2p.gameserver.handler.interfaces;

import l2p.gameserver.model.L2Player;

public interface IAdminCommandHandler
{
	public boolean useAdminCommand(L2Player player, String command, String args);

	public String[] getAdminCommandList();
}