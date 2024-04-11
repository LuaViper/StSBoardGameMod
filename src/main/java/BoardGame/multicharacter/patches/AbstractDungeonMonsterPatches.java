package BoardGame.multicharacter.patches;

import BoardGame.monsters.AbstractBGMonster;
import BoardGame.multicharacter.ALLEnemiesMonster;
import BoardGame.multicharacter.BGMultiCreature;
import BoardGame.multicharacter.NullMonster;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class AbstractDungeonMonsterPatches {

    //TODO: decent chance this breaks when we try to summon a monster and it ends up in the current group instead of the original group
    @SpirePatch(
            clz=AbstractRoom.class,
            method=SpirePatch.CLASS
    )
    public static class Field
    {
        public static SpireField<MonsterGroup> originalMonsters = new SpireField<>(()->null);
    }

    public static MonsterGroup getMonstersInSameRow(AbstractCreature context){
        if(Field.originalMonsters.get(AbstractDungeon.getCurrRoom())==null){
            Field.originalMonsters.set(AbstractDungeon.getCurrRoom(),AbstractDungeon.getMonsters());
        }
        if(context==null || context instanceof NullMonster || context instanceof ALLEnemiesMonster){
            return Field.originalMonsters.get(AbstractDungeon.getCurrRoom());
        }
        ArrayList<AbstractMonster> row = new ArrayList<>();
        for(AbstractMonster m : Field.originalMonsters.get(AbstractDungeon.getCurrRoom()).monsters){
            if(BGMultiCreature.Field.currentRow.get(m).equals(BGMultiCreature.Field.currentRow.get(context))){
                row.add(m);
            }
        }
        Object[] a = row.toArray();
        AbstractMonster[] array = Arrays.copyOf(a,a.length,AbstractMonster[].class);
        return new MonsterGroup(array);
    }

//    @SpirePatch(clz = AbstractDungeon.class, method = "getMonsters")
//    public static class GetMonstersPatch{
//        @SpirePrefixPatch
//        public static SpireReturn<MonsterGroup> Foo() {
//            AbstractCreature context=ContextPatches.targetContextHistory.peek();
//            if(context==null)
//                return SpireReturn.Continue();
//            ArrayList<AbstractMonster> row = new ArrayList<>();
//            for(AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters){
//                if(BGMultiCreature.Field.currentRow.get(m)==BGMultiCreature.Field.currentRow.get(context)){
//                    row.add(m);
//                }
//            }
//            Object[] a = row.toArray();
//            AbstractMonster[] array = Arrays.copyOf(a,a.length,AbstractMonster[].class);
//            MonsterGroup group = new MonsterGroup(array);
//
//            return SpireReturn.Return(group);
//        }
//    }


}
