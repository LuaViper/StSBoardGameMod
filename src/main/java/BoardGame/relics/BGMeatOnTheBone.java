package BoardGame.relics;
import BoardGame.dungeons.AbstractBGDungeon;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.relics.AbstractRelic;


import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

public class BGMeatOnTheBone extends AbstractBGRelic {
    public static final String ID = "BGMeat on the Bone";

    public BGMeatOnTheBone() {
        super("BGMeat on the Bone", "meat.png", AbstractRelic.RelicTier.UNCOMMON, AbstractRelic.LandingSound.HEAVY);
    }

    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    public int getPrice() {return 8;}

    //note that VG MotB is hard-coded to call onTrigger just before player.onVictory is called
            //(this guarantees it acts first before other on-victory relics)
    public void onTrigger() {
        AbstractPlayer p = AbstractDungeon.player;
        if (p.currentHealth < 4 && p.currentHealth > 0) {
            flash();
            int healAmount=4-p.currentHealth;
            addToTop((AbstractGameAction)new RelicAboveCreatureAction((AbstractCreature)AbstractDungeon.player, this));
            p.heal(healAmount);
            stopPulse();
        }
    }

    @SpirePatch2(clz = AbstractRoom.class, method = "endBattle",
            paramtypez = {})
    public static class endBattleMotBPatch {
        @SpirePrefixPatch
        public static void endBattle(){
            if(CardCrawlGame.dungeon instanceof AbstractBGDungeon){
                if(AbstractDungeon.player.hasRelic("BGMeat on the Bone")) {
                    AbstractDungeon.player.getRelic("BGMeat on the Bone").onTrigger();
                }
            }
        }
    }


    //TODO: patch onBloodied to work at 3HP instead of 5HP
//    public void onBloodied() {
//        flash();
//        this.pulse = true;
//    }
//
//
//    public void onNotBloodied() {
//        stopPulse();
//    }



    public AbstractRelic makeCopy() {
        return new BGMeatOnTheBone();
    }
}


