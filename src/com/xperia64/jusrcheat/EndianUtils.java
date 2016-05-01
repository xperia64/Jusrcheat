package com.xperia64.jusrcheat;

public class EndianUtils {

	
	public static int little2int(byte[] b)
	{
		if(b.length!=4)
		{
			System.out.println("Error: Bad Int Length");
		}
		return ( ((b[3]<<24)&0xFF000000) | ((b[2] << 16)&0xFF0000) | ((b[1] << 8)&0xFF00) | (b[0]&0xFF));
	}
	public static byte[] int2little(int i)
	{
		return new byte[]{(byte) (i&0xFF), (byte) ((i>>8)&0xFF), (byte) ((i>>16)&0xFF), (byte) ((i>>24)&0xFF)};
	}
	
	public static short little2short(byte[] b)
	{
		if(b.length!=2)
		{
			System.out.println("Error: Bad Short Length");
		}
		return (short)( ((b[1] << 8)&0xFF00) | (b[0]&0xFF));
	}
	public static byte[] short2little(short s)
	{
		return new byte[]{(byte) (s&0xFF), (byte) ((s>>8)&0xFF)};
	}
	
	// Note: &3 = %4
	public static int alignto4(long pos)
	{
		return (int) ((4-(pos&3))&3);
	}
	public static int alignstr(int len)
	{
		return ((4-(len&3)))+len;
	}
	public static byte[] str2byte(String s, boolean padding)
	{
		byte[] b;
		if(padding)
		{
			b = new byte[alignstr(s.length())];
		}else{
			b = new byte[s.length()+1];
		}
		for(int i = 0; i<b.length; i++)
		{
			if(i<s.length())
			{
				b[i] = (byte) s.charAt(i);
			}else{
				b[i] = 0;
			}
		}
		return b;
	}
	public static byte[] str2byte(String s1, String s2, boolean padding)
	{
		byte[] b;
		if(padding)
		{
			b = new byte[alignstr(s1.length()+1+s2.length())];
		}else{
			b = new byte[s1.length()+1+s2.length()+1];
		}
		for(int i = 0; i<b.length; i++)
		{
			if(i<s1.length())
			{
				b[i] = (byte) s1.charAt(i);
			}else if(i==s1.length())
			{
				b[i] = 0;
			}else if((i-(s1.length()+1))<s2.length())
			{
				b[i] = (byte) s2.charAt(i-(s1.length()+1));
			}else{
				b[i] = 0;
			}
		}
		return b;
	}
}
