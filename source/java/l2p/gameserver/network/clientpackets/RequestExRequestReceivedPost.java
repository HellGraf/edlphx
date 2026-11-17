package l2p.gameserver.network.clientpackets;

import l2p.gameserver.model.L2Player;
import l2p.gameserver.network.serverpackets.ExReplyReceivedPost;
import l2p.gameserver.network.serverpackets.ExShowReceivedPostList;

/**
 * Запрос информации об полученном письме. Появляется при нажатии на письмо из списка {@link ExShowReceivedPostList}.
 * @see RequestExRequestSentPost
 */
public class RequestExRequestReceivedPost extends L2GameClientPacket
{
	private int postId;

	/**
	 * format: d
	 */
	@Override
	public void readImpl()
	{
		postId = readD(); // id письма
	}

	@Override
	public void runImpl()
	{
		L2Player cha = getClient().getActiveChar();
		if(cha != null)
			cha.sendPacket(new ExReplyReceivedPost(cha, postId));
	}
}