package l2p.gameserver.handler.interfaces;

import l2p.gameserver.model.L2Player;

public interface IUserCommandHandler
{
	public void useUserCommand(L2Player player, int id);

	public int[] getUserCommandList();
}
