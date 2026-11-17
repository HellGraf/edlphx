package commands.user;

import java.util.Calendar;

import l2p.gameserver.handler.UserCommandHandler;
import l2p.gameserver.handler.interfaces.IUserCommandHandler;
import l2p.gameserver.model.L2Player;
import l2p.gameserver.network.serverpackets.SystemMessage;
import l2p.gameserver.scripts.ScriptFile;

/**
 * Support for /mybirthday command
 */
public class MyBirthday implements IUserCommandHandler, ScriptFile
{
	private static final int[] COMMAND_IDS = { 126 };

	public void useUserCommand(L2Player player, int id)
	{
		if(player.getCreateTime() == 0)
			return;

		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(player.getCreateTime());

		player.sendPacket(new SystemMessage(SystemMessage.C1S_CHARACTER_BIRTHDAY_IS_S3S4S2).addString(player.getName()).addNumber(c.get(Calendar.YEAR)).addNumber(c.get(Calendar.MONTH) + 1).addNumber(c.get(Calendar.DAY_OF_MONTH)));
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