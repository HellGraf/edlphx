package l2p.gameserver.network.serverpackets;

import java.util.TreeSet;

import l2p.gameserver.model.L2Player;
import l2p.gameserver.model.items.Inventory;
import l2p.gameserver.model.items.L2ItemInstance;

//0x2e TradeStart   d h (h dddhh dhhh)
public class TradeStart extends L2GameServerPacket
{
	private TreeSet<L2ItemInstance> _tradelist = new TreeSet<L2ItemInstance>(Inventory.OrderComparator);
	private boolean can_writeImpl = false;
	private int requester_obj_id;

	public TradeStart(L2Player me, L2Player other)
	{
		if(me == null)
			return;

		requester_obj_id = other.getObjectId();

		L2ItemInstance[] inventory = me.getInventory().getItems();
		for(L2ItemInstance item : inventory)
			if(item != null && item.canBeTraded(me))
				_tradelist.add(item);

		can_writeImpl = true;
	}

	@Override
	protected final void writeImpl()
	{
		if(!can_writeImpl)
			return;

		writeC(0x14);
		writeD(requester_obj_id);
		writeH(_tradelist.size());

		for(L2ItemInstance temp : _tradelist)
		{
			writeItem(temp);
		}
	}
}