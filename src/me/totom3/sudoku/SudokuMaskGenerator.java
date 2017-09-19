package me.totom3.sudoku;

import java.util.Collections;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 *
 * @author Totom3
 */
public class SudokuMaskGenerator {

	private static final int[] MASK_1 = new int[81];

	static {
		for (int i = 0; i < 81; ++i)
			MASK_1[i] = 1;
	}
	
	public static int[] generateMask(int[] sudoku, int holes) {
		return new SudokuMaskGenerator(sudoku, holes).generateMask();
	}

	private final int[] sudoku;
	private final int[] mask;
	private final int numberOfHoles;

	private boolean solved;

	private SudokuMaskGenerator(int[] sudoku, int numberOfHoles) {
		this.sudoku = new int[81];
		System.arraycopy(sudoku, 0, this.sudoku, 0, 81);

		this.mask = new int[81];
		System.arraycopy(MASK_1, 0, this.mask, 0, 81);

		this.numberOfHoles = numberOfHoles;
	}

	private int[] generateMask() {

		LinkedList<Integer> options = new LinkedList<>();
		for (int i = 0; i < 81; ++i) {
			options.add(i);
		}

		Collections.shuffle(options);
		makeHole(0, options);

		return mask;
	}

	private void makeHole(int depth, LinkedList<Integer> options) {
		for (ListIterator<Integer> it = options.listIterator(); it.hasNext();) {
			int index = it.next();
			int value = sudoku[index];
			mask[index] = 0;
			sudoku[index] = 0;

			SudokuSolver solver = new SudokuSolver(sudoku);
			if (solver.getSolutions(2).size() != 1) {
				mask[index] = 1;
				sudoku[index] = value;
				it.remove();
				continue;
			}

			if (depth >= numberOfHoles) {
				// Found solution
				solved = true;
			} else {
				makeHole(depth + 1, removeFirstAndShuffleCopy(options));
				if (solved) {
					return;
				}
			}

			mask[index] = 1;
			sudoku[index] = value;
			it.remove();
		}
	}

	private static LinkedList<Integer> removeFirstAndShuffleCopy(LinkedList<Integer> list) {
		LinkedList<Integer> copy = new LinkedList<>(list);
		if (copy.isEmpty()) {
			return copy;
		}

		copy.removeFirst();
		Collections.shuffle(copy);

		return copy;
	}
}
