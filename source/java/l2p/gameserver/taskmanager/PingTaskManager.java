package l2p.gameserver.taskmanager;

import l2p.commons.threading.ThreadPoolManager;
import l2p.gameserver.model.L2ObjectsStorage;
import l2p.gameserver.model.L2Player;

public class PingTaskManager implements Runnable
{
	//==================== SINGLETON BEGIN ====================
	private static volatile PingTaskManager _instance = null;

	public static PingTaskManager getInstance()
	{
		if(_instance == null)
		{
			synchronized(PingTaskManager.class)
			{
				if(_instance == null)
				{
					PingTaskManager _local = new PingTaskManager();
					_instance = _local;
				}
			}
		}
		return _instance;
	}
	//==================== SINGLETON END ====================

	private PingTaskManager()
	{
		ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(this, 30000, 30000);
	}
	
	public void run()
	{
		try
		{
			for(L2Player player : L2ObjectsStorage.getAllPlayersForIterate())
				player.ping(false);
		}
		catch(Exception e) {}
	}
}
