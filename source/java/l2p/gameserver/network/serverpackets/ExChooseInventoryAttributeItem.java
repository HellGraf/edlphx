package l2p.gameserver.network.serverpackets;

import l2p.gameserver.model.items.L2ItemInstance;
import l2p.gameserver.templates.L2Item;

public class ExChooseInventoryAttributeItem extends L2GameServerPacket
{
	private int _itemId;
	private final byte _atribute;
	private final int _level;

	public ExChooseInventoryAttributeItem(L2ItemInstance item)
	{
		_itemId = item.getItemId();
		_atribute = L2Item.ATTRIBUTE_NONE; //TODO: _atribute = Elementals.getItemElement(_itemId);
		_level = 0; //TODO: _level = Elementals.getMaxElementLevel(_itemId);
	}

	@Override
	protected final void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(0x62);
		writeD(_itemId);
		writeD(_atribute == L2Item.ATTRIBUTE_FIRE ? 1 : 0); // Fire
		writeD(_atribute == L2Item.ATTRIBUTE_WATER ? 1 : 0); // Water
		writeD(_atribute == L2Item.ATTRIBUTE_WIND ? 1 : 0); // Wind
		writeD(_atribute == L2Item.ATTRIBUTE_EARTH ? 1 : 0); // Earth
		writeD(_atribute == L2Item.ATTRIBUTE_HOLY ? 1 : 0); // Holy
		writeD(_atribute == L2Item.ATTRIBUTE_DARK ? 1 : 0); // Unholy
		writeD(_level); // Item max attribute level
	}
}