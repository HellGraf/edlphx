package commands.admin;

import l2p.gameserver.cache.Msg;
import l2p.gameserver.handler.AdminCommandHandler;
import l2p.gameserver.handler.interfaces.IAdminCommandHandler;
import l2p.gameserver.model.L2Character;
import l2p.gameserver.model.L2Object;
import l2p.gameserver.model.L2Player;
import l2p.gameserver.model.base.Experience;
import l2p.gameserver.model.instances.L2PetInstance;
import l2p.gameserver.scripts.ScriptFile;
import l2p.gameserver.tables.PetDataTable;

public class AdminLevel implements IAdminCommandHandler, ScriptFile
{
	private static String[] _commands =
	{
		"admin_add_level",
		"admin_addLevel",
		"admin_set_level",
		"admin_setLevel"
	};
	
	@Override
	public String[] getAdminCommandList()
	{
		return _commands;
	}

	@Override

	public boolean useAdminCommand(L2Player activeChar, String command, String args)
	{
		if(!activeChar.getPlayerAccess().CanEditChar)
			return false;

		L2Object target = activeChar.getTarget();
		if(target == null || !(target.isPlayer() || target.isPet()))
		{
			activeChar.sendPacket(Msg.INVALID_TARGET);
			return false;
		}
		int level;

		if(command.equals("admin_add_level") || command.equals("admin_addLevel"))
		{
			try
			{
				level = Integer.parseInt(args);
			}
			catch(NumberFormatException e)
			{
				activeChar.sendMessage("USAGE: //addLevel level");
				return false;
			}
			setLevel(activeChar, target, level + ((L2Character) target).getLevel());
		}
		else if(command.equals("admin_set_level") || command.equals("admin_setLevel"))
		{
			try
			{
				level = Integer.parseInt(args);
			}
			catch(NumberFormatException e)
			{
				activeChar.sendMessage("USAGE: //setLevel level");
				return false;
			}
			setLevel(activeChar, target, level);
		}

		return true;
	}
	
	private void setLevel(L2Player activeChar, L2Object target, int level)
	{
		if(target == null || !(target.isPlayer() || target.isPet()))
		{
			activeChar.sendPacket(Msg.INVALID_TARGET);
			return;
		}
		if(level < 1 || level > Experience.getMaxLevel())
		{
			activeChar.sendMessage("You must specify level 1 - " + Experience.getMaxLevel());
			return;
		}
		if(target.isPlayer())
		{
			Long exp_add = Experience.LEVEL[level] - ((L2Player) target).getExp();
			((L2Player) target).addExpAndSp(exp_add, 0, false, false);
			return;
		}
		if(target.isPet())
		{
			Long exp_add = PetDataTable.getInstance().getInfo(((L2PetInstance) target).getNpcId(), level).getExp() - ((L2PetInstance) target).getExp();
			((L2PetInstance) target).addExpAndSp(exp_add, 0, false, true);
			((L2PetInstance) target).startRegeneration();
		}
	}

	public void onLoad()
	{
		AdminCommandHandler.getInstance().registerHandler(this);
	}
	
	public void onReload(){}
	
	public void onShutdown(){}
}