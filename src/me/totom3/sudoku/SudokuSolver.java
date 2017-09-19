package me.totom3.sudoku;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Totom3
 */
public class SudokuSolver {

	private static final List<Integer> DIGITS = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);

	private final List<Integer>[] rowMissingValues = new List[9];
	private final List<Integer>[] columnMissingValues = new List[9];
	private final List<Integer>[] groupMissingValues = new List[9];

	private final List<Integer> emptyCells, nonEmptyCells;
	private final LinkedList<PotentialCellSolution> potentialSolutions;

	private final int[] sudoku;
	private final Set<int[]> solutions;

	private boolean solved;

	public SudokuSolver(int[] sudoku) {
		this.sudoku = sudoku;
		this.emptyCells = new ArrayList<>();
		this.nonEmptyCells = new ArrayList<>();
		this.solutions = new HashSet<>();
		this.potentialSolutions = new LinkedList<>();

		for (int i = 0; i < 81; ++i) {
			if (sudoku[i] == 0) {
				emptyCells.add(i);
			} else {
				nonEmptyCells.add(i);
			}
		}

		if (emptyCells.isEmpty()) {
			solved = true;

			int[] solution = new int[81];
			System.arraycopy(sudoku, 0, solution, 0, 81);
			solutions.add(solution);
			return;
		}

		for (int i = 0; i < 9; ++i) {
			rowMissingValues[i] = new ArrayList<>(DIGITS);
			columnMissingValues[i] = new ArrayList<>(DIGITS);
			groupMissingValues[i] = new ArrayList<>(DIGITS);
		}

		for (int index : nonEmptyCells) {
			int clue = sudoku[index];

			rowMissingValues[getRow(index)].remove((Integer) clue);
			columnMissingValues[getColumn(index)].remove((Integer) clue);
			groupMissingValues[getGroup(index)].remove((Integer) clue);
		}
	}

	public Set<int[]> getSolutions() {
		return getSolutions(-1);
	}
	
	public Set<int[]> getSolutions(int limit) {
		if (solved)
			return solutions;

		try {
			evaluateCell(0, limit);
		} catch (SolvedException ex) {
		}

		solved = true;

		emptyCells.clear();
		nonEmptyCells.clear();
		potentialSolutions.clear();

		return solutions;
	}

	private void evaluateCell(int emptyCellIndex, int limit) {
		boolean isLastEmptyCell = emptyCells.size() == emptyCellIndex + 1;
		int cellIndex = emptyCells.get(emptyCellIndex);

		int row = getRow(cellIndex);
		int column = getColumn(cellIndex);
		int group = getGroup(cellIndex);

		List<Integer> possibleValues = intersect(rowMissingValues[row], columnMissingValues[column], groupMissingValues[group]);

		// Branch is dead; stop here.
		if (possibleValues.isEmpty())
			return;

		for (int value : possibleValues) {
			removeFromMissingValues(value, row, column, group);
			potentialSolutions.add(new PotentialCellSolution(cellIndex, value));

			if (isLastEmptyCell) {
				// Save this as a solution
				saveSolution();
				if (limit >= 1 && solutions.size() >= limit)
					throw new SolvedException();
			} else {
				evaluateCell(emptyCellIndex + 1, limit);
			}

			addToMissingValues(value, row, column, group);
			potentialSolutions.removeLast();
		}
	}

	private void saveSolution() {
		int[] solution = new int[81];
		System.arraycopy(sudoku, 0, solution, 0, 81);

		for (PotentialCellSolution pcs : potentialSolutions) {
			solution[pcs.index] = pcs.solution;
		}

		solutions.add(solution);
	}

	private void addToMissingValues(int number, int row, int column, int group) {
		rowMissingValues[row].add(number);
		columnMissingValues[column].add(number);
		groupMissingValues[group].add(number);
	}

	private void removeFromMissingValues(int number, int row, int column, int group) {
		rowMissingValues[row].remove((Integer) number);
		columnMissingValues[column].remove((Integer) number);
		groupMissingValues[group].remove((Integer) number);
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

	private static class PotentialCellSolution {

		int index;
		int solution;

		PotentialCellSolution(int index, int solution) {
			this.index = index;
			this.solution = solution;
		}
	}

	private static class SolvedException extends RuntimeException {

	}
}
