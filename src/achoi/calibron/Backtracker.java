package achoi.calibron;

import java.io.PrintStream;
import java.util.*;

/**
 * Solves the Calibron 12 puzzle using backtracking.
 * 
 * The placement strategy at any given step is to look for the uppermost, leftmost available 
 * corner, and try all remaining pieces in that spot in all possible orientations.
 * 
 * With 12 pieces times 2 orientations each, there are 24 choices with an empty board,
 * 22 choices after 1 piece is placed, 20 choices after 2 pieces are placed, etc...
 * In theory, the search space is 24*22*20*...*2 or 10^12 permutations.
 * In practice, with pieces ordered from largest to smallest, we find a solution in under 33000 
 * backtracks (10^4).
 * 
 * @author Albert Choi
 */
public class Backtracker {
	static PrintStream sout = System.out, serr = System.err;

	public static void main(String[] args) {
		new Backtracker().doProblem();
	}

	Rect board = new Rect(56, 56);
	Rect[] pieces = {
			new Rect(28, 14),    // area 392
			new Rect(21, 18, 2), // area 378 (x2)
			new Rect(32, 11),    // area 352
			new Rect(32, 10),    // area 320
			new Rect(21, 14, 2), // area 294 (x2)
			new Rect(17, 14),    // area 238
			new Rect(28, 7),     // area 196
			new Rect(28, 6),     // area 168
			new Rect(10, 7),     // area 70
			new Rect(14, 4),     // area 56
	};

	Rect[] rotated = new Rect[pieces.length];

	boolean[] bools = {true, false};

	int[] pieceCount = new int[pieces.length];

	int totalPieces; // 12

	void doProblem() {

		// Reversing the piece list increases the number of iterations before finding a solution
		// from 1_910 to 1_001_773.
		// Collections.reverse(Arrays.asList(pieces));

		for (int i=0; i<pieces.length; i++) {
			Rect pc = pieces[i];
			rotated[i] = new Rect(pc.w, pc.h);
			pieceCount[i] = pc.count;
			totalPieces += pc.count;
		}

		// A heap-based priority queue keeps the lowest element at the top of the heap.
		// Allows for O(1) time for peek and O(logN) time for remove.
		// Ideal for our algorithm which looks for the topmost, leftmost edge every time.

		PriorityQueue<Edge> edges = new PriorityQueue<>();

		// These edges represent how much of the board space has been occupied.
		// Since our strategy always places pieces as far up as possible, then we only need to 
		// track the lower boundary of the occupied board using these horizontal Edge objects.
		// At the start, the "lower boundary" is simply the top edge of an empty board, 
		// i.e. y = 0, left = 0, right = 56 

		edges.add(new Edge(0, board.w, 0));

		iterations = 0;

		if (search(0, edges)) {
			sout.println("found solution!");
		}
		sout.println(iterations);
	}

	long iterations = 0; // how many times did we backtrack?

	// This contains the pieces we placed in order and will be continually modified while searching.
	// A Deque is a double-ended queue. We only use one end of the queue (the tail). It's just that
	// calling Deque.removeLast() is more convenient than List.remove(List.size() - 1). :)
	Deque<Rect> answer = new ArrayDeque<>();

	// true:      1_910 backtracks
	// false: 5_485_425 backtracks, 8 solutions found (4 rotations * 2 mirrors)
	boolean stopAfterFirstSolution = true;

	/**
	 * Recursive backtracking search. At each level of the search, tries all remaining pieces, 
	 * calling the search recursively for each attempt.
	 * 
	 * @param i - the number of pieces already placed. Also the 0-based depth of the search.
	 * @param edges - edges representing the lower boundary of the occupied board.
	 * @return true if solution has been found, otherwise false
	 */
	boolean search(int i, PriorityQueue<Edge> edges) {
		if (i == totalPieces) {
			sout.println(answer);
			return stopAfterFirstSolution;
		}

		for (int j=0; j<pieces.length; j++) {
			if (pieceCount[j] == 0) continue;

			for (boolean rotate : bools) {

				// insertRect will mutate the edges, so make a copy!
				PriorityQueue<Edge> edgesCopy = new PriorityQueue<>(edges);

				Rect piece = rotate ? rotated[j] : pieces[j];

				if (insertRect(edgesCopy, piece)) {
					// the piece fits. continue searching this path

					pieceCount[j]--;

					answer.addLast(piece);

					if (search(i + 1, edgesCopy)) {
						// found a solution, exit!
						if (stopAfterFirstSolution) {
							return true;
						}
					}

					// else backtrack
					answer.removeLast();

					pieceCount[j]++;
				}
			}
		}
		iterations++;
		return false;
	}

	/**
	 * Inserts a piece at the topmost, leftmost corner if possible, modifying the edges to 
	 * represent the new lower boundary.
	 * 
	 * @param edges - edges representing the lower boundary of the occupied board.
	 *                Will be mutated by this method!
	 * @param rect - the piece to insert
	 * @return false if failed to insert, otherwise true
	 */
	boolean insertRect(PriorityQueue<Edge> edges, Rect rect) {
		int h = rect.h;
		int w = rect.w;

		Edge top = edges.remove();

		// Can we fit vertically?

		if (top.y + h > board.h) {
			return false;
		}

		/* Are we adjacent to another edge with same y value?
		 * Merge them.

		+-----+--------+
		|     |        |
		|     |        |
		+-----+--------+
           ^
		 */

		Edge next = edges.peek();

		while (next != null && next.y == top.y && next.x1 == top.x2) {

			edges.remove();
			top = new Edge(top.x1, next.x2, top.y);

			next = edges.peek();
		}

		/* This is the highest edge, so we can't go any further right
		+-----+--------+---+
		|     |        |   |
		|     +--------+   |
		+-----+   ^    |   |
		               |   |
		               |   |
		               +---+
		 */

		if (top.x1 + w > top.x2) {
			return false;
		}

		// Now add the bottom of the rectangle to be inserted as new edge

		Edge bottom = new Edge(top.x1, top.x1 + w, top.y + h);
		edges.add(bottom);

		/* 
		+-----+--------+---+
		|     |        |   |
		|     +---+----+   |
		+-----+   |    |   |
		      +---+    |   |
		        ^      |   |
		               +---+
		 */

		// And re-add the unused part of the old top edge

		int leftover = top.x2 - bottom.x2;
		if (leftover > 0) {
			edges.add(new Edge(bottom.x2, top.x2, top.y));
		}

		//sout.println(topEdges);
		return true;
	}

	/**
	 * A rectangle with height and width.
	 */
	static class Rect {
		int h;
		int w;
		int count;
		Rect(int h, int w) {
			this(h, w, 1);
		}
		Rect(int h, int w, int c) {
			this.h = h;
			this.w = w;
			this.count = c;
		}
		public String toString() {
			return "(" + h + "," + w + ")";
		}
	}

	/**
	 * A horizontal edge, defined by 3 properties:
	 * - x1 (left point)
	 * - x2 (right point)
	 * - y (vertical position)
	 * 
	 * Smaller x-values are further left (horizontally).
	 * Smaller y-values are higher (vertically).
	 * 
	 * The natural sort order is by smaller y-value, then smaller x-value.
	 */
	static class Edge implements Comparable<Edge> {
		int x1; int x2; int y;
		Edge(int x1, int x2, int y) {
			this.x1 = x1;
			this.x2 = x2;
			this.y = y;
		}

		@Override
		public int compareTo(Edge o) {
			// smaller y, then smaller x
			if (o.y == this.y) {
				return this.x1 - o.x1;
			}
			return this.y - o.y;
		}

		public String toString() {
			return "y[" + y + "] " + x1 + ":" + x2;
		}
	}

}
