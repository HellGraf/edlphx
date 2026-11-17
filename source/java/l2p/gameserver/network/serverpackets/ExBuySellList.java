package l2p.gameserver.network.serverpackets;

import java.util.TreeSet;
import java.util.concurrent.ConcurrentLinkedQueue;

import l2p.gameserver.model.L2Player;
import l2p.gameserver.model.items.Inventory;
import l2p.gameserver.model.items.L2ItemInstance;

public class ExBuySellList extends L2GameServerPacket
{
	private boolean _done;
	private final TreeSet<L2ItemInstance> _sellList;
	private final ConcurrentLinkedQueue<L2ItemInstance> _refundList;

	public ExBuySellList(L2Player activeChar, boolean done)
	{
		_refundList = activeChar.getInventory().getRefundItemsList();
		_sellList = new TreeSet<L2ItemInstance>(Inventory.OrderComparator);

		for(L2ItemInstance item : activeChar.getInventory().getItemsList())
		{
			if(item.getItem().isSellable() && item.canBeTraded(activeChar) && item.getReferencePrice() > 0)
			{
				_sellList.add(item);
			}
		}
		_done = done;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(0xB7);
		writeD(0x01);
		
		if(_sellList != null && _sellList.size() > 0)
		{
			writeH(_sellList.size());
			for(L2ItemInstance item : _sellList)
			{
				writeItem(item);	
				writeQ(item.getItem().getReferencePrice() / 2);
			}
		}
		else
		{
			writeH(0);
		}

		if(_refundList != null && _refundList.size() > 0)
		{
			writeH(_refundList.size());
			int i = 0;
			for(L2ItemInstance item : _refundList)
			{
				writeItem(item);
				writeD(i++);
				writeQ(item.getItem().getReferencePrice() / 2);
			}
		}
		else
		{
			writeH(0);
		}
		writeC(_done ? 0x01 : 0x00);
	}
}