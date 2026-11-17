package l2p.loginserver.gameservercon.gspackets;

import java.util.logging.Logger;

import l2p.loginserver.GameServerTable;
import l2p.loginserver.gameservercon.AttGS;
import l2p.loginserver.gameservercon.GameServerInfo;
import l2p.loginserver.gameservercon.lspackets.AuthResponse;
import l2p.loginserver.gameservercon.lspackets.BanIPList;
import l2p.loginserver.gameservercon.lspackets.LoginServerFail;

public class AuthRequest extends ClientBasePacket
{
	protected static Logger log = Logger.getLogger(AuthRequest.class.getName());

	public AuthRequest(byte[] decrypt, AttGS gameserver)
	{
		super(decrypt, gameserver);
	}

	@Override
	public void read()
	{
		int requestId = readC();
		String externalIp = readS();
		String internalIp = readS();
		int portsCount = readH();
		int[] ports;
		if(portsCount == 0xFFFF) //новый формат, многопортовый
		{
			ports = new int[readC()];
			for(int i = 0; i < ports.length; i++)
				ports[i] = readH();
		}
		else
			ports = new int[] { portsCount }; // старый формат, однопортовый
		int maxOnline = readD();
		
		int protocolVersion;
		try
		{
			protocolVersion = readH();
		}
		catch(Exception e)
		{
			protocolVersion = 0;
		}

		log.info("Trying to register server: " + requestId + ", " + getGameServer().getConnectionIpAddress());
		AttGS client = getGameServer();

		GameServerTable gameServerTable = GameServerTable.getInstance();

		GameServerInfo gsi = gameServerTable.getRegisteredGameServerById(requestId);
		// is there a gameserver registered with this id?
		if(gsi != null)
		{
			// check to see if this GS is already connected
			synchronized (gsi)
			{
				if(gsi.isAuthed())
					sendPacket(new LoginServerFail(LoginServerFail.REASON_ALREADY_LOGGED8IN));
				else
				{
					client.setGameServerInfo(gsi);
					gsi.setGameServer(client);
					gsi.setPorts(ports);
					gsi.setGameHosts(externalIp, internalIp);
					gsi.setMaxPlayers(maxOnline);
					gsi.setAuthed(true);
				}
			}
		}
		else
		{
			gsi = new GameServerInfo(requestId, client);
			if(gameServerTable.register(requestId, gsi))
			{
				client.setGameServerInfo(gsi);
				gsi.setGameServer(client);
				gsi.setPorts(ports);
				gsi.setGameHosts(externalIp, internalIp);
				gsi.setMaxPlayers(maxOnline);
				gsi.setAuthed(true);
			}
			else
				// some one took this ID meanwhile
				sendPacket(new LoginServerFail(LoginServerFail.REASON_ID_RESERVED));
		}

		if(gsi != null && gsi.isAuthed())
		{
			AuthResponse ar = new AuthResponse(gsi.getId());
			client.setProtocolVersion(protocolVersion);
			client.setAuthed(true);
			client.setServerId(gsi.getId());
			sendPacket(ar);
			sendPacket(new BanIPList());
			log.info("Server registration successful.");
		}
		else
			log.info("Server registration failed.");
	}
}
