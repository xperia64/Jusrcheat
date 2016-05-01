package com.xperia64.jusrcheat;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

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
	
	public interface R4ProgressCallback
	{
		void setProgress(int num, int max);
	}
	
	public static ArrayList<R4Game> getGames(String inFile, R4Header header) throws IOException
	{
		int offset = header.getCodeOffset();
		R4PointerBlock block = new R4PointerBlock(inFile, offset);
		ArrayList<R4Game> games = new ArrayList<>();
		int numGames = block.getNumGames();
		for(int i = 0; i<numGames; i++)
		{
			R4GamePointer tmp = block.getGame(i);
			R4Game game = new R4Game(inFile, tmp.getPointer(), tmp.getGameId(), tmp.getGameIdNum());
			games.add(game);
		}
		return games;
	}
	
	public static ArrayList<R4Game> getGames(String inFile, R4Header header, R4ProgressCallback prog) throws IOException
	{
		int offset = header.getCodeOffset();
		R4PointerBlock block = new R4PointerBlock(inFile, offset);
		ArrayList<R4Game> games = new ArrayList<>();
		int numGames = block.getNumGames();
		for(int i = 0; i<numGames; i++)
		{
			R4GamePointer tmp = block.getGame(i);
			R4Game game = new R4Game(inFile, tmp.getPointer(), tmp.getGameId(), tmp.getGameIdNum());
			games.add(game);
			prog.setProgress(i, numGames);
		}
		return games;
	}
	public static void writeUsrCheat(String outFile, R4Header header, ArrayList<R4Game> games ) throws IOException
	{
		DataOutputStream output = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(outFile)));
		byte[] headerArr = header.toByte();
		R4PointerBlock newBlock = new R4PointerBlock(games);
		
		Byte[] newBlockArr1 = newBlock.toByte();
		byte[] newBlockArr2 = new byte[newBlockArr1.length];
		for(int o = 0; o< newBlockArr1.length; o++)
		{
			newBlockArr2[o] = newBlockArr1[o];
		}
		output.write(headerArr);
		output.write(newBlockArr2);
		int numGames = games.size();
		for(int i = 0; i<numGames; i++)
		{
			Byte[] b = games.get(i).toByte();
			byte[] bb = new byte[b.length];
			for(int o = 0; o< b.length; o++)
			{
				bb[o] = b[o];
			}
			output.write(bb);
		}
		output.close();
	}
	public static void writeUsrCheat(String outFile, R4Header header, ArrayList<R4Game> games ,R4ProgressCallback prog) throws IOException
	{
		DataOutputStream output = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(outFile)));
		byte[] headerArr = header.toByte();
		R4PointerBlock newBlock = new R4PointerBlock(games);

		Byte[] newBlockArr1 = newBlock.toByte();
		byte[] newBlockArr2 = new byte[newBlockArr1.length];
		for(int o = 0; o< newBlockArr1.length; o++)
		{
			newBlockArr2[o] = newBlockArr1[o];
		}
		output.write(headerArr);
		output.write(newBlockArr2);
		int numGames = games.size();
		for(int i = 0; i<numGames; i++)
		{
			Byte[] b = games.get(i).toByte();
			byte[] bb = new byte[b.length];
			for(int o = 0; o< b.length; o++)
			{
				bb[o] = b[o];
			}
			output.write(bb);
			prog.setProgress(i, numGames);
		}
		output.close();
	}
	public static String[] getIds(String file) throws IOException
	{
		byte[] header = new byte[0x200];
		RandomAccessFile raf = new RandomAccessFile(file, "r");
		raf.read(header);
		raf.close();
		String s = new String(header, 0x0C, 4);
		Checksum crc = new CRC32();
		crc.update(header, 0, header.length);
		int chck = (int)((~crc.getValue()));
		return new String[]{s, String.format("%08X",chck)};
	}


	public static int validateGame(String title, String id1, String master)
	{
		if(title.isEmpty())
		{
			return 1;
		}else if(id1.isEmpty())
		{
			return 2;
		}else if(master.isEmpty())
		{
			return 3;
		}
		master = master.replaceAll("[\n]+"," ");
		master = master.replaceAll("[ ]+"," ");
		String[] tmp = master.split(" ");
		if(tmp.length!=8) {
			return 4;
		}
		for(String s : tmp)
		{
			if(!s.matches("([0-9a-fA-F]{8})"))
			{
				return 5;
			}
		}
		return 0;
	}
	public static int validateCode(String title, String code)
	{
		if(title.isEmpty())
		{
			return 1;
		}
		code = code.replaceAll("[\n]+"," ");
		code = code.replaceAll("[ ]+"," ");
		if(code.isEmpty())
		{
			return 0;
		}
		String[] tmp = code.split(" ");
		for(String s : tmp)
		{
			if(!s.matches("([0-9a-fA-F]{8})"))
			{
				return 2;
			}
		}
		return 0;
	}
}
