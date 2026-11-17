package l2p.gameserver.network.serverpackets;

public class KeyPacket extends L2GameServerPacket
{
	private byte[] _key;

	public KeyPacket(byte[] key)
	{
		_key = key;
	}

	@Override
	public void writeImpl()
	{
		writeC(0x2e);
		if(_key == null || _key.length == 0)
		{
			writeC(0x00);
		}
		else
		{
			writeC(0x01);
			for (int i = 0; i < 8; i++)
			{
				writeC(_key[i]); // key
			}
			writeD(0x01);
			writeD(0x01);
			writeC(0x01);
			writeD(0x00); // Seed (obfuscation key)
		}
	}
}