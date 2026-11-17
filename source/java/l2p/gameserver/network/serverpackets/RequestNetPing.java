package l2p.gameserver.network.serverpackets;

public class RequestNetPing extends L2GameServerPacket
{
	private int _kid;
	
	public RequestNetPing(int kid)
	{
		_kid = kid;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0xd9);
		writeD(_kid);
	}
}