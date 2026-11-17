package l2p.gameserver.network.clientpackets;

import java.util.logging.Logger;

import l2p.gameserver.model.L2Player;

/**
 * Format: chdd
 * d: team
 */
public final class RequestExCubeGameChangeTeam extends L2GameClientPacket
{
	private static Logger _log = Logger.getLogger(RequestExCubeGameChangeTeam.class.getName());

	private int _team;
	//private int _arena;

	@Override
	protected void readImpl()
	{
		//_arena = readD() + 1;
		readD();
		_team = readD();
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null || activeChar.isDead())
			return;

		switch(_team)
		{
			case 0:
			case 1:
				// Change Player Team
				break;
			case -1:
				// Remove Player (me)
				break;
			default:
				_log.warning("Wrong Team ID: " + _team);
				break;
		}
	}
}
