package l2p.gameserver.network.serverpackets;

public class PetDelete extends L2GameServerPacket
{
	private int _petId;
	private int _petType;

	public PetDelete(int petId, int petType)
	{
		_petId = petId;
		_petType = petType;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0xb7);
		writeD(_petType);
		writeD(_petId);// dont really know what these two are since i never needed them
	}
}