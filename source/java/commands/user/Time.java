package commands.user;

import l2p.gameserver.GameTimeController;
import l2p.gameserver.handler.UserCommandHandler;
import l2p.gameserver.handler.interfaces.IUserCommandHandler;
import l2p.gameserver.model.L2Player;
import l2p.gameserver.network.serverpackets.SystemMessage;
import l2p.gameserver.scripts.ScriptFile;

public class Time implements IUserCommandHandler, ScriptFile
{
	private static final int[] COMMAND_IDS = { 77 };

	public void useUserCommand(L2Player player, int id)
	{
		int t = GameTimeController.getInstance().getGameTime();
		int h = t / 60 % 24;
		int m = t % 60;

		SystemMessage sm;
		if(h >= 0 && h < 6)
			sm = new SystemMessage(SystemMessage.THE_CURRENT_TIME_IS_S1S2_IN_THE_NIGHT);
		else
			sm = new SystemMessage(SystemMessage.THE_CURRENT_TIME_IS_S1S2_IN_THE_DAY);
		sm.addNumber(h).addNumber(m);
		player.sendPacket(sm);
	}

	public final int[] getUserCommandList()
	{
		return COMMAND_IDS;
	}

	public void onLoad()
	{
		UserCommandHandler.getInstance().registerHandler(this);
	}

	public void onReload()
	{}

	public void onShutdown()
	{}
}
