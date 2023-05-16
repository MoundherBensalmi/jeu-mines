package mines;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.security.SecureRandom;
import java.util.Arrays;

public class Board extends JPanel {
    private static final long serialVersionUID = 6195235521361212179L;

    private static final int NUM_IMAGES = 13;
    private static final int CELL_SIZE = 15;

    private static final int COVER_FOR_CELL = 10;
    private static final int MARK_FOR_CELL = 10;
    private static final int EMPTY_CELL = 0;
    private static final int MINE_CELL = 9;
    private static final int COVERED_MINE_CELL = MINE_CELL + COVER_FOR_CELL;
    private static final int MARKED_MINE_CELL = COVERED_MINE_CELL + MARK_FOR_CELL;
    public static final int COVERED_EMPTY_CELL = -1;

    private static final int DRAW_MINE = 9;
    private static final int DRAW_COVER = 10;
    private static final int DRAW_MARK = 11;
    private static final int DRAW_WRONG_MARK = 12;

    private int[] field;
    private boolean inGame;
    private int mines_left;
    private final Image[] img;
    private static int mines = 40;
    private static int rows = 16;
    private static int cols = 16;
    private static int all_cells;
    private  final JLabel statusbar ;
    private static  SecureRandom random = new SecureRandom();


    public Board(JLabel statusbar) {

        this.statusbar = statusbar;

        img = new Image[NUM_IMAGES];

        for (int i = 0; i < NUM_IMAGES; i++) {
           img[i] = (new ImageIcon(Thread.currentThread().getContextClassLoader().getResource((i) + ".gif"))).getImage();

        }

        setDoubleBuffered(true);

        addMouseListener(new MinesAdapter());
        newGame();
    }


    public void newGame() {
        int position;

        inGame = true;
        mines_left = mines;

        all_cells = rows * cols;
        field = new int[all_cells];
        Arrays.fill(field, COVER_FOR_CELL);

        statusbar.setText(Integer.toString(mines_left));

        int i = 0;
        while (i < mines) {
            position = random.nextInt(all_cells);

            if (field[position] != COVERED_MINE_CELL) {
                field[position] = COVERED_MINE_CELL;
                i++;

                int[] neighbors = getNeighbors(position);

                for (int neighbor : neighbors) {
                    if (neighbor >= 0 && neighbor < all_cells && field[neighbor] != COVERED_MINE_CELL && field[neighbor] != COVERED_EMPTY_CELL) {
                        field[neighbor] += 1;
                    }
                }
            }
        }
    }

    private int[] getNeighbors(int position) {
        int[] neighbors;
        int current_col = position % cols;

        if (current_col > 0 && current_col < cols - 1) {
            neighbors = new int[]{position - 1 - cols, position - 1, position + cols - 1, position - cols, position + cols, position - cols + 1, position + cols + 1, position + 1};
        } else if (current_col == 0) {
            neighbors = new int[]{position - cols, position + cols, position - cols + 1, position + cols + 1, position + 1};
        } else {
            neighbors = new int[]{position - 1 - cols, position - 1, position + cols - 1, position - cols, position + cols};
        }

        return neighbors;
    }

  public void find_empty_cells(int j) {
    int current_col = j % cols;
    int cell;

    if (current_col > 0) {
        cell = j - cols - 1;
        if (isValidCell(cell) && field[cell] > MINE_CELL && field[cell] <= COVERED_MINE_CELL) {
            field[cell] -= COVER_FOR_CELL;
            if (field[cell] == EMPTY_CELL) {
                find_empty_cells(cell);
            }
        }

        cell = j - 1;
        if (isValidCell(cell) && field[cell] > MINE_CELL && field[cell] <= COVERED_MINE_CELL) {
            field[cell] -= COVER_FOR_CELL;
            if (field[cell] == EMPTY_CELL) {
                find_empty_cells(cell);
            }
        }

        cell = j + cols - 1;
        if (isValidCell(cell) && field[cell] > MINE_CELL && field[cell] <= COVERED_MINE_CELL) {
            field[cell] -= COVER_FOR_CELL;
            if (field[cell] == EMPTY_CELL) {
                find_empty_cells(cell);
            }
        }
    }

    cell = j - cols;
    if (isValidCell(cell) && field[cell] > MINE_CELL && field[cell] <= COVERED_MINE_CELL) {
        field[cell] -= COVER_FOR_CELL;
        if (field[cell] == EMPTY_CELL) {
            find_empty_cells(cell);
        }
    }

    cell = j + cols;
    if (isValidCell(cell) && field[cell] > MINE_CELL && field[cell] <= COVERED_MINE_CELL) {
        field[cell] -= COVER_FOR_CELL;
        if (field[cell] == EMPTY_CELL) {
            find_empty_cells(cell);
        }
    }

    if (current_col < (cols - 1)) {
        cell = j - cols + 1;
        if (isValidCell(cell) && field[cell] > MINE_CELL && field[cell] <= COVERED_MINE_CELL) {
            field[cell] -= COVER_FOR_CELL;
            if (field[cell] == EMPTY_CELL) {
                find_empty_cells(cell);
            }
        }

        cell = j + cols + 1;
        if (isValidCell(cell) && field[cell] > MINE_CELL && field[cell] <= COVERED_MINE_CELL) {
            field[cell] -= COVER_FOR_CELL;
            if (field[cell] == EMPTY_CELL) {
                find_empty_cells(cell);
            }
        }

        cell = j + 1;
        if (isValidCell(cell) && field[cell] > MINE_CELL && field[cell] <= COVERED_MINE_CELL) {
            field[cell] -= COVER_FOR_CELL;
            if (field[cell] == EMPTY_CELL) {
                find_empty_cells(cell);
            }
        }
    }
}

private boolean isValidCell(int cell) {
    return cell >= 0 && cell < all_cells;
}

        public void paint(Graphics g){

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
            public void mousePressed(MouseEvent e) {

                int x = e.getX();
                int y = e.getY();

                int cCol = x / CELL_SIZE;
                int cRow = y / CELL_SIZE;

                boolean rep = false;


                if (!inGame) {
                    newGame();
                    repaint();
                }


                if ((x < cols * CELL_SIZE) && (y < rows * CELL_SIZE)) {

                    if (e.getButton() == MouseEvent.BUTTON3) {

                        if (field[(cRow * cols) + cCol] > MINE_CELL) {
                            rep = true;

                            if (field[(cRow * cols) + cCol] <= COVERED_MINE_CELL) {
                                if (mines_left > 0) {
                                    field[(cRow * cols) + cCol] += MARK_FOR_CELL;
                                    mines_left--;
                                    statusbar.setText(Integer.toString(mines_left));
                                } else
                                    statusbar.setText("No marks left");
                            } else {

                                field[(cRow * cols) + cCol] -= MARK_FOR_CELL;
                                mines_left++;
                                statusbar.setText(Integer.toString(mines_left));
                            }
                        }

                    } else {

                        if (field[(cRow * cols) + cCol] > COVERED_MINE_CELL) {
                            return;
                        }

                        if ((field[(cRow * cols) + cCol] > MINE_CELL) &&
                                (field[(cRow * cols) + cCol] < MARKED_MINE_CELL)) {

                            field[(cRow * cols) + cCol] -= COVER_FOR_CELL;
                            rep = true;

                            if (field[(cRow * cols) + cCol] == MINE_CELL)
                                inGame = false;
                            if (field[(cRow * cols) + cCol] == EMPTY_CELL)
                                find_empty_cells((cRow * cols) + cCol);
                        }
                    }

                    if (rep)
                        repaint();

                }
            }
        }
    }

