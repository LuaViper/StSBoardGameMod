package BoardGame.multicharacter.patches;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.util.ArrayDeque;
import java.util.Deque;

public class ContextPatches {

    public static Deque<AbstractPlayer> contextHistory = new ArrayDeque<>();
    public static AbstractPlayer originalBGMultiCharacter=null;

    //TODO: clear originalBGMultiCharacter and currentGlobalContext upon starting a new game
    //TODO: consolidate push/pop and AbstractDungeon.player assignments into the same function
    public static void pushContext(AbstractPlayer newContext){
        contextHistory.push(AbstractDungeon.player);
        if(newContext!=null)AbstractDungeon.player=newContext;
    }
    public static void popContext(){
        AbstractDungeon.player = contextHistory.pop();
    }

}
