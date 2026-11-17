package l2p.gameserver.network.serverpackets;

import l2p.gameserver.model.L2Player;
import l2p.gameserver.model.items.L2ItemInstance;

public class ItemList extends L2GameServerPacket
{
	private final L2ItemInstance[] _items;
	private final boolean _showWindow;

	public ItemList(L2Player cha, boolean showWindow)
	{
		_items = cha.getInventory().getItems();
		_showWindow = showWindow;
	}

	public ItemList(L2ItemInstance[] items, boolean showWindow)
	{
		_items = items;
		_showWindow = showWindow;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x11);
		writeH(_showWindow ? 1 : 0);

		writeH(_items.length);
		for(L2ItemInstance temp : _items)
		{
			if((temp == null) || (temp.getItem() == null))
				continue;
			
			writeItem(temp);
		}

		short some_count = 0; // кол-во хз чего
		writeH(some_count);
		if(some_count > 0)
		{
			writeC(0); //?
			for(int i = 0; i < some_count; i++)
				writeD(0); //?
		}
		
		/*
		if (_inventory.hasInventoryBlock())
		{
			writeH(_inventory.getBlockItems().length);
			writeC(_inventory.getBlockMode());
			for (int i : _inventory.getBlockItems())
			{
				writeD(i);
			}
		}
		else
		{
			writeH(0x00);
		}
		*/
	}
}