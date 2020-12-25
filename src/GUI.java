import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Vector;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class GUI extends JFrame{
	private static final long serialVersionUID = 1L;
	
	static int spacing = 5;
	static int width = 770, height = 770;
	static int h = 3, w = 4;
	static int size = width/Math.max(h, w);
	static int state[][] = new int [h][w];
	public double mx,my;
	int turn = 1, win =0;
	int connect = 4;
	
	public GUI() {
		this.setTitle("CONNECT 4");
		this.setSize(770+6+6+spacing,770+29+6+spacing); 
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setBackground(Color.BLACK);
		this.setVisible(true);
		this.setResizable(false);
		
		Board board = new Board();
		this.setContentPane(board);
		Move move = new Move();
		this.addMouseMotionListener(move);
		Click click = new Click();
		this.addMouseListener(click);
		
		for(int i=0;i<h;i++)
			for(int j=0;j<w;j++)
				state[i][j]= 0;
	}		
	public class Board extends JPanel{	
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		
		public void paintComponent(Graphics g) {
			Graphics2D g2D = (Graphics2D) g;
			RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
	    	rh.put(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
	    	g2D.setRenderingHints(rh);
			for(int i=0;i<h;i++) {
				for(int j=0;j<w;j++) {
					if (state[i][j]== 0)
						g2D.setColor(Color.WHITE);
					if(inBoxX()==j) 
						g2D.setColor(Color.DARK_GRAY);
					if (state[i][j]== 1)
						g2D.setColor(Color.RED);
					if (state[i][j]== 2)
						g2D.setColor(Color.BLUE);
					g2D.fill(new Rectangle2D.Double(spacing+j*size, spacing+(i+1)*size, size-2*spacing, size-2*spacing));
				}
			}
		}
	}
	
	public int inBoxX() {
		for(int i=0;i<h;i++) {
			for(int j=0;j<w;j++) {
				if(mx >= spacing+j*size+7 && mx <= (j+1)*size+2)
					return j;
			}
		}
		return -1;
	}
	
	public int inBoxY() {
		for(int i=0;i<h;i++) {
			for(int j=0;j<w;j++) {
				if(my >= spacing+(i+1)*size+26+5 && my <= 26+5+(i+2)*size-spacing-1)
					return i;
			}
		}
		return -1;
	}
	
	public int checkWin() {
		
		// _ horizontal
		for (int i=h-1;i>=0;i--) {
			for (int j=0;j<1+w-connect;j++) {
				int value = state[i][j];
				if (value!=0&&state[i][j+1]==value&&state[i][j+2]==value&&state[i][j+3]==value) {
					return value;
				}
			}
		}
		// | vertical
		for (int j=w-1;j>=0;j--) {
			for (int i=0;i<1+h-connect;i++) {
				int value = state[i][j];
				if (value!=0&&state[i+1][j]==value&&state[i+2][j]==value&&state[i+3][j]==value) {
					return value;
				}
			}
		}
		// / diagonal 
		for (int i=connect-1;i<h;i++) {
			for (int j=0;j<1+w-connect;j++) {
				int value = state[i][j];
				if (value!=0&&state[i-1][j+1]==value&&state[i-2][j+2]==value&&state[i-3][j+3]==value) {
					return value;
				}
			}
		}
		// \ diagonal a
		for (int i=0;i<connect-1;i++) {
			for (int j=0;j<1+w-connect;j++) {
				int value = state[i][j];
				if (value!=0&&state[i+1][j+1]==value&&state[i+2][j+2]==value&&state[i+3][j+3]==value) {
					return value;
				}
			}
		}
		// draw
		int count = 0;
		for(int i=0;i<h;i++) {
			for(int j=0;j<w;j++) {
				if (state[i][j]==0) count++;
			}
		}
		if (count == h*w) return 3;
		
		// continue game
		return 0;
	}
	
	public class Move implements MouseMotionListener{
		@Override
		public void mouseDragged(MouseEvent e) {
			
		}
		@Override
		public void mouseMoved(MouseEvent e) {
			mx = e.getX();
			my = e.getY();
		}
	}
	
	private void computer() {
		int moveI=-1,moveJ=-1,score=-99999999;
		int tempJ = (new Random()).nextInt(6);
		for(int j=tempJ,cnti=0;cnti<w;j++,j%=w,cnti++) {
			int i = falldown(-1, j);
			if (i!=-1) {
				state[i][j]=2;
				int temp = minimax(false);
				if (temp>score) {
					score=temp;
					moveI=i;
					moveJ=j;
				}
			}
			state[i][j]=0;
		}	
		state[moveI][moveJ]=2;
		return;
	}
	
	private int minimax(boolean isMax) {
		//terminating
		if(checkWin()==2) return 1;
		else if (checkWin()==1) return -1;
		
		if(isMax) {
			int best = -9999999;
			for(int j=0;j<w;j++) {
				int i = falldown(-1, j);
				if (i!=-1) {
					state[i][j]=2;
					int temp = minimax(false);
					best = Math.max(best,temp);
					state[i][j]=0;
				}
				
			}
			return best;
		}
		else {
			int best = 9999999;
			for(int j=0;j<w;j++) {
				int i = falldown(-1, j);
				if (i!=-1) {
					state[i][j]=1;
					int temp = minimax(true);
					best = Math.min(best,temp);
					state[i][j]=0;
				}

			}	
			return best;
		}
		
	}
	
	int falldown(int i, int j) {
		while(i+1<h&&state[i+1][j]==0) i++;
		return i;
	}
	
	public class Click implements MouseListener{
		@Override
		public void mouseClicked(MouseEvent e) {

		}
		@Override
		public void mouseEntered(MouseEvent e) {
		}
		@Override
		public void mouseExited(MouseEvent e) {
		}
		@Override
		public void mousePressed(MouseEvent e) {
			int ii = inBoxY(), jj = inBoxX();
			if(ii!=-1 && jj!=-1 && state[0][jj]==0&&win==0) {
				if (turn == 1) {
					ii=-1; // start falling
					ii = falldown(ii, jj);
					state[ii][jj] = turn;
					//repaint();
				}
				else {
					computer();
				}
				turn = 1 + (turn++)%2;
				win = checkWin();
				System.out.println(win);
				//repaint();
			}
		}
		@Override
		public void mouseReleased(MouseEvent e) {
		} 
	}
}