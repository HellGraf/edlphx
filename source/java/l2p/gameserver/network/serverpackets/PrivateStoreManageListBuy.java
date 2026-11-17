package l2p.gameserver.network.serverpackets;

import l2p.gameserver.model.L2Player;
import l2p.gameserver.model.L2TradeList;
import l2p.gameserver.model.TradeItem;
import l2p.gameserver.model.items.L2ItemInstance;
import l2p.gameserver.templates.L2Item;

public class PrivateStoreManageListBuy extends L2GameServerPacket
{
	private int buyer_id;
	private long buyer_adena;
	private L2TradeList _list;
	private TradeItem[] _buyList;

	/**
	 * Окно управления личным магазином продажи
	 * @param buyer
	 */
	public PrivateStoreManageListBuy(L2Player buyer)
	{
		buyer_id = buyer.getObjectId();
		buyer_adena = buyer.getAdena();

		_buyList = (TradeItem[]) buyer.getBuyList().toArray();

		_list = new L2TradeList(0);
		for(L2ItemInstance item : buyer.getInventory().getItems())
			if(item != null && item.canBeTraded(buyer) && item.getItemId() != L2Item.ITEM_ID_ADENA)
			{
				for(TradeItem ti : buyer.getBuyList())
					if(ti.getItemId() == item.getItemId())
						continue;
				_list.addItem(item);
			}
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0xBD);
		//section 1
		writeD(buyer_id);
		writeQ(buyer_adena);

		//section2
		writeD(_list.getItems().size());//for potential sells
		for(L2ItemInstance item : _list.getItems())
		{
			writeItem(item);
			writeQ(item.getReferencePrice() * 2);
		}

		//section 3
		writeD(_buyList.length);//count for any items already added for sell
		for(TradeItem item : _buyList)
		{
			writeItem(item);
			writeQ(item.getOwnersPrice());
			writeQ(item.getStorePrice());
			writeQ(item.getCount());
		}
	}
}