package commands.user;

import l2p.gameserver.cache.Msg;
import l2p.gameserver.extensions.multilang.CustomMessage;
import l2p.gameserver.handler.UserCommandHandler;
import l2p.gameserver.handler.interfaces.IUserCommandHandler;
import l2p.gameserver.model.L2CommandChannel;
import l2p.gameserver.model.L2Party;
import l2p.gameserver.model.L2Player;
import l2p.gameserver.network.serverpackets.ExMultiPartyCommandChannelInfo;
import l2p.gameserver.network.serverpackets.SystemMessage;
import l2p.gameserver.scripts.ScriptFile;

/**
 * Support for CommandChannel commands:<br>
 * 92	/channelcreate<br>
 * 93	/channeldelete<br>
 * 94	/channelinvite [party leader] отправляет пакет RequestExMPCCAskJoin<br>
 * 95	/channelkick [party leader] отправляет пакет RequestExMPCCExit<br>
 * 96	/channelleave<br>
 * 97	/channelinfo<br>
 *
 * @author SYS
 */
public class CommandChannel implements IUserCommandHandler, ScriptFile
{
	private static final int[] COMMAND_IDS = { 92, 93, 96, 97 };

	public void useUserCommand(L2Player player, int id)
	{
		switch(id)
		{
			case 92: //channelcreate
				// "Используйте команду /channelinvite"
				player.sendMessage(new CustomMessage("scripts.commands.user.CommandChannel", player));
				break;
			case 93: //channeldelete
				if(!player.isInParty() || !player.getParty().isInCommandChannel())
					return;
				if(player.getParty().getCommandChannel().getChannelLeader() == player)
				{
					L2CommandChannel channel = player.getParty().getCommandChannel();
					channel.disbandChannel();
				}
				else
					player.sendPacket(Msg.ONLY_THE_CREATOR_OF_A_CHANNEL_CAN_USE_THE_CHANNEL_DISMISS_COMMAND);
				break;
			case 96: //channelleave
				//FIXME создатель канала вылетел, надо автоматом передать кому-то права
				if(!player.isInParty() || !player.getParty().isInCommandChannel())
					return;
				if(!player.getParty().isLeader(player))
				{
					player.sendPacket(Msg.ONLY_A_PARTY_LEADER_CAN_CHOOSE_THE_OPTION_TO_LEAVE_A_CHANNEL);
					return;
				}
				L2CommandChannel channel = player.getParty().getCommandChannel();

				//Лидер СС не может покинуть СС, можно только распустить СС
				//FIXME по идее может, права автоматом должны передаться другой партии
				if(channel.getChannelLeader() == player)
				{
					if(channel.getParties().size() > 1)
						return;

					// Закрываем СС, если в СС 1 партия и лидер нажал Quit
					channel.disbandChannel();
					return;
				}

				L2Party party = player.getParty();
				channel.removeParty(party);
				party.broadcastToPartyMembers(Msg.YOU_HAVE_QUIT_THE_COMMAND_CHANNEL);
				channel.broadcastToChannelMembers(new SystemMessage(SystemMessage.S1_PARTY_HAS_LEFT_THE_COMMAND_CHANNEL).addString(player.getName()));
				break;
			case 97: //channelinfo
				if(!player.isInParty() || !player.getParty().isInCommandChannel())
					return;
				player.sendPacket(new ExMultiPartyCommandChannelInfo(player.getParty().getCommandChannel()));
				break;
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