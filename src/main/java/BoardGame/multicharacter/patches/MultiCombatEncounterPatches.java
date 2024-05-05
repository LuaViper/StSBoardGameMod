package BoardGame.multicharacter.patches;

import BoardGame.dungeons.BGExordium;
import BoardGame.multicharacter.MultiCharacter;
import BoardGame.multicharacter.MultiCreature;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.rooms.MonsterRoom;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.util.ArrayList;
import java.util.Collections;

public class MultiCombatEncounterPatches {

    @SpirePatch2(clz= MonsterRoom.class, method="onPlayerEntry")
    public static class AddAdditionalEnemiesPatch {
        @SpirePostfixPatch
        public static void Foo() {
            if(AbstractDungeon.player instanceof MultiCharacter){
                //monsters need to end up in left-to-right, top-to-bottom order
                //we're assembling the rows from bottom-to-top, so we'll add each row right-to-left then reverse
                Collections.reverse(AbstractDungeon.getMonsters().monsters);
                if(MultiCharacter.getSubcharacters().size()>1) {
                    for (int i = 1; i < MultiCharacter.getSubcharacters().size(); i += 1) {
                        //TODO: need to add lastCombatMetricKey for each row
                        //TODO: better first encounter check, maybe
                        AbstractDungeon.monsterList.remove(0);
                        MonsterGroup newGroup = CardCrawlGame.dungeon.getMonsterForRoomCreation();
                        Collections.reverse(newGroup.monsters);
                        for (AbstractMonster m : newGroup.monsters) {
                            MultiCreature.Field.currentRow.set(m, i);
                            AbstractDungeon.getMonsters().add(m);
                            m.init();
                        }
                    }
                    Collections.reverse(AbstractDungeon.getMonsters().monsters);
                    if(CardCrawlGame.dungeon instanceof BGExordium && AbstractDungeon.floorNum==1){
                        //if this was the first encounter, force switch to the strong enemies list
                        AbstractDungeon.monsterList.clear();
                    }
                }
            }

        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractDungeon.class, "setCurrMapNode");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
            }
        }
    }
}
