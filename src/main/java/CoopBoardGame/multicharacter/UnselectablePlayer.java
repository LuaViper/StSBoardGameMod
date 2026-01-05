package CoopBoardGame.multicharacter;

//TODO: when a player profile is erased, unselectableplayer pref files are improperly staying behind
/**
 * Marker interface for characters that should NOT appear in the character select screen.
 *
 * Note: Character filtering is now handled by BoardGameMenuButton.FilterCharactersPatch
 * which filters characters based on whether Board Game mode is active:
 * - Normal "Play" mode: hides all AbstractBGPlayer characters
 * - "Board Game" mode: shows only AbstractBGPlayer characters (except MultiCharacter)
 *
 * This interface is kept for backwards compatibility but the patches have been removed
 * in favor of the more flexible runtime filtering approach.
 */
public interface UnselectablePlayer {
    // Marker interface - no methods required
}
