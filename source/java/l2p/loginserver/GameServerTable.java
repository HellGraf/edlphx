package l2p.loginserver;

import java.io.File;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.spec.RSAKeyGenParameterSpec;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import l2p.commons.util.GArray;
import l2p.commons.util.Rnd;
import l2p.commons.xml.XMLDocumentFactory;
import l2p.loginserver.gameservercon.GameServerInfo;

public class GameServerTable
{
	private static Logger _log = Logger.getLogger(GameServerTable.class.getName());
	private static GameServerTable _instance;

	// Server Names Config
	static Map<Integer, String> _serverNames = new HashMap<Integer, String>();

	// Game Server Table
	private final Map<Integer, GameServerInfo> _gameServerTable = new ConcurrentHashMap<Integer, GameServerInfo>();

	// RSA Config
	private KeyPair[] _keyPairs;

	public static void load() throws SQLException, GeneralSecurityException
	{
		if(_instance == null)
			_instance = new GameServerTable();
		else
			throw new IllegalStateException("Load can only be invoked a single time.");
	}

	public static GameServerTable getInstance()
	{
		return _instance;
	}

	public GameServerTable() throws SQLException, NoSuchAlgorithmException, InvalidAlgorithmParameterException
	{
		loadServerNames();
		_log.info("Loaded " + _serverNames.size() + " server names");
		loadRSAKeys();
		_log.info("Cached " + _keyPairs.length + " RSA keys for Game Server communication.");
	}

	private void loadRSAKeys() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException
	{
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
		RSAKeyGenParameterSpec spec = new RSAKeyGenParameterSpec(512, RSAKeyGenParameterSpec.F4);
		keyGen.initialize(spec);

		_keyPairs = new KeyPair[Config.LOGIN_RSA_KEYPAIRS];
		for(int i = 0; i < _keyPairs.length; i++)
			_keyPairs[i] = keyGen.genKeyPair();
	}

	private void loadServerNames()
	{
		try
		{
			final File f = new File("servername.xml");
			final Document doc = XMLDocumentFactory.getInstance().loadDocument(f);
			
			Node n = doc.getFirstChild();
			for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
			{
				if (d.getNodeName().equalsIgnoreCase("server"))
				{
					NamedNodeMap attrs = d.getAttributes();
					
					int id = Integer.parseInt(attrs.getNamedItem("id").getNodeValue());
					String name = attrs.getNamedItem("name").getNodeValue();
					
					_serverNames.put(id, name);
				}
			}
		}
		catch (Exception e)
		{
			_log.warning("GameServerTable: servername.xml could not be loaded.");
		}
	}

	public Map<Integer, GameServerInfo> getRegisteredGameServers()
	{
		return _gameServerTable;
	}

	public GameServerInfo getRegisteredGameServerById(int id)
	{
		return _gameServerTable.get(id);
	}

	public boolean hasRegisteredGameServerOnId(int id)
	{
		return _gameServerTable.containsKey(id);
	}

	public boolean registerWithFirstAvailableId(GameServerInfo gsi)
	{
		for(Integer id : _serverNames.keySet())
		{
			if(register(id, gsi))
				return true;
		}
		return false;
	}

	public boolean register(int id, GameServerInfo gsi)
	{
		if(!_gameServerTable.containsKey(id))
		{
			_gameServerTable.put(id, gsi);
			gsi.setId(id);
			return true;
		}
		return false;
	}

	public String getServerNameById(int id)
	{
		return getServerNames().get(id);
	}

	public Map<Integer, String> getServerNames()
	{
		return _serverNames;
	}

	public KeyPair getKeyPair()
	{
		return _keyPairs[Rnd.get(_keyPairs.length)];
	}

	public GArray<String> status()
	{
		GArray<String> str = new GArray<String>();
		str.add("There are " + _gameServerTable.size() + " GameServers");
		for(GameServerInfo gsi : _gameServerTable.values())
			str.add(gsi.toString());
		return str;
	}
}
