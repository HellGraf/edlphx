package l2p.gameserver.network.serverpackets;

public class ExPutEnchantTargetItemResult extends L2GameServerPacket
{
	private int _result;

	public ExPutEnchantTargetItemResult(int result)
	{
		_result = result;
	}

	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(0x81);
		writeD(_result);
	}
}