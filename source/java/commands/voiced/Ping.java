package commands.voiced;

import l2p.gameserver.handler.VoicedCommandHandler;
import l2p.gameserver.handler.interfaces.IVoicedCommandHandler;
import l2p.gameserver.model.L2Player;
import l2p.gameserver.scripts.ScriptFile;

public class Ping implements IVoicedCommandHandler, ScriptFile
{
	private static String[] _voicedCommands = { "ping" };

	public boolean useVoicedCommand(L2Player activeChar, String command, String target)
	{
		if(command.startsWith("ping"))
			activeChar.ping(true);
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