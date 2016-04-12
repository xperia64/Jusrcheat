package com.xperia64.jusrcheat;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;

public class R4Game {

	
	private boolean gameEnabled;
	private int[] masterCode = new int[8];
	private String gameTitle;
	
	private String gameId;
	private int gameIdNum;
	// Total number of codes/folders for a game, including codes in folders
	private int numItems;
	
	private ArrayList<R4Item> items;
	
	// Read an existing game
	public R4Game(RandomAccessFile raf, int pointer, String gameId, int gameIdNum) throws IOException
	{
		raf.seek(pointer);
		this.gameId = gameId;
		this.gameIdNum = gameIdNum;
		byte tmpChar;
		StringBuilder sb = new StringBuilder();
		while((tmpChar=raf.readByte())!=0)
		{
			sb.append((char)tmpChar);
		}
		gameTitle = sb.toString();
		// Align to nearest multiple of 4
		raf.skipBytes(EndianUtils.alignto4(raf.getFilePointer()));
		
		// Get number of codes/folders
		byte[] tmpba = new byte[2];
		raf.read(tmpba);
		
		numItems = (EndianUtils.little2short(tmpba)&0xFFFF);
		// Get flags
		raf.read(tmpba);
		gameEnabled = (tmpba[1] & 0xF0) == 0xF0;
		
		tmpba = new byte[4];
		for(int i = 0; i<masterCode.length; i++)
		{
			raf.read(tmpba);
			masterCode[i] = EndianUtils.little2int(tmpba);
		}
		items = new ArrayList<>();
		
		tmpba = new byte[2];
		int i = 0;
		while(i<numItems)
		{
			raf.read(tmpba);
			short numberThings = EndianUtils.little2short(tmpba);
			raf.read(tmpba);
			short flags = EndianUtils.little2short(tmpba);
			
			if((flags&0x0100)==0x0100)
			{
				// Folder
				i++;
				R4Folder tmpFold = new R4Folder(numberThings, flags, raf);
				i += (numberThings&0xFFFF);
				items.add(tmpFold);
			}else{
				// Code
				i++;
				R4Code tmpCode = new R4Code(numberThings, flags, raf);
				items.add(tmpCode);
			}
		}
	}
	// Create a new game
	public R4Game(String gameId, int gameIdNum, String gameTitle)
	{
		this.gameId = gameId;
		this.gameIdNum = gameIdNum;
		this.gameTitle = gameTitle;
		items = new ArrayList<>();
	}
	public String getTitle()
	{
		return gameTitle;
	}
	public void setTitle(String gameTitle)
	{
		this.gameTitle = gameTitle;
	}
	public int[] getMasterCode()
	{
		return masterCode;
	}
	public void setMasterCode(int[] master)
	{
		for(int i = 0; i<8; i++)
		{
			this.masterCode[i] = master[i];
		}
	}
	public boolean getEnable()
	{
		return gameEnabled;
	}
	public void setEnable(boolean gameEnabled)
	{
		this.gameEnabled = gameEnabled;
	}
	public ArrayList<R4Item> getItems()
	{
		return items;
	}
	public void addItem(R4Item item)
	{
		items.add(item);
	}
	public void addItem(R4Item item, int num)
	{
		items.add(num, item);
	}
	public void setItem(R4Item item, int num)
	{
		items.set(num, item);
	}
	public void delItem(int num)
	{
		items.remove(num);
	}
	public String getGameId()
	{
		return gameId;
	}
	public void setGameId(String gameId)
	{
		this.gameId = gameId;
	}
	public int getGameIdNum()
	{
		return gameIdNum;
	}
	public void setGameIdNum(int gameIdNum)
	{
		this.gameIdNum = gameIdNum;
	}
	public Byte[] toByte()
	{
		numItems = 0;
		for(R4Item item : items)
		{
			if(item instanceof R4Folder)
			{
				numItems+=((R4Folder)item).getNumCodes();
			}
			numItems++;
		}
		ArrayList<Byte> b = new ArrayList<Byte>();
		// Write the Game title
		byte[] tmp = EndianUtils.str2byte(gameTitle, true);
		for(byte bb : tmp)
		{
			b.add(bb);
		}
		tmp = EndianUtils.short2little((short)numItems);
		b.add(tmp[0]);
		b.add(tmp[1]);
		b.add((byte) 0);
		b.add((byte) (gameEnabled?0xF0:0x00));
		for(int mast : masterCode)
		{
			tmp = EndianUtils.int2little(mast);
			b.add(tmp[0]);
			b.add(tmp[1]);
			b.add(tmp[2]);
			b.add(tmp[3]);
		}
		for(R4Item item : items)
		{
			b.addAll(Arrays.asList(item.toByte()));
		}
		Byte[] arr = new Byte[b.size()];
		arr = b.toArray(arr);
		return arr;
	}
	

	
}
