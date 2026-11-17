package l2p.gameserver.network.serverpackets;

public class SetupGauge extends L2GameServerPacket
{
	public static final int BLUE = 0;
	public static final int RED = 1;
	public static final int CYAN = 2;
	public static final int GREEN = 3;

	private int _id;
	private int _color;
	private int _time;

	public SetupGauge(int obj_id, int color, int time)
	{
		_id = obj_id;		
		_color = color; //0-blue, 1-red, 2-cyan, 3-green
		_time = time;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x6b);
		writeD(_id);
		writeD(_color);
		writeD(_time);
		writeD(_time);
	}
}