package l2p.gameserver.network.serverpackets;

public class GameGuardQuery extends L2GameServerPacket
{
	@Override
	protected final void writeImpl()
	{
		getClient().setGameGuardOk(false);
		writeC(0x74);
		//writeD(0x00); // ? - Меняется при каждом перезаходе.
		//writeD(0x00); // ? - Меняется при каждом перезаходе.
		//writeD(0x00); // ? - Меняется при каждом перезаходе.
		//writeD(0x00); // ? - Меняется при каждом перезаходе.
		writeD(0x27533DD9);
		writeD(0x2E72A51D);
		writeD(0x2017038B);
		writeD(0xC35B1EA3);
	}
}