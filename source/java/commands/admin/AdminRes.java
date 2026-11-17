package commands.admin;

import l2p.commons.util.Log;
import l2p.gameserver.cache.Msg;
import l2p.gameserver.handler.AdminCommandHandler;
import l2p.gameserver.handler.interfaces.IAdminCommandHandler;
import l2p.gameserver.model.L2Character;
import l2p.gameserver.model.L2Object;
import l2p.gameserver.model.L2Player;
import l2p.gameserver.model.L2World;
import l2p.gameserver.network.serverpackets.Revive;
import l2p.gameserver.network.serverpackets.SocialAction;
import l2p.gameserver.scripts.ScriptFile;
import l2p.gameserver.taskmanager.DecayTaskManager;

@SuppressWarnings("unused")
public class AdminRes implements IAdminCommandHandler, ScriptFile
{
	private static String[] _commands =
	{
		"admin_res"
	};
	
	@Override
	public String[] getAdminCommandList()
	{
		return _commands;
	}

	public boolean useAdminCommand(L2Player activeChar, String command, String args)
	{
		if(!activeChar.getPlayerAccess().Res)
			return false;

		if(command.equals("admin_res"))
			handleRes(activeChar, args);

		return true;
	}
	
	private void handleRes(L2Player activeChar, String args)
	{
		if(args.length() > 0)
		{
			try
			{
				int radius = Math.max(Integer.parseInt(args), 100);
				for(L2Character character : activeChar.getAroundCharacters(radius, 200))
				{
					character.setCurrentHpMp(character.getMaxHp(), character.getMaxMp(), true);
					character.setCurrentCp(character.getMaxCp());
					if(character.isPlayer())
						((L2Player) character).restoreExp();
					// If the target is an NPC, then abort it's auto decay and respawn.
					else
						DecayTaskManager.getInstance().cancelDecayTask(character);

					character.broadcastPacket(new SocialAction(character.getObjectId(), SocialAction.LEVEL_UP));
					character.broadcastPacket(new Revive(character));
					character.doRevive();
				}
				activeChar.sendMessage("Resurrected within " + radius + " unit radius.");
				return;
			}
			catch(NumberFormatException e)
			{
				activeChar.sendMessage("USAGE: //res [radius]");
				return;
			}
		}
		else
		{
			L2Character target = (L2Character)activeChar.getTarget();
			if(target == null)
				target = activeChar;
			
			if(target.isDead())
			{
				target.setCurrentHpMp(target.getMaxHp(), target.getMaxMp(), true);
				target.setCurrentCp(target.getMaxCp());
				if(target.isPlayer())
					((L2Player)target).restoreExp();
				target.broadcastPacket(new SocialAction(target.getObjectId(), 15));
				target.broadcastPacket(new Revive(target));
				target.doRevive();
			}
		}
	}

	public void onLoad()
	{
		AdminCommandHandler.getInstance().registerHandler(this);
	}

	public void onReload(){}

	public void onShutdown(){}
}
