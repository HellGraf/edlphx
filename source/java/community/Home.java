package community;

import l2p.gameserver.Config;
import l2p.gameserver.handler.CommunityBoardHandler;
import l2p.gameserver.handler.interfaces.ICommunityBoardHandler;
import l2p.gameserver.model.L2Player;
import l2p.gameserver.network.serverpackets.ShowBoard;
import l2p.gameserver.scripts.ScriptFile;

public class Home implements ICommunityBoardHandler, ScriptFile
{
	private static String[] _commands =
	{
		Config.BBS_DEFAULT
	};

	@Override
	public String[] getCommunityCommandList()
	{
		return _commands;
	}

	@Override
	public void useCommunityCommand(L2Player player, String command, String args)
	{
		ShowBoard.separateAndSend("<html><body><br><br><center>" + command + " is not implemented yet</center><br><br></body></html>", player);
	}

	@Override
	public void onLoad()
	{
		CommunityBoardHandler.getInstance().registerHandler(this);
	}

	@Override
	public void onReload() {}

	@Override
	public void onShutdown() {}
}
