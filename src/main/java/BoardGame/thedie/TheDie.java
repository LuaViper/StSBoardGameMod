package BoardGame.thedie;

import BoardGame.BoardGame;
import BoardGame.actions.DieMoveAction;
import BoardGame.monsters.DieControlledMoves;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import BoardGame.monsters.AbstractBGMonster;
import com.megacrit.cardcrawl.random.Random;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class TheDie {
    public static Random theDieRng=new Random();
    public static int initialRoll=0;
    public static int finalRelicRoll=0;
    public static int monsterRoll=0;

    public TheDie(){
    }

    public static void roll(){
        int r=TheDie.theDieRng.random(1,6);
        final Logger logger = LogManager.getLogger(BoardGame.class.getName());
        logger.info("ROLL THE DIE: "+r);
        TheDie.initialRoll=r;
        TheDie.finalRelicRoll=-1;
        TheDie.monsterRoll=r;
        TheDie.setMonsterMoves(TheDie.monsterRoll);
    }
    public static void setMonsterMoves(int roll){
        for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters){
            if(m instanceof DieControlledMoves){
                //((DieControlledMoves) m).dieMove(roll);
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DieMoveAction((DieControlledMoves) m));
            }
        }
    }

    //TODO: maybe use an existing RNG for die so it saves to file without any further work
    @SpirePatch2(clz = AbstractDungeon.class, method = "generateSeeds",
            paramtypez={})
    public static class generateSeedsPatch {
        @SpirePostfixPatch
        public static void generateSeeds() {
            TheDie.theDieRng=new Random(Settings.seed);
        }
    }

}
