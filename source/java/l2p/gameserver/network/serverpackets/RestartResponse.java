package l2p.gameserver.network.serverpackets;

public class RestartResponse extends L2GameServerPacket
{
	public static final RestartResponse OK = new RestartResponse(1), FAIL = new RestartResponse(0);
	private int _param;

	public RestartResponse(int param)
	{
		_param = param;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x71);
		writeD(_param); //01-ok
	}
}