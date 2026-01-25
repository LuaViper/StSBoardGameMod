package CoopBoardGame.multicharacter;

import CoopBoardGame.characters.BGDefect;
import CoopBoardGame.characters.BGIronclad;
import CoopBoardGame.characters.BGSilent;
import CoopBoardGame.characters.BGWatcher;
import CoopBoardGame.ui.OverlayMenuPatches;
import CoopBoardGame.util.TogetherInSpireHelper;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Handles automatic character assignment when using TogetherInSpire multiplayer mode.
 *
 * In multiplayer mode, instead of showing the MultiCharacterSelectScreen,
 * players are automatically assigned to rows based on the character class
 * they selected when joining the multiplayer session.
 */
public class MultiplayerCharacterAssignment {

    private static final Logger logger = LogManager.getLogger(MultiplayerCharacterAssignment.class.getName());

    /**
     * Checks if we should use automatic character assignment (multiplayer mode).
     *
     * @return true if TogetherInSpire multiplayer is active
     */
    public static boolean shouldAutoAssign() {
        return TogetherInSpireHelper.isMultiplayerActive();
    }

    /**
     * Automatically assigns players to rows based on their chosen character classes
     * from the TogetherInSpire multiplayer session.
     *
     * This method:
     * 1. Gets the list of player classes from all players in the multiplayer session
     * 2. Creates BG character instances for each player class
     * 3. Assigns them to rows (bottom to top, row 0 first)
     * 4. Syncs the subcharacters list with the MultiCharacter
     */
    public static void performAutoAssignment() {
        if (!TogetherInSpireHelper.isMultiplayerActive()) {
            logger.warn("performAutoAssignment called but multiplayer is not active");
            return;
        }

        if (!(AbstractDungeon.player instanceof MultiCharacter)) {
            logger.warn("performAutoAssignment called but player is not MultiCharacter");
            return;
        }

        logger.info("Performing automatic character assignment for multiplayer mode");

        // Get the row boxes from the overlay menu
        MultiCharacterRowBoxes rowBoxes = (MultiCharacterRowBoxes)
            OverlayMenuPatches.OverlayMenuExtraInterface.multiCharacterRowBoxes.get(
                AbstractDungeon.overlayMenu
            );

        CharacterRowAssignment rowAssignment = rowBoxes.getRowAssignment();
        rowAssignment.clear();

        // Get all player classes from the multiplayer session
        List<AbstractPlayer.PlayerClass> playerClasses = TogetherInSpireHelper.getAllPlayerClasses();
        logger.info("Found " + playerClasses.size() + " players in multiplayer session");

        // Assign each player to a row based on their character class
        int row = 0;
        for (AbstractPlayer.PlayerClass playerClass : playerClasses) {
            if (row >= CharacterRowAssignment.MAX_ROWS) {
                logger.warn("More players than rows available, skipping remaining players");
                break;
            }

            AbstractPlayer bgCharacter = createBGCharacterForClass(playerClass);
            if (bgCharacter != null) {
                logger.info("Assigning " + bgCharacter.name + " to row " + row);

                // Initialize the character's deck and relics
                bgCharacter.initializeStarterDeck();

                // Set the character's row
                MultiCreature.Field.currentRow.set(bgCharacter, row);

                // Assign to row
                rowAssignment.assignCharacterToRow(bgCharacter, row);
                row++;
            } else {
                logger.warn("Could not create BG character for class: " + playerClass);
            }
        }

        // Rebuild swap buttons and sync subcharacters
        rowBoxes.rebuildSwapButtons();

        // Sync subcharacters from row assignment
        MultiCharacter mc = (MultiCharacter) AbstractDungeon.player;
        mc.subcharacters.clear();
        mc.subcharacters.addAll(rowAssignment.getAssignedCharactersBottomToTop());

        // Set currentRow for each character to their index in the subcharacters list
        // This is needed for proper Y positioning during combat rendering
        for (int i = 0; i < mc.subcharacters.size(); i++) {
            MultiCreature.Field.currentRow.set(mc.subcharacters.get(i), i);
        }

        logger.info("Auto-assignment complete. " + mc.subcharacters.size() + " characters assigned.");
    }

    /**
     * Creates a BG character instance based on the vanilla player class.
     * Maps vanilla classes (IRONCLAD, SILENT, DEFECT, WATCHER) to their BG equivalents.
     *
     * @param playerClass The vanilla player class enum
     * @return A new BG character instance, or null if the class is not recognized
     */
    public static AbstractPlayer createBGCharacterForClass(AbstractPlayer.PlayerClass playerClass) {
        if (playerClass == null) {
            return null;
        }

        String className = playerClass.name();

        // Handle vanilla classes
        switch (className) {
            case "IRONCLAD":
                return new BGIronclad("The Ironclad", BGIronclad.Enums.BG_IRONCLAD);
            case "THE_SILENT":
                return new BGSilent("The Silent", BGSilent.Enums.BG_SILENT);
            case "DEFECT":
                return new BGDefect("The Defect", BGDefect.Enums.BG_DEFECT);
            case "WATCHER":
                return new BGWatcher("The Watcher", BGWatcher.Enums.BG_WATCHER);
        }

        // Handle BG classes (in case someone selected a BG character directly)
        if (playerClass == BGIronclad.Enums.BG_IRONCLAD) {
            return new BGIronclad("The Ironclad", BGIronclad.Enums.BG_IRONCLAD);
        } else if (playerClass == BGSilent.Enums.BG_SILENT) {
            return new BGSilent("The Silent", BGSilent.Enums.BG_SILENT);
        } else if (playerClass == BGDefect.Enums.BG_DEFECT) {
            return new BGDefect("The Defect", BGDefect.Enums.BG_DEFECT);
        } else if (playerClass == BGWatcher.Enums.BG_WATCHER) {
            return new BGWatcher("The Watcher", BGWatcher.Enums.BG_WATCHER);
        }

        logger.warn("Unknown player class: " + className);
        return null;
    }

    /**
     * Marks the MultiCharacterSelectScreen as done and allows proceeding to gameplay.
     * Called after auto-assignment to skip the manual character selection UI.
     */
    public static void markSelectionComplete() {
        // Hide the row boxes UI since we're skipping the selection screen
        MultiCharacterRowBoxes rowBoxes = (MultiCharacterRowBoxes)
            OverlayMenuPatches.OverlayMenuExtraInterface.multiCharacterRowBoxes.get(
                AbstractDungeon.overlayMenu
            );
        rowBoxes.hide();

        logger.info("Character selection marked as complete for multiplayer");
    }

    /**
     * Assigns the local player's individual character to their row in multiplayer mode.
     * This is called when a player selects an individual BG character (not MultiCharacter)
     * in TogetherInSpire multiplayer mode.
     *
     * Each player is assigned to a row based on their player ID:
     * - Player 0 -> Row 0 (bottom)
     * - Player 1 -> Row 1
     * - Player 2 -> Row 2
     * - Player 3 -> Row 3 (top)
     */
    public static void assignLocalPlayerToRow() {
        if (!TogetherInSpireHelper.isMultiplayerActive()) {
            logger.info("Not in multiplayer mode, skipping row assignment");
            return;
        }

        // Get local player ID to determine row
        int localPlayerId = TogetherInSpireHelper.getLocalPlayerId();

        // Clamp to valid row range
        int row = Math.min(localPlayerId, CharacterRowAssignment.MAX_ROWS - 1);

        // Assign current player to their row
        MultiCreature.Field.currentRow.set(AbstractDungeon.player, row);

        logger.info("Assigned local player " + AbstractDungeon.player.name + " to row " + row +
                   " (player ID: " + localPlayerId + ")");
    }

    /**
     * Checks if we should skip the character selection screen in multiplayer mode.
     * Returns true if TogetherInSpire is active and the player selected an individual BG character.
     *
     * @return true if we should skip character selection and directly assign to row
     */
    public static boolean shouldSkipCharacterSelection() {
        if (!TogetherInSpireHelper.isMultiplayerActive()) {
            return false;
        }

        // In multiplayer mode, individual BG characters should be directly assigned to rows
        // without going through the MultiCharacter selection screen
        return !(AbstractDungeon.player instanceof MultiCharacter);
    }
}
