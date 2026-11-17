package commands.voiced;

import l2p.gameserver.handler.VoicedCommandHandler;
import l2p.gameserver.handler.interfaces.IVoicedCommandHandler;
import l2p.gameserver.model.L2Player;
import l2p.gameserver.network.serverpackets.ExJumpToLocation;
import l2p.gameserver.scripts.ScriptFile;

public class Jump implements IVoicedCommandHandler, ScriptFile
{
	private static String[] _voicedCommands = { "jump" };

	public boolean useVoicedCommand(L2Player activeChar, String command, String target)
	{
		if(command.startsWith("jump"))
			activeChar.sendPacket(new ExJumpToLocation(activeChar.getObjectId(), activeChar.getLoc(), activeChar.getLoc()));
		return true;
	}

	public String[] getVoicedCommandList()
	{
		return _voicedCommands;
	}

	public void onLoad()
	{
		VoicedCommandHandler.getInstance().registerHandler(this);
	}
	
	public void onReload(){}
	
	public void onShutdown(){}
}