package commands.user;

import l2p.commons.util.GArray;
import l2p.gameserver.cache.Msg;
import l2p.gameserver.handler.UserCommandHandler;
import l2p.gameserver.handler.interfaces.IUserCommandHandler;
import l2p.gameserver.model.L2Clan;
import l2p.gameserver.model.L2Player;
import l2p.gameserver.network.serverpackets.SystemMessage;
import l2p.gameserver.scripts.ScriptFile;

/**
 * Support for /attacklist /underattacklist /warlist commands
 */
public class ClanWarsList implements IUserCommandHandler, ScriptFile
{
	private static final int[] COMMAND_IDS = { 88, 89, 90 };

	public void useUserCommand(L2Player player, int id)
	{
		L2Clan clan = player.getClan();
		if(clan == null)
		{
			player.sendPacket(Msg.NOT_JOINED_IN_ANY_CLAN);
			return;
		}

		SystemMessage sm;
		GArray<L2Clan> data = new GArray<L2Clan>();
		if(id == 88)
		{
			// attack list
			player.sendPacket(Msg._ATTACK_LIST_);
			data = clan.getEnemyClans();
		}
		else if(id == 89)
		{
			// under attack list
			player.sendPacket(Msg._UNDER_ATTACK_LIST_);
			data = clan.getAttackerClans();
		}
		else
		// id = 90
		{
			// war list
			player.sendPacket(Msg._WAR_LIST_);
			for(L2Clan c : clan.getEnemyClans())
				if(clan.getAttackerClans().contains(c))
					data.add(c);
		}

		for(L2Clan c : data)
		{
			String clanName = c.getName();
			int ally_id = c.getAllyId();
			if(ally_id > 0)
				sm = new SystemMessage(SystemMessage.S1_S2_ALLIANCE).addString(clanName).addString(c.getAlliance().getAllyName());
			else
				sm = new SystemMessage(SystemMessage.S1_NO_ALLIANCE_EXISTS).addString(clanName);
			player.sendPacket(sm);
		}

		player.sendPacket(Msg.__EQUALS__);
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