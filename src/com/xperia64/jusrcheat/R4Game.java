package com.xperia64.jusrcheat;

import java.io.IOException;
import java.io.DataInputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
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
	public static final String DEFAULT_MASTER="00000000 00000001\n00000000 00000000\n00000000 00000000\n00000000 00000000";
	// Read an existing game
	public R4Game(String inFile, int pointer, String gameId, int gameIdNum) throws IOException
	{
		DataInputStream input = new DataInputStream(new BufferedInputStream(new FileInputStream(inFile)));

		input.skip(pointer);
		this.gameId = gameId;
		this.gameIdNum = gameIdNum;
		byte tmpChar;
		StringBuilder sb = new StringBuilder();
		while((tmpChar=input.readByte())!=0)
		{
			sb.append((char)tmpChar);
		}
		gameTitle = sb.toString();
		// Align to nearest multiple of 4
		input.skipBytes(EndianUtils.alignto4(gameTitle.length()+1));
		
		// Get number of codes/folders
		byte[] tmpba = new byte[2];
		input.read(tmpba);
		
		numItems = (EndianUtils.little2short(tmpba)&0xFFFF);
		// Get flags
		input.read(tmpba);
		gameEnabled = (tmpba[1] & 0xF0) == 0xF0;
		
		tmpba = new byte[4];
		for(int i = 0; i<masterCode.length; i++)
		{
			input.read(tmpba);
			masterCode[i] = EndianUtils.little2int(tmpba);
		}
		items = new ArrayList<>();
		
		tmpba = new byte[2];
		int i = 0;
		while(i<numItems)
		{
			input.read(tmpba);
			short numberThings = EndianUtils.little2short(tmpba);
			input.read(tmpba);
			short flags = EndianUtils.little2short(tmpba);
			
			if((flags&0x1000)==0x1000)
			{
				// Folder
				i++;
				R4Folder tmpFold = new R4Folder(numberThings, flags, input);
				i += (numberThings&0xFFFF);
				items.add(tmpFold);
			}else{
				// Code
				i++;
				R4Code tmpCode = new R4Code(numberThings, flags, input);
				items.add(tmpCode);
			}
		}
		input.close();
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
	public String getMasterCodeStr(){
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i<masterCode.length; i+=2)
		{
			sb.append(String.format("%08X %08X\n",masterCode[i], masterCode[i+1]));
		}
		return sb.substring(0, sb.length()-1);
	}
	public void setMasterCode(int[] master)
	{
		for(int i = 0; i<8; i++)
		{
			this.masterCode[i] = master[i];
		}
	}
	public void setMasterCode(String mas)
	{
		mas = mas.replaceAll("[\n]+"," ");
		mas = mas.replaceAll("[ ]+"," ");
		String[] strMasS = mas.split(" ");
		for(int o = 0; o<strMasS.length; o++)
		{
			this.masterCode[o] = ((int)Long.parseLong(strMasS[o],16));
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
	public void delAll(){ items.clear(); }
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
		ArrayList<Byte> b = new ArrayList<>();
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
