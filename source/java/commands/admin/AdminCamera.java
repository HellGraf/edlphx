package commands.admin;

import l2p.gameserver.handler.AdminCommandHandler;
import l2p.gameserver.handler.interfaces.IAdminCommandHandler;
import l2p.gameserver.model.L2Player;
import l2p.gameserver.network.serverpackets.CameraMode;
import l2p.gameserver.network.serverpackets.SpecialCamera;
import l2p.gameserver.scripts.ScriptFile;

public class AdminCamera implements IAdminCommandHandler, ScriptFile
{
	private static String[] _commands =
	{
		"admin_freelook",
		"admin_cinematic",
		"admin_scene"
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

		if(command.equals("admin_freelook"))
		{
			try
			{
				int mode = Integer.parseInt(args);
				if(mode == 1)
				{
					activeChar.setInvisible(true);
					activeChar.setIsInvul(true);
					activeChar.setNoChannel(-1);
					activeChar.setFlying(true);
				}
				else
				{
					activeChar.setInvisible(false);
					activeChar.setIsInvul(false);
					activeChar.setNoChannel(0);
					activeChar.setFlying(false);
				}
				activeChar.sendPacket(new CameraMode(mode));
			}
			catch(Exception e)
			{
				activeChar.sendMessage("Usage: //freelook 1 or //freelook 0");
				return false;
			}
		}
		else if(command.equals("admin_cinematic"))
		{
			String[] wordList = args.split("\\s+");
			
			int id = Integer.parseInt(wordList[0]);
			int dist = Integer.parseInt(wordList[1]);
			int yaw = Integer.parseInt(wordList[2]);
			int pitch = Integer.parseInt(wordList[3]);
			int time = Integer.parseInt(wordList[4]);
			int duration = Integer.parseInt(wordList[5]);
			activeChar.sendPacket(new SpecialCamera(id, dist, yaw, pitch, time, duration));
		}
		else if(command.equals("admin_scene"))
		{
			int id = Integer.parseInt(args);
			activeChar.showQuestMovie(id);
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
