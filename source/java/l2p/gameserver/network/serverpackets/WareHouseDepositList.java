package l2p.gameserver.network.serverpackets;

import java.util.TreeSet;

import l2p.gameserver.model.L2Player;
import l2p.gameserver.model.items.Inventory;
import l2p.gameserver.model.items.L2ItemInstance;
import l2p.gameserver.model.items.Warehouse.WarehouseType;

public class WareHouseDepositList extends L2GameServerPacket
{
	private int _whType;
	private long _playerAdena;
	private TreeSet<L2ItemInstance> _items = new TreeSet<L2ItemInstance>(Inventory.OrderComparator);

	public WareHouseDepositList(L2Player cha, WarehouseType whtype)
	{
		cha.setUsingWarehouseType(whtype);
		_whType = whtype.getPacketValue();
		_playerAdena = cha.getAdena();
		for(L2ItemInstance item : cha.getInventory().getItems())
			if(item != null && item.canBeStored(cha, _whType == 1))
				_items.add(item);
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x41);
		writeH(_whType);
		writeQ(_playerAdena);
		writeH(_items.size());
		
		for (L2ItemInstance item : _items)
		{
			writeItem(item);
			writeD(item.getObjectId());
		}
	}
}