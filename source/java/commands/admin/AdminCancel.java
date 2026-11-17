package commands.admin;

import l2p.commons.util.Log;
import l2p.gameserver.cache.Msg;
import l2p.gameserver.handler.AdminCommandHandler;
import l2p.gameserver.handler.interfaces.IAdminCommandHandler;
import l2p.gameserver.model.L2Character;
import l2p.gameserver.model.L2Object;
import l2p.gameserver.model.L2Player;
import l2p.gameserver.model.L2World;
import l2p.gameserver.scripts.ScriptFile;

public class AdminCancel implements IAdminCommandHandler, ScriptFile
{
	private static String[] _commands =
	{
		"admin_cancel"
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

		if(command.equals("admin_cancel"))
			handleCancel(activeChar, args.length() > 0 ? args : null);

		return true;
	}

	private void handleCancel(L2Player activeChar, String targetName)
	{
		L2Object obj = activeChar.getTarget();
		if(targetName != null)
		{
			L2Player plyr = L2World.getPlayer(targetName);
			if(plyr != null)
				obj = plyr;
			else
				try
				{
					int radius = Math.max(Integer.parseInt(targetName), 100);
					for(L2Character character : activeChar.getAroundCharacters(radius, 200))
						character.getEffectList().stopAllEffects();
					activeChar.sendMessage("Apply Cancel within " + radius + " unit radius.");
					return;
				}
				catch(NumberFormatException e)
				{
					activeChar.sendMessage("Enter valid player name or radius");
					return;
				}
		}

		if(obj == null)
			obj = activeChar;
		if(obj.isCharacter())
		{
			((L2Character) obj).getEffectList().stopAllEffects();
			Log.LogCommand(activeChar, Log.Adm_DelSkill, "admin_cancel", 1);
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