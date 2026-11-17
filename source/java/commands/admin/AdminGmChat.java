package commands.admin;

import l2p.gameserver.handler.AdminCommandHandler;
import l2p.gameserver.handler.interfaces.IAdminCommandHandler;
import l2p.gameserver.model.L2Player;
import l2p.gameserver.network.serverpackets.Say2;
import l2p.gameserver.scripts.ScriptFile;
import l2p.gameserver.tables.GmListTable;

public class AdminGmChat implements IAdminCommandHandler, ScriptFile
{
	private static String[] _commands =
	{
		"admin_gmchat",
		"admin_snoop"
	};
	
	@Override
	public String[] getAdminCommandList()
	{
		return _commands;
	}

	public boolean useAdminCommand(L2Player activeChar, String command, String args)
	{
		if(!activeChar.getPlayerAccess().CanAnnounce)
			return false;

		if(command.equals("admin_gmchat"))
		{
			try
			{
				Say2 cs = new Say2(0, 9, activeChar.getName(), args);
				GmListTable.broadcastToGMs(cs);
			}
			catch(StringIndexOutOfBoundsException e)
			{}
		}
		else if(command.equals("admin_snoop"))
		{
			/**
			L2Object target = activeChar.getTarget();
			if(target == null)
			{
				activeChar.sendMessage("You must select a target.");
				return false;
			}
			if(!target.isPlayer)
			{
				activeChar.sendMessage("Target must be a player.");
				return false;
			}
			L2Player player = (L2Player) target;
			player.addSnooper(activeChar);
			activeChar.addSnooped(player);
			*/
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
