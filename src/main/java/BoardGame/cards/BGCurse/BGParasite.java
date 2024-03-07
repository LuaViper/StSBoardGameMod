package BoardGame.cards.BGCurse;

import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGCurse;
import BoardGame.dungeons.BGTheBeyond;
import BoardGame.events.BGSensoryStone;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.util.ArrayList;

public class BGParasite extends AbstractBGCard {
    public static final String ID = "BGParasite";
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGParasite");

    public BGParasite() {
        super("BGParasite", cardStrings.NAME, "curse/parasite", -2, cardStrings.DESCRIPTION, AbstractCard.CardType.CURSE, BGCurse.Enums.BG_CURSE, AbstractCard.CardRarity.CURSE, AbstractCard.CardTarget.NONE);
    }







    public void use(AbstractPlayer p, AbstractMonster m) {}




    public static int BGParasiteRemoveCount=0;


    public void onRemoveFromMasterDeck() {
        BGParasiteRemoveCount++;
//        AbstractDungeon.player.damage(new DamageInfo(null,
//                2, DamageInfo.DamageType.HP_LOSS));

        CardCrawlGame.sound.play("BLOOD_SWISH");
    }



    public void upgrade() {}


    public AbstractCard makeCopy() {
        return new BGParasite();
    }


    @SpirePatch2(clz=AbstractDungeon.class,method="update",paramtypez={})
    public static class BGParasitePatch {
        @SpireInsertPatch(
                locator = Locator.class,
                localvars = {}
        )
        public static void Insert() {
            if (BGParasite.BGParasiteRemoveCount > 0) {
                AbstractDungeon.player.damage(new DamageInfo(null, 2 * BGParasite.BGParasiteRemoveCount, DamageInfo.DamageType.HP_LOSS));
                BGParasite.BGParasiteRemoveCount = 0;
            }
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractDungeon.class, "effectsQueue");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
            }
        }
    }



}


