package commands.admin;

import l2p.gameserver.handler.AdminCommandHandler;
import l2p.gameserver.handler.interfaces.IAdminCommandHandler;
import l2p.gameserver.instancemanager.CursedWeaponsManager;
import l2p.gameserver.model.CursedWeapon;
import l2p.gameserver.model.L2Object;
import l2p.gameserver.model.L2Player;
import l2p.gameserver.model.items.L2ItemInstance;
import l2p.gameserver.scripts.ScriptFile;
import l2p.gameserver.tables.ItemTable;

public class AdminCursedWeapons implements IAdminCommandHandler, ScriptFile
{
	private static String[] _commands =
	{
		"admin_cw_info",
		"admin_cw_remove",
		"admin_cw_goto",
		"admin_cw_reload",
		"admin_cw_add",
		"admin_cw_drop"
	};
	
	@Override
	public String[] getAdminCommandList()
	{
		return _commands;
	}

	public boolean useAdminCommand(L2Player activeChar, String command, String args)
	{
		if(!activeChar.getPlayerAccess().Menu)
			return false;

		CursedWeaponsManager cwm = CursedWeaponsManager.getInstance();

		CursedWeapon cw = null;
		
		if(command.equals("admin_cw_remove") || command.equals("admin_cw_goto") || command.equals("admin_cw_add") || command.equals("admin_cw_drop"))
		{
			try
			{
				for(CursedWeapon cwp : CursedWeaponsManager.getInstance().getCursedWeapons())
				{
					if(cwp.getName().toLowerCase().contains(args.toLowerCase()))
					{
						cw = cwp;
						break;
					}
				}
			}
			catch(Exception e)
			{
				
			}
			if(cw == null)
			{
				activeChar.sendMessage("Неизвестный id");
				return false;
			}
		}

		if(command.equals("admin_cw_info"))
		{
			activeChar.sendMessage("======= Cursed Weapons: =======");
			for(CursedWeapon c : cwm.getCursedWeapons())
			{
				activeChar.sendMessage("> " + c.getName() + " (" + c.getItemId() + ")");
				if(c.isActivated())
				{
					L2Player pl = c.getPlayer();
					activeChar.sendMessage("  Player holding: " + pl.getName());
					activeChar.sendMessage("  Player karma: " + c.getPlayerKarma());
					activeChar.sendMessage("  Time Remaining: " + c.getTimeLeft() / 60000 + " min.");
					activeChar.sendMessage("  Kills : " + c.getNbKills());
				}
				else if(c.isDropped())
				{
					activeChar.sendMessage("  Lying on the ground.");
					activeChar.sendMessage("  Time Remaining: " + c.getTimeLeft() / 60000 + " min.");
					activeChar.sendMessage("  Kills : " + c.getNbKills());
				}
				else
					activeChar.sendMessage("  Don't exist in the world.");
			}
		}
		else if(command.equals("admin_cw_reload"))
		{
			cwm.reload();
			activeChar.sendMessage("Cursed weapons reloaded.");
		}
		else if(command.equals("admin_cw_remove"))
		{
			if(cw == null)
				return false;
			CursedWeaponsManager.getInstance().endOfLife(cw);
		}
		else if(command.equals("admin_cw_goto"))
		{
			if(cw == null)
				return false;
			activeChar.teleToLocation(cw.getLoc());
		}
		else if(command.equals("admin_cw_add"))
		{
			if(cw == null)
				return false;
			if(cw.isActive())
				activeChar.sendMessage("This cursed weapon is already active.");
			else
			{
				L2Object target = activeChar.getTarget();
				if(target != null && target.isPlayer() && !((L2Player) target).isInOlympiadMode())
				{
					L2Player player = (L2Player) target;
					L2ItemInstance item = ItemTable.getInstance().createItem(cw.getItemId());
					cwm.activate(player, player.getInventory().addItem(item));
					cwm.showUsageTime(player, cw);
				}
			}
		}
		else if(command.equals("admin_cw_drop"))
		{
			if(cw == null)
				return false;
			if(cw.isActive())
				activeChar.sendMessage("This cursed weapon is already active.");
			else
			{
				L2Object target = activeChar.getTarget();
				if(target != null && target.isPlayer() && !((L2Player) target).isInOlympiadMode())
				{
					L2Player player = (L2Player) target;
					cw.create(null, player, true);
				}
			}
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
