package l2p.gameserver.handler.interfaces;

import l2p.gameserver.model.L2Player;

public interface IVoicedCommandHandler
{
	public boolean useVoicedCommand(L2Player player, String command, String args);

	public String[] getVoicedCommandList();
}
