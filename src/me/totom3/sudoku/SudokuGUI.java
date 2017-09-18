package me.totom3.sudoku;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.MatteBorder;

/**
 *
 * @author Tomer Moran
 */
public class SudokuGUI extends JFrame {

	private static final int SUDOKU_HOLES = 40;

	private static final String INSTRUCTIONS = "<html><b>Instructions:</b> blablabla.</html>";
	private static final String FOOTER = "Sudoku made by Tomer Moran (2017)";
	private static final String GENERATE_BOARD_TEXT = "Generate New";
	private static final String CHECK_ANSWER_TEXT = "Check Answer";

	private static final Font BOLD_FONT = new Font("Dialog", Font.BOLD, 15);
	private static final Font PLAIN_FONT = new Font("Dialog", Font.PLAIN, 15);
	private static final Font ITALIC_FONT = new Font("Dialog", Font.ITALIC, 15);

	private static final Font BOLD_FONT_L = new Font("Dialog", Font.BOLD, 17);
	private static final Font PLAIN_FONT_L = new Font("Dialog", Font.PLAIN, 17);

	private JPanel contentPane;
	private JPanel headerPanel;
	private JPanel bodyPanel;
	private JPanel boardPanel;
	private JPanel footerPanel;

	private JLabel instructions;
	private JLabel timeLabel;
	private JLabel errorsLabel;
	private JLabel footerNote;
	private JButton generateBoardButton;
	private JButton showSolutionButton;

	// Ordered by rows. Usage: cells[row][column]
	private SudokuBoardCell[][] cells;

	private boolean finished;

	public void start() {
		setSize(800, 800);
		setTitle("Sudoku Game by Tomer Moran");
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setVisible(true);
	}

	public SudokuGUI init() {

		// Initiate all components and panels
		initComponents();
		initBoardPanel(boardPanel);

		// Customize the components
		footerNote.setHorizontalAlignment(SwingConstants.RIGHT);

		// Build up the panels
		buildPanels();
		return this;
	}

	private void initComponents() {
		contentPane = new JPanel(new GridBagLayout());
		headerPanel = new JPanel();
		bodyPanel = new JPanel(new GridBagLayout());
		boardPanel = new JPanel(new GridBagLayout());
		footerPanel = new JPanel();

		headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
		footerPanel.setLayout(new BoxLayout(footerPanel, BoxLayout.Y_AXIS));

		instructions = new JLabel(INSTRUCTIONS);
		timeLabel = new JLabel(getTimeLabelText(-1));
		errorsLabel = new JLabel(getErrorsLabelText(-1));
		footerNote = new JLabel(FOOTER);

		generateBoardButton = new JButton(GENERATE_BOARD_TEXT);
		showSolutionButton = new JButton(CHECK_ANSWER_TEXT);

		instructions.setFont(PLAIN_FONT);
		timeLabel.setFont(PLAIN_FONT);
		errorsLabel.setFont(PLAIN_FONT);
		footerNote.setFont(ITALIC_FONT);
		generateBoardButton.setFont(BOLD_FONT);
		showSolutionButton.setFont(BOLD_FONT);
		footerNote.setAlignmentX(JLabel.RIGHT_ALIGNMENT);

		generateBoardButton.addActionListener(e -> generateNewBoard());
		showSolutionButton.addActionListener(e -> {
			if (finished)
				return;

			for (int row = 0; row < 9; ++row) {
				for (int column = 0; column < 9; ++column) {
					cells[row][column].checkAnswer();
				}
			}
		});
	}

	private void buildPanels() {
		headerPanel.add(instructions);
		footerPanel.add(footerNote);

		GridBagConstraints cons = makeConstraints(0, 0);
		cons.gridwidth = 2;
		bodyPanel.add(boardPanel, cons);

		Insets insetsRight = new Insets(10, 0, 0, 5);
		Insets insetsLeft = new Insets(10, 5, 0, 0);
		bodyPanel.add(generateBoardButton, makeConstraints(0, 1, 1, 0, insetsRight));
		bodyPanel.add(showSolutionButton, makeConstraints(1, 1, 1, 0, insetsLeft));
		bodyPanel.add(timeLabel, makeConstraints(0, 2, 1, 0, insetsRight));
		bodyPanel.add(errorsLabel, makeConstraints(1, 2, 1, 0, insetsLeft));

		Insets insetsBottom = new Insets(0, 0, 20, 0);
		contentPane.add(headerPanel, makeConstraints(0, 0, insetsBottom));
		contentPane.add(bodyPanel, makeConstraints(0, 1, insetsBottom));
		contentPane.add(footerPanel, makeConstraints(0, 2, insetsBottom));
		add(contentPane);
	}

	private void initBoardPanel(JPanel boardPanel) {
		cells = new SudokuBoardCell[9][9];

		for (int row = 0; row < 9; ++row) {
			for (int column = 0; column < 9; ++column) {
				SudokuBoardCell cell = new SudokuBoardCell(row, column);
				GridBagConstraints cons = makeConstraints(column, row);
				boardPanel.add(cell, cons);

				cells[row][column] = cell;
			}
		}

		generateNewBoard();
	}

	private void generateNewBoard() {
		finished = false;
		int[] sudoku = SudokuGenerator.generateSudoku();
		int[] mask = SudokuMaskGenerator.generateMask(sudoku, SUDOKU_HOLES);

		for (int row = 0; row < 9; ++row) {
			for (int column = 0; column < 9; ++column) {
				int index = 9 * row + column;

				boolean masked = (mask[index] == 1);
				SudokuBoardCell cell = cells[row][column];
				cell.setChangeable(!masked);
				cell.setDisplayDigit(masked ? sudoku[index] : 0);
				cell.solutionDigit = sudoku[index];
				cell.setBackground(CELL_BACKGROUND);
			}
		}
	}

	private static int getDigit(int keyCode) {
		switch (keyCode) {
			case KeyEvent.VK_1:
			case KeyEvent.VK_NUMPAD1:
				return 1;
			case KeyEvent.VK_2:
			case KeyEvent.VK_NUMPAD2:
				return 2;
			case KeyEvent.VK_3:
			case KeyEvent.VK_NUMPAD3:
				return 3;
			case KeyEvent.VK_4:
			case KeyEvent.VK_NUMPAD4:
				return 4;
			case KeyEvent.VK_5:
			case KeyEvent.VK_NUMPAD5:
				return 5;
			case KeyEvent.VK_6:
			case KeyEvent.VK_NUMPAD6:
				return 6;
			case KeyEvent.VK_7:
			case KeyEvent.VK_NUMPAD7:
				return 7;
			case KeyEvent.VK_8:
			case KeyEvent.VK_NUMPAD8:
				return 8;
			case KeyEvent.VK_9:
			case KeyEvent.VK_NUMPAD9:
				return 9;
			default:
				return -1;
		}
	}

	// TODO: implement actual time display
	private static String getTimeLabelText(int seconds) {
		return "<html><b>Time:</b> _____</html>";
	}

	// TODO: implement actual error count display
	private static String getErrorsLabelText(int errors) {
		return "<html><b>Errors:</b> _____</html>";
	}

	private static final Insets ZERO_INSETS = new Insets(0, 0, 0, 0);

	private static GridBagConstraints makeConstraints(int gridx, int gridy) {
		return makeConstraints(gridx, gridy, 0, 0, ZERO_INSETS);
	}

	private static GridBagConstraints makeConstraints(int gridx, int gridy, Insets insets) {
		return makeConstraints(gridx, gridy, 0, 0, insets);
	}

	private static GridBagConstraints makeConstraints(int gridx, int gridy, int weightx, int weighty, Insets insets) {
		GridBagConstraints cons = new GridBagConstraints();
		cons.gridx = gridx;
		cons.gridy = gridy;
		cons.insets = insets;
		cons.weightx = weightx;
		cons.weighty = weighty;
		cons.fill = GridBagConstraints.HORIZONTAL;
		return cons;
	}

	static final Color CELL_BACKGROUND = Color.WHITE;
	static final Color CELL_BACKGROUND_SELECTED = new Color(180, 219, 255);
	static final Color CELL_BACKGROUND_CORRECT = new Color(117, 255, 117);
	static final Color CELL_BACKGROUND_INCORRECT = new Color(255, 117, 117);

	private final class SudokuBoardCell extends JTextField {

		String previousValue = "";
		final int row, column;

		int digit, solutionDigit;
		boolean changeable;

		SudokuBoardCell(int row, int column) {
			super(0);
			this.row = row;
			this.column = column;

			computeBorder();

			setEditable(false);
			setBackground(CELL_BACKGROUND);
			setHorizontalAlignment(JTextField.CENTER);
			setFont(PLAIN_FONT_L);

			initListeners();
		}

		void initListeners() {
			addFocusListener(new FocusListener() {
				@Override
				public void focusGained(FocusEvent fe) {
					if (!finished)
						setBackground(CELL_BACKGROUND_SELECTED);
				}

				@Override
				public void focusLost(FocusEvent fe) {
					if (!finished)
						setBackground(CELL_BACKGROUND);
				}
			});

			addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent e) {
					if (!finished)
						handleArrowKeys(e);

					if (changeable)
						handleDigit(e);
				}

				void handleDigit(KeyEvent e) {
					int keyCode = e.getKeyCode();
					int newDigit = getDigit(keyCode);

					if (keyCode == KeyEvent.VK_DELETE || keyCode == KeyEvent.VK_BACK_SPACE) {
						newDigit = 0;
						e.consume();
					}

					if (newDigit == -1)
						return;

					setDisplayDigit(newDigit);
				}

				void handleArrowKeys(KeyEvent e) {
					int newRow, newColumn;
					switch (e.getKeyCode()) {
						case KeyEvent.VK_UP:
							newRow = row - 1;
							newColumn = column;
							break;
						case KeyEvent.VK_DOWN:
							newRow = row + 1;
							newColumn = column;
							break;
						case KeyEvent.VK_LEFT:
							newRow = row;
							newColumn = column - 1;
							break;
						case KeyEvent.VK_RIGHT:
							newRow = row;
							newColumn = column + 1;
							break;
						default:
							return;
					}

					if (newRow < 0 || newRow > 8 || newColumn < 0 || newColumn > 8)
						return;

					SudokuBoardCell newCell = cells[newRow][newColumn];
					newCell.requestFocusInWindow();
				}
			});
		}

		void setDisplayDigit(int digit) {
			this.digit = digit;
			setText((digit == 0) ? "" : String.valueOf(digit));
		}

		void computeBorder() {
			int top, left, bottom, right;

			top = (row == 0 || row == 3 || row == 6) ? 2 : 1;
			left = (column == 0 || column == 3 || column == 6) ? 2 : 1;
			bottom = (row == 8) ? 2 : 0;
			right = (column == 8) ? 2 : 0;

			setBorder(new MatteBorder(top, left, bottom, right, Color.BLACK));
		}

		void setChangeable(boolean changeable) {
			this.changeable = changeable;
			setFont(changeable ? PLAIN_FONT_L : BOLD_FONT_L);
		}

		void checkAnswer() {
			if (!changeable || digit == 0)
				return;

			if (digit == solutionDigit) {
				setChangeable(false);
				setBackground(CELL_BACKGROUND_CORRECT);
				revalidate();
				
				new Thread(() -> {
				try {
					Thread.sleep(5_000);
				} catch (InterruptedException ex) {
					throw new AssertionError(ex);
				}

				SwingUtilities.invokeLater(() -> {
					if (CELL_BACKGROUND_CORRECT.equals(getBackground()))
						setBackground(CELL_BACKGROUND);
				});
			}).start();
			} else {
				setBackground(CELL_BACKGROUND_INCORRECT);
			}
			
		}

		@Override
		public Dimension getPreferredSize() {
			return new Dimension(50, 50);
		}
	}

}
