package com.xperia64.jusrcheat;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import com.xperia64.jusrcheat.R4PointerBlock.R4GamePointer;

public class R4Cheat {

	// All numbers are little endian
	// Strings must be aligned to 0x03, 0x07, 0x0B, or 0x0F
	// Strings are null terminated
	/* Structure
	 * 0x00: R4 CheatCode
	 * 0x0C: 00 01 00 00 -> Offset of where the cheats start? (0x0100)
	 * 0x10-0x4B: Cheat database title + \0
	 * 0x4C-0x4D: XX XX -> Encoding. GBK = D5 53, BIG5 = F5 53, SJIS = 75 53 UTF8 = 55 73
	 * 0x4E-0x4F: 41 59
	 * 0x50: "Cheat Enable"
	 * Game Pointer
	 * 0x00-0x03: Game ID
	 * 0x04-0x07: That version number?
	 * 0x08-0x0B: Offset in file
	 * 0x0C-0x0F: ????
	 * 
	 * 16 byte Separator block of 00s
	 * 
	 * Game
	 * 0x0000-0x????: Title, zero terminated, padded to blocks of 4 bytes
	 * Title is in blocks of 4*n-1. If the title is a multiple of 4, four extra zeros are added. 
	 * 
	 * (reset after title)
	 * 0x00-0x01: Number of items
	 * 0x03-0x04: Flags. Master code enable. 0xF0 is enable, 0x00 is disable 
	 * 0x05-0x24: Master code, 4 byte chunks
	 * 
	 * 
	 * 
	 * 0x25-0x26: Number of 4 byte chunks associated with item if it's a code, number of codes if it's a folder
	 * 0x27-0x28: Flags. 0x0010 = Folder 0x0001 = Code Enable for Codes, "One Hot" for folders
	 * 0x29-0x??: Title+Description
	 * 
	 * (reset after desc)
	 * For codes:
	 * 0x00-0x03: Number of 4 byte cheat code chunks
	 * 0x04-0x??: The codes
	 * For Folders:
	 * Codes.
	 * 
	 */
	
	public static ArrayList<R4Game> getGames(RandomAccessFile raf, R4Header header) throws IOException
	{
		int offset = header.getCodeOffset();
		R4PointerBlock block = new R4PointerBlock(raf, offset);
		ArrayList<R4Game> games = new ArrayList<>();
		for(int i = 0; i<block.getNumGames(); i++)
		{
			R4GamePointer tmp = block.getGame(i);
			R4Game game = new R4Game(raf, tmp.getPointer(), tmp.getGameId(), tmp.getGameIdNum());
			games.add(game);
		}
		return games;
	}
	
	public static void writeUsrCheat(RandomAccessFile raf, R4Header header, ArrayList<R4Game> games ) throws IOException
	{
		byte[] headerArr = header.toByte();
		R4PointerBlock newBlock = new R4PointerBlock(games);
		
		Byte[] newBlockArr1 = newBlock.toByte();
		byte[] newBlockArr2 = new byte[newBlockArr1.length];
		for(int o = 0; o< newBlockArr1.length; o++)
		{
			newBlockArr2[o] = newBlockArr1[o];
		}
		raf.seek(0);
		raf.write(headerArr);
		raf.write(newBlockArr2);
		for(int i = 0; i<games.size(); i++)
		{
			Byte[] b = games.get(i).toByte();
			byte[] bb = new byte[b.length];
			for(int o = 0; o< b.length; o++)
			{
				bb[o] = b[o];
			}
			raf.write(bb);
		}
	}
}
