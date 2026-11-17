package l2p.gameserver.handler;

import java.util.HashMap;
import java.util.Map;

import l2p.gameserver.handler.interfaces.IItemHandler;
import l2p.gameserver.model.L2Player;
import l2p.gameserver.model.items.L2ItemInstance;

public class ItemHandler
{
	//==================== SINGLETON BEGIN ====================
	private static volatile ItemHandler _instance = null;

	public static ItemHandler getInstance()
	{
		if(_instance == null)
		{
			synchronized(ItemHandler.class)
			{
				if(_instance == null)
				{
					ItemHandler _local = new ItemHandler();
					_instance = _local;
				}
			}
		}
		return _instance;
	}
	//==================== SINGLETON END ====================

	private Map<Integer, IItemHandler> _handlers = null;

	private ItemHandler()
	{
		_handlers = new HashMap<Integer, IItemHandler>();
	}

	public void registerHandler(IItemHandler handler)
	{
		int[] ids = handler.getItemIds();
		for(int element : ids)
			_handlers.put(element, handler);
	}

	public void useItem(L2Player player, L2ItemInstance item, boolean ctrl_pressed)
	{
		IItemHandler h = _handlers.get(item.getItemId());
		if(h != null)
			h.useItem(player, item, ctrl_pressed);
	}

	public int size()
	{
		return _handlers.size();
	}
}
