package l2p.commons.network;

public interface IClientFactory<T extends MMOClient<?>>
{
	public T create(MMOConnection<T> con);
}