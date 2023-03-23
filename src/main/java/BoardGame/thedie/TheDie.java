package BoardGame.thedie;

import BoardGame.BoardGame;
import BoardGame.actions.DieMoveAction;
import BoardGame.monsters.DieControlledMoves;
import BoardGame.powers.BGTheDiePower;
import BoardGame.relics.BGGamblingChip;
import BoardGame.relics.BGTheDieRelic;
import BoardGame.relics.DieControlledRelic;
import BoardGame.ui.LockInRollButton;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import BoardGame.monsters.AbstractBGMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.random.Random;
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
            //AbstractDungeon.overlayMenu.endTurnButton.disable(false); //DO NOT DO THIS. many sideeffects.
            //TODO: check for +1/-1 relics too
            LockInRollButton.OverlayMenuDiceInterface.lockinrollbutton.get(AbstractDungeon.overlayMenu).visible = true;
        }else{
            LockInRollButton.OverlayMenuDiceInterface.lockinrollbutton.get(AbstractDungeon.overlayMenu).visible = false;
            if(AbstractDungeon.player.hasRelic("BoardGame:BGTheDieRelic")) {
                AbstractRelic relic = AbstractDungeon.player.getRelic("BoardGame:BGTheDieRelic");
                ((BGTheDieRelic) relic).lockRollAndActivateDieRelics();
            }
        }
        if(gambleok) {
            LockInRollButton.OverlayMenuDiceInterface.rerollbutton.get(AbstractDungeon.overlayMenu).visible = true;
        }else{
            LockInRollButton.OverlayMenuDiceInterface.rerollbutton.get(AbstractDungeon.overlayMenu).visible = false;
        }

    }
    public static void setMonsterMoves(int roll){
        for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters){
            if(m instanceof DieControlledMoves){
                //((DieControlledMoves) m).dieMove(roll);
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DieMoveAction((DieControlledMoves) m));
            }
        }
    }



}
