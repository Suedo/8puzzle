public class Solver {

	private SearchNode result;

	/**
	 * Each search-node is a different combination of the cells of the initial
	 * board asked to solve. The A* search algorithm scans through the different
	 * search-nodes , operating on the ones with minimum priority . This is done
	 * is a step-by-step manner , handled by the
	 * <i>calculateOneStep(MinPQ<SearchNode>)</i> method
	 */
	private class SearchNode implements Comparable<SearchNode> {
		private final Board board;
		private final int moves;
		private final SearchNode previous;
		private final int priority;

		private SearchNode(Board b, SearchNode p) {
			board = b;
			previous = p;
			if (previous == null)
				moves = 0;
			else
				moves = previous.moves + 1;
			priority = board.manhattan() + moves;
			// Property of A* algorithm.
			assert previous == null || priority >= previous.priority;
		}

		public int compareTo(SearchNode that) {
			return this.priority - that.priority;
		}
	}

	// find a solution to the initial board (using the A* algorithm)
	public Solver(Board initial) {
		if (initial.isGoal())
			result = new SearchNode(initial, null);
		else
			result = solve(initial, initial.twin());
	}

	/**
	 * <b>if( exp1 || exp2 ) { ... } </b>: the nature of such a statement is
	 * that the 'check' goes to exp2 iff exp1 is false.<br/>
	 * <b>exp1</b>: least.previous == null : this signifies that least is the
	 * <i>initial board</i> asked to solve <br/>
	 * <b>exp2 </b>: there are <i>three</i> invariants followed here ,<i>one</i>
	 * is that all the searchnodes are obtained by combinations of each other ,
	 * and <i>two</i> is that the priority queue will not contain duplicate
	 * nodes. These two combined leads to the <i>third</i> invariant that each
	 * search node in the priority queue is the realted to only one other
	 * searchnode via the neighbor relationship. <b>exp2</b> uses this invariant
	 * to ensure that no duplicates gets added to the queue , thus enforcing it
	 * in turn.
	 * 
	 * @param pq
	 *            : Minimum Priority Queue
	 * @param least
	 *            : Board with lowest priority
	 * @param neighbors
	 *            : set of all the boards/serach-nodes that can be obtained from
	 *            least , by exchanging one adjacent cell with the blank cell.
	 *            Notice that 'neighbors' is plural. the singular 'neighbor'
	 *            refers to one element from the set of 'neighbors' <br/>
	 * @return The lowest priority board on <b>pq</b> as of this iteration<br/>
	 */
	private SearchNode calculateOneStep(MinPQ<SearchNode> pq) {
		SearchNode least = pq.delMin();
		for (Board neighbor : least.board.neighbors()) {
			if (least.previous == null
					|| !neighbor.equals(least.previous.board))
				pq.insert(new SearchNode(neighbor, least));
		}
		return least;
	}

	private SearchNode solve(Board initial, Board twin) {
		SearchNode last;
		MinPQ<SearchNode> mainpq = new MinPQ<SearchNode>();
		MinPQ<SearchNode> twinpq = new MinPQ<SearchNode>();
		mainpq.insert(new SearchNode(initial, null));
		twinpq.insert(new SearchNode(twin, null));
		while (true) {
			last = calculateOneStep(mainpq);
			if (last.board.isGoal())
				return last;
			if (calculateOneStep(twinpq).board.isGoal())
				return null;
		}
	}

	// is the initial board solvable?
	public boolean isSolvable() {
		return result != null;
	}

	// min number of moves to solve initial board; -1 if no solution
	public int moves() {
		if (result != null)
			return result.moves;
		return -1;
	}

	// sequence of boards in a shortest solution; null if no solution
	public Iterable<Board> solution() {
		if (result == null)
			return null;
		Stack<Board> s = new Stack<Board>();
		for (SearchNode n = result; n != null; n = n.previous)
			s.push(n.board);
		return s;
	}

	// solve a slider puzzle (given below)
	public static void main(String[] args) {
		// create initial board from file
		// In in = new
		// In("F:/code/java/eclipse/CourseraAlgo/8Puzzle/8puzzle-testing/puzzle04.txt");
		In in = new In(args[0]);
		int N = in.readInt();
		int[][] blocks = new int[N][N];
		for (int i = 0; i < N; i++)
			for (int j = 0; j < N; j++)
				blocks[i][j] = in.readInt();
		Board initial = new Board(blocks);

		// solve the puzzle
		Solver solver = new Solver(initial);

		// print solution to standard output
		if (!solver.isSolvable())
			StdOut.println("No solution possible");
		else {
			StdOut.println("Minimum number of moves = " + solver.moves() + "\n");
			for (Board board : solver.solution())
				StdOut.println(board);
		}
	}
}
