package commands.admin;

import l2p.gameserver.handler.AdminCommandHandler;
import l2p.gameserver.handler.interfaces.IAdminCommandHandler;
import l2p.gameserver.model.L2Object;
import l2p.gameserver.model.L2Player;
import l2p.gameserver.model.L2World;
import l2p.gameserver.scripts.ScriptFile;

public class AdminTarget implements IAdminCommandHandler, ScriptFile
{
	private static String[] _commands =
	{
		"admin_target"
	};

	@Override
	public String[] getAdminCommandList()
	{
		return _commands;
	}

	@Override
	public boolean useAdminCommand(L2Player activeChar, String command, String args)
	{
		if(!activeChar.getPlayerAccess().CanViewChar)
			return false;

		if(command.equals("admin_target"))
		{
			try
			{
				L2Object obj = L2World.getPlayer(args);
				if(obj != null && obj.isPlayer())
					obj.onAction(activeChar, false);
				else
					activeChar.sendMessage("Player " + args + " not found");
			}
			catch(IndexOutOfBoundsException e)
			{
				activeChar.sendMessage("Please specify correct name.");
			}
			return true;
		}
		else return false;
	}

	public void onLoad()
	{
		AdminCommandHandler.getInstance().registerHandler(this);
	}

	public void onReload(){}

	public void onShutdown(){}
}
