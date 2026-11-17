package l2p.gameserver.network.clientpackets;

import l2p.gameserver.model.L2Player;

/**
 * format: ddd
 */
public class NetPing extends L2GameClientPacket
{
	private int _kid;
	private int _ping;
	private int _delta;

	@Override
	public void readImpl()
	{
		_kid = readD();
		_ping = readD();
		_delta = readD();
	}

	@Override
	public void runImpl()
	{
		L2Player player = getClient().getActiveChar();
		player.pingResult(_kid, _ping, _delta);
	}
}