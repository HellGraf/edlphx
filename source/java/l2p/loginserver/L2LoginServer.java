package l2p.loginserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import l2p.commons.MemoryWatchDog;
import l2p.commons.database.L2DatabaseFactory;
import l2p.commons.network.SelectorConfig;
import l2p.commons.network.SelectorThread;
import l2p.commons.threading.ThreadPoolManager;
import l2p.commons.util.Util;
import l2p.loginserver.gameservercon.GSConnection;

public class L2LoginServer
{
	private static L2LoginServer _instance;
	private Logger _log = Logger.getLogger(L2LoginServer.class.getName());
	private GSConnection _gameServerListener;
	private SelectorThread<L2LoginClient> _selectorThread;
	public LoginController loginController;

	//private GameServer gameServer;

	public static void main(String[] args)
	{
		_instance = new L2LoginServer();
	}

	public static L2LoginServer getInstance()
	{
		return _instance;
	}

	public L2LoginServer()
	{
		//      Local Constants
		final String LOG_FOLDER = "log"; // Name of folder for log file
		final String LOG_NAME = "./config/log.properties"; // Name of log file

		/*** Main ***/
		// Create log folder
		File logFolder = new File("./", LOG_FOLDER);
		logFolder.mkdir();

		// Create input stream for log file -- or store file data into memory
		InputStream is = null;
		try
		{
			is = new FileInputStream(new File(LOG_NAME));
			LogManager.getLogManager().readConfiguration(is);
			is.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(is != null)
					is.close();
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
		
		ThreadPoolManager.full = false;

		// Load Config
		Config.load();

		// Prepare Database
		try
		{
			L2DatabaseFactory base = new L2DatabaseFactory(Config.DATABASE_DRIVER, Config.DATABASE_URL, Config.DATABASE_LOGIN, Config.DATABASE_PASSWORD, Config.DATABASE_MAX_CONNECTIONS, Config.DATABASE_MAX_IDLE_TIMEOUT, Config.DATABASE_IDLE_TEST_PERIOD, Config.DATABASE_MAX_CONNECTIONS, false);
			L2DatabaseFactory.setInstance(base);
		}
		catch(SQLException e)
		{
			_log.severe("FATAL: Failed initializing database. Reason: " + e.getMessage());
			if(Config.LOGIN_DEBUG)
				e.printStackTrace();
			System.out.println("Server exiting [status=1] / Reason: Failed initializing database. Reason: " + e.getMessage());
			Runtime.getRuntime().exit(1);
		}

		try
		{
			LoginController.load();
		}
		catch(GeneralSecurityException e)
		{
			_log.severe("FATAL: Failed initializing LoginController. Reason: " + e.getMessage());
			if(Config.LOGIN_DEBUG)
				e.printStackTrace();
			System.out.println("Server exiting [status=1] / Reason: Failed initializing LoginController. Reason: " + e.getMessage());
			Runtime.getRuntime().exit(1);
		}

		try
		{
			GameServerTable.load();
		}
		catch(GeneralSecurityException e)
		{
			_log.severe("FATAL: Failed to load GameServerTable. Reason: " + e.getMessage());
			if(Config.LOGIN_DEBUG)
				e.printStackTrace();
			System.out.println("Server exiting [status=1] / Reason: Failed to load GameServerTable. Reason: " + e.getMessage());
			Runtime.getRuntime().exit(1);
		}
		catch(SQLException e)
		{
			_log.severe("FATAL: Failed to load GameServerTable. Reason: " + e.getMessage());
			if(Config.LOGIN_DEBUG)
				e.printStackTrace();
			System.out.println("Server exiting [status=1] / Reason: Failed to load GameServerTable. Reason: " + e.getMessage());
			Runtime.getRuntime().exit(1);
		}

		//this.loadBanFile();

		/* Accepting connections from players */
		Util.waitForFreePorts(Config.LOGIN_HOST, Config.PORT_LOGIN);
		InetAddress ad = null;
		try
		{
			ad = InetAddress.getByName(Config.LOGIN_HOST);
		}
		catch(Exception e)
		{}

		L2LoginPacketHandler loginPacketHandler = new L2LoginPacketHandler();
		SelectorHelper sh = new SelectorHelper();
		SelectorConfig<L2LoginClient> sc = new SelectorConfig<L2LoginClient>(sh);
		try
		{
			_selectorThread = new SelectorThread<L2LoginClient>(sc, loginPacketHandler, sh, sh, sh);
			_selectorThread.setAcceptFilter(sh);
		}
		catch(IOException e)
		{
			_log.severe("FATAL: Failed to open Selector. Reason: " + e.getMessage());
			if(Config.LOGIN_DEBUG)
				e.printStackTrace();
			System.out.println("Server exiting [status=1] / Reason: Failed to open Selector. Reason: " + e.getMessage());
			Runtime.getRuntime().exit(1);
		}

		_gameServerListener = GSConnection.getInstance();
		_gameServerListener.start();
		_log.info("Listening for GameServers on " + Config.GAME_SERVER_LOGIN_HOST + ":" + Config.GAME_SERVER_LOGIN_PORT);

		try
		{
			SelectorThread.setAntiFlood(Config.ANTIFLOOD_ENABLE);
			SelectorThread.setAntiFloodSocketsConf(Config.MAX_UNHANDLED_SOCKETS_PER_IP, Config.UNHANDLED_SOCKET_MIN_TTL);
			_selectorThread.openServerSocket(ad, Config.PORT_LOGIN);
		}
		catch(IOException e)
		{
			_log.severe("FATAL: Failed to open server socket on " + ad + ":" + Config.PORT_LOGIN + ". Reason: " + e.getMessage());
			if(Config.LOGIN_DEBUG)
				e.printStackTrace();
			System.out.println("Server exiting [status=1] / Reason: Failed to open server socket on " + ad + ":" + Config.PORT_LOGIN + ". Reason: " + e.getMessage());
			Runtime.getRuntime().exit(1);
		}
		_selectorThread.start();
		_log.info("Login Server ready on port " + Config.PORT_LOGIN);
		_log.info(IpManager.getInstance().getBannedCount() + " banned IPs defined");

		Shutdown.getInstance().startShutdownH(Config.LRESTART_TIME, true);

		Util.gc(3, 333);
		_log.info("Free memory " + MemoryWatchDog.getMemFreeMb() + " of " + MemoryWatchDog.getMemMaxMb());
	}

	public GSConnection getGameServerListener()
	{
		return _gameServerListener;
	}

	public boolean unblockIp(String ipAddress)
	{
		return loginController.ipBlocked(ipAddress);
	}

	public boolean setPassword(String account, String password)
	{
		return loginController.setPassword(account, password);
	}
}