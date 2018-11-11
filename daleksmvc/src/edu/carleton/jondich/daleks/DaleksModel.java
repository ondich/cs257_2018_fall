/**
 * DaleksModel.java
 * Jeff Ondich, 2018
 *
 * The Model for this MVC sample application based on the old game Daleks.
 */
package edu.carleton.jondich.daleks;

import java.util.Random;

public class DaleksModel {

    public enum CellValue {
        EMPTY, RUNNER, DALEK, SCRAPHEAP
    };

    private boolean gameOver;
    private int score;
    private int level;

    // Note that runnerRow, runnerColumn, and dalekCount are all redundant with
    // the contents of cells, so we have to be careful throughout to keep them
    // coherent. We maintain this redundancy to avoid lags for large boards.
    private CellValue[][] cells;
    private int runnerRow;
    private int runnerColumn;
    private int dalekCount;

    public DaleksModel(int rowCount, int columnCount) {
        assert rowCount > 0 && columnCount > 0;
        this.cells = new CellValue[rowCount][columnCount];
        this.startNewGame();
    }

    public void startNewGame() {
        this.gameOver = false;
        this.score = 0;
        this.level = 1;
        this.initializeLevel();
    }

    public void startNextLevel() {
        if (this.isLevelComplete()) {
            this.level++;
            this.initializeLevel();
        }
    }

    public boolean isLevelComplete() {
        return this.dalekCount == 0;
    }

    public boolean isGameOver() {
        return this.gameOver;
    }

    private void initializeLevel() {
        int rowCount = this.cells.length;
        int columnCount = this.cells[0].length;

        // Empty all the cells
        for (int row = 0; row < rowCount; row++) {
            for (int column = 0; column < columnCount; column++) {
                this.cells[row][column] = CellValue.EMPTY;
            }
        }

        // Place the runner
        Random random = new Random();
        this.runnerRow = random.nextInt(rowCount);
        this.runnerColumn = random.nextInt(columnCount);
        this.cells[this.runnerRow][this.runnerColumn] = CellValue.RUNNER;

        // Place the daleks
        this.dalekCount = this.numberOfDaleksForLevel(this.level);
        for (int k = 0; k < this.dalekCount; k++) {
            int row = random.nextInt(rowCount);
            int column = random.nextInt(columnCount);
            if (this.cells[row][column] == CellValue.EMPTY) {
                this.cells[row][column] = CellValue.DALEK;
            }
        }
    }

    private int numberOfDaleksForLevel(int level) {
        int rowCount = this.cells.length;
        int columnCount = this.cells[0].length;
        int cellCount = rowCount * columnCount;

        // Using a sigmoid function shifted and scaled to gradually increase the density of daleks
        // up to an asymptotic limit.
        double numerator = Math.pow(Math.E, (double)level - 8.0);
        double denominator = numerator + 1.0;
        double dalekDensity = 0.2 * numerator / denominator;
        if (dalekDensity < 0.008) {
            dalekDensity = 0.008;
        }

        return (int)Math.round(dalekDensity * (double)cellCount);
    }

    public int getRowCount() {
        return this.cells.length;
    }

    public int getColumnCount() {
        assert this.cells.length > 0;
        return this.cells[0].length;
    }

    public int getScore() {
        return this.score;
    }

    public CellValue getCellValue(int row, int column) {
        assert row >= 0 && row < this.cells.length && column >= 0 && column < this.cells[0].length;
        return this.cells[row][column];
    }

    /**
     * Moves the runner by the specified distances in the row (y) and column (x)
     * directions, and then calls moveDaleksToFollowRunner.
     *
     * @param rowChange the offset to be added to the runner's current row (thus,
     *                  negative rowChange values will move the runner up the grid,
     *                  while positive values will move the runner down the grid). If
     *                  rowChange would cause the runner to leave the board, the runner
     *                  is moved to either to the top row of the grid (if rowChange < 0)
     *                  or the bottom row of the grid (if rowChange > 0).
     * @param columnChange the offset to be added to the runner's current column.
     *                     If columnChange would cause the runner to leave the board,
     *                     the runner is moved either to the leftmost column (if columnChange < 0)
     *                     or the rightmost column (if columnChange > 0).
     */
    public void moveRunnerBy(int rowChange, int columnChange) {
        if (this.gameOver || this.dalekCount == 0) {
            return;
        }

        int newRow = this.runnerRow + rowChange;
        if (newRow < 0) {
            newRow = 0;
        }
        if (newRow >= this.getRowCount()) {
            newRow = this.getRowCount() - 1;
        }

        int newColumn = this.runnerColumn + columnChange;
        if (newColumn < 0) {
            newColumn = 0;
        }
        if (newColumn >= this.getColumnCount()) {
            newColumn = this.getColumnCount() - 1;
        }


        this.cells[this.runnerRow][this.runnerColumn] = CellValue.EMPTY;
        this.runnerRow = newRow;
        this.runnerColumn = newColumn;
        this.cells[this.runnerRow][this.runnerColumn] = CellValue.RUNNER;
        this.moveDaleksToFollowRunner();
    }

    /**
     * Moves the runner to a random location on the grid, and then calls
     * moveDaleksToFollowRunner.
     */
    public void teleportRunner() {
        if (this.gameOver || this.dalekCount == 0) {
            return;
        }

        int rowCount = this.cells.length;
        int columnCount = this.cells[0].length;
        Random random = new Random();
        int newRow = random.nextInt(rowCount);
        int newColumn = random.nextInt(columnCount);
        this.cells[this.runnerRow][this.runnerColumn] = CellValue.EMPTY;
        this.runnerRow = newRow;
        this.runnerColumn = newColumn;
        this.cells[this.runnerRow][this.runnerColumn] = CellValue.RUNNER;
        this.moveDaleksToFollowRunner();
    }


    /**
     * Moves all the daleks to get as close to the runner's new position as
     * possible using a one-square move (horizontally, vertically, or diagonally).
     * If two objects end up on the same square as a result of their motion, both
     * are removed from the grid and replaced by a scrap heap. Each dalek removed
     * from the grid increases the score by 1. If the runner is removed from the
     * grid, the game is over.
     */
    private void moveDaleksToFollowRunner() {
        // Initialize a new game board
        int rowCount = this.cells.length;
        int columnCount = this.cells[0].length;
        CellValue[][] newCells = new CellValue[rowCount][columnCount];
        for (int row = 0; row < rowCount; row++) {
            for (int column = 0; column < columnCount; column++) {
                newCells[row][column] = CellValue.EMPTY;
            }
        }

        // Move the daleks on the old game board to their new positions on
        // the new game board. If a collision occurs, adjust score, check for
        // game-over, check for level-complete, etc.

        // Note that because the runner is on the grid, the newRow and newColumn
        // values for the Daleks will never leave the grid.
        for (int row = 0; row < rowCount; row++) {
            for (int column = 0; column < columnCount; column++) {
                CellValue cellValue = this.cells[row][column];
                if (cellValue != CellValue.EMPTY) {
                    int newRow = row;
                    int newColumn = column;
                    if (cellValue == CellValue.DALEK) {
                        if (newRow < this.runnerRow) {
                            newRow++;
                        } else if (newRow > this.runnerRow) {
                            newRow--;
                        }

                        if (newColumn < this.runnerColumn) {
                            newColumn++;
                        } else if (newColumn > this.runnerColumn) {
                            newColumn--;
                        }
                    }

                    CellValue newCellValue = newCells[newRow][newColumn];
                    if (newCellValue == CellValue.EMPTY) {
                        newCells[newRow][newColumn] = cellValue;

                    } else {
                        if (cellValue == CellValue.DALEK) {
                            this.score++;
                            this.dalekCount--;
                        }
                        if (newCellValue == CellValue.DALEK) {
                            this.score++;
                            this.dalekCount--;
                        }
                        if (cellValue == CellValue.RUNNER || newCellValue == CellValue.RUNNER) {
                            this.gameOver = true;
                        }

                        newCells[newRow][newColumn] = CellValue.SCRAPHEAP;
                    }
                }
            }
        }

       this.cells = newCells;
    }
}
