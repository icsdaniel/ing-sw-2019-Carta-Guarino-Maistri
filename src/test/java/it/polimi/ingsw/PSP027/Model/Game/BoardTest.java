package it.polimi.ingsw.PSP027.Model.Game;

import it.polimi.ingsw.PSP027.Model.Game.Board;
import it.polimi.ingsw.PSP027.Model.Game.Cell;
import org.junit.Before;
import org.junit.Test;
import java.util.List;
import java.util.ArrayList;
import static org.junit.Assert.*;

/**
 * @author danielecarta
 */

public class BoardTest {
    Board gameBoard;

    /**
     * setUps a Board to be tested
     */

    @Before
    public void setUp() {
        gameBoard = new Board();
    }

    @Test
    public void getCell_wrongIndex() {
        assertEquals(null,gameBoard.getCell(25));
    }
    @Test
    public void resetBoard_shouldResetTheBoard() {
        Board expectedBoard = gameBoard;
        gameBoard.getCell(0).addLevel();
        gameBoard.getCell(0).addLevel();
        gameBoard.getCell(0).addLevel();
        gameBoard.getCell(0).addDome();
        gameBoard.getCell(3).addLevel();
        gameBoard.getCell(6).addLevel();
        gameBoard.getCell(14).addLevel();
        gameBoard.resetBoard();
        for(Cell cell:gameBoard.getBoard()){
            assertFalse(cell.checkDome());
            assertEquals(0,cell.getLevel());
            assertFalse(cell.isOccupiedByWorker());
        }
    }

    @Test
    public void getNextCellAlongThePath_itExists() {
        //going n
        assertEquals(gameBoard.getCell(22),gameBoard.getNextCellAlongThePath(gameBoard.getCell(12),gameBoard.getCell(17)));
        //going ne
        assertEquals(gameBoard.getCell(24),gameBoard.getNextCellAlongThePath(gameBoard.getCell(12),gameBoard.getCell(18)));
        //going e
        assertEquals(gameBoard.getCell(14),gameBoard.getNextCellAlongThePath(gameBoard.getCell(12),gameBoard.getCell(13)));
        //going se
        assertEquals(gameBoard.getCell(4),gameBoard.getNextCellAlongThePath(gameBoard.getCell(12),gameBoard.getCell(8)));
        //going s
        assertEquals(gameBoard.getCell(2),gameBoard.getNextCellAlongThePath(gameBoard.getCell(12),gameBoard.getCell(7)));
        //going sw
        assertEquals(gameBoard.getCell(0),gameBoard.getNextCellAlongThePath(gameBoard.getCell(12),gameBoard.getCell(6)));
        //going w
        assertEquals(gameBoard.getCell(10),gameBoard.getNextCellAlongThePath(gameBoard.getCell(12),gameBoard.getCell(11)));
        //going nw
        assertEquals(gameBoard.getCell(20),gameBoard.getNextCellAlongThePath(gameBoard.getCell(12),gameBoard.getCell(16)));

    }

    @Test
    public void getNextCellAlongThePath_itDoesntExists() {
        Cell x42FirstArgumentCell = gameBoard.getCell(8);
        Cell x51SecondArgument = gameBoard.getCell(4);
        Cell actualCell = gameBoard.getNextCellAlongThePath(x42FirstArgumentCell,x51SecondArgument);
        assertEquals(null,actualCell);
    }

    @Test
    public void getNeighbouringCells_usingANonPerimetricCell() {
        Cell x23ArgumentCell = gameBoard.getCell(11);
        Cell x12 = gameBoard.getCell(5);
        Cell x21 = gameBoard.getCell(6);
        Cell x32 = gameBoard.getCell(7);
        Cell x13 = gameBoard.getCell(10);
        Cell x33 = gameBoard.getCell(12);
        Cell x14 = gameBoard.getCell(15);
        Cell x24 = gameBoard.getCell(16);
        Cell x34 = gameBoard.getCell(17);
        List<Cell> expectedCells= new ArrayList<Cell>();
        expectedCells.add(x12);
        expectedCells.add(x21);
        expectedCells.add(x32);
        expectedCells.add(x13);
        expectedCells.add(x33);
        expectedCells.add(x14);
        expectedCells.add(x24);
        expectedCells.add(x34);
        List<Cell> actualCells = gameBoard.getNeighbouringCells(x23ArgumentCell);
        assertTrue(actualCells.containsAll(expectedCells) && expectedCells.containsAll(actualCells));
    }

    @Test
    public void getNeighbouringCells_usingAPerimetricCell() {
        Cell x14ArgumentCell = gameBoard.getCell(15);
        Cell x13 = gameBoard.getCell(10);
        Cell x23 = gameBoard.getCell(11);
        Cell x24 = gameBoard.getCell(16);
        Cell x25 = gameBoard.getCell(21);
        Cell x15 = gameBoard.getCell(20);
        List<Cell> expectedCells= new ArrayList<Cell>();
        expectedCells.add(x13);
        expectedCells.add(x23);
        expectedCells.add(x24);
        expectedCells.add(x25);
        expectedCells.add(x15);
        List<Cell> actualCells = gameBoard.getNeighbouringCells(x14ArgumentCell);
        assertTrue(actualCells.containsAll(expectedCells) && expectedCells.containsAll(actualCells));
    }
}