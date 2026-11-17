package l2p.gameserver.network.serverpackets;

public class ExVariationCancelResult extends L2GameServerPacket
{
	private int _result;

	public ExVariationCancelResult(int result)
	{
		_result = result;
	}

	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(0x58);
		writeD(_result);
	}
}