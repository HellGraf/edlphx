package l2p.loginserver.gameservercon.gspackets;

import l2p.loginserver.gameservercon.AttGS;

public class Restart extends ClientBasePacket
{
	public Restart(byte[] decrypt, AttGS gameserver)
	{
		super(decrypt, gameserver);
	}

	@Override
	public void read()
	{
		// Полный рестарт логинсервера.
		System.out.println("Server exiting [status=2] / Reason: Recived full restart command from: " + getGameServer());
		Runtime.getRuntime().exit(2);
	}
}