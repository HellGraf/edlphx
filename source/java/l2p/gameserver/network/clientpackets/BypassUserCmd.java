package l2p.gameserver.network.clientpackets;

import l2p.gameserver.extensions.multilang.CustomMessage;
import l2p.gameserver.handler.UserCommandHandler;
import l2p.gameserver.model.L2Player;

/**
 * format:  cd
 * Пример пакета по команде /loc:
 * AA 00 00 00 00
 */
public class BypassUserCmd extends L2GameClientPacket
{
	private int _command;

	@Override
	public void readImpl()
	{
		_command = readD();
	}

	@Override
	public void runImpl()
	{
		L2Player player = getClient().getActiveChar();
		if(player == null)
			return;

		if(!UserCommandHandler.getInstance().useCommand(player, _command))
			player.sendMessage(new CustomMessage("common.S1NotImplemented", player).addString(String.valueOf(_command)));
	}
}