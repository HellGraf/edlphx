package l2p.gameserver.network.serverpackets;

import l2p.commons.util.GArray;
import l2p.gameserver.TradeController.NpcTradeList;
import l2p.gameserver.model.L2Player;
import l2p.gameserver.model.TradeItem;

public class BuyList extends L2GameServerPacket
{
	private int _listId;
	private final GArray<TradeItem> _Buylist;
	private long _money;
	private double _TaxRate = 0;

	public BuyList(NpcTradeList Buylist, L2Player activeChar)
	{
		if(Buylist == null)
		{
			_listId = 0;
			_Buylist = null;
		}
		else
		{
			_listId = Buylist.getListId();
			_Buylist = cloneAndFilter(Buylist.getItems());
		}
		
		if(activeChar == null)
			_money = 0;
		else
		{
			_money = activeChar.getAdena();
			activeChar.setBuyListId(_listId);
		}
	}

	public BuyList(NpcTradeList Buylist, L2Player activeChar, double taxRate)
	{
		if(Buylist == null)
		{
			_listId = 0;
			_Buylist = null;
		}
		else
		{
			_listId = Buylist.getListId();
			_Buylist = cloneAndFilter(Buylist.getItems());
		}
		
		if(activeChar == null)
			_money = 0;
		else
		{
			_money = activeChar.getAdena();
			activeChar.setBuyListId(_listId);
		}
		_TaxRate = taxRate;
	}

	protected static GArray<TradeItem> cloneAndFilter(GArray<TradeItem> list)
	{
		if(list == null)
			return null;

		GArray<TradeItem> ret = new GArray<TradeItem>(list.size());

		for(TradeItem item : list)
		{
			// А не пора ли обновить количество лимитированных предметов в трейд листе?
			if(item.getCurrentValue() < item.getCount() && item.getLastRechargeTime() + item.getRechargeTime() <= System.currentTimeMillis() / 60000)
			{
				item.setLastRechargeTime(item.getLastRechargeTime() + item.getRechargeTime());
				item.setCurrentValue(item.getCount());
			}

			if(item.getCurrentValue() == 0 && item.getCount() != 0)
				continue;

			ret.add(item);
		}

		return ret;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0xFE);
		writeH(0xB7);
		writeD(0x00);
		writeQ(_money); // current money
		writeD(_listId);

		if(_Buylist == null)
			writeH(0);
		else
		{
			writeH(_Buylist.size());
			for(TradeItem item : _Buylist)
			{
				writeD(item.getItemId());
				writeD(item.getItemId());
				writeD(0x00);
				writeQ(item.getCount() < 0 ? 0 : item.getCount());
				writeH(item.getItem().getType2ForPackets()); // item type2
				writeH(item.getItem().getType1()); // item type1
				writeH(0x00);
				writeD(item.getItem().getBodyPart()); // rev 415  slot    0006-lr.ear  0008-neck  0030-lr.finger  0040-head  0080-??  0100-l.hand  0200-gloves  0400-chest  0800-pants  1000-feet  2000-??  4000-r.hand  8000-r.hand
				writeH(0);
				writeH(0);
				writeD(0);
				writeD(-1);
				writeD(-9999);
				writeH(0);
				writeH(0);
				writeH(0);
				writeH(0);
				writeH(0);
				writeH(0);
				writeH(0);
				writeH(0);
				
				writeH(0);
				writeH(0);
				writeH(0);
				
				writeQ((long) (item.getOwnersPrice() * (1 + _TaxRate)));
			}
		}
	}
}