package commands.user;

import l2p.gameserver.cache.Msg;
import l2p.gameserver.handler.UserCommandHandler;
import l2p.gameserver.handler.interfaces.IUserCommandHandler;
import l2p.gameserver.model.L2Player;
import l2p.gameserver.model.entity.olympiad.Olympiad;
import l2p.gameserver.network.serverpackets.SystemMessage;
import l2p.gameserver.scripts.ScriptFile;

/**
 * Support for /olympiadstat command
 */
public class OlympiadStat implements IUserCommandHandler, ScriptFile
{
	private static final int[] COMMAND_IDS = { 109 };

	public void useUserCommand(L2Player player, int id)
	{
		if(!player.isNoble())
			player.sendPacket(Msg.THIS_COMMAND_CAN_ONLY_BE_USED_BY_A_NOBLESSE);
		else
		{
			SystemMessage sm = new SystemMessage(SystemMessage.THE_CURRENT_FOR_THIS_OLYMPIAD_IS_S1_WINS_S2_DEFEATS_S3_YOU_HAVE_EARNED_S4_OLYMPIAD_POINTS);
			sm.addNumber(Olympiad.getCompetitionDone(player.getObjectId()));
			sm.addNumber(Olympiad.getCompetitionWin(player.getObjectId()));
			sm.addNumber(Olympiad.getCompetitionLoose(player.getObjectId()));
			sm.addNumber(Olympiad.getNoblePoints(player.getObjectId()));
			player.sendPacket(sm);
		}
	}

	public int[] getUserCommandList()
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