package BoardGame.events;

import BoardGame.dungeons.BGTheCity;
import BoardGame.dungeons.BGTheEnding;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.events.RoomEventDialog;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.MonsterHelper;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.EventRoom;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import com.megacrit.cardcrawl.vfx.ThoughtBubble;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public class FakeMonsterRoomEvent extends AbstractEvent {
    public static final String ID = "FakeMonsterRoomEvent";
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString("BoardGame:FakeMonsterRoomEvent");
    public static final String NAME = eventStrings.NAME;
    public static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    public static final String[] OPTIONS = eventStrings.OPTIONS;

    private static final String INTRO_MSG = DESCRIPTIONS[0];
    private CurScreen screen = CurScreen.INTRO;

    private int phase=1;
    private enum CurScreen {
        INTRO, PRE_COMBAT, COMPLETE;
    }


    public FakeMonsterRoomEvent() {
        this.hasDialog = true;
        this.hasFocus = true;
        this.body="";
        this.phase=0;
        this.waitTimer=2.0F;
    }

    public void update() {
        super.update();

//        Logger logger = LogManager.getLogger(FakeMonsterRoomEvent.class.getName());
//        logger.info("FMRE: "+this.hasDialog+" "+" "+this.phase+" "+this.waitTimer);

        if(this.hasDialog){
            if (this.waitTimer >= 0.0F) {
                this.waitTimer -= Gdx.graphics.getDeltaTime();
                if (this.waitTimer < 0.0F) {
                    this.phase+=1;
                    if(this.phase==1){
                        AbstractDungeon.effectList.add(new ThoughtBubble(AbstractDungeon.player.dialogX, AbstractDungeon.player.dialogY, 3.0F, DESCRIPTIONS[0], true));
                        this.waitTimer=3.0F;
                    }else{
                        this.hasDialog=false;

                    }
                }
            }
        }else{
            this.screen = CurScreen.COMPLETE;
            (AbstractDungeon.getCurrRoom()).phase = AbstractRoom.RoomPhase.COMPLETE;
            AbstractDungeon.overlayMenu.proceedButton.show();
        }


    }


    protected void buttonEffect(int buttonPressed) {
        openMap();
    }


    @Override
    public void renderRoomEventPanel(SpriteBatch sb) {
        //do nothing
    }


//    @SpirePatch2(clz= AbstractDungeon.class,method="nextRoomTransition",
//            paramtypez={SaveFile.class})
//    public static class Act4EventRoomIconLogPatch{
//        @SpireInsertPatch(
//                locator= Locator.class,
//                localvars={}
//        )
//        public static SpireReturn<Void> Insert(AbstractDungeon __instance, SaveFile saveFile){
//            Logger logger = LogManager.getLogger(FakeMonsterRoomEvent.class.getName());
//            //logger.info("AbstractDungeon nextRoomTransition patch");
//            if(CardCrawlGame.dungeon instanceof BGTheEnding) {
//                //logger.info("EEEEEEEEEEE");
//                //__instance.nextRoom.room.setMapSymbol("E");   //TODO: this works, but are we sure we want to?
//            }
//            return SpireReturn.Continue();
//        }
//        private static class Locator extends SpireInsertLocator {
//            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
//                Matcher finalMatcher = new Matcher.FieldAccessMatcher(ImageMaster.class,"MAP_NODE_EVENT");
//                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
//            }
//        }
//    }


}


