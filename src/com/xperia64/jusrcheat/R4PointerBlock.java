package com.xperia64.jusrcheat;

import java.io.IOException;
import java.io.DataInputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class R4PointerBlock {	
	private ArrayList<R4GamePointer> gamePointers;
	
	// Never edit the existing PointerBlock. Always create a new one
	public R4PointerBlock(String inFile, int dataOffset) throws IOException
	{
		gamePointers = new ArrayList<>();
		DataInputStream input = new DataInputStream(new BufferedInputStream(new FileInputStream(inFile)));
		input.skip(dataOffset);
		byte[] tmp = new byte[0x10];
		while(true)
		{
			input.read(tmp);
			if(tmp[0]==0)
			{
				break;
			}
			gamePointers.add(new R4GamePointer(tmp));
		}
		input.close();
	}
	public R4PointerBlock(ArrayList<R4Game> gamz)
	{
		gamePointers = new ArrayList<>();
		// Number of game pointers = number of games + one blank line
		int nextOffset = 0x110+gamz.size()*0x10;
		for(int i = 0; i<gamz.size(); i++)
		{
			R4Game gam = gamz.get(i);
			gamePointers.add(new R4GamePointer(gam.getGameId(), gam.getGameIdNum(), nextOffset));
			nextOffset += gam.toByte().length;
		}
	}
	public R4GamePointer getGame(int i)
	{
		return gamePointers.get(i);
	}
	public int getNumGames()
	{
		return gamePointers.size();
	}
	
	public Byte[] toByte()
	{
		ArrayList<Byte> b = new ArrayList<>();
		for(int i = 0; i<gamePointers.size(); i++)
		{
			b.addAll(Arrays.asList(gamePointers.get(i).toByte()));
		}
		// Write the separator block
		for(int i = 0; i<0x10; i++)
		{
			b.add((byte) 0);
		}
		
		Byte[] arr = new Byte[b.size()];
		arr = b.toArray(arr);
		return arr;
	}
	public class R4GamePointer {
		
		private String gameId;
		private int gameIdNum;
		private int pointer;
		
		public R4GamePointer(byte[] b)
		{
			StringBuilder sb = new StringBuilder();
			for(int i = 0; i<4; i++)
			{
				sb.append((char)b[i]);
			}
			gameId = sb.toString();
			gameIdNum = EndianUtils.little2int(Arrays.copyOfRange(b, 0x04, 0x08));
			pointer = EndianUtils.little2int(Arrays.copyOfRange(b, 0x08, 0x0C));
		}
		public R4GamePointer(String title, int id, int pointer)
		{
			this.gameId = title;
			this.gameIdNum = id;
			this.pointer = pointer;
		}
		
		public String getGameId()
		{
			return gameId;
		}
		public int getGameIdNum()
		{
			return gameIdNum;
		}
		public int getPointer()
		{
			return pointer;
		}
		public void setGameId(String gameId)
		{
			this.gameId = gameId;
		}
		public void setGameIdNum(int idNum)
		{
			this.gameIdNum = idNum;
		}
		public void setPointer(int pointer)
		{
			this.pointer = pointer;
		}
		
		public Byte[] toByte()
		{
			Byte[] b = new Byte[0x10];
			for(int i = 0; i<4; i++)
			{
				b[i] = (byte) gameId.charAt(i);
			}
			byte[] tmpid = EndianUtils.int2little(gameIdNum);
			for(int i = 0; i<4; i++)
			{
				b[i+0x04] = tmpid[i];
			}
			byte[] tmpPointer = EndianUtils.int2little(pointer);
			for(int i = 0; i<4; i++)
			{
				b[i+0x08] = tmpPointer[i];
			}
			for(int i = 0; i<4; i++)
			{
				b[i+0x0c] = 0;
			}
			return b;
		}
	}
}
