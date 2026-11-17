package l2p.gameserver.handler;

import java.util.HashMap;
import java.util.Map;

import l2p.gameserver.handler.interfaces.IUserCommandHandler;
import l2p.gameserver.model.L2Player;

public class UserCommandHandler
{
	//==================== SINGLETON BEGIN ====================
	private static volatile UserCommandHandler _instance = null;

	public static UserCommandHandler getInstance()
	{
		if(_instance == null)
		{
			synchronized(UserCommandHandler.class)
			{
				if(_instance == null)
				{
					UserCommandHandler _local = new UserCommandHandler();
					_instance = _local;
				}
			}
		}
		return _instance;
	}
	//==================== SINGLETON END ====================
	
	private Map<Integer, IUserCommandHandler> _handlers = null;

	private UserCommandHandler()
	{
		_handlers = new HashMap<Integer, IUserCommandHandler>();
	}

	public void registerHandler(IUserCommandHandler handler)
	{
		int[] ids = handler.getUserCommandList();
		for(int element : ids)
			_handlers.put(element, handler);
	}

	public boolean useCommand(L2Player player, int id)
	{
		IUserCommandHandler h = _handlers.get(id);
		if(h == null) return false;
		h.useUserCommand(player, id);
		return true;
	}

	public int size()
	{
		return _handlers.size();
	}
}
