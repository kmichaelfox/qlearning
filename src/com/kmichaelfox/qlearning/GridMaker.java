package com.kmichaelfox.qlearning;

import java.util.Arrays;
import java.util.Random;

public class GridMaker {
	private int width;
	private int height;
	private Random rand;
	
	public GridMaker(int w, int h) {
		width = w;
		height = h;
		
		rand = new Random();
	}
	
	public GridMaker(int dim) {
		this(dim, dim);
	}
	
	public void generateGrid(int blockedSites) {
		char[] grid = new char[width * height];
		Arrays.fill(grid, '.');
		
		// set start position
		int startPos = rand.nextInt(width * height);
		grid[startPos] = 'S';
		
		// set end position
		int endPos = rand.nextInt(width * height);
		while (grid[endPos] != '.') {
			endPos = rand.nextInt(width * height);
		}
		grid[endPos] = 'F';
		
		// close "blockedSites" number of open sites
		for (int i = 0; i < blockedSites; i++) {
			int siteToBlock = rand.nextInt(width * height);
			while (grid[siteToBlock] != '.') {
				siteToBlock = rand.nextInt(width * height);
			}
			
			grid[siteToBlock] = '*';
		}
		
		// print out the generated grid
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				System.out.print(grid[x + (y * width)] + " ");
			}
			System.out.println();
		}
		System.out.println();
	}
	
	public static void main(String [] args) {
		GridMaker gridMaker = new GridMaker(10);
		
		int gridsToGenerate;
		
		if (args.length == 1 && Integer.parseInt(args[0]) > 0) {
			gridsToGenerate = Integer.parseInt(args[0]);
		} else {
			gridsToGenerate = 40;
		}
		
		System.out.println("Generating 40 (default) new grids...");
		
		for (int i = 0; i < 40; i++) {
			System.out.println("Grid no. " + (i + 1));
			gridMaker.generateGrid(10);
			System.out.println();
		}
	}
}
