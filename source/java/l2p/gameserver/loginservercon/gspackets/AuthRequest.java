package l2p.gameserver.loginservercon.gspackets;

import l2p.commons.network.MMOSocket;
import l2p.gameserver.Config;

public class AuthRequest extends GameServerBasePacket
{
	public AuthRequest()
	{
		writeC(0x01);
		writeC(Config.REQUEST_ID);
		writeS(MMOSocket.getInstance(0) == null ? Config.EXTERNAL_HOSTNAME : MMOSocket.getInstance(0));
		writeS(MMOSocket.getInstance(1) == null ? Config.INTERNAL_HOSTNAME : MMOSocket.getInstance(1));
		if(Config.PORTS_GAME.length == 1) // старый формат, однопортовый
			writeH(Config.PORTS_GAME[0]);
		else
		//новый формат, многопортовый
		{
			writeH(0xFFFF);
			writeC(Config.PORTS_GAME.length);
			for(int PORT_GAME : Config.PORTS_GAME)
				writeH(PORT_GAME);
		}
		writeD(Config.MAXIMUM_ONLINE_USERS);
	}
}