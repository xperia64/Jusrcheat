package com.xperia64.jusrcheat;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

public class R4Header {

	// Constants
	
	private final int HEADER_SIZE = 0x100;
	private final String MAGIC = "R4 CheatCode";
	private final int START_OFFSET = 0x00000100;
	private final byte[][] ENCODINGS = 
	{
			// GBK					  		// BIG5							//SJIS					  // UTF8
			{(byte)0xD5, 0x53, 0x41, 0x59}, {(byte)0xF5, 0x53, 0x41, 0x59}, {0x75, 0x53, 0x41, 0x59}, {0x55, 0x73, 0x41, 0x59}
	};
	
	
	private byte[] header;
	
	public R4Header(String title, int encoding, boolean enable)
	{
		header = new byte[HEADER_SIZE];
		for(int i = 0; i<MAGIC.length(); i++)
		{
			header[i]= (byte)MAGIC.charAt(i);
		}
		resetCodeOffset();
		setDatabaseName(title);
		setEncoding(encoding);
		setCheatEnable(enable);
	}
	
	public R4Header(String inFile) throws IOException
	{
		header = new byte[HEADER_SIZE];
		DataInputStream input = new DataInputStream(new BufferedInputStream(new FileInputStream(inFile)));
		input.read(header);
		input.close();
	}
	
	public String getDatabaseName()
	{
		StringBuilder sb = new StringBuilder();
		for(int i = 0x10; i<0x4B; i++)
		{
			if(header[i]==0)
			{
				// String is terminated
				break;
			}
			sb.append((char)header[i]);
		}
		return sb.toString();
	}
	public int getEncoding()
	{
		byte encoding = header[0x4C]; // Not the full encoding, but the encoding can be determined with the first byte
		for(int i = 0; i<ENCODINGS.length; i++)
		{
			if(ENCODINGS[i][0] == encoding)
			{
				return i;
			}
		}
		return -1;
	}
	public boolean getCheatEnable()
	{
		return ((header[0x50]&0x01)==1);
	}
	public int getCodeOffset()
	{
		byte[] b = Arrays.copyOfRange(header, 0x0C, 0x10);
		return EndianUtils.little2int(b);
	}
	
	public boolean isHeaderValid()
	{
		for(int i = 0x00; i<0x0C; i++)
		{
			if(header[i]!=MAGIC.charAt(i))
			{
				return false;
			}
		}
		return true;
	}
	
	public void setDatabaseName(String databaseName)
	{
		if(databaseName.length()>59)
		{
			System.out.println("Database name will be truncated!");
		}
		String realname = databaseName.substring(0, Math.min(databaseName.length(), 59));
		for(int i = 0; i<59; i++)
		{
			if(i<realname.length())
			{
				header[i+0x10] = (byte)realname.charAt(i);
			}else{
				header[i+0x10] = 0;
			}
		}
	}
	public void setEncoding(int encoding)
	{
		byte[] encodingBytes = ENCODINGS[encoding];
		for(int i = 0; i<4; i++)
		{
			header[i+0x4C] = encodingBytes[i];
		}
	}
	public void setCheatEnable(boolean enable)
	{
		if(enable)
		{
			header[0x50] |= 0x01;
		}else{
			header[0x50] &= ~0x01;
		}
	}
	public void setCodeOffset(int offset)
	{
		byte[] b = EndianUtils.int2little(offset);
		System.arraycopy(b, 0, header, 0x0C, 4);
	}
	public void resetCodeOffset()
	{
		byte[] b = EndianUtils.int2little(START_OFFSET);
		System.arraycopy(b, 0, header, 0x0C, 4);
	}
	public byte[] toByte()
	{
		return header;
	}
	
}
