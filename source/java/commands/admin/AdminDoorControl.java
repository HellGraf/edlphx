package commands.admin;

import l2p.gameserver.cache.Msg;
import l2p.gameserver.handler.AdminCommandHandler;
import l2p.gameserver.handler.interfaces.IAdminCommandHandler;
import l2p.gameserver.model.L2Object;
import l2p.gameserver.model.L2Player;
import l2p.gameserver.model.instances.L2DoorInstance;
import l2p.gameserver.scripts.ScriptFile;
import l2p.gameserver.tables.DoorTable;

public class AdminDoorControl implements IAdminCommandHandler, ScriptFile
{
	private static String[] _commands =
	{
		"admin_open",
		"admin_close",
		"admin_openall",
		"admin_closeall"
	};
	
	@Override
	public String[] getAdminCommandList()
	{
		return _commands;
	}

	@Override
	public boolean useAdminCommand(L2Player activeChar, String command, String args)
	{
		

		if(!activeChar.getPlayerAccess().Door)
			return false;

		if(command.equals("admin_open"))
		{
			if(args.length() > 0)
				DoorTable.getInstance().getDoor(Integer.parseInt(args)).openMe();
			else
			{
				L2Object target = activeChar.getTarget();
				if(target.isDoor())
					((L2DoorInstance) target).openMe();
				else
					activeChar.sendPacket(Msg.INVALID_TARGET);
			}
		}
		else if(command.equals("admin_close"))
		{
			if(args.length() > 0)
					DoorTable.getInstance().getDoor(Integer.parseInt(args)).closeMe();
				else
				{
					L2Object target = activeChar.getTarget();
					if(target.isDoor())
						((L2DoorInstance) target).closeMe();
					else
						activeChar.sendPacket(Msg.INVALID_TARGET);
				}
		}
		else if(command.equals("admin_closeall"))
		{
			for(L2DoorInstance door : DoorTable.getInstance().getDoors())
				door.closeMe();
		}
		else if(command.equals("admin_openall"))
		{
			for(L2DoorInstance door : DoorTable.getInstance().getDoors())
				door.openMe();
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
