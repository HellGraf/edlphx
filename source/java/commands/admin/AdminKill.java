package commands.admin;

import l2p.gameserver.cache.Msg;
import l2p.gameserver.handler.AdminCommandHandler;
import l2p.gameserver.handler.interfaces.IAdminCommandHandler;
import l2p.gameserver.model.L2Character;
import l2p.gameserver.model.L2Object;
import l2p.gameserver.model.L2ObjectsStorage;
import l2p.gameserver.model.L2Player;
import l2p.gameserver.model.L2World;
import l2p.gameserver.model.instances.L2NpcInstance;
import l2p.gameserver.scripts.ScriptFile;

public class AdminKill implements IAdminCommandHandler, ScriptFile
{
	private static String[] _commands =
	{
		"admin_kill",
		"admin_kill_all_mobs"
	};
	
	@Override
	public String[] getAdminCommandList()
	{
		return _commands;
	}
	
	public boolean useAdminCommand(L2Player activeChar, String command, String args)
	{
		if(!activeChar.getPlayerAccess().CanEditNPC)
			return false;

		if(command.equals("admin_kill"))
		{
				if(args.length() > 0)
					handleKill(activeChar, args);
				else
					handleKill(activeChar, null);
		}
		else if(command.equals("admin_kill_all_mobs"))
		{
			int iKillSuccess = 0,
			iKillFail = 0;
			for(L2NpcInstance npc : L2ObjectsStorage.getAllNpcsForIterate())
			{
				if(!npc.isMonster() || npc.isDead() || npc.isRaid())
					continue;
				npc.reduceCurrentHp(npc.getMaxHp() + npc.getMaxCp() + 1, activeChar, null, true, true, false, false);
				if(npc.isDead())
					iKillSuccess++;
				else
					iKillFail++;
			}
			activeChar.sendMessage("Killed " + iKillSuccess + " mobs, failed to kill: " + iKillFail);
		}

		return true;
	}

	private void handleKill(L2Player activeChar, String player)
	{
		L2Object obj = activeChar.getTarget();
		if(player != null)
		{
			L2Player plyr = L2World.getPlayer(player);
			if(plyr != null)
				obj = plyr;
			else
			{
				int radius = Math.max(Integer.parseInt(player), 100);
				for(L2Character character : activeChar.getAroundCharacters(radius, 200))
					character.reduceCurrentHp(character.getMaxHp() + character.getMaxCp() + 1, activeChar, null, true, true, false, false);
				activeChar.sendMessage("Killed within " + radius + " unit radius.");
				return;
			}
		}

		if(obj != null && obj.isCharacter())
		{
			L2Character target = (L2Character) obj;
			target.reduceCurrentHp(target.getMaxHp() + target.getMaxCp() + 1, activeChar, null, true, true, false, false);
		}
		else
			activeChar.sendPacket(Msg.INVALID_TARGET);
	}

	public void onLoad()
	{
		AdminCommandHandler.getInstance().registerHandler(this);
	}
	
	public void onReload(){}
	
	public void onShutdown(){}
}