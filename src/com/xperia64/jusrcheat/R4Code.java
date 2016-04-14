package com.xperia64.jusrcheat;

import java.io.IOException;
import java.io.DataInputStream;
import java.util.ArrayList;

public class R4Code implements R4Item {
	// Number of chunks after the numChunks/flag block
	private int numChunks;
	private int numCodeChunks;
	private boolean codeEnabled;
	private String codeName;
	private String codeDesc;
	private ArrayList<Integer> code;
	
	// Note: Alignment happens after description

	public R4Code(short numChunks, short flags, DataInputStream input) throws IOException
	{
		this.numChunks = (numChunks)&0xFFFF;
		this.codeEnabled = ((flags&0x0100)==0x0100);
		StringBuilder sb = new StringBuilder();
		byte tmpChar;
		while((tmpChar=input.readByte())!=0)
		{
			sb.append((char)tmpChar);
		}
		this.codeName = sb.toString();
		sb = new StringBuilder();
		while((tmpChar=input.readByte())!=0)
		{
			sb.append((char)tmpChar);
		}
		this.codeDesc = sb.toString();
		input.skipBytes(EndianUtils.alignto4(codeName.length()+codeDesc.length()+2));
		byte[] tmpba = new byte[4];
		input.read(tmpba);
		numCodeChunks = EndianUtils.little2int(tmpba);
		code = new ArrayList<>();
		for(int i = 0; i<numCodeChunks; i++)
		{
			input.read(tmpba);
			code.add(EndianUtils.little2int(tmpba));
		}
	}
	public R4Code(String codeName, String codeDesc)
	{
		this.codeName = codeName;
		this.codeDesc = codeDesc;
		code = new ArrayList<>();
	}
	public ArrayList<Integer> getCode()
	{
		return code;
	}
	public void addCode(int cod)
	{
		code.add(cod);
	}
	public void deleteCode()
	{
		code.clear();
	}
	public String getName()
	{
		return codeName;
	}
	public void setName(String codeName)
	{
		this.codeName = codeName;
	}
	public String getDesc()
	{
		return codeDesc;
	}
	public void setDesc(String codeDesc)
	{
		this.codeDesc = codeDesc;
	}
	
	public Byte[] toByte()
	{
		ArrayList<Byte> b = new ArrayList<Byte>();
		// Total chunks
		b.add((byte) 0); // Set this later
		b.add((byte) 0); // This too
		b.add((byte) 0);
		b.add((byte) (codeEnabled?1:0));
		int chunks = 0;
		
		byte[] codeText = EndianUtils.str2byte(codeName, codeDesc, true);
		chunks += (codeText.length/4);
		for(byte bb : codeText)
		{
			b.add(bb);
		}
		chunks+=1;
		// Code chunks
		// Technically can be 32 bit, but the total chunk count is 16 bit.
		int codeChunks = code.size();
		byte[] tmp = EndianUtils.int2little(codeChunks);
		b.add(tmp[0]);
		b.add(tmp[1]);
		b.add(tmp[2]);
		b.add(tmp[3]);
		chunks += codeChunks;
		for(int fragment : code)
		{
			tmp = EndianUtils.int2little(fragment);
			b.add(tmp[0]);
			b.add(tmp[1]);
			b.add(tmp[2]);
			b.add(tmp[3]);
		}
		tmp = EndianUtils.short2little((short)chunks);
		b.set(0, tmp[0]);
		b.set(1, tmp[1]);
		Byte[] arr = new Byte[b.size()];
		arr = b.toArray(arr);
		return arr;
	}
	
}
