package l2p.gameserver.network.clientpackets;

import l2p.gameserver.model.L2Player;
import l2p.gameserver.network.serverpackets.ExBR_MinigameLoadScoresPacket;

public class RequestBR_MinigameLoadScores extends L2GameClientPacket
{
	@Override
	protected void readImpl() throws Exception
	{
	//just a trigger
	}

	@Override
	protected void runImpl() throws Exception
	{
		L2Player player = getClient().getActiveChar();
		if(player == null)
			return;

		player.sendPacket(new ExBR_MinigameLoadScoresPacket());
		//TODO send
	}
}