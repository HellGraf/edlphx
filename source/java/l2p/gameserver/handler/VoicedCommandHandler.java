package l2p.gameserver.handler;

import java.util.HashMap;
import java.util.Map;

import l2p.gameserver.handler.interfaces.IVoicedCommandHandler;
import l2p.gameserver.model.L2Player;

public class VoicedCommandHandler
{
	//==================== SINGLETON BEGIN ====================
	private static volatile VoicedCommandHandler _instance = null;

	public static VoicedCommandHandler getInstance()
	{
		if(_instance == null)
		{
			synchronized(VoicedCommandHandler.class)
			{
				if(_instance == null)
				{
					VoicedCommandHandler _local = new VoicedCommandHandler();
					_instance = _local;
				}
			}
		}
		return _instance;
	}
	//==================== SINGLETON END ====================
	
	private Map<String, IVoicedCommandHandler> _handlers = null;
	
	private VoicedCommandHandler()
	{
		_handlers = new HashMap<String, IVoicedCommandHandler>();
	}
	
	public void registerHandler(IVoicedCommandHandler handler)
	{
		String[] commands = handler.getVoicedCommandList();
		for(String c : commands)
			_handlers.put(c, handler);
	}

	public boolean useCommand(L2Player player, String command)
	{		
		if(command.length() > 0)
		{
			String cmd = command.split("\\s+")[0];
			String args = command.substring(cmd.length()).trim();
			
			IVoicedCommandHandler h = _handlers.get(cmd);
			if(h != null)
				return h.useVoicedCommand(player, cmd, args);
		}
		return false;
	}
	
	public int size()
	{
		return _handlers.size();
	}
}
