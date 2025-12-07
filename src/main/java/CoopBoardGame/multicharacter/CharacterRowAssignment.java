package CoopBoardGame.multicharacter;

import com.megacrit.cardcrawl.characters.AbstractPlayer;

import java.util.ArrayList;
import java.util.List;

/**
 * Data structure for managing row-to-character mapping in the multi-character select screen.
 * Rows are indexed 0-3 from bottom to top.
 */
public class CharacterRowAssignment {
    public static final int MAX_ROWS = 4;

    private AbstractPlayer[] rowAssignments = new AbstractPlayer[MAX_ROWS];
    private int selectedRow = 0;

    /**
     * Gets the character assigned to a specific row.
     * @param row Row index (0 = bottom, 3 = top)
     * @return The character at that row, or null if empty
     */
    public AbstractPlayer getCharacterAtRow(int row) {
        if (row < 0 || row >= MAX_ROWS) return null;
        return rowAssignments[row];
    }

    /**
     * Assigns a character to a specific row.
     * If the row is occupied, the existing character is replaced.
     * @param character The character to assign
     * @param row Row index (0 = bottom, 3 = top)
     */
    public void assignCharacterToRow(AbstractPlayer character, int row) {
        if (row < 0 || row >= MAX_ROWS) return;
        rowAssignments[row] = character;
    }

    /**
     * Removes the character from a specific row.
     * @param row Row index (0 = bottom, 3 = top)
     * @return The removed character, or null if row was empty
     */
    public AbstractPlayer removeCharacterFromRow(int row) {
        if (row < 0 || row >= MAX_ROWS) return null;
        AbstractPlayer removed = rowAssignments[row];
        rowAssignments[row] = null;
        return removed;
    }

    /**
     * Finds which row a character is assigned to.
     * @param character The character to find
     * @return Row index (0-3), or -1 if not found
     */
    public int getRowForCharacter(AbstractPlayer character) {
        if (character == null) return -1;
        for (int i = 0; i < MAX_ROWS; i++) {
            if (rowAssignments[i] != null &&
                rowAssignments[i].getClass() == character.getClass()) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Checks if a row is occupied.
     * @param row Row index (0 = bottom, 3 = top)
     * @return true if a character is assigned to this row
     */
    public boolean isRowOccupied(int row) {
        if (row < 0 || row >= MAX_ROWS) return false;
        return rowAssignments[row] != null;
    }

    /**
     * Gets the currently selected row.
     * @return Selected row index (0-3)
     */
    public int getSelectedRow() {
        return selectedRow;
    }

    /**
     * Sets the selected row.
     * @param row Row index (0 = bottom, 3 = top)
     */
    public void setSelectedRow(int row) {
        if (row >= 0 && row < MAX_ROWS) {
            selectedRow = row;
        }
    }

    /**
     * Advances the selected row to the next empty row upward.
     * If no empty rows exist above, stays on current row.
     */
    public void autoAdvanceSelectedRow() {
        // Start from current row and look upward for empty slot
        for (int i = selectedRow + 1; i < MAX_ROWS; i++) {
            if (!isRowOccupied(i)) {
                selectedRow = i;
                return;
            }
        }
        // If no empty rows above, look from bottom
        for (int i = 0; i < selectedRow; i++) {
            if (!isRowOccupied(i)) {
                selectedRow = i;
                return;
            }
        }
        // No empty rows, stay on current
    }

    /**
     * Swaps the characters in two rows.
     * @param row1 First row index
     * @param row2 Second row index
     */
    public void swapRows(int row1, int row2) {
        if (row1 < 0 || row1 >= MAX_ROWS || row2 < 0 || row2 >= MAX_ROWS) return;
        if (row1 == row2) return;

        AbstractPlayer temp = rowAssignments[row1];
        rowAssignments[row1] = rowAssignments[row2];
        rowAssignments[row2] = temp;
    }

    /**
     * Returns assigned characters in order from bottom (row 0) to top.
     * Empty rows are skipped.
     * @return List of characters in row order
     */
    public List<AbstractPlayer> getAssignedCharactersBottomToTop() {
        List<AbstractPlayer> result = new ArrayList<>();
        for (int i = 0; i < MAX_ROWS; i++) {
            if (rowAssignments[i] != null) {
                result.add(rowAssignments[i]);
            }
        }
        return result;
    }

    /**
     * Returns the number of assigned characters.
     * @return Count of non-null row assignments
     */
    public int getAssignedCount() {
        int count = 0;
        for (int i = 0; i < MAX_ROWS; i++) {
            if (rowAssignments[i] != null) count++;
        }
        return count;
    }

    /**
     * Clears all row assignments and resets selected row to 0.
     */
    public void clear() {
        for (int i = 0; i < MAX_ROWS; i++) {
            rowAssignments[i] = null;
        }
        selectedRow = 0;
    }
}
