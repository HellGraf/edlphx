package commands.admin;

import l2p.gameserver.handler.AdminCommandHandler;
import l2p.gameserver.handler.interfaces.IAdminCommandHandler;
import l2p.gameserver.model.L2Object;
import l2p.gameserver.model.L2Player;
import l2p.gameserver.scripts.ScriptFile;
import l2p.gameserver.util.DummyDeadlock.ReentrantDeadlock;
import l2p.gameserver.util.DummyDeadlock.SynchronizedDeadlock;

public class AdminDebug implements IAdminCommandHandler, ScriptFile
{
	private static String[] _commands =
	{
		"admin_dump_obj",
		"admin_debug_deadlock_sync",
		"admin_debug_deadlock_lock"
	};
	
	@Override
	public String[] getAdminCommandList()
	{
		return _commands;
	}

	public boolean useAdminCommand(L2Player activeChar, String command, String args)
	{
		if(!activeChar.isGM())
			return false;

		if(command.equals("admin_dump_obj"))
		{
			L2Object target = activeChar.getTarget();
			if(target == null)
				activeChar.sendMessage("No Target");
			else
			{
				System.out.println(target.dump());
				activeChar.sendMessage("Object dumped to stdout");
			}
		}
		else if(command.equals("admin_debug_deadlock_sync"))
		{
			activeChar.sendMessage("Testing Synchronized Deadlock");
			new SynchronizedDeadlock().start();
		}
		else if(command.equals("admin_debug_deadlock_lock"))
		{
			activeChar.sendMessage("Testing Reentrant Deadlock");
			new ReentrantDeadlock().start();
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
