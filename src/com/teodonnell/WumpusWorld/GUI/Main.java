/*
 * Wumpus World
 * Travis O'Donnell John Coleman
 * Frostburg State University
 * COSC 450 Artificial Intelligence
 * Email: teodonnell0@gmail.com
 * Github: http://github.com/teodonnell
 * Copyright (c) 2013
 */

package com.teodonnell.WumpusWorld.GUI;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main extends JPanel{
	private static WumpusWorld wumpusWorld;
	private static final long serialVersionUID = 1L;
	public static final int GRID_COUNT = 4;
	public static BufferedImage stench,
	pit,
	breeze,
	wumpus,
	gold,
	player;
	private final static Byte STENCH_BYTE = 0x08,
			BREEZE_BYTE = 0x04,
			PIT_BYTE    = 0x20,
			GLITTER_BYTE= 0x10,
			WUMPUS_BYTE = 0x40;
	private static Agent agent;
	public void paint(Graphics g)
	{
		super.paintComponent(g);
		Graphics2D graphics = (Graphics2D) g;
		graphics.setColor(Color.black);
		Dimension size = getSize();
		Insets insets = getInsets();
		int w = size.width - insets.left - insets.right;
		int h = size.height - insets.top - insets.bottom;

		int sqrWidth = (int)((double)w / 4);
		int sqrHeight = (int)((double)h / 4);

		for(int i = 0; i < 4; i++)
			for(int j = 0; j < 4; j++)
			{


				int x = (int)(i * (double)w / 4);
				int y = (int)(j * (double)h / 4);

				graphics.setColor(wumpusWorld.getColor(i, j));
				graphics.fillRect(x, y, sqrWidth, sqrHeight);
				if(wumpusWorld.checkFlag(i, j, BREEZE_BYTE))
					graphics.drawImage(breeze, x+40, y+30, null);
				if(wumpusWorld.checkFlag(i, j, GLITTER_BYTE))
					graphics.drawImage(gold, x+40, y+30, null);
				if(wumpusWorld.checkFlag(i, j, WUMPUS_BYTE))
					graphics.drawImage(wumpus,x+30,y+30, null);
				if(wumpusWorld.checkFlag(i, j, STENCH_BYTE))
					graphics.drawImage(stench,x+30, y+30, null);
				if(agent.getX() == i && agent.getY() == j)
					graphics.drawImage(player, x+30, y+30, null);
				graphics.setColor(Color.black);
				graphics.drawLine(x, y, x+100, y);
				graphics.drawLine(x, y, x, y+100);

			}
	}


	public static void main(String[] args) throws IOException, InterruptedException {
		wumpusWorld = new WumpusWorld();
		init();
		Main main = new Main();
		main.setPreferredSize(new Dimension(400,400));
		JFrame frame = new JFrame("Wumpus World");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(main);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		agent.startGame();
		while(!agent.makeMove())
			frame.repaint();
	}

    /**
     * Load images into memory
     * @throws IOException
     */
	public static void init() throws IOException
	{

		player = ImageIO.read(new File("./resources/player.png"));
		stench =  ImageIO.read(new File("./resources/stench.png"));
		pit =  ImageIO.read(new File("./resources/pit.png"));
		breeze =  ImageIO.read(new File("./resources/breeze.png"));
		wumpus =  ImageIO.read(new File("./resources/wumpus.png"));
		gold =  ImageIO.read(new File("./resources/gold.png"));
		agent = new Agent(wumpusWorld);

	}

}
