package l2p.gameserver.network.clientpackets;

import l2p.gameserver.handler.AdminCommandHandler;
import l2p.gameserver.model.L2Player;

public class SendBypassBuildCmd extends L2GameClientPacket
{
	private String _command = "";

	@Override
	public void readImpl()
	{
		_command = readS();

		if(_command != null)
			_command = _command.trim();
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();

		if(activeChar == null)
			return;

		String cmd = _command;

		if(!cmd.startsWith("admin_"))
			cmd = "admin_" + cmd;

		AdminCommandHandler.getInstance().useCommand(activeChar, cmd);
	}
}