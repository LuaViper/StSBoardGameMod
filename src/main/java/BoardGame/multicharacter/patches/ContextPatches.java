package BoardGame.multicharacter.patches;


import BoardGame.multicharacter.MultiCreature;
import BoardGame.multicharacter.NullMonster;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

import java.util.ArrayDeque;
import java.util.Deque;

public class ContextPatches {
    public static Deque<AbstractPlayer> playerContextHistory = new ArrayDeque<>();
    public static Deque<AbstractCreature> targetContextHistory = new ArrayDeque<>();
    public static AbstractPlayer originalBGMultiCharacter=null;
    public static AbstractCreature currentTargetContext=null;

    public static void pushPlayerContext(AbstractPlayer newContext){
        //TODO: can we ... like ... push energy and maybe relics and potions here too?
        MultiCreature.Field.savedCurrentEnergy.set(AbstractDungeon.player,EnergyPanel.totalCount);
        playerContextHistory.push(AbstractDungeon.player);
        if(newContext!=null) {
            AbstractDungeon.player = newContext;
            EnergyPanel.totalCount = MultiCreature.Field.savedCurrentEnergy.get(AbstractDungeon.player);
        }else{
            //TODO: consider complaining loudly here -- energy tracking is liable to mess up
        }
    }
    public static void popPlayerContext(){
        MultiCreature.Field.savedCurrentEnergy.set(AbstractDungeon.player,EnergyPanel.totalCount);
        AbstractDungeon.player = playerContextHistory.pop();
        //note that if we pushContext(null)ed earlier, we are essentially loading savestate with energy, so be careful
        EnergyPanel.totalCount = MultiCreature.Field.savedCurrentEnergy.get(AbstractDungeon.player);
    }

    public static void pushTargetContext(AbstractCreature newContext){
        //BoardGame.BoardGame.logger.info("push "+newContext);
        if(currentTargetContext!=null) {
            targetContextHistory.push(currentTargetContext);
        }else{
            targetContextHistory.push(new NullMonster());
        }
        currentTargetContext=newContext;
        AbstractDungeon.getCurrRoom().monsters = AbstractDungeonMonsterPatches.getMonstersInSameRow(newContext);

    }
    public static void popTargetContext(){
        //BoardGame.BoardGame.logger.info("pop");
        AbstractCreature oldContext = targetContextHistory.pop();
        AbstractDungeon.getCurrRoom().monsters = AbstractDungeonMonsterPatches.getMonstersInSameRow(oldContext);
        currentTargetContext=oldContext;

    }

}
