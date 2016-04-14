package com.xperia64.jusrcheat;

import java.io.IOException;
import java.util.ArrayList;


public class TestClass {

	public static void main(String[] args)
	{
		try {
			String file = "usrcheat.dat";
			R4Header header = new R4Header(file);
			if(!header.isHeaderValid())
			{
				System.out.println("Bad header");
				System.exit(1);
			}
			ArrayList<R4Game> games = R4Cheat.getGames(file, header, new CallbackTestClass());
			R4Cheat.writeUsrCheat(file+"2", header, games);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	
	
}
