package achoi.calibron;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.PriorityQueue;

import org.junit.Test;

import achoi.calibron.Backtracker.Edge;
import achoi.calibron.Backtracker.Rect;


public class BacktrackerTest {

	@Test
	public void testInsertRect() {
		Backtracker bt = new Backtracker();

		PriorityQueue<Edge> edges = new PriorityQueue<>();
		edges.add(new Edge(0, 56, 0));

		boolean r = bt.insertRect(edges, new Rect(4, 6));

		assertTrue(r);

		Edge e;

		e = edges.remove();

		assertEquals(0, e.y);
		assertEquals(6, e.x1);
		assertEquals(56, e.x2);

		e = edges.remove();

		assertEquals(4, e.y);
		assertEquals(0, e.x1);
		assertEquals(6, e.x2);

	}

	@Test
	public void testInsertBigSmallBig() {
		Backtracker bt = new Backtracker();

		PriorityQueue<Edge> edges = new PriorityQueue<>();
		edges.add(new Edge(0, 56, 0));

		Edge e;

		// 5 + 10 + 41 = 56
		assertTrue(bt.insertRect(edges, new Rect(8, 5)));

		/*
		+-----+--------------------+
		|     |
		|     |
		|     | 8
		|     |
		|     |
		+-----+
		   5           51
		 */

		e = edges.peek();
		assertEquals(0, e.y);
		assertEquals(5, e.x1);
		assertEquals(56, e.x2);

		assertTrue(bt.insertRect(edges, new Rect(2, 10)));

		/*
		+-----+--------+-----------+
		|     |        | 2
		|     +--------+ 
		|     |        
		|     |          6 
		|     |        
		+-----+        
		   5     10        41
		 */

		e = edges.peek();
		assertEquals(0, e.y);
		assertEquals(15, e.x1);
		assertEquals(56, e.x2);

		assertTrue(bt.insertRect(edges, new Rect(8, 41)));

		/*
		+-----+--------+-----------+
		|     |        | 2         |
		|     +--------+           | 8
		|     |        |           |
		|     |        | 6         |
		|     |        |           |
		+-----+        +-----------+
		   5     10         41
		 */

		e = edges.peek();
		assertEquals(2, e.y);
		assertEquals(5, e.x1);
		assertEquals(15, e.x2);

		assertTrue(bt.insertRect(edges, new Rect(5, 5)));

		/*
		+-----+--------+-----------+
		|     |        | 2         |
		|     +--------+           | 8
		|     |   | 5  |           |
		|     +---+    | 6         |
		|     |     1  |           |
		+-----+        +-----------+
		   5    5   5          41
		 */

		e = edges.peek();
		assertEquals(2, e.y);
		assertEquals(10, e.x1);
		assertEquals(15, e.x2);

		////////

		e = edges.remove();
		assertEquals(2, e.y);
		assertEquals(10, e.x1);
		assertEquals(15, e.x2);

		e = edges.remove();
		assertEquals(7, e.y);
		assertEquals(5, e.x1);
		assertEquals(10, e.x2);

		e = edges.remove();
		assertEquals(8, e.y);
		assertEquals(0, e.x1);
		assertEquals(5, e.x2);

		e = edges.remove();
		assertEquals(8, e.y);
		assertEquals(15, e.x1);
		assertEquals(56, e.x2);

	}

}
