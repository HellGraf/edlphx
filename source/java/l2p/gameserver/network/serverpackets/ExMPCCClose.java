package l2p.gameserver.network.serverpackets;

/**
 * Close the CommandChannel Information window
 */
public class ExMPCCClose extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(0x13);
	}
}
