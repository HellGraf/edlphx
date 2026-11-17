package l2p.gameserver.handler;

import java.util.HashMap;
import java.util.Map;

import l2p.commons.util.Log;
import l2p.gameserver.extensions.multilang.CustomMessage;
import l2p.gameserver.handler.interfaces.IAdminCommandHandler;
import l2p.gameserver.model.L2Player;

public class AdminCommandHandler
{
	//==================== SINGLETON BEGIN ====================
	private static volatile AdminCommandHandler _instance = null;

	public static AdminCommandHandler getInstance()
	{
		if(_instance == null)
		{
			synchronized(AdminCommandHandler.class)
			{
				if(_instance == null)
				{
					AdminCommandHandler _local = new AdminCommandHandler();
					_instance = _local;
				}
			}
		}
		return _instance;
	}
	//==================== SINGLETON END ====================
	
	private Map<String, IAdminCommandHandler> _handlers = null;

	private AdminCommandHandler()
	{
		_handlers = new HashMap<String, IAdminCommandHandler>();
	}
	
	public void registerHandler(IAdminCommandHandler handler)
	{
		String[] commands = handler.getAdminCommandList();
		for(String c : commands)
			_handlers.put(c, handler);
	}

	public void useCommand(L2Player player, String command)
	{
		if(!player.isGM() || !player.getPlayerAccess().CanUseGMCommand)
		{
			player.sendMessage(new CustomMessage("l2p.gameserver.clientpackets.SendBypassBuildCmd.NoCommandOrAccess", player).addString(command));
			return;
		}
		
		if(command.length() > 0)
		{			
			String cmd = command.split("\\s+")[0];
			String args = command.substring(cmd.length()).trim();
			
			IAdminCommandHandler h = _handlers.get(cmd);
			if(h != null)
			{
				boolean success = h.useAdminCommand(player, cmd, args);
				Log.LogCommand(player, Log.CMD_ADMH, cmd, success ? 1 : 0);
			}
		}
	}

	public int size()
	{
		return _handlers.size();
	}
}
