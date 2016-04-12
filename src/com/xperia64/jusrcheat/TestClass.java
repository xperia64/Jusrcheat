package com.xperia64.jusrcheat;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;


public class TestClass {

	public static void main(String[] args)
	{
		try {
			RandomAccessFile raf = new RandomAccessFile("origcheat.dat","r");
			RandomAccessFile raf2 = new RandomAccessFile("newcheats.dat","rw");
			R4Header head = new R4Header(raf);
			if(!head.isHeaderValid())
			{
				System.out.println("Bad header!");
				System.exit(0);
			}
			
			ArrayList<R4Game> games = R4Cheat.getGames(raf, head);
			games.clear();
			R4Game ng = new R4Game("TEST", 0x5900DAF3, "Test Game 1");
			R4Code code1 = new R4Code("TestCode1","Desc");
			code1.addCode(0xDEADBEEF);
			code1.addCode(0xD00DEAEA);
			R4Folder ff = new R4Folder("TestFolder", "");
			R4Code code2 = new R4Code("TestCode2", "Desc 2");
			code2.addCode(0x12345678);
			code2.addCode(0x87654321);
			ff.addCode(code2);
			R4Code code3 = new R4Code("TestCode3","Another desc");
			code3.addCode(0xd4d3d2d1);
			code3.addCode(0xd5d4d5d6);
			ff.addCode(code3);
			R4Code code4 = new R4Code("Code","Description 4");
			code4.addCode(0xAAAAAAAA);
			code4.addCode(0xBBBBBBBB);
			
			ng.addItem(code1);
			ng.addItem(ff);
			ng.addItem(code4);
			
			R4Game ng2 = new R4Game("T3ST",0x00000000,"Test Game 2");
			R4Code code5 = new R4Code("Another Code","");
			code5.addCode(0x010c0b04);
			code5.addCode(0x12141616);
			ng2.addItem(code5);
			games.add(ng);
			games.add(ng2);
			R4Header newHeader = new R4Header("My Cheats", 3, true);
			R4Cheat.writeUsrCheat(raf2, newHeader, games);
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
