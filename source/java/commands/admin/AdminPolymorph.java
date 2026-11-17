package commands.admin;

import l2p.gameserver.cache.Msg;
import l2p.gameserver.handler.AdminCommandHandler;
import l2p.gameserver.handler.interfaces.IAdminCommandHandler;
import l2p.gameserver.model.L2Object;
import l2p.gameserver.model.L2Player;
import l2p.gameserver.scripts.ScriptFile;

public class AdminPolymorph implements IAdminCommandHandler, ScriptFile
{
	private static String[] _commands =
	{
		"admin_polyself",
		"admin_polymorph",
		"admin_poly",
		"admin_unpolyself",
		"admin_unpolymorph",
		"admin_unpoly"
	};
	
	@Override
	public String[] getAdminCommandList()
	{
		return _commands;
	}

	@Override
	public boolean useAdminCommand(L2Player activeChar, String command, String args)
	{
		if(!activeChar.getPlayerAccess().CanPolymorph)
			return false;

		L2Object target = activeChar.getTarget();

		if(command.equals("admin_polyself") || command.equals("admin_polymorph") || command.equals("admin_poly"))
		{
			if(command.equals("admin_polyself"))
					target = activeChar;

			if(target == null)
			{
				activeChar.sendPacket(Msg.INVALID_TARGET);
				return false;
			}
			try
			{
				String[] wordList = args.split("\\s+");
				
				int id = Integer.parseInt(wordList[0]);
				int type = L2Object.POLY_NPC;
				if(wordList.length > 1 && (wordList[1].equalsIgnoreCase("item") || wordList[1].equalsIgnoreCase("i")))
					type = L2Object.POLY_ITEM;
				target.setPolyInfo(type, id);
			}
			catch(Exception e)
			{
				activeChar.sendMessage("USAGE: //poly id [type:npc|item]");
				return false;
			}
		}
		else if(command.equals("admin_unpolyself") || command.equals("admin_unpolymorph") || command.equals("admin_unpoly"))
		{
			if(command.equals("admin_unpolyself"))
				target = activeChar;
			
			if(target == null)
			{
				activeChar.sendPacket(Msg.INVALID_TARGET);
				return false;
			}
			target.setPolyInfo(0, 0);
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
