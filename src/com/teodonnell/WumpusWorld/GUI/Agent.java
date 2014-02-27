package com.teodonnell.WumpusWorld.GUI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Stack;
import java.util.logging.Logger;

public class Agent {

	private int location_x, location_y, wumpus_x, wumpus_y, pit_x, pit_y,
			temp_x, temp_y;

	private int direction; // 0 - NORTH, 1 - EAST, 2 - SOUTH, 3 - WEST

	private int numArrows;

	private boolean isDead, hasGold, gameOver;
	private byte[][] world;

	private Random random;

	/**
	 * Byte values
	 * 
	 * Visited - 00000001 Safe - 00000010 Breeze - 00000100 Stench - 00001000
	 * Glitter - 00010000 Pit - 00100000 Wumpus - 01000000
	 */
	private WumpusWorld enviroment;

	private final byte VISITED = 0x01, SAFE = 0x02, BREEZE = 0x04,
			STENCH = 0x08, GLITTER = 0x10, PIT = 0x20, WUMPUS = 0x40;

	private CoordList stenchList, breezeList;

    private Logger logger;
	public Agent(WumpusWorld wumpusWorld) {
		this.world = new byte[4][4];
		this.location_x = 0;
		this.location_y = 3;
		this.direction = '0';
		this.numArrows = 1;
		this.pit_x = -1;
		this.pit_y = -1;
		this.wumpus_x = -1;
		this.wumpus_y = -1;
		this.isDead = false;
		this.hasGold = false;
		this.gameOver = false;
        this.logger = Logger.getLogger(Agent.class.getName());
		this.enviroment = wumpusWorld;
		this.random = new Random();
		this.stenchList = new CoordList();
		this.breezeList = new CoordList();


		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++)
				setFlag(SAFE, i, j);

	}

	private boolean checkFlag(byte VALUE) {
		return ((world[location_x][location_y] & VALUE) == VALUE);
	}

	private void delFlag(byte VALUE, int x, int y) {
		world[x][y] &= ~VALUE;
		enviroment.delFlag(x, y, VALUE);
	}

	private boolean checkFlag(byte VALUE, int x, int y) {
		return ((world[x][y] & VALUE) == VALUE);
	}

	/**
	 * Grabs the byte information for current coordinate in wumpus map and
	 * stores it in agents map
	 */
	private void setFlags() {
		world[location_x][location_y] |= enviroment.enviroment[location_x][location_y];
	}

	/**
	 * Used to set visited
	 * 
	 * @param f
	 */
	private void setFlag(Byte f) {
		world[location_x][location_y] |= f;
	}

	public int getX() {
		return location_x;
	}

	public int getY() {
		return location_y;
	}

	private void setFlag(Byte f, int x, int y) {
		world[x][y] |= f;
	}

	private void setWumpus() {
		wumpus_x = temp_x;
		wumpus_y = temp_y;
		world[temp_x][temp_y] |= WUMPUS;
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++)
				if (i != pit_x && j != pit_y) {
					delFlag(STENCH, i, j);
					setFlag(SAFE, i, j);
				}
		}
	}

	private boolean pitSet() {
		if (pit_x != -1)
			return true;
		return false;
	}


	private void setPit(int x, int y) {
        logger.info("Pit set at: "+ x + ", "+y);
		pit_x = x;
		pit_y = y;
		world[pit_x][pit_y] |= PIT;
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++)
				if (i != pit_x && j != pit_y) {
					delFlag(BREEZE, i, j);
					setFlag(SAFE, i, j);
					delFlag(VISITED, i, j);
				}
		}
	}

	private void setTemp(int x, int y) {
        logger.info("Wumpus set at: "+ x + ", "+y);
		temp_x = x;
		temp_y = y;
	}

	private void setDead(boolean b) {
		isDead = b;
		world[wumpus_x][wumpus_y] |= SAFE;
		
	}

	private void setGold(boolean b) {
		hasGold = b;
	}

	private boolean checkDead() {
		return isDead;
	}

	private boolean checkGold() {
		return hasGold;
	}

	private boolean shootArrow() {
        logger.info("Shooting arrow: direction -"+direction);
		if (numArrows > 0) {
			numArrows--;
			if (enviroment.shootArrow(this.location_x, this.location_y,
					this.direction)) {
				isDead = true;
				return true;
			}

		}
		return false;
	}

	private boolean checkValid(int x, int y)
	{
		if(x >= 0 && x < 4 && y >= 0 && y < 4)
			return true;
		return false;
	}
	
	private boolean checkValidCoord(Coordinate c) {
		if (c.getX() >= 0 && c.getX() < 4 && c.getY() >= 0 && c.getY() < 4)
			return true;
		return false;
	}

	private void changeDirection(int d) {
		direction = d;
	}

	private boolean traverse() {

		switch (direction) {
		case 0:
			if (location_y + 1 < 4) {
				if (checkFlag(SAFE, location_x, location_y + 1)
						&& location_x != wumpus_x && location_y + 1 != wumpus_y)
					location_y++;
				else {
					changeDirection((int) (Math.random() * 4));
					traverse();
				}
				return true;
			}
			return false;
		case 2:
			if (location_y - 1 >= 0) {
				if (checkFlag(SAFE, location_x, location_y - 1)
						&& location_x != wumpus_x && location_y - 1 != wumpus_y)
					location_y--;
				else {
					changeDirection((int)(Math.random() * 4));
					traverse();
				}
			}
			return false;
		case 1:
			if (location_x + 1 < 4) {
				if (checkFlag(SAFE, location_x + 1, location_y)
						&& location_x + 1 != wumpus_x && location_y != wumpus_y)
					location_x++;
				else {
					changeDirection(((int) Math.random() * 4));
					traverse();
				}
				return true;
			}
			return false;
		case 3:
			if (location_x - 1 >= 0) {
				if (checkFlag(SAFE, location_x - 1, location_y)
						&& location_x - 1 != wumpus_x && location_y != wumpus_y)
					location_x--;
				else {
					changeDirection(((int) Math.random() * 4));
					traverse();
				}
				return true;
			}
			return false;
		default:
			return false;
		}

	}

	/**
	 * Checks to make sure the agent didn't make a mistake and end up in
	 * dangerous room (wumpus, pit)
	 * 
	 * @return true if game over
	 */
	private boolean checkForMistake() {
		if (checkFlag(WUMPUS) || checkFlag(PIT)) {
			gameOver = true;
			return true;
		}
		return false;
	}

	private void getOppositeDirection() {
		direction = (direction + 2) % 4;
	}
	
	private void setUnsafeFlags()
	{
		switch(direction)
		{
		case 0:
			if(checkValid(location_x, location_y+1))
				delFlag(SAFE, location_x, location_y+1);
			if(checkValid(location_x+1, location_y))
				delFlag(SAFE, location_x+1, location_y);
			if(checkValid(location_x-1, location_y))
				delFlag(SAFE, location_x-1, location_y);
			break;
		case 1:
			if(checkValid(location_x, location_y+1))
				delFlag(SAFE, location_x, location_y+1);
			if(checkValid(location_x+1, location_y))
				delFlag(SAFE, location_x+1, location_y);
			if(checkValid(location_x, location_y-1))
				delFlag(SAFE, location_x, location_y-1);
			break;
		case 2:
			if(checkValid(location_x, location_y-1))
				delFlag(SAFE, location_x, location_y-1);
			if(checkValid(location_x+1, location_y))
				delFlag(SAFE, location_x+1, location_y);
			if(checkValid(location_x-1, location_y))
				delFlag(SAFE, location_x-1, location_y);
			break;
		case 3:
			if(checkValid(location_x, location_y+1))
				delFlag(SAFE, location_x, location_y+1);
			if(checkValid(location_x, location_y-1))
				delFlag(SAFE, location_x, location_y-1);
			if(checkValid(location_x-1, location_y))
				delFlag(SAFE, location_x-1, location_y);
			break;
			
		}
	}

	private boolean checkStench() {
		boolean checked = false;
		for (int i = 0; i < stenchList.size(); i++) {
			if ((location_x == stenchList.indexOf(i).getX())
					&& (location_y == stenchList.indexOf(i).getY()))
				checked = true;
		}
		if (checkFlag(STENCH) && !checked) {
			stenchList.addCoord(location_x, location_y);
			logger.info("Set stench at: " +location_x+ ", " +location_y);
			switch (stenchList.size()) {
			case 1:
				setUnsafeFlags();
				getOppositeDirection();
				for(int i = 0; i < 4; i++)
					for(int j = 0; j < 4; j++)
						delFlag(VISITED,i,j);
				break;
			case 2:
				Coordinate t = stenchList.indexOf(0);
				int d= calculateDirection(t);
				changeDirection(d);
				shootArrow();
				for (int i = 0; i < 4; i++) {
					for (int j = 0; j < 4; j++)
						if (i != pit_x && j != pit_y) {
							setFlag(SAFE, i, j);
							delFlag(VISITED, i, j);
							delFlag(WUMPUS, i, j);
							delFlag(STENCH, i, j);
						}
				}
				try{
					Thread.sleep(1000);
				}catch (Exception e){}
					break;
			default: // We already found wumpus, so we don't need to do anything
						// for 3 and 4
				break;
			}
			return true;
		}
		return false;
	}

	private int calculateDirection(Coordinate t) {
		if(t.getX() == location_x)
			if(t.getY() > location_y)
				return 3;
			else
				return 1;
		else if(t.getY() == location_y)
			if(t.getX() > location_x)
				return 2;
			else
				return 0;
		else if((t.getX()+1 == location_x))
			return 3;
		else if(t.getX()-1 == location_x)
			return 1;
		else if(t.getY()+1 == location_y)
			return 0;
		else if(t.getY()-1 == location_y)
			return 2;
		return -1;
	}

	private boolean checkGlitter() {
		if (checkFlag(GLITTER)) {
			setGold(true); // Picked up gold
			return true;
		}
		return false;
	}

	private boolean checkBreeze() {

		boolean checked = false;
		for (int i = 0; i < breezeList.size(); i++) {
			if ((location_x == breezeList.indexOf(i).getX())
					&& (location_y == breezeList.indexOf(i).getY()))
				checked = true;
		}
		if (checked) {
			breezeList.addCoord(location_x, location_y);
			switch (breezeList.size()) {
			case 1:
				setUnsafeFlags();
				for(int i = 0; i < 4; i++)
					for(int j = 0; j < 4; j++)
						delFlag(VISITED,i,j);
				break;
			case 2:
				Coordinate t = breezeList.indexOf(0);
				pit_x = (int)((location_x + t.getX())/2 + 0.5);
				pit_y = (int)((location_y + t.getY())/2 + 0.5);
				setPit(pit_x, pit_y);
                logger.info("Agent set pit at: "+pit_x + " " + pit_y);
				break;
			default: // We already found pit, so we don't need to do anything
						// for 3 and 4
				break;
			}
			return true;
		}
		return false;
	}

	/**
	 * If we start off and detect a breeze in (0,3), pit will either be in (1,0)
	 * or (0,1). Must take suicide action (random choice of down or right)
	 * Likewise, if we start off in a stench, Wumpus will either be in (1,0) or
	 * (0,1). By firing the arrow, we will know which room he is in. Returns
	 * true is game over (died)
	 */
	private boolean suicideAction() {
		if (checkFlag(BREEZE)) {
			boolean b = random.nextBoolean();
			if (b) {
				traverse();
			} else {
				changeDirection(1);
				traverse();
			}
			if (checkFlag(PIT)) {
				gameOver = true;
				return true;
			}
			if (direction == 0)
				setPit(1, 3);
			else
				setPit(0, 2);
			return false;
		}
		if (checkFlag(STENCH)) {
			logger.info("Suicide action... shooting arrow east");
			shootArrow();
			if (checkDead()) {
				logger.info("Wumpus shot dead");
				traverse();
				return true;
			} else {
				logger.info("Arrow missed Wumpus, he is north of agent!");
                logger.info("Wumpus set: "+location_x+1 +", "+ location_y);
				setTemp(location_x + 1, location_y);
				setWumpus();
				traverse();
				return false;
			}
		}
		return false;
	}

	private CoordList neighborHelper() {
		CoordList neighbors = new CoordList();

		neighbors.addCoord(location_x, location_y + 1);
		neighbors.addCoord(location_x + 1, location_y);
		neighbors.addCoord(location_x, location_y - 1);
		neighbors.addCoord(location_x - 1, location_y);

		return neighbors;
	}

	private void traverseHelper() {
		CoordList neighbors = neighborHelper();
		int x, y;
		for (int i = 0; i < 4; i++) {
			x = neighbors.indexOf(i).getX();
			y = neighbors.indexOf(i).getY();
			if (x < 4 && y < 4 && x >= 0 && y >= 0) {
				if (checkFlag(SAFE, x, y) && !checkFlag(VISITED, x, y)) {
					direction = i;
					traverse();
					return;
				}
			}
		}
		for(int i = 0; i < 4; i++)
			for(int j = 0; j < 4; j++)
				delFlag(VISITED,i,j);
		
	}

	public void shortestPathToStart() {

		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++)
				delFlag(VISITED, i, j); // Unset all visited

		Stack<Coordinate> path = new Stack<Coordinate>();

		path.push(new Coordinate(location_x, location_y + 1));
		path.push(new Coordinate(location_x + 1, location_y));
		path.push(new Coordinate(location_x, location_y - 1));
		path.push(new Coordinate(location_x - 1, location_y));

		Coordinate current, next;
		while (!path.isEmpty()) {
			current = path.pop();
			if (current.getX() == 0 && current.getY() == 0)
				break;
			next = current.north();
			if (checkValidCoord(next)
					&& checkFlag(VISITED, next.getX(), next.getY())
					&& checkFlag(SAFE, next.getX(), next.getY())) {
				path.push(next);
			}
			next = current.west();
			if (checkValidCoord(next)
					&& checkFlag(VISITED, next.getX(), next.getY())
					&& checkFlag(SAFE, next.getX(), next.getY())) {
				path.push(next);
			}
			next = current.east();
			if (checkValidCoord(next)
					&& checkFlag(VISITED, next.getX(), next.getY())
					&& checkFlag(SAFE, next.getX(), next.getY())) {
				path.push(next);
			}
			next = current.south();
			if (checkValidCoord(next)
					&& checkFlag(VISITED, next.getX(), next.getY())
					&& checkFlag(SAFE, next.getX(), next.getY())) {
				path.push(next);
			}

		}
	}

	public void startGame() throws InterruptedException {
		setFlags();
		suicideAction();
		if (checkFlag(WUMPUS))
			logger.info("Agent killed by Wumpus");
		if (checkGold())
			logger.info("Agent found gold!");

	}

	public boolean makeMove() throws InterruptedException
	{
		Thread.sleep(1000);
		setFlags();
		if (!checkForMistake()) {
			setFlag(VISITED);
			if (!checkDead())
				checkStench();
			if (!pitSet())
				checkBreeze();
			if (checkGlitter())
            {
                logger.info("Agent found gold!");
				return true;
            }
			traverseHelper();
		} 
		return false;
	}
	static class Coordinate {
		private int x, y;

		public Coordinate(int x, int y) {
			this.x = x;
			this.y = y;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

		public Coordinate north() {
			return new Coordinate(x, y + 1);
		}

		public Coordinate south() {
			return new Coordinate(x, y - 1);
		}

		public Coordinate east() {
			return new Coordinate(x + 1, y);
		}

		public Coordinate west() {
			return new Coordinate(x - 1, y);
		}
	}

	static class CoordList {
		private ArrayList<Coordinate> list;
		private int size;

		public CoordList() {
			size = 0;
			list = new ArrayList<Coordinate>();
		}

		public void addCoord(int x, int y) {
			size++;
			list.add(new Coordinate(x, y));
		}

		public int size() {
			return size;
		}

		public Coordinate indexOf(int n) {
			return list.get(n);
		}

	}

}
