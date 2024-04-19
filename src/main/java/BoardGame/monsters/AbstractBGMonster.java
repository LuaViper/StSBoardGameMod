package BoardGame.monsters;
import BoardGame.multicharacter.MultiCreature;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.AbstractPower;

//TODO: can we just have AbstractBGMonster implement BGDamageIcons instead of every individual monster?
public abstract class AbstractBGMonster extends AbstractMonster implements MultiCreature {


    public AbstractBGMonster(String name, String id, int maxHealth, float hb_x, float hb_y, float hb_w, float hb_h, String imgUrl, float offsetX, float offsetY) {
        super(name, id, maxHealth, hb_x, hb_y, hb_w, hb_h, imgUrl, offsetX, offsetY);
        MultiCreature.Field.currentRow.set(this,0);
    }

    public AbstractBGMonster(String name, String id, int maxHealth, float hb_x, float hb_y, float hb_w, float hb_h, String imgUrl, float offsetX, float offsetY, boolean ignoreBlights) {
        super(name, id, maxHealth, hb_x, hb_y, hb_w, hb_h, imgUrl, offsetX, offsetY, ignoreBlights);
        MultiCreature.Field.currentRow.set(this,0);
    }

    public AbstractBGMonster(String name, String id, int maxHealth, float hb_x, float hb_y, float hb_w, float hb_h, String imgUrl) {
        super(name, id, maxHealth, hb_x, hb_y, hb_w, hb_h, imgUrl);
        MultiCreature.Field.currentRow.set(this,0);
    }


//    public int currentRow=0;
//    public int getCurrentRow(){
//        return currentRow;
//    }

    public void publicBrokeBlock(){}


    //PublicMoveField is used by BGFlameBarrierAction
    @SpirePatch(
            clz= AbstractMonster.class,
            method=SpirePatch.CLASS
    )
    public static class Field
    {
        //TODO: replace publicMove with ReflectionHacks.getPrivate
        public static SpireField<EnemyMoveInfo> publicMove = new SpireField<>(()->null);
    }

    @SpirePatch2(clz = AbstractMonster.class, method = "setMove",
            paramtypez={String.class, byte.class, AbstractMonster.Intent.class, int.class, int.class, boolean.class})
    public static class setMovePatch {
        @SpirePostfixPatch
        public static void setMovePatch(AbstractMonster __instance, String moveName, byte nextMove, AbstractMonster.Intent intent, int baseDamage, int multiplier, boolean isMultiDamage, EnemyMoveInfo ___move) {
            Field.publicMove.set(__instance,___move);
        }
    }


    public int getCalculateDamage(int dmg) {
        AbstractPlayer target = AbstractDungeon.player;
        float tmp = dmg;


//        if (Settings.isEndless && AbstractDungeon.player.hasBlight("DeadlyEnemies")) {
//            float mod = AbstractDungeon.player.getBlight("DeadlyEnemies").effectFloat();
//            tmp *= mod;
//        }


        for (AbstractPower p : this.powers) {
            tmp = p.atDamageGive(tmp, DamageInfo.DamageType.NORMAL);
        }




        for (AbstractPower p : target.powers) {
            tmp = p.atDamageReceive(tmp, DamageInfo.DamageType.NORMAL);
        }


        tmp = AbstractDungeon.player.stance.atDamageReceive(tmp, DamageInfo.DamageType.NORMAL);


//        if (applyBackAttack()) {
//            tmp = (int)(tmp * 1.5F);
//        }


        for (AbstractPower p : this.powers) {
            tmp = p.atDamageFinalGive(tmp, DamageInfo.DamageType.NORMAL);
        }


        for (AbstractPower p : target.powers) {
            tmp = p.atDamageFinalReceive(tmp, DamageInfo.DamageType.NORMAL);
        }


        dmg = MathUtils.floor(tmp);
        if (dmg < 0) {
            dmg = 0;
        }


        return dmg;
    }

}
