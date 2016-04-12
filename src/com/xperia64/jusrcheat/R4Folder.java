package com.xperia64.jusrcheat;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;


public class R4Folder implements R4Item {
	private int numCodes;
	private boolean oneHot;
	private String foldName;
	private String foldDesc;
	
	private ArrayList<R4Code> codes;
	public R4Folder(short numCodes, short flags, RandomAccessFile raf) throws IOException
	{
		this.numCodes = (numCodes)&0xFFFF;
		this.oneHot = ((flags&0x1000)==0x1000);
		StringBuilder sb = new StringBuilder();
		byte tmpChar;
		while((tmpChar=raf.readByte())!=0)
		{
			sb.append((char)tmpChar);
		}
		this.foldName = sb.toString();
		sb = new StringBuilder();
		while((tmpChar=raf.readByte())!=0)
		{
			sb.append((char)tmpChar);
		}
		this.foldDesc = sb.toString();
		raf.skipBytes(EndianUtils.alignto4(raf.getFilePointer()));
		codes = new ArrayList<>();
		byte[] tmpba = new byte[2];
		for(int i = 0; i<this.numCodes; i++)
		{
			raf.read(tmpba);
			short numberThings = EndianUtils.little2short(tmpba);
			raf.read(tmpba);
			short cflags = EndianUtils.little2short(tmpba);
			
			if((cflags&0x0100)==0x0100)
			{
				// Folder
				System.out.println("Error: Folder in folder");
			}else{
				// Code
				R4Code tmpCode = new R4Code(numberThings, cflags, raf);
				codes.add(tmpCode);
			}
		}
	}
	public R4Folder(String foldName, String foldDesc)
	{
		this.foldName = foldName;
		this.foldDesc = foldDesc;
		codes = new ArrayList<>();
	}
	public ArrayList<R4Code> getCodes()
	{
		return codes;
	}
	public void addCode(R4Code code)
	{
		codes.add(code);
	}
	public void addCode(R4Code code, int num)
	{
		codes.add(num, code);
	}
	public void setCode(R4Code code, int num)
	{
		codes.set(num, code);
	}
	public void delCode(int num)
	{
		codes.remove(num);
	}
	
	public String getName()
	{
		return foldName;
	}
	public void setName(String foldName)
	{
		this.foldName = foldName;
	}
	public String getDesc()
	{
		return foldDesc;
	}
	public void setDesc(String foldDesc)
	{
		this.foldDesc = foldDesc;
	}
	public boolean getOneHot()
	{
		return oneHot;
	}
	public void setOneHot(boolean oneHot)
	{
		this.oneHot = oneHot;
	}
	public int getNumCodes()
	{
		numCodes = codes.size();
		return numCodes;
	}
	
	
	public Byte[] toByte()
	{
		ArrayList<Byte> b = new ArrayList<Byte>();
		byte[] tmp = EndianUtils.short2little((short)numCodes);
		// Total chunks
		b.add((byte) tmp[0]); // Set this later
		b.add((byte) tmp[1]); // This too
		b.add((byte) 0);
		b.add((byte) (0x10|(oneHot?1:0)));
		byte[] foldText = EndianUtils.str2byte(foldName, foldDesc, true);
		for(byte bb : foldText)
		{
			b.add(bb);
		}
		for(R4Code cod : codes)
		{
			b.addAll(Arrays.asList(cod.toByte()));
		}
		Byte[] arr = new Byte[b.size()];
		arr = b.toArray(arr);
		return arr;
	}
}
