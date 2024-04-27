package BoardGame.patches.bestiary;

import Bestiary.BestiaryMod;
import Bestiary.database.AscensionMoveSet;
import Bestiary.database.MonsterInfo;
import Bestiary.database.Move;
import Bestiary.database.MoveEffect;
import Bestiary.ui.Label;
import Bestiary.ui.MonsterInfoRenderHelper;
import Bestiary.ui.MonsterOverlay;
import Bestiary.ui.SmartLabel;
import Bestiary.utils.ExtraColors;
import Bestiary.utils.ExtraFonts;
import BoardGame.dungeons.BGExordium;
import BoardGame.dungeons.BGTheBeyond;
import BoardGame.events.BGColosseum;
import BoardGame.events.BGDeadAdventurer;
import BoardGame.events.BGHallwayEncounter;
import BoardGame.monsters.AbstractBGMonster;
import BoardGame.monsters.MonsterGroupRewardsList;
import BoardGame.screen.OrbSelectScreen;
import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.actions.common.DiscardAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.map.DungeonMap;
import com.megacrit.cardcrawl.map.MapEdge;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.rooms.*;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;


public class OverlayPatches {

    //TODO NEXT: also display combat rewards

    @SpirePatch(
            clz= BestiaryMod.class,
            method=SpirePatch.CLASS
    )
    public static class Field
    {
        public static SpireField<AbstractMonster> monster = new SpireField<>(()->null);
    }

    @SpirePatch2(clz = BestiaryMod.class, method = "openOverlayForMonster",
            paramtypez={String.class})
    public static class AddMonsterReferenceToOverlayPatch {
        @SpirePrefixPatch
        public static void Foo(BestiaryMod __instance) {
            AbstractRoom room = AbstractDungeon.getCurrRoom();
            if (room != null && room.monsters != null && room.monsters.hoveredMonster != null){
                Field.monster.set(
                        ReflectionHacks.getPrivateStatic(BestiaryMod.class,"instance"),
                        room.monsters.hoveredMonster);
            }
        }
    }


    @SpirePatch2(clz= MonsterInfoRenderHelper.class,method="setCurrMonster",paramtypez={MonsterInfo.class})
    public static class CreateLabelsPatch {
        @SpireInsertPatch(
                locator = Locator.class,
                localvars = {"moveSet"}
        )
        public static SpireReturn<Void> Foo(MonsterInfoRenderHelper __instance, MonsterInfo monster, AscensionMoveSet ___moveSet) {
            AbstractMonster actualMonster=(AbstractMonster)(Field.monster.get(ReflectionHacks.getPrivateStatic(BestiaryMod.class,"instance")));
            if(actualMonster==null)actualMonster=BestiaryMod.intentRenderingMonster;
            if(!(actualMonster instanceof AbstractBGMonster) ) return SpireReturn.Continue();

            ArrayList<Label> labels=(ReflectionHacks.getPrivate(__instance,MonsterInfoRenderHelper.class,"labels"));
            MonsterInfo currMonster=(ReflectionHacks.getPrivate(__instance,MonsterInfoRenderHelper.class,"currMonster"));
            labels.add(new Label(currMonster.getName() + "  " + ___moveSet.getHp(), ExtraFonts.overlayTitleFont(), 315.0F, 886.0F, Settings.GOLD_COLOR));

            labels.add(new SmartLabel(___moveSet.getDesc(), FontHelper.tipBodyFont, 1086.0F, 847.0F, 534.0F, 30.0F, ExtraColors.OJB_GRAY_COLOR));
            if (___moveSet.hasNotes())
                labels.add(new SmartLabel(___moveSet.getNotes(), FontHelper.tipBodyFont, 1086.0F, 334.0F, 534.0F, 30.0F, ExtraColors.OJB_GRAY_COLOR));
            float movesY = 780.0F;
            for (Move m : ___moveSet.getMoves()) {
                boolean breakLoop=false;
                for (MoveEffect e : m.getMoveEffects()) {
                    if(!e.getName().contains("<")){
                        breakLoop=true;
                    }
                }
                if(breakLoop)break;

                labels.add(new Label(m.getName(), FontHelper.tipBodyFont, 315.0F, movesY, Settings.CREAM_COLOR));

                movesY -= 80.0F;
            }

            if(((AbstractBGMonster)actualMonster).behavior.contains("-")) {
                for (Move m : ___moveSet.getMoves()) {
                    boolean continueLoop = false;
                    for (MoveEffect e : m.getMoveEffects()) {
                        if (!e.getName().contains("-")) {
                            continueLoop = true;
                        }
                    }
                    if (continueLoop) continue;

                    labels.add(new Label(m.getName(), FontHelper.tipBodyFont, 315.0F, movesY, Settings.CREAM_COLOR));

                    movesY -= 80.0F;
                }
            }else{
                int len=((AbstractBGMonster)actualMonster).behavior.length();
                ArrayList<String>dice=new ArrayList<>();
                if(len==2){
                    dice.add("[BoardGame:Die1Icon] [BoardGame:Die2Icon] [BoardGame:Die3Icon] [] [] []");
                    dice.add("[BoardGame:Die4Icon] [BoardGame:Die5Icon] [BoardGame:Die6Icon] [] [] []");
                }else if(len==3){
                    dice.add("[BoardGame:Die1Icon] [BoardGame:Die2Icon] [] [] []");
                    dice.add("[BoardGame:Die3Icon] [BoardGame:Die4Icon] [] [] []");
                    dice.add("[BoardGame:Die5Icon] [BoardGame:Die6Icon] [] [] []");
                }
                for(int i=0;i<len;i+=1){
                    String move = String.valueOf(((AbstractBGMonster)actualMonster).behavior.charAt(i));
                    for (Move m : ___moveSet.getMoves()) {
                        boolean continueLoop = false;
                        for (MoveEffect e : m.getMoveEffects()) {
                            if (!e.getName().contains(move)) {
                                continueLoop = true;
                            }
                        }
                        if (continueLoop) continue;

                        labels.add(new Label(dice.get(i)+" "+m.getName(), FontHelper.tipBodyFont, 315.0F, movesY, Settings.CREAM_COLOR));

                        movesY -= 80.0F;
                    }
                }
            }

            for (Move m : ___moveSet.getMoves()) {
                boolean continueLoop=false;
                for (MoveEffect e : m.getMoveEffects()) {
                    if(!e.getName().contains(">")){
                        continueLoop=true;
                    }
                }
                if(continueLoop)continue;

                labels.add(new Label(m.getName(), FontHelper.tipBodyFont, 315.0F, movesY, Settings.CREAM_COLOR));

                movesY -= 80.0F;
            }

            //TODO: localization
            String rewards = "At the end of combat, the player in this row will receive ";

            AbstractRoom room=AbstractDungeon.getCurrRoom();
            String encounter="";

            int goldModifier = 0;
            int gold=0;
            String goldstr="";
            String and="";
            String item="";
            if(room instanceof MonsterRoomBoss) {
                encounter = AbstractDungeon.bossKey;
                if(AbstractDungeon.ascensionLevel>=10) {
                    goldModifier = -1;
                }
            }else if(room instanceof MonsterRoomElite){
                encounter = AbstractDungeon.eliteMonsterList.get(0);
            }else if(room instanceof MonsterRoom){
                encounter = AbstractDungeon.monsterList.get(0);
            }else if(room instanceof TreasureRoom){
                gold=0;
            }else if(room instanceof EventRoom && room.event instanceof BGColosseum){
                encounter = ((BGColosseum)room.event).encounterID;
            }else if(room instanceof EventRoom && room.event instanceof BGDeadAdventurer){
                encounter = ((BGDeadAdventurer)room.event).encounterID;
            }else if(room instanceof EventRoom && room.event instanceof BGHallwayEncounter){
                //note: BGHallwayEncounter currently swaps itself out for a MonsterRoom, so this line shouldn't be reachable
                encounter = ((BGHallwayEncounter)room.event).encounterID;
            }
            if(MonsterGroupRewardsList.rewards.containsKey(encounter)){
                gold=MonsterGroupRewardsList.rewards.get(encounter).gold;
                item=(MonsterGroupRewardsList.rewards.get(encounter).potion) ? "a potion" : "";
                item=(MonsterGroupRewardsList.rewards.get(encounter).relic) ? "a relic" : item;
            }
            if(gold>0) {
                gold += goldModifier;
                goldstr=gold+" gold";
            }
            if(gold>0 && !item.isEmpty()) and=" and ";

            rewards = rewards + goldstr + and + item + ".";
            //rewards = rewards + goldstr + and + item + " in addition to the card reward.";

            labels.add(new Label(rewards, FontHelper.tipBodyFont, 315.0F, movesY, Settings.CREAM_COLOR));


            return SpireReturn.Return();
        }
        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.NewExprMatcher(Label.class);
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
            }
        }
    }
}
