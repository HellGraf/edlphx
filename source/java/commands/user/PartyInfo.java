package commands.user;

import l2p.gameserver.cache.Msg;
import l2p.gameserver.handler.UserCommandHandler;
import l2p.gameserver.handler.interfaces.IUserCommandHandler;
import l2p.gameserver.model.L2Party;
import l2p.gameserver.model.L2Player;
import l2p.gameserver.network.serverpackets.SystemMessage;
import l2p.gameserver.scripts.ScriptFile;

/**
 * Support for /partyinfo command
 */
public class PartyInfo implements IUserCommandHandler, ScriptFile
{
	private static final int[] COMMAND_IDS = { 81 };

	public void useUserCommand(L2Player player, int id)
	{
		L2Party playerParty = player.getParty();
		if(!player.isInParty())
			return;

		L2Player partyLeader = playerParty.getPartyLeader();
		if(partyLeader == null)
			return;

		int memberCount = playerParty.getMemberCount();
		int lootDistribution = playerParty.getLootDistribution();

		player.sendPacket(Msg._PARTY_INFORMATION_);

		switch(lootDistribution)
		{
			case L2Party.ITEM_LOOTER:
				player.sendPacket(Msg.LOOTING_METHOD_FINDERS_KEEPERS);
				break;
			case L2Party.ITEM_ORDER:
				player.sendPacket(Msg.LOOTING_METHOD_BY_TURN);
				break;
			case L2Party.ITEM_ORDER_SPOIL:
				player.sendPacket(Msg.LOOTING_METHOD_BY_TURN_INCLUDING_SPOIL);
				break;
			case L2Party.ITEM_RANDOM:
				player.sendPacket(Msg.LOOTING_METHOD_RANDOM);
				break;
			case L2Party.ITEM_RANDOM_SPOIL:
				player.sendPacket(Msg.LOOTING_METHOD_RANDOM_INCLUDING_SPOIL);
				break;
		}

		player.sendPacket(new SystemMessage(SystemMessage.PARTY_LEADER_S1).addString(partyLeader.getName()), new SystemMessage("scripts.commands.user.PartyInfo.Members", player, new Integer(memberCount)), Msg.__DASHES__);
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