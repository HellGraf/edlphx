package commands.admin;

import l2p.gameserver.cache.Msg;
import l2p.gameserver.handler.AdminCommandHandler;
import l2p.gameserver.handler.interfaces.IAdminCommandHandler;
import l2p.gameserver.model.L2Character;
import l2p.gameserver.model.L2Object;
import l2p.gameserver.model.L2Player;
import l2p.gameserver.model.L2World;
import l2p.gameserver.scripts.ScriptFile;

public class AdminHeal implements IAdminCommandHandler, ScriptFile
{
	private static String[] _commands =
	{
		"admin_heal"
	};

	@Override
	public String[] getAdminCommandList()
	{
		return _commands;
	}

	@Override
	public boolean useAdminCommand(L2Player activeChar, String command, String args)
	{
		if(!activeChar.getPlayerAccess().Heal)
			return false;

		if(command.equals("admin_heal"))
		{
			if(args.length() > 0)
				handleRes(activeChar, args);
			else
				handleRes(activeChar, null);
		}

		return true;
	}

	private void handleRes(L2Player activeChar, String player)
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
				{
					character.setCurrentHpMp(character.getMaxHp(), character.getMaxMp());
					if(character.isPlayer())
						character.setCurrentCp(character.getMaxCp());
				}
				activeChar.sendMessage("Healed within " + radius + " unit radius.");
				return;
			}
		}

		if(obj == null)
			obj = activeChar;

		if(obj.isCharacter())
		{
			L2Character target = (L2Character) obj;
			target.setCurrentHpMp(target.getMaxHp(), target.getMaxMp());
			if(target.isPlayer())
				target.setCurrentCp(target.getMaxCp());
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