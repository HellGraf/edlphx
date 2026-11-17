package l2p.loginserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.logging.Logger;

public class Config
{
	protected static Logger _log = Logger.getLogger(Config.class.getName());
	
	private static String lastKey = null;
	
	public static final String LOGIN_CONFIGURATION_FILE = "./config/loginserver.properties";
	
	public static String LOGIN_HOST;
	public static int PORT_LOGIN;
	public static int LOGIN_TRY_BEFORE_BAN;

	public static String GAME_SERVER_LOGIN_HOST;
	public static int GAME_SERVER_LOGIN_PORT;

	public static boolean SHOW_LICENCE;

	public static String DATABASE_DRIVER;
	public static String DATABASE_URL;
	public static String DATABASE_LOGIN;
	public static String DATABASE_PASSWORD;
	public static int DATABASE_MAX_CONNECTIONS;
	public static int DATABASE_MAX_IDLE_TIMEOUT;
	public static int DATABASE_IDLE_TEST_PERIOD;

	public static boolean AUTO_CREATE_ACCOUNTS;

	//public static String ANAME_TEMPLATE;
	//public static String APASSWD_TEMPLATE;

	public static int LOGIN_BLOWFISH_KEYS;
	public static int LOGIN_RSA_KEYPAIRS;
	
	public static boolean LOGIN_DEBUG;

	//public static int IP_UPDATE_TIME;
	
	public static boolean LOGIN_GG_CHECK;
	
	public static int LRESTART_TIME;
	
	//public static int SELECTOR_SLEEP_TIME;
	
	public static int LOGIN_WATCHDOG_TIMEOUT;
	
	public static boolean ANTIFLOOD_ENABLE;
	public static int MAX_UNHANDLED_SOCKETS_PER_IP;
	public static int UNHANDLED_SOCKET_MIN_TTL;

	

	public static void load()
	{
		_log.info("loading login config");
		try
		{
			Properties serverSettings = loadPropertiesFile(LOGIN_CONFIGURATION_FILE);
			
			LOGIN_HOST = getProperty(serverSettings, "LoginserverHostname", "127.0.0.1");
			PORT_LOGIN = getIntProperty(serverSettings, "LoginserverPort", 2106);
			LOGIN_TRY_BEFORE_BAN = getIntProperty(serverSettings, "LoginTryBeforeBan", 10);
			
			GAME_SERVER_LOGIN_HOST = getProperty(serverSettings, "LoginHost", "127.0.0.1");
			GAME_SERVER_LOGIN_PORT = getIntProperty(serverSettings, "LoginPort", 9013);

			SHOW_LICENCE = getBooleanProperty(serverSettings, "ShowLicence", true);
			
			DATABASE_DRIVER = getProperty(serverSettings, "Driver", "com.mysql.jdbc.Driver");
			DATABASE_URL = getProperty(serverSettings, "URL", "jdbc:mysql://localhost/l2pdb");
			DATABASE_LOGIN = getProperty(serverSettings, "Login", "root");
			DATABASE_PASSWORD = getProperty(serverSettings, "Password", "");
			DATABASE_MAX_CONNECTIONS = getIntProperty(serverSettings, "MaximumDbConnections", 10);
			DATABASE_MAX_IDLE_TIMEOUT = getIntProperty(serverSettings, "MaxIdleConnectionTimeout", 600);
			DATABASE_IDLE_TEST_PERIOD = getIntProperty(serverSettings, "IdleConnectionTestPeriod", 60);

			AUTO_CREATE_ACCOUNTS = getBooleanProperty(serverSettings, "AutoCreateAccounts", true);

			//ANAME_TEMPLATE = getProperty(serverSettings, "AnameTemplate", "[A-Za-z0-9]{3,14}");
			//APASSWD_TEMPLATE = getProperty(serverSettings, "ApasswdTemplate", "[A-Za-z0-9]{5,16}");

			LOGIN_BLOWFISH_KEYS = getIntProperty(serverSettings, "BlowFishKeys", 20);
			LOGIN_RSA_KEYPAIRS = getIntProperty(serverSettings, "RSAKeyPairs", 10);

			LOGIN_DEBUG = getBooleanProperty(serverSettings, "Debug", false);

			//IP_UPDATE_TIME = getIntProperty(serverSettings, "IpUpdateTime", 15);

			LOGIN_GG_CHECK = getBooleanProperty(serverSettings, "GGCheck", true);

			LRESTART_TIME = getIntProperty(serverSettings, "AutoRestart", -1);

			//SELECTOR_SLEEP_TIME = getIntProperty(serverSettings, "SelectorSleepTime", 5);
			l2p.gameserver.Config.SELECTOR_SLEEP_TIME = 5;

			LOGIN_WATCHDOG_TIMEOUT = getIntProperty(serverSettings, "LoginWatchdogTimeout", 15000);
			
			ANTIFLOOD_ENABLE = getBooleanProperty(serverSettings, "AntiFloodEnable", false);
			MAX_UNHANDLED_SOCKETS_PER_IP = getIntProperty(serverSettings, "MaxUnhandledSocketsPerIP", 5);
			UNHANDLED_SOCKET_MIN_TTL = getIntProperty(serverSettings, "UnhandledSocketsMinTTL", 5000);

		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new Error("Failed to Load " + LOGIN_CONFIGURATION_FILE + " File. [" + lastKey + "]");
		}
	}

	public static Properties loadPropertiesFile(String pathname) throws IOException
	{
		return loadPropertiesFile(new File(pathname));
	}

	public static Properties loadPropertiesFile(File f) throws IOException
	{
		Properties result = new Properties();
		InputStream is = new FileInputStream(f);
		InputStreamReader reader = new InputStreamReader(is, "UTF-8");
		result.load(reader);
		is.close();
		return result;
	}

	private static String getProperty(Properties prop, String name)
	{
		lastKey = name;
		String result = prop.getProperty(name.trim());
		lastKey += " = " + result;
		return result;
	}

	private static String getProperty(Properties prop, String name, String _default)
	{
		String s = getProperty(prop, name);
		return s == null ? _default : s;
	}

	private static int getIntProperty(Properties prop, String name, int _default)
	{
		String s = getProperty(prop, name);
		return s == null ? _default : Integer.parseInt(s.trim());
	}

	private static boolean getBooleanProperty(Properties prop, String name, boolean _default)
	{
		String s = getProperty(prop, name);
		return s == null ? _default : Boolean.parseBoolean(s.trim());
	}
}
