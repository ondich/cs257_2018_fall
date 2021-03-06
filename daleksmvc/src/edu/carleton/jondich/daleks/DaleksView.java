/**
 * DaleksView.java
 * Jeff Ondich, 2018
 *
 * The View for this MVC sample application based on the old game Daleks.
 */
package edu.carleton.jondich.daleks;

import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class DaleksView extends Group {
    public final static double CELL_WIDTH = 20.0;

    @FXML private int rowCount;
    @FXML private int columnCount;
    private Rectangle[][] cellViews;

    public DaleksView() {
    }

    public int getRowCount() {
        return this.rowCount;
    }

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
        this.initializeGrid();
    }

    public int getColumnCount() {
        return this.columnCount;
    }

    public void setColumnCount(int columnCount) {
        this.columnCount = columnCount;
        this.initializeGrid();
    }

    private void initializeGrid() {
        if (this.rowCount > 0 && this.columnCount > 0) {
            this.cellViews = new Rectangle[this.rowCount][this.columnCount];
            for (int row = 0; row < this.rowCount; row++) {
                for (int column = 0; column < this.columnCount; column++) {
                    Rectangle rectangle = new Rectangle();
                    rectangle.setX((double)column * CELL_WIDTH);
                    rectangle.setY((double)row * CELL_WIDTH);
                    rectangle.setWidth(CELL_WIDTH);
                    rectangle.setHeight(CELL_WIDTH);
                    this.cellViews[row][column] = rectangle;
                    this.getChildren().add(rectangle);
                }
            }
        }
    }

    public void update(DaleksModel model) {
        assert model.getRowCount() == this.rowCount && model.getColumnCount() == this.columnCount;
        for (int row = 0; row < this.rowCount; row++) {
            for (int column = 0; column < this.columnCount; column++) {
                DaleksModel.CellValue cellValue = model.getCellValue(row, column);
                if (cellValue == DaleksModel.CellValue.DALEK) {
                    this.cellViews[row][column].setFill(Color.RED);
                } else if (cellValue == DaleksModel.CellValue.SCRAPHEAP) {
                    this.cellViews[row][column].setFill(Color.BLACK);
                } else if (cellValue == DaleksModel.CellValue.RUNNER) {
                    this.cellViews[row][column].setFill(Color.GREEN);
                } else {
                    this.cellViews[row][column].setFill(Color.WHITE);
                }
            }
        }
    }
}
