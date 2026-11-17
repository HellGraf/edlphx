package l2p.loginserver.gameservercon.gspackets;

import java.util.logging.Logger;

import l2p.commons.database.DatabaseUtils;
import l2p.commons.database.FiltredPreparedStatement;
import l2p.commons.database.L2DatabaseFactory;
import l2p.commons.database.ThreadConnection;
import l2p.loginserver.gameservercon.AttGS;

/**
 * @Author: SYS
 * @Date: 10/4/2007
 */
public class LockAccountIP extends ClientBasePacket
{
	private static final Logger _log = Logger.getLogger(LockAccountIP.class.getName());

	public LockAccountIP(byte[] decrypt, AttGS gameserver)
	{
		super(decrypt, gameserver);
	}

	@Override
	public void read()
	{
		String accname = readS();
		String IP = readS();
		int time = readD();

		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("UPDATE accounts SET AllowIPs = ?, lock_expire = ? WHERE login = ?");
			statement.setString(1, IP);
			statement.setInt(2, time);
			statement.setString(3, accname);
			statement.executeUpdate();
			DatabaseUtils.closeStatement(statement);
		}
		catch(Exception e)
		{
			_log.severe("Failed to lock/unlock account: " + e.getMessage());
		}
		finally
		{
			DatabaseUtils.closeConnection(con);
		}
	}
}