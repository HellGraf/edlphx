package commands.admin;

import l2p.gameserver.cache.Msg;
import l2p.gameserver.handler.AdminCommandHandler;
import l2p.gameserver.handler.interfaces.IAdminCommandHandler;
import l2p.gameserver.model.L2Object;
import l2p.gameserver.model.L2Player;
import l2p.gameserver.model.L2Spawn;
import l2p.gameserver.model.instances.L2NpcInstance;
import l2p.gameserver.scripts.ScriptFile;

public class AdminDelete implements IAdminCommandHandler, ScriptFile
{
	private static String[] _commands =
	{
		"admin_delete"
	};
	
	@Override
	public String[] getAdminCommandList()
	{
		return _commands;
	}

	@Override
	public boolean useAdminCommand(L2Player activeChar, String command, String args)
	{
		if(!activeChar.getPlayerAccess().CanEditNPC)
			return false;

		if(command.equals("admin_delete"))
		{
			L2Object obj = activeChar.getTarget();
			if(obj != null && obj.isNpc())
			{
				L2NpcInstance target = (L2NpcInstance) obj;
				target.deleteMe();

				L2Spawn spawn = target.getSpawn();
				if(spawn != null)
					spawn.stopRespawn();
			}
			else activeChar.sendPacket(Msg.INVALID_TARGET);
		}

		return true;
	}

	public void onLoad()
	{
		AdminCommandHandler.getInstance().registerHandler(this);
	}

	public void onReload(){}

	public void onShutdown(){}
}
