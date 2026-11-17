package commands.user;

import java.text.SimpleDateFormat;

import l2p.gameserver.cache.Msg;
import l2p.gameserver.handler.UserCommandHandler;
import l2p.gameserver.handler.interfaces.IUserCommandHandler;
import l2p.gameserver.instancemanager.InstancedZoneManager;
import l2p.gameserver.model.L2Player;
import l2p.gameserver.network.serverpackets.SystemMessage;
import l2p.gameserver.scripts.Functions;
import l2p.gameserver.scripts.ScriptFile;
import l2p.gameserver.util.Files;

/**
 * Support for commands:
 * /clanpenalty
 * /instancezone 
 */
public class Penalty extends Functions implements IUserCommandHandler, ScriptFile
{
	private static final int[] COMMAND_IDS = { 100, 114 };

	public void useUserCommand(L2Player player, int id)
	{
		if(id == 100)
		{
			long leaveClan = 0;
			if(player.getLeaveClanTime() != 0)
				leaveClan = player.getLeaveClanTime() + 1 * 24 * 60 * 60 * 1000L;
			long deleteClan = 0;
			if(player.getDeleteClanTime() != 0)
				deleteClan = player.getDeleteClanTime() + 10 * 24 * 60 * 60 * 1000L;
			SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
			String html = Files.read("data/scripts/commands/user/penalty.htm", player);

			if(player.getClanId() == 0)
			{
				if(leaveClan == 0 && deleteClan == 0)
				{
					html = html.replaceFirst("%reason%", "No penalty is imposed.");
					html = html.replaceFirst("%expiration%", " ");
				}
				else if(leaveClan > 0 && deleteClan == 0)
				{
					html = html.replaceFirst("%reason%", "Penalty for leaving clan.");
					html = html.replaceFirst("%expiration%", format.format(leaveClan));
				}
				else if(deleteClan > 0)
				{
					html = html.replaceFirst("%reason%", "Penalty for dissolving clan.");
					html = html.replaceFirst("%expiration%", format.format(deleteClan));
				}
			}
			else if(player.getClan().canInvite())
			{
				html = html.replaceFirst("%reason%", "No penalty is imposed.");
				html = html.replaceFirst("%expiration%", " ");
			}
			else
			{
				html = html.replaceFirst("%reason%", "Penalty for expelling clan member.");
				html = html.replaceFirst("%expiration%", format.format(player.getClan().getExpelledMemberTime()));
			}
			show(html, player);
		}
		else if(id == 114)
		{
			//TODO: Сделать отображение названия активного инстанса.
			// SystemMessage.INSTANT_ZONE_CURRENTLY_IN_USE__S1
			player.sendPacket(Msg.INSTANCE_ZONE_TIME_LIMIT);
			int limit;
			boolean noLimit = true;
			InstancedZoneManager ilm = InstancedZoneManager.getInstance();
			for(String name : ilm.getNames())
			{
				limit = ilm.getTimeToNextEnterInstance(name, player);
				if(limit > 0)
				{
					noLimit = false;
					player.sendPacket(new SystemMessage(SystemMessage.S1_WILL_BE_AVAILABLE_FOR_RE_USE_AFTER_S2_HOURS_S3_MINUTES).addString(name).addNumber(limit / 60).addNumber(limit % 60));
				}
			}
			if(noLimit)
				player.sendPacket(Msg.THERE_IS_NO_INSTANCE_ZONE_UNDER_A_TIME_LIMIT);
		}
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