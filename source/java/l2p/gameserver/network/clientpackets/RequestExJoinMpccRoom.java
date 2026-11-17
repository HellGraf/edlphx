package l2p.gameserver.network.clientpackets;

public class RequestExJoinMpccRoom extends L2GameClientPacket
{
	private int _roomId;

	@Override
	public void runImpl()
	{
		System.out.println(getType() + " :: " + _roomId);
	}

	/**
	 * format: dd
	 */
	@Override
	public void readImpl()
	{
		_roomId = readD();
	}
}