package com.teodonnell.WumpusWorld.GUI;

import java.awt.Color;
import java.util.*;
public class WumpusWorld {

	/**
	 * Byte values
	 * 
	 * Visited - 00000001	1
	 * Safe    - 00000010	2
	 * Breeze  - 00000100	4
	 * Stench  - 00001000	8
	 * Glitter - 00010000	10
	 * Pit     - 00100000	20
	 * Wumpus  - 01000000	40
	 */
	public byte[][] enviroment;
	
	public Color[][] colors;
	
	public final int ROWS = 4,
					 COLUMNS = 4,
					 START_X = 0,
					 START_Y = 3,
					 INITIAL_SCORE = 1000,
					 NO_OF_PITS = 1,
					 NO_OF_WUMPUS = 1,
					 NO_OF_GOLD = 1;
	
	public final byte BREEZE = 0x04,
					  STENCH = 0x08,
					  GLITTER = 0x10,
					  PIT = 0x20,
					  WUMPUS = 0x40;
	
	
	public WumpusWorld()
	{
		enviroment = new byte[ROWS][COLUMNS];
		colors = new Color[ROWS][COLUMNS];
		generateWorld();
	}
	
	
	public byte returnRoom(int x, int y)
	{
		return enviroment[x][y];
	}
	
	public boolean shootArrow(int x, int y, int d)
	{
		switch(d)
		{
			case '0':
				for(int i = y; i < 4; i++)
				{	
					if(checkFlag(x, i, WUMPUS))
						return true;
				}
				return false;
			case '2':
				for(int i = y; i > -1; i++)
				{	
					if(checkFlag(x, i, WUMPUS))
						return true;
				}
				return false;
			case '1':
				for(int i = x; i < 4; i++)
				{
					if(checkFlag(i, y, WUMPUS))
						return true;
				}
				return false;
			case '3':
				for(int i = x; i > -1; i++)
				{	
					if(checkFlag(i, y, WUMPUS))
						return true;
				}
				return false;
			default:
				return false;
		}
	}
	
	//Agent class can not see anything below this line
	private void generateWorld()
	{
		generatePits();
		generateWumpus();
		generateGold();
		generateColors();
	}
	
	/*
	 * Generates a total of two different pits on the map as well as surrounding breezes
	 * Pits can not be placed in position 0, 0
	 * Breezes are set on surrounding pit positions
	 */
	private void generatePits() throws ArrayIndexOutOfBoundsException
	{
		int x = 0;
		int y = 3;
		for(int i = 0; i < NO_OF_PITS; i++)
		{
			while(x == 0 && y == 3)
			{
				x = (int)(Math.random() * 2);
				y = (int)(Math.random() * 2);
			}
			setPit(x,y);
		}
		
	}
	
	/*
	 * Generates a Wumpus in the grid at random points x, y
	 * Wumpus can not be placed at position 0,0
	 * Stenches are set on surrounding pit positions (north, south, east, west)
	 */
	private void generateWumpus() throws ArrayIndexOutOfBoundsException
	{
		int x = 0;
		int y = 3;
			while((x == 0 && y == 3) && !checkFlag(x, y, PIT))
			{
				x = (int)(Math.random() * 2)+2;
				y = (int)(Math.random() * 2)+2;
			}
			setWumpus(x, y);
	}
	
	/*
	 * Sets Boolean[][] glitter to true, only if there is no pit in position
	 */
	private void generateGold()
	{
		int x = 0;
		int y = 3;
		for(int i = 0; i < NO_OF_GOLD; i++)
		{
			while((x == 0 && y == 3 ) && !checkFlag(x, y, PIT))
			{
				x = (int)(Math.random() * 4);
				y = (int)(Math.random() * 4);
			}
			setGlitter(x,y);
		}
	}
	
	/*
	 * Sets Boolean[][] breeze to true, only if  x and y are within world (4x4)
	 */
	private void setBreeze(int x, int y)
	{
		if(x >= 0 && x < 4 && y >= 0 && y < 4)
			enviroment[x][y] |= (byte)0x04;
	}
	
	/*
	 * Sets Boolean[][] breeze to true, only if  x and y are within world (4x4)
	 */
	private void setStench(int x, int y)
	{
		if(x >= 0 && x < 4 && y >= 0 && y < 4)
			enviroment[x][y] |= (byte)0x08;
	}
	
	
	private void setGlitter(int x, int y)
	{
			enviroment[x][y] |= (byte)0x10;
	}
	
	
	/*
	 * Sets both of the position of the Boolean[][] pits to true
	 * Sends surrounding positions (north, south, east, west) to method setBreeze()
	 */
	private void setPit(int x, int y)
	{
			enviroment[x][y] |= (byte)0x20;
			setBreeze(x+1, y);
			setBreeze(x-1, y);
			setBreeze(x, y+1);
			setBreeze(x, y-1);
	}
	
	
	/*
	 * Sets the position of the Boolean[][] wumpus to true
	 * Sends surrounding positions (north, south, east, west) to method setStench()
	 */
	private void setWumpus(int x, int y)
	{
		enviroment[x][y] |= (byte)0x40;
		setStench(x+1, y);
		setStench(x-1, y);
		setStench(x, y+1);
		setStench(x, y-1);
	}
	
	public void delFlag(int x, int y, byte VALUE)
	{
		enviroment[x][y] &= ~VALUE;
	}
	public boolean checkFlag(int x, int y, byte VALUE)
	{
		return ((enviroment[x][y] & VALUE) == VALUE);
	}
	
	
	
	private void generateColors()
	{
		for(int i = 0; i < 4; i++)
			for(int j = 0; j < COLUMNS; j++)
				colors[i][j] = getColor(i, j);
	}
	
	public Color getColor(int x, int y)
	{
		if(checkFlag(x, y, PIT))
			return Color.BLACK;
		if(checkFlag(x, y, WUMPUS) && checkFlag(x, y, GLITTER) && checkFlag(x, y, BREEZE))
			return Color.DARK_GRAY;
		if(checkFlag(x, y, WUMPUS) && checkFlag(x, y, GLITTER))
			return Color.LIGHT_GRAY;
		if(checkFlag(x, y, WUMPUS))
			return Color.GRAY;
		if(checkFlag(x, y, BREEZE) && checkFlag(x, y, STENCH) && checkFlag(x, y, GLITTER))
			return Color.PINK;
		if(checkFlag(x, y, BREEZE) && checkFlag(x, y, STENCH))
			return Color.GREEN;
		if(checkFlag(x, y, BREEZE) && checkFlag(x, y, GLITTER))
			return Color.RED;
		if(checkFlag(x, y, GLITTER) && checkFlag(x, y, STENCH))
			return Color.ORANGE;
		if(checkFlag(x, y, BREEZE))
			return Color.CYAN;
		if(checkFlag(x, y, STENCH))
			return Color.MAGENTA;
		if(checkFlag(x, y, GLITTER))
			return Color.YELLOW;
		return Color.WHITE;
	}
}
