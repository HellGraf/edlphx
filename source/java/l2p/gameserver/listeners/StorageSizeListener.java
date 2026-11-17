package l2p.gameserver.listeners;

import l2p.gameserver.model.L2Player;
import l2p.gameserver.network.serverpackets.ExStorageMaxCount;
import l2p.gameserver.skills.Env;
import l2p.gameserver.skills.Stats;

public class StorageSizeListener extends StatsChangeListener
{
	public StorageSizeListener(Stats stat)
	{
		super(stat);
	}

	@Override
	public void statChanged(Double oldValue, double newValue, double baseValue, Env env)
	{
		_calculator._character.sendPacket(new ExStorageMaxCount((L2Player) _calculator._character));
	}
}