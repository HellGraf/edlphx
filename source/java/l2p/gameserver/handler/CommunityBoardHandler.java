package l2p.gameserver.handler;

import java.util.HashMap;
import java.util.Map;

import l2p.gameserver.Config;
import l2p.gameserver.cache.Msg;
import l2p.gameserver.handler.interfaces.ICommunityBoardHandler;
import l2p.gameserver.model.L2Player;
import l2p.gameserver.network.serverpackets.ShowBoard;

public class CommunityBoardHandler
{
	//==================== SINGLETON BEGIN ====================
	private static volatile CommunityBoardHandler _instance = null;

	public static CommunityBoardHandler getInstance()
	{
		if(_instance == null)
		{
			synchronized(CommunityBoardHandler.class)
			{
				if(_instance == null)
				{
					CommunityBoardHandler _local = new CommunityBoardHandler();
					_instance = _local;
				}
			}
		}
		return _instance;
	}
	//==================== SINGLETON END ====================
	
	private Map<String, ICommunityBoardHandler> _handlers = null;
	
	private CommunityBoardHandler()
	{
		_handlers = new HashMap<String, ICommunityBoardHandler>();
	}
	
	public void registerHandler(ICommunityBoardHandler handler)
	{
		String[] commands = handler.getCommunityCommandList();
		for(String c : commands)
			_handlers.put(c, handler);
	}

	public void useCommand(L2Player player, String command)
	{
		if(!Config.ALLOW_COMMUNITYBOARD)
		{
			player.sendPacket(Msg.THE_COMMUNITY_SERVER_IS_CURRENTLY_OFFLINE);
			return;
		}
		
		if(command == null)
			command = Config.BBS_DEFAULT;
		
		String cmd = command.split("\\s+")[0];
		String args = command.substring(cmd.length()).trim();
		
		if(cmd.length() > 0)
		{
			ICommunityBoardHandler h = _handlers.get(cmd);
			if(h != null)
				h.useCommunityCommand(player, cmd, args);
			else
				ShowBoard.separateAndSend("<html><body><br><br><center>" + cmd + " is not implemented yet</center><br><br></body></html>", player);
		}
	}

	public void writeCommand(L2Player player, String url, String arg1, String arg2, String arg3, String arg4, String arg5)
	{
		if(!Config.ALLOW_COMMUNITYBOARD)
		{
			player.sendPacket(Msg.THE_COMMUNITY_SERVER_IS_CURRENTLY_OFFLINE);
			return;
		}

		ShowBoard.separateAndSend("<html><body><br><br><center>" + url + " is not implemented yet</center><br><br></body></html>", player);
	}
	
	public int size()
	{
		return _handlers.size();
	}
}
