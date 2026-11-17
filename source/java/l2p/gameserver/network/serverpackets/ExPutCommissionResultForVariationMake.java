package l2p.gameserver.network.serverpackets;

public class ExPutCommissionResultForVariationMake extends L2GameServerPacket
{
	private int _gemstoneObjId, _itemId, _unk1, _unk2, _unk3;
	private long _gemstoneCount;

	public ExPutCommissionResultForVariationMake(int gemstoneObjId, long gemstoneCount, int itemId)
	{
		_gemstoneObjId = gemstoneObjId;
		_itemId = itemId;
		_gemstoneCount = gemstoneCount;
		_unk1 = 0;
		_unk2 = 0;
		_unk3 = 1;
	}

	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(0x55);
		writeD(_gemstoneObjId);
		writeD(_itemId);
		writeQ(_gemstoneCount);
		writeD(_unk1);
		writeD(_unk2);
		writeD(_unk3);
	}
}