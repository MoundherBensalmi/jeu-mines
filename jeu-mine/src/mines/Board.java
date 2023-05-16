package mines;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class Board extends JPanel {
	private static final long serialVersionUID = 6195235521361212179L;

	private static final int NUM_IMAGES = 13;
    private static final int CELL_SIZE = 15;

    private static final int COVER_FOR_CELL = 10;
    private static final int MARK_FOR_CELL = 10;
    private static final int EMPTY_CELL = 0;
    private static final int MINE_CELL = 9;
    private static final int COVERED_MINE_CELL = MINE_CELL + COVER_FOR_CELL;
    public static final int COVERED_EMPTY_CELL = -1;
    private static final int MARKED_MINE_CELL = COVERED_MINE_CELL + MARK_FOR_CELL;

    private static final int DRAW_MINE = 9;
    private static final int DRAW_COVER = 10;
    private static final int DRAW_MARK = 11;
    private static final int DRAW_WRONG_MARK = 12;

    private int[] field;
    private boolean inGame;
    private int minesLeft;
    private transient Image[] img;
    private static final int mines = 40;
    private static final int rows = 16;
    private static final int cols = 16;
    private int allCells;
    private final JLabel statusbar;
    private static  SecureRandom random = new SecureRandom();


    public Board(JLabel statusbar) {

        this.statusbar = statusbar;

        img = new Image[NUM_IMAGES];

        for (int i = 0; i < NUM_IMAGES; i++) {
			  img[i] = (new ImageIcon(Thread.currentThread().getContextClassLoader().getResource((i) + ".gif"))).getImage();

        }

        setDoubleBuffered(true);

        addMouseListener(new MinesAdapter());
     intiBoard();

    }

    public void intiBoard(){
        newGame();
    }


    public void newGame() {
        
        int position;

        inGame = true;
        minesLeft = mines;

        allCells = rows * cols;
        field = new int[allCells];
        Arrays.fill(field, COVER_FOR_CELL);

        statusbar.setText(Integer.toString(minesLeft));

        int i = 0;
        while (i < mines) {
            position = random.nextInt(allCells);

            if (field[position] != COVERED_MINE_CELL) {
                field[position] = COVERED_MINE_CELL;
                i++;

                int[] neighbors = getNeighbors(position);

                for (int neighbor : neighbors) {
                    if (neighbor >= 0 && neighbor < allCells && field[neighbor] != COVERED_MINE_CELL && field[neighbor] != COVERED_EMPTY_CELL) {
                        field[neighbor] += 1;
                    }
                }
            }
        }
    }

    private int[] getNeighbors(int position) {
        int[] neighbors;
        int currentCol = position % cols;

        if (currentCol > 0 && currentCol < cols - 1) {
            neighbors = new int[]{position - 1 - cols, position - 1, position + cols - 1, position - cols, position + cols, position - cols + 1, position + cols + 1, position + 1};
        } else if (currentCol == 0) {
            neighbors = new int[]{position - cols, position + cols, position - cols + 1, position + cols + 1, position + 1};
        } else {
            neighbors = new int[]{position - 1 - cols, position - 1, position + cols - 1, position - cols, position + cols};
        }

        return neighbors;
    }


    public void findEmptyCells(int j) {
        int currentCol = j % cols;
        int cell;

        if (currentCol > 0) {
            cell = j - cols - 1;
            updateCellAndCheckForEmpty(cell);

            cell = j - 1;
            updateCellAndCheckForEmpty(cell);

            cell = j + cols - 1;
            updateCellAndCheckForEmpty(cell);
        }

        cell = j - cols;
        updateCellAndCheckForEmpty(cell);

        cell = j + cols;
        updateCellAndCheckForEmpty(cell);

        if (currentCol < (cols - 1)) {
            cell = j - cols + 1;
                updateCellAndCheckForEmpty(cell);
    
                cell = j + cols + 1;
                updateCellAndCheckForEmpty(cell);
    
                cell = j + 1;
                updateCellAndCheckForEmpty(cell);
            }
        }
    
        private void updateCellAndCheckForEmpty(int cell) {
            if (isValidCell(cell) && field[cell] > MINE_CELL && field[cell] <= COVERED_MINE_CELL) {
                field[cell] -= COVER_FOR_CELL;
                if (field[cell] == EMPTY_CELL) {
                    findEmptyCells(cell);
                }
            }
        }
    
        private boolean isValidCell(int cell) {
            return cell >= 0 && cell < allCells;
        }
    
        @Override
        public void paint(Graphics g) {
    
            int cell = 0;
            int uncover = 0;
    
    
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
    
                    cell = field[(i * cols) + j];
    
                    if (inGame && cell == MINE_CELL)
                        inGame = false;
    
                    if (!inGame) {
                        if (cell == COVERED_MINE_CELL) {
                            cell = DRAW_MINE;
                        } else if (cell == MARKED_MINE_CELL) {
                            cell = DRAW_MARK;
                        } else if (cell > COVERED_MINE_CELL) {
                            cell = DRAW_WRONG_MARK;
                        } else if (cell > MINE_CELL) {
                            cell = DRAW_COVER;
                        }
    
    
                    } else {
                        if (cell > COVERED_MINE_CELL)
                            cell = DRAW_MARK;
                        else if (cell > MINE_CELL) {
                            cell = DRAW_COVER;
                            uncover++;
                        }
                    }
    
                    g.drawImage(img[cell], (j * CELL_SIZE),
                        (i * CELL_SIZE), this);
                }
            }
    
    
            if (uncover == 0 && inGame) {
                inGame = false;
                statusbar.setText("Game won");
            } else if (!inGame)
                statusbar.setText("Game lost");
        }
    
    
        class MinesAdapter extends MouseAdapter {
            @Override
            public void mousePressed(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
    
                int cCol = x / CELL_SIZE;
                int cRow = y / CELL_SIZE;
    
                if (!inGame) {
                    newGame();
                    repaint();
                }
    
                if ((x < cols * CELL_SIZE) && (y < rows * CELL_SIZE)) {
                    if (e.getButton() == MouseEvent.BUTTON3) {
                        handleRightClick(cRow, cCol);
                    } else {
                        handleLeftClick(cRow, cCol);
                    }
                }
            }
    
            private void handleRightClick(int cRow, int cCol) {
                if (field[(cRow * cols) + cCol] > MINE_CELL) {
    
                    if (field[(cRow * cols) + cCol] <= COVERED_MINE_CELL) {
                        if (minesLeft > 0) {
                            field[(cRow * cols) + cCol] += MARK_FOR_CELL;
                            minesLeft--;
                            statusbar.setText(Integer.toString(minesLeft));
                        } else {
                            statusbar.setText("No marks left");
                        }
                    } else {
                        field[(cRow * cols) + cCol] -= MARK_FOR_CELL;
                        minesLeft++;
                        statusbar.setText(Integer.toString(minesLeft));
                    }
    
                    repaint();
                }
            }
    
            private void handleLeftClick(int cRow, int cCol) {
                if (field[(cRow * cols) + cCol] > COVERED_MINE_CELL) {
                    return;
                }
    
    
                if ((field[(cRow * cols) + cCol] > MINE_CELL) && (field[(cRow * cols) + cCol] < MARKED_MINE_CELL)) {
                    field[(cRow * cols) + cCol] -= COVER_FOR_CELL;
    
                    if (field[(cRow * cols) + cCol] == MINE_CELL) {
                        inGame = false;
                    }
    
                    if (field[(cRow * cols) + cCol] == EMPTY_CELL) {
                        findEmptyCells((cRow * cols) + cCol);
                    }
                }
    
                repaint();
            }
    
        }
}
