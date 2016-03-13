package com.kmichaelfox.qlearning;

import com.kmichaelfox.qlearning.DataWriter;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Random;

public class QLearning {
	char[] grid;
	float[][] qTable;
	float alpha;
	float gamma;
	float epsilon;
	
	int episodeCounter = 0;
	float totalReward = 0;
	
	int startPos;
	int endPos;
	int currentPos;
	
	private static Random rand;
	
	public DataWriter out;
	
	public QLearning(char[] grid, float alpha, float gamma, float epsilon, boolean randomInit) {
		this.grid = grid;
		this.alpha = alpha;
		this.gamma = gamma;
		this.epsilon = epsilon;
		
		rand = new Random();
		
		for (int i = 0; i < grid.length; i++) {
			if (grid[i] == 'S') {
				startPos = i;
			}
			if (grid[i] == 'F') {
				endPos = i;
			}
		}
		
		qTable = new float[grid.length][4];
		for (int state = 0; state < grid.length; state++) {
			//Arrays.fill(state, 0);
			for (int action = 0; action < 4; action++) {
				if (randomInit) {
					qTable[state][action] = (float)(rand.nextGaussian() * 0.1);
				} else {
					qTable[state][action] = 0;
				}
			}
		}
	}
	
	public void runOneEpisode() {
		currentPos = startPos;
		//int counter = 0;
		//System.out.println("Starting position: " + currentPos);
		while (!isFinished()) {
			stepEpisodeOnce();
			//counter++;
			//System.out.println("Current position: " + currentPos);
		}
		episodeCounter++;
		runOneEpisodeNonLearning();
		//System.out.println("End of Episode!");
		//System.out.println("Completed in " + counter + " steps.");
	}
	
	public void runOneEpisodeNonLearning() {
		//System.out.println("Running episode");
		float learningAlpha = alpha;
		float learningEpsilon = epsilon;
		
		alpha = 0;
		epsilon = 0;
		
		currentPos = startPos;
		int counter = 0;
		while (!isFinished() && counter < 500) {
			stepEpisodeOnce();
			counter++;
			//System.out.println("Current position: " + currentPos);
		}
		//out.println("Completed in " + counter + " steps.");
		//out.println(""+counter);
		out.println(""+totalReward);
		
		// set parameters back for learning
		alpha = learningAlpha;
		epsilon = learningEpsilon;
	}
	
	public void printQTable() {
		for (int y = 0; y < 100; y++) {
			for (int x = 0; x < 4; x++) {
				System.out.println(qTable[y][x]);
			}
			//System.out.println();
		}
	}
	
	private void stepEpisodeOnce() {
		Direction action = chooseAction();
		int actionIdx = action.ordinal();
		int newState = getNewState(action);
		int reward = getReward(action);
		totalReward += reward;
		
		qTable[currentPos][actionIdx] = qTable[currentPos][actionIdx] + alpha * (reward + gamma * getMaxQForState(newState) - qTable[currentPos][actionIdx]);
		currentPos = newState;
		
		if (false) {
			printSimulation();
		}
	}
	
	private Direction chooseAction() {
		ArrayList<Direction> dir = new ArrayList<Direction>();
		
		// chance for exploration
		if (rand.nextFloat() < epsilon) {
			dir.add(getDirection(rand.nextInt(4)));
		} else { // else exploit memory
			// find action with max Q
			dir.add(Direction.UP);
			//int maxActionIdx = 0;
			boolean optimaFound = false;
			for (int i = 1; i < 4; i++) {
				// check for all actions have same Q value
				if (qTable[currentPos][i] != qTable[currentPos][dir.get(0).ordinal()]) {
					optimaFound = true;
				}
				
				if (qTable[currentPos][i] > qTable[currentPos][dir.get(0).ordinal()]) {
					dir.clear();
					dir.add(getDirection(i));
				} else if (qTable[currentPos][i] == qTable[currentPos][dir.get(0).ordinal()]) {
					dir.add(getDirection(i));
				}
			}
			
			// if all Q values the same, pick random direction
			if (!optimaFound) {
				return getDirection(rand.nextInt(4));
			}
		}
		
		// else use action with max Q, or random action when several equal actions 
		return dir.get(rand.nextInt(dir.size()));
	}
	
	private int getReward(Direction dir) {
		if (!canMoveInDirection(dir)) {
			return -10;
		} else if (grid[getPositionFromDirection(dir)] == 'F') {
			return 10;
		}
		
		return 0;
	}
	
	private int getNewState(Direction dir) {
		if (!canMoveInDirection(dir)) {
			return currentPos;
		}
		
		return getPositionFromDirection(dir);
	}
	
	private float getMaxQForState(int state) {
		float maxQ = qTable[state][0];
		for (int i = 1; i < 4; i++) {
			// check for all actions have same Q value
			if (maxQ < qTable[state][i]) {
				maxQ = qTable[state][i];
			}
		}
		
		return maxQ;
	}
	
	private int getPositionFromDirection(Direction dir) {
		switch(dir) {
		case UP:
			return currentPos - 10;
		case DOWN:
			return currentPos + 10;
		case LEFT:
			return currentPos - 1;
		case RIGHT:
			return currentPos + 1;
		default:
			return currentPos;
		}
	}
	
	private Direction getDirection(int value) {
		switch(value) {
		case 0:
			return Direction.UP;
			
		case 1:
			return Direction.DOWN;
			
		case 2:
			return Direction.LEFT;
			
		case 3:
		default:
			return Direction.RIGHT;
		}
	}
	
	private boolean canMoveInDirection(Direction dir) {
		// check for edge of map
		if (dir == Direction.LEFT && (currentPos % 10) == 0) {
			return false;
		} else if (dir == Direction.RIGHT && (currentPos % 10) == 9) {
			return false;
		} else if (dir == Direction.UP && (currentPos / 10) == 0) {
			return false;
		} else if (dir == Direction.DOWN && (currentPos / 10) == 9) {
			return false;
		}
		
		// check for blocked site
		if (dir == Direction.LEFT && grid[currentPos - 1] == '*') {
			return false;
		} else if (dir == Direction.RIGHT && grid[currentPos + 1] == '*') {
			return false;
		} else if (dir == Direction.UP && grid[currentPos - 10] == '*') {
			return false;
		} else if (dir == Direction.DOWN && grid[currentPos + 10] == '*') {
			return false;
		}
		
		return true;
	}
	
	private boolean isFinished() {
		return grid[currentPos] == 'F';
	}
	
	private void printSimulation() {
		for(int i = 0; i < grid.length; i++) {
			if (i == currentPos) {
				System.out.print('#');
			} else {
				System.out.print(grid[i]);
			}
			System.out.print(" ");
			if (i % 10 == 9) {
				System.out.println();
			}
		}
		
		try {
		    Thread.sleep(100);                 //1000 milliseconds is one second.
		} catch(InterruptedException ex) {
		    Thread.currentThread().interrupt();
		}
	}
	
	public void setOutputFilename(String filename) {
		if (out != null) {
			out.closeFile();
		}

		out = new DataWriter(filename);
	}
	
	public void printParameters() {
		out.println("a = " + alpha);
		out.println("g = " + gamma);
		out.println("e = " + epsilon);
		out.println("random initial weights = " + ((qTable[0][0] != 0.0f) ? "true" : "false"));
		out.println();
	}
	
	public static void main(String [] args) {
		
		char[] gridA = {
				'*','S','.','.','.','.','.','.','.','.',
				'.','.','.','.','*','.','.','.','.','.',
				'.','.','.','.','.','.','.','.','.','.',
				'.','.','*','*','.','.','.','.','.','.',
				'.','.','.','.','.','.','.','.','.','*',
				'.','.','.','.','.','.','.','.','.','.',
				'.','.','.','.','.','.','.','.','.','.',
				'.','*','.','.','.','.','F','*','.','.',
				'.','*','.','.','.','.','*','.','.','*',
				'.','.','.','.','.','.','.','.','.','.'
		};
		
		char[] gridB = {
				'.','.','.','.','.','.','.','*','.','.',
				'.','.','.','.','.','.','.','.','.','.',
				'.','.','*','.','.','.','.','.','.','.',
				'.','.','.','.','.','.','.','.','*','.',
				'.','.','.','.','F','.','.','.','.','.',
				'.','.','.','.','.','.','*','.','.','.',
				'.','.','.','.','.','*','*','.','.','.',
				'.','.','.','.','*','.','.','.','.','.',
				'.','.','.','.','*','S','*','.','.','.',
				'.','.','.','.','*','.','.','.','.','.' 
		};
		
		char[] gridC = {
				'.','.','.','.','.','.','.','.','.','.',
				'.','.','.','.','.','.','*','.','.','.',
				'.','.','.','.','.','.','.','.','.','.',
				'.','*','.','.','.','.','*','.','.','S',
				'.','.','.','.','.','*','.','*','.','.',
				'*','.','.','.','.','.','.','.','.','.',
				'.','.','.','.','.','.','.','.','.','.',
				'.','.','.','.','.','.','.','*','*','.',
				'.','F','.','.','.','*','.','.','.','.',
				'.','.','.','.','.','*','.','.','.','.' 
		};
		
		String trial_id;
		QLearning q;
		boolean random = true;
		int numIterations = 1500;

		float[] a = {0.2f, 0.5f, 0.8f};
		float[] g = {0.2f, 0.5f, 0.8f};
		float[] e = {0.1f, 0.3f, 0.5f};
		
		for (int k = 0; k < 3; k++) {
			for (int j = 0; j < 3; j++) {
				for (int i = 0; i < 3; i++) {
					// set the filename modifier
					trial_id = "_"+(i + (j*3) + (k*9) + 1)+(random ? "a" : "b");
					
					// create the QLearning object for gridA
					q = new QLearning(gridA, a[k], g[j], e[i], random);
					q.setOutputFilename("gridA" + trial_id + ".txt");
					q.printParameters();
					for (int episodeNum = 0; episodeNum < numIterations; episodeNum++) {
						//q.out.print(""+episodeNum);
						q.runOneEpisode();
					}
					q.out.closeFile();
					
					// create the QLearning object for gridB
					q = new QLearning(gridB, a[k], g[j], e[i], random);
					q.setOutputFilename("gridB" + trial_id + ".txt");
					q.printParameters();
					for (int episodeNum = 0; episodeNum < numIterations; episodeNum++) {
						//q.out.print(""+episodeNum);
						q.runOneEpisode();
					}
					q.out.closeFile();
					
					// create the QLearning object for gridC
					q = new QLearning(gridC, a[k], g[j], e[i], random);
					q.setOutputFilename("gridC" + trial_id + ".txt");
					q.printParameters();
					for (int episodeNum = 0; episodeNum < numIterations; episodeNum++) {
						//q.out.print(""+episodeNum);
						q.runOneEpisode();
					}
					q.out.closeFile();
				}
			}
		}
		
		
		
//		QLearning q = new QLearning(gridA, a, g, e);
////		for (int i = 0; i < 1000000; i++) {
////			System.out.println("Episode: " + (i + 1));
////			q.runOneEpisode();
////			System.out.println();
////		}
////		q.printQTable();QLearning q = new QLearning(gridC, 0.5f, 0.5f, 0.1f);
//		q.setOutputFilename("gridA" + trial_id + ".txt");
//		q.printParameters();
//		for (int i = 0; i < 15000; i++) {
//			//q3.out.println("Episode: " + (i + 1));
//			q.out.print(""+i);
//			q.runOneEpisode();
//			//q3.out.println();
//		}
//		//q.printQTable();
//		q.out.closeFile();
//		
////		QLearning q2 = new QLearning(gridB, 0.5f, 0.5f, 0.1f);
////		for (int i = 0; i < 1000000; i++) {
////			System.out.println("Episode: " + (i + 1));
////			q2.runOneEpisode();
////			System.out.println();
////		}
////		q2.printQTable();QLearning q = new QLearning(gridC, 0.5f, 0.5f, 0.1f);
//		q = new QLearning(gridB, a, g, e);
//		q.setOutputFilename("gridB" + trial_id + ".txt");
//		q.printParameters();
//		for (int i = 0; i < 15000; i++) {
//			//q3.out.println("Episode: " + (i + 1));
//			q.out.print(""+i);
//			q.runOneEpisode();
//			//q3.out.println();
//		}
//		//q.printQTable();
//		q.out.closeFile();
//		
//		q = new QLearning(gridC, a, g, e);
//		q.setOutputFilename("gridC" + trial_id + ".txt");
//		q.printParameters();
//		for (int i = 0; i < 15000; i++) {
//			//q3.out.println("Episode: " + (i + 1));
//			q.out.print(""+i);
//			q.runOneEpisode();
//			//q3.out.println();
//		}
//		//q.printQTable();
//		q.out.closeFile();
		
		System.out.println("Completed!");
	}
}

enum Direction {
	UP(0), DOWN(1), LEFT(2), RIGHT(3);
	private int value;
	
	private Direction(int value) {
		this.value = value;
	}
}