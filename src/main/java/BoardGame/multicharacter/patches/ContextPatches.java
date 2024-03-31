package BoardGame.multicharacter.patches;

import BoardGame.characters.AbstractBGCharacter;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

import java.util.ArrayDeque;
import java.util.Deque;

public class ContextPatches {

    public static Deque<AbstractPlayer> contextHistory = new ArrayDeque<>();
    public static AbstractPlayer originalBGMultiCharacter=null;

    //TODO: clear originalBGMultiCharacter and contextHistory upon starting a new game and/or ending old game
    public static void pushContext(AbstractPlayer newContext){
        //TODO: can we ... like ... push energy and maybe relics and potions here too?
        ((AbstractBGCharacter)AbstractDungeon.player).savedCurrentEnergy=EnergyPanel.totalCount;
        contextHistory.push(AbstractDungeon.player);
        if(newContext!=null) {
            AbstractDungeon.player = newContext;
            EnergyPanel.totalCount=((AbstractBGCharacter)AbstractDungeon.player).savedCurrentEnergy;
        }else{
            //TODO: consider complaining loudly here -- energy tracking is liable to mess up
        }
    }
    public static void popContext(){
        ((AbstractBGCharacter)AbstractDungeon.player).savedCurrentEnergy = EnergyPanel.totalCount;
        AbstractDungeon.player = contextHistory.pop();
        //note that if we pushContext(null)ed earlier, we are essentially loading savestate with energy, so be careful
        EnergyPanel.totalCount=((AbstractBGCharacter)AbstractDungeon.player).savedCurrentEnergy;
    }

}
