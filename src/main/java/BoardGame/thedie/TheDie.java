package BoardGame.thedie;

import BoardGame.BoardGame;
import BoardGame.actions.BGLockInRollAction;
import BoardGame.actions.DieMoveAction;
import BoardGame.monsters.DieControlledMoves;
import BoardGame.potions.BGGamblersBrew;
import BoardGame.powers.BGTheDiePower;
import BoardGame.relics.*;
import BoardGame.ui.LockInRollButton;
import BoardGame.ui.OverlayMenuPatches;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.megacrit.cardcrawl.dungeons.AbstractDungeon.monsterRng;


public class TheDie {
    public static int initialRoll=0;
    public static int finalRelicRoll=0;
    public static int monsterRoll=0;
    //fLIR is set when the player takes an action that can only be taken after relic phase
    public static boolean forceLockInRoll=false;

    public TheDie(){
    }

    public static void roll(){
        //TODO: can we get TheDie's displayed number to update BEFORE applying Block (and triggering a 20-hit Transient combo)?
        int r=monsterRng.random(1,6);
        final Logger logger = LogManager.getLogger(BoardGame.class.getName());
        logger.info("ROLL THE DIE: "+r);
        TheDie.initialRoll=r;
        TheDie.finalRelicRoll=-1;
        TheDie.monsterRoll=r;
        TheDie.setMonsterMoves(TheDie.monsterRoll);


        LockInRollButton.relicList="";
        for(AbstractRelic relic : AbstractDungeon.player.relics) {
            if(relic instanceof DieControlledRelic) {
                String relictext = ((DieControlledRelic) relic).getQuickSummary();
                if(relictext!=""){
                    LockInRollButton.relicList+=" NL "+relictext+" ("+relic.name+")";
                }
            }
        }
        AbstractDungeon.actionManager.addToTop((AbstractGameAction)new ApplyPowerAction((AbstractCreature)AbstractDungeon.player, (AbstractCreature)AbstractDungeon.player, (AbstractPower)new BGTheDiePower((AbstractCreature)AbstractDungeon.player, TheDie.initialRoll), TheDie.initialRoll));

        boolean gambleok=false;
        if(AbstractDungeon.player.hasRelic("BGGambling Chip")) {
            AbstractRelic relic=AbstractDungeon.player.getRelic("BGGambling Chip");
            if(((BGGamblingChip)relic).available)gambleok=true;
        }
        if(gambleok) {
            OverlayMenuPatches.OverlayMenuExtraInterface.rerollbutton.get(AbstractDungeon.overlayMenu).visible = true;
        }else{
            OverlayMenuPatches.OverlayMenuExtraInterface.rerollbutton.get(AbstractDungeon.overlayMenu).visible = false;
        }

        boolean abacusok=false;
        if(AbstractDungeon.player.hasRelic("BGTheAbacus")) {
            AbstractRelic relic=AbstractDungeon.player.getRelic("BGTheAbacus");
            if(((BGTheAbacus)relic).available)abacusok=true;
        }
        if(abacusok) {
            OverlayMenuPatches.OverlayMenuExtraInterface.theabacusbutton.get(AbstractDungeon.overlayMenu).visible = true;
        }else{
            OverlayMenuPatches.OverlayMenuExtraInterface.theabacusbutton.get(AbstractDungeon.overlayMenu).visible = false;
        }

        boolean toolboxok=false;
        if(AbstractDungeon.player.hasRelic("BGToolbox")) {
            AbstractRelic relic=AbstractDungeon.player.getRelic("BGToolbox");
            if(((BGToolbox)relic).available)toolboxok=true;
        }
        if(toolboxok) {
            OverlayMenuPatches.OverlayMenuExtraInterface.toolboxbutton.get(AbstractDungeon.overlayMenu).visible = true;
        }else{
            OverlayMenuPatches.OverlayMenuExtraInterface.toolboxbutton.get(AbstractDungeon.overlayMenu).visible = false;
        }

        boolean potionok=false;
        if(BGGamblersBrew.doesPlayerHaveGamblersBrew()>-1){
            potionok=true;
        }
        if(potionok){
            OverlayMenuPatches.OverlayMenuExtraInterface.potionbutton.get(AbstractDungeon.overlayMenu).visible = true;
        }else{
            OverlayMenuPatches.OverlayMenuExtraInterface.potionbutton.get(AbstractDungeon.overlayMenu).visible = false;
        }

        if(gambleok || abacusok || toolboxok || potionok) {
            //AbstractDungeon.overlayMenu.endTurnButton.disable(false); //DO NOT DO THIS. many sideeffects.
            OverlayMenuPatches.OverlayMenuExtraInterface.lockinrollbutton.get(AbstractDungeon.overlayMenu).visible = true;
        }else{
            OverlayMenuPatches.OverlayMenuExtraInterface.lockinrollbutton.get(AbstractDungeon.overlayMenu).visible = false;
//            if(AbstractDungeon.player.hasRelic("BoardGame:BGTheDieRelic")) {
//                AbstractRelic relic = AbstractDungeon.player.getRelic("BoardGame:BGTheDieRelic");
//                ((BGTheDieRelic) relic).lockRollAndActivateDieRelics();
//            }
            AbstractDungeon.actionManager.addToBottom(new BGLockInRollAction());
        }
    }

    public static void tentativeRoll(int r){
        TheDie.monsterRoll = r;
        TheDie.setMonsterMoves(TheDie.monsterRoll);
        AbstractDungeon.player.hand.refreshHandLayout();
        LockInRollButton.relicList="";
        for(AbstractRelic relic : AbstractDungeon.player.relics) {
            if(relic instanceof DieControlledRelic) {
                String relictext = ((DieControlledRelic) relic).getQuickSummary();
                if(relictext!=""){
                    LockInRollButton.relicList+=" NL "+relictext+" ("+relic.name+")";
                }
            }
        }
        AbstractDungeon.actionManager.addToTop((AbstractGameAction)new ApplyPowerAction((AbstractCreature)AbstractDungeon.player, (AbstractCreature)AbstractDungeon.player, (AbstractPower)new BGTheDiePower((AbstractCreature)AbstractDungeon.player, TheDie.monsterRoll), TheDie.monsterRoll));
    }

    public static void setMonsterMoves(int roll){
        for (AbstractMonster m : AbstractDungeon.getMonsters().monsters){
            if(m instanceof DieControlledMoves){
                //((DieControlledMoves) m).dieMove(roll);
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DieMoveAction((DieControlledMoves) m));
            }
        }
    }



}
