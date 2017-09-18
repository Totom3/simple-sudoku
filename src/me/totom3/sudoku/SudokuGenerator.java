package me.totom3.sudoku;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

/**
 *
 * @author Totom3
 */
public class SudokuGenerator {

	private static final List<Integer> DIGITS = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);

	public static void main(String[] args) {
		final int MAX = 4000000;
		long totalTime = 0;
		for (int attempts = 0; attempts <= MAX; ++attempts) {
			long t = System.currentTimeMillis();
			int[] sudoku = generateSudoku();
			totalTime += (System.currentTimeMillis() - t);
		}

		System.out.println("Avg time: " + ((double) totalTime / MAX));
		System.out.println("Total time: " + (totalTime/1000) + "s");
	}
	
	private SudokuGenerator() {
	}

	public static int[] generateSudoku() {
		int[] sudokuGrid = new int[81];

		Random random = new Random();

		List<Integer>[] rows = new List[9];
		List<Integer>[] columns = new List[9];
		List<Integer>[] groups = new List[9];
		List<Integer>[] exclusions = new List[81];

		// Generate arrays
		for (int i = 0; i < 9; ++i) {
			rows[i] = new ArrayList<>(DIGITS);
			columns[i] = new ArrayList<>(DIGITS);
			groups[i] = new ArrayList<>(DIGITS);
		}

		// Generate sudoku
		for (int i = 0; i < 81; ++i) {
			int rowNumber = getRow(i);
			int columnNumber = getColumn(i);
			int groupNumber = getGroup(i);
			List<Integer> row = rows[rowNumber];
			List<Integer> column = columns[columnNumber];
			List<Integer> group = groups[groupNumber];

			List<Integer> list = intersect(row, column, group);
			List<Integer> excl = exclusions[i];
			if (excl != null)
				list.removeAll(excl);

			if (list.isEmpty()) {
				// Problem! Time to backtrack.

				// Reset the exclusions for the current cell
				exclusions[i] = null;

				int prevI = i - 1;

				// Re-add the previous number & add exclusion
				int previousNumber = sudokuGrid[prevI];
				rows[getRow(prevI)].add(previousNumber);
				columns[getColumn(prevI)].add(previousNumber);
				groups[getGroup(prevI)].add(previousNumber);
				sudokuGrid[prevI] = 0;

				excl = exclusions[prevI];
				if (excl == null) {
					excl = new ArrayList<>();
					exclusions[prevI] = excl;
				}

				excl.add(previousNumber);

				// Go back in time
				i -= 2; // Decrement by 2 because the loop will add 1
				continue;
			}

			int number = selectRandom(random, list);
			sudokuGrid[i] = number;
			row.remove((Integer) number);
			column.remove((Integer) number);
			group.remove((Integer) number);
		}

		return sudokuGrid;
	}

	public static void printSudoku(int[] sudoku) {
		for (int i = 0; i < 81; ++i) {
			int row = i / 9;
			int column = i % 9;

			System.out.print(sudoku[i]);

			if (column == 2 || column == 5) {
				System.out.print('|');
			} else if (column == 8) {
				System.out.print('\n');
				if (row == 2 || row == 5)
					System.out.println("-----------");
			}
		}
	}
 
	// Utility methods
	private static int selectRandom(Random random, List<Integer> list) {
		if (list.isEmpty())
			throw new NoSuchElementException("List is empty");

		if (list.size() == 1)
			return list.get(0);

		return list.get(random.nextInt(list.size()));
	}

	private static List<Integer> intersect(List<Integer> list1, List<Integer> list2, List<Integer> list3) {
		List<Integer> result = new ArrayList<>(list1);
		result.removeIf(i -> !list2.contains(i) || !list3.contains(i));
		return result;
	}

	private static int getRow(int i) {
		return i / 9;
	}

	private static int getColumn(int i) {
		return i % 9;
	}

	private static int getGroup(int i) {
		// Java equivalent of 3*floor(i/27) + (floor(i/3) mod 3)
		return 3 * (i / 27) + ((i / 3) % 3);
	}
}
