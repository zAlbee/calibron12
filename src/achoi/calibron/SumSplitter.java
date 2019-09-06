package achoi.calibron;

import java.io.PrintStream;
import java.util.*;


/**
 * @author Albert Choi
 */
public class SumSplitter {
	static PrintStream sout = System.out, serr = System.err;

	public static void main(String[] args) {
		new SumSplitter().precompute();
	}

	Rect goal = new Rect(56, 56);
	Rect[] pieces = {
			new Rect(32, 11),
			new Rect(32, 10),
			new Rect(28, 14),
			new Rect(28, 7),
			new Rect(28, 6),
			new Rect(21, 18),
			new Rect(21, 18),
			new Rect(21, 14),
			new Rect(21, 14),
			new Rect(17, 14),
			new Rect(14, 4),
			new Rect(10, 7)
	};

	void precompute() {
		tryAllSums(0, 0);
		sout.println(its);
	}

	List<Integer> summands = new ArrayList<>();

	long its = 0; // how many combinations did we try?

	void tryAllSums(int i, int sum) {
		if (sum == 56) {
			System.out.println("56 = " + summands);
		}
		if (sum >= 56 || i == pieces.length) {
			its++;
			return;
		}

		Rect pc = pieces[i];

		summands.add(pc.h);
		tryAllSums(i+1, sum + pc.h); // pick the height
		summands.remove(summands.size()-1);

		summands.add(pc.w);
		tryAllSums(i+1, sum + pc.w); // pick the width
		summands.remove(summands.size()-1);

		tryAllSums(i+1, sum); // do not pick this piece
	}

	static class Rect {
		int h;
		int w;
		Rect(int h, int w) {
			this.h = h;
			this.w = w;
		}
		public String toString() {
			return "(" + h + "," + w + ")";
		}
	}

}
