package l2p.gameserver.network.clientpackets;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import l2p.commons.util.GArray;
import l2p.gameserver.cache.Msg;
import l2p.gameserver.model.L2Character;
import l2p.gameserver.model.L2Player;
import l2p.gameserver.model.instances.L2CastleChamberlainInstance;
import l2p.gameserver.model.instances.L2ClanHallManagerInstance;
import l2p.gameserver.model.instances.L2MercManagerInstance;
import l2p.gameserver.model.instances.L2MerchantInstance;
import l2p.gameserver.model.instances.L2NpcFriendInstance;
import l2p.gameserver.model.instances.L2NpcInstance;
import l2p.gameserver.model.items.L2ItemInstance;
import l2p.gameserver.network.serverpackets.ExBuySellList;

public class RequestExRefundItem extends L2GameClientPacket
{
	@SuppressWarnings("unused")
	private int _listId;
	private int _count;
	private int[] _items;

	/**
	 * format: d dx[d]
	 */
	@Override
	public void readImpl()
	{
		_listId = readD();
		_count = readD();
		if(_count * 4 > _buf.remaining() || _count > Short.MAX_VALUE || _count < 1)
		{
			_count = 0;
			return;
		}
		_items = new int[_count];
		for(int i = 0; i < _count; i++)
		{
			_items[i] = readD();
		}
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();

		if(activeChar == null)
			return;

		ConcurrentLinkedQueue<L2ItemInstance> list = activeChar.getInventory().getRefundItemsList();

		Map<Integer, L2ItemInstance> _list = new HashMap<Integer, L2ItemInstance>();
		
		int i = 0;
		for(L2ItemInstance item : list)
		{
			_list.put(i, item);
			i++;
		}
		
		
		if(list == null || list.isEmpty())
		{
			activeChar.sendActionFailed();
			return;
		}

		L2NpcInstance npc = activeChar.getLastNpc();

		boolean isValidMerchant = npc instanceof L2ClanHallManagerInstance || npc instanceof L2MerchantInstance || npc instanceof L2MercManagerInstance || npc instanceof L2CastleChamberlainInstance || npc instanceof L2NpcFriendInstance;
		if(!activeChar.isGM() && (npc == null || !isValidMerchant || !activeChar.isInRange(npc.getLoc(), L2Character.INTERACTION_DISTANCE)))
		{
			activeChar.sendActionFailed();
			return;
		}

		GArray<L2ItemInstance> toreturn = new GArray<L2ItemInstance>(_items.length);
		long price = 0, weight = 0;

		for(int itemId : _items)
		{
			if(_list.containsKey(itemId))
			{
				L2ItemInstance item = _list.get(itemId);
				price += item.getCount() * item.getReferencePrice() / 2;
				weight += item.getCount() * item.getItem().getWeight();
				toreturn.add(item);
			}
		}

		if(toreturn.isEmpty())
		{
			activeChar.sendActionFailed();
			return;
		}

		if(activeChar.getAdena() < price)
		{
			activeChar.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			activeChar.sendActionFailed();
			return;
		}

		if(!activeChar.getInventory().validateWeight(weight))
		{
			sendPacket(Msg.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
			activeChar.sendActionFailed();
			return;
		}

		if(!activeChar.getInventory().validateCapacity(toreturn))
		{
			sendPacket(Msg.YOUR_INVENTORY_IS_FULL);
			activeChar.sendActionFailed();
			return;
		}

		if(!activeChar.getInventory().validateWeight(weight))
		{
			sendPacket(Msg.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
			activeChar.sendActionFailed();
			return;
		}

		activeChar.reduceAdena(price, true);

		for(L2ItemInstance itm : toreturn)
		{
			list.remove(itm);
			activeChar.getInventory().addItem(itm);
		}

		/*
		double taxRate = 0;
		Castle castle = null;
		if(npc != null)
		{
			castle = npc.getCastle(activeChar);
			if(castle != null)
				taxRate = castle.getTaxRate();
		}
		*/

		activeChar.sendPacket(new ExBuySellList(activeChar, true));
		
		//activeChar.sendPacket(/*new ExRefundList(activeChar), */new ExBuySellList(TradeController.getInstance().getBuyList(_listId), activeChar, taxRate).done());
		activeChar.updateStats();
	}
}