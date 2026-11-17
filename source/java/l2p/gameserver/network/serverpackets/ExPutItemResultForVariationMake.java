package l2p.gameserver.network.serverpackets;

public class ExPutItemResultForVariationMake extends L2GameServerPacket
{
	private int _itemObjId;
	private int _itemId;

	public ExPutItemResultForVariationMake(int itemObjId, int itemId)
	{
		_itemObjId = itemObjId;
		_itemId = itemId;
	}

	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(0x53);
		writeD(_itemObjId);
		writeD(_itemId);
		writeD(0x01);
	}
}