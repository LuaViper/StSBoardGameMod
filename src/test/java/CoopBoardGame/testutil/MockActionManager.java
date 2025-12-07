package CoopBoardGame.testutil;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Controllable action queue for testing that processes the update() lifecycle.
 * This allows tests to execute actions and verify their effects without requiring
 * the full game framework.
 */
public class MockActionManager {

    private Queue<AbstractGameAction> actions = new LinkedList<>();

    /**
     * Add an action to the bottom of the queue (like addToBot in the real game).
     */
    public void addToBottom(AbstractGameAction action) {
        actions.add(action);
    }

    /**
     * Add an action to the top of the queue (like addToTop in the real game).
     */
    public void addToTop(AbstractGameAction action) {
        ((LinkedList<AbstractGameAction>) actions).addFirst(action);
    }

    /**
     * Process all actions in the queue by calling update() until they're done.
     * This simulates the game's frame-by-frame action processing.
     */
    public void processAllActions() {
        while (!actions.isEmpty()) {
            AbstractGameAction action = actions.poll();
            if (action != null) {
                while (!action.isDone) {
                    action.update();
                }
            }
        }
    }

    /**
     * Get the number of actions currently in the queue.
     */
    public int size() {
        return actions.size();
    }

    /**
     * Check if the queue is empty.
     */
    public boolean isEmpty() {
        return actions.isEmpty();
    }

    /**
     * Clear all actions from the queue.
     */
    public void clear() {
        actions.clear();
    }
}
