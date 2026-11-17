package l2p.loginserver.crypt;

import java.security.MessageDigest;

public class PasswordHash
{
	public static String encrypt(String password)
	{
		MessageDigest md = null;
		try
		{
			md = MessageDigest.getInstance("SHA");
			byte[] raw = password.getBytes("UTF-8");
			byte[] hash = md.digest(raw);
			return toBase64(hash);
		}
		catch (Exception e) { e.printStackTrace(); }
		return password;
	}
	
	public static String toBase64(byte[] in)
	{
		char[] a = 
		{
			'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R',
			'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
			'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1',
			'2', '3', '4', '5', '6', '7', '8', '9', '+', '/'
		};
		int oDataLen = (in.length * 4 + 2) / 3;
		int oLen = ((in.length + 2) / 3) * 4;
		char[] out = new char[oLen];
		int ip = 0;
		int iEnd = 0 + in.length;
		int op = 0;
		while (ip < iEnd)
		{
			int i0 = in[ip++] & 0xff;
			int i1 = ip < iEnd ? in[ip++] & 0xff : 0;
			int i2 = ip < iEnd ? in[ip++] & 0xff : 0;
			int o0 = i0 >>> 2;
			int o1 = ((i0 &   3) << 4) | (i1 >>> 4);
			int o2 = ((i1 & 0xf) << 2) | (i2 >>> 6);
			int o3 = i2 & 0x3F;
			out[op++] = a[o0];
			out[op++] = a[o1];
			out[op] = op < oDataLen ? a[o2] : '='; op++;
			out[op] = op < oDataLen ? a[o3] : '='; op++;
		}
		return String.valueOf(out);
	}
}