package l2p.gameserver.network.serverpackets;

import java.util.TreeSet;

import l2p.gameserver.model.L2Player;
import l2p.gameserver.model.items.Inventory;
import l2p.gameserver.model.items.L2ItemInstance;

public class ExReplyPostItemList extends L2GameServerPacket
{
	private TreeSet<L2ItemInstance> _itemslist = new TreeSet<L2ItemInstance>(Inventory.OrderComparator);

	public ExReplyPostItemList(L2Player cha)
	{
		if(!cha.getPlayerAccess().UseTrade) // если не разрешен трейд передавать предметы нельзя
			return;

		String tradeBan = cha.getVar("tradeBan"); // если трейд забанен тоже
		if(tradeBan != null && (tradeBan.equals("-1") || Long.parseLong(tradeBan) >= System.currentTimeMillis()))
			return;

		for(L2ItemInstance item : cha.getInventory().getItems())
			if(item != null && item.canBeTraded(cha))
				_itemslist.add(item);
	}

	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(0xB2);
		writeD(_itemslist.size());
		for(L2ItemInstance item : _itemslist)
			writeItem(item);
	}
}