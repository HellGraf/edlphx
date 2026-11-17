package l2p.gameserver.network.serverpackets;

import l2p.commons.util.GArray;
import l2p.gameserver.TradeController.NpcTradeList;
import l2p.gameserver.model.TradeItem;

/**
 * Format: c ddh[hdddhhd]
 * c - id (0xE8)
 *
 * d - money
 * d - manor id
 * h - size
 * [
 * h - item type 1
 * d - object id
 * d - item id
 * d - count
 * h - item type 2
 * h
 * d - price
 * ]
 */
public final class BuyListSeed extends L2GameServerPacket
{
	private int _manorId;
	private GArray<TradeItem> _list = new GArray<TradeItem>();
	private long _money;

	public BuyListSeed(NpcTradeList list, int manorId, long currentMoney)
	{
		_money = currentMoney;
		_manorId = manorId;
		_list = list.getItems();
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0xe9);

		writeQ(_money); // current money
		writeD(_manorId); // manor id
		
		if ((_list != null) && (_list.size() > 0))
		{
			writeH(_list.size()); // list length
	
			for(TradeItem item : _list)
			{
				writeD(item.getItemId());
				writeD(item.getItemId());
				writeD(0x00); // objectId
				writeQ(item.getCount());
				
				writeH(0x05); // Custom Type 2
				writeH(0x00); // Custom Type 1
				writeH(0x00); // Equipped
				writeD(0x00); // Body Part
				writeH(0x00); // Enchant
				writeH(0x00); // Custom Type
				writeD(0x00); // Augment
				writeD(-1); // Mana
				writeD(-9999); // Time
				writeH(0x00); // Element Type
				writeH(0x00); // Element Power
				for (byte i = 0; i < 6; i++)
				{
					writeH(0x00);
				}
				// Enchant Effects
				writeH(0x00);
				writeH(0x00);
				writeH(0x00);
				
				writeQ(item.getOwnersPrice());
			}
		}
		else
		{
			writeH(0x00);
		}
	}
}