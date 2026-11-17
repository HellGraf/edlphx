package l2p.gameserver.network.serverpackets;

import l2p.gameserver.model.items.L2ItemInstance;

public class ExPutItemResultForVariationCancel extends L2GameServerPacket
{
	private int _itemObjId;
	private int _itemId;
	private int _itemAug1;
	private int _itemAug2;
	private int _price;

	public ExPutItemResultForVariationCancel(L2ItemInstance item, int price)
	{
		_itemObjId = item.getObjectId();
		_itemId = item.getItemId();
		_price = price;
		_itemAug1 = item.getAugmentationId();
		_itemAug2 = item.getAugmentationId() >> 16;
	}

	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(0x57);
		writeD(_itemObjId);
		writeD(_itemId);
		writeD(_itemAug1);
		writeD(_itemAug2);
		writeQ(_price);
		writeD(0x01);
	}
}