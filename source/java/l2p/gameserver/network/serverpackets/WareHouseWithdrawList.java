package l2p.gameserver.network.serverpackets;

import java.util.NoSuchElementException;

import l2p.gameserver.cache.Msg;
import l2p.gameserver.model.L2Player;
import l2p.gameserver.model.items.L2ItemInstance;
import l2p.gameserver.model.items.L2ItemInstance.ItemClass;
import l2p.gameserver.model.items.Warehouse.WarehouseType;

public class WareHouseWithdrawList extends L2GameServerPacket
{
	public static final int PRIVATE = 1;
	public static final int CLAN = 2; // final - 4?
	public static final int CASTLE = 3;
	public static final int FREIGHT = 4; // final - 1?

	private long _playerAdena;
	private L2ItemInstance[] _items;
	private int _whType;
	private boolean can_writeImpl = false;
	/**
	 * <ul>
	 * <li>0x01-Private Warehouse</li>
	 * <li>0x02-Clan Warehouse</li>
	 * <li>0x03-Castle Warehouse</li>
	 * <li>0x04-Warehouse</li>
	 * </ul>
	 */

	public WareHouseWithdrawList(L2Player cha, WarehouseType type, ItemClass clss)
	{
		if(cha == null)
			return;

		_playerAdena = cha.getAdena();
		_whType = type.getPacketValue();
		cha.setUsingWarehouseType(type);
		switch(type)
		{
			case PRIVATE:
				_items = cha.getWarehouse().listItems(clss);
				break;
			case CLAN:
			case CASTLE:
				_items = cha.getClan().getWarehouse().listItems(clss);
				break;
			/*
			 case CASTLE:
			 items = _cha.getClan().getCastleWarehouse().listItems();
			 break;
			 */
			case FREIGHT:
				_items = cha.getFreight().listItems(clss);
				break;
			default:
				throw new NoSuchElementException("Invalid value of 'type' argument");
		}

		if(_items.length == 0)
		{
			cha.sendPacket(Msg.YOU_HAVE_NOT_DEPOSITED_ANY_ITEMS_IN_YOUR_WAREHOUSE);
			return;
		}

		can_writeImpl = true;
	}

	@Override
	protected final void writeImpl()
	{
		if(!can_writeImpl)
			return;
		
		writeC(0x42);
		writeH(_whType);
		writeQ(_playerAdena);
		writeH(_items.length);
		
		for (L2ItemInstance item : _items)
		{
			writeItem(item);
			writeD(item.getObjectId());
		}
	}
}