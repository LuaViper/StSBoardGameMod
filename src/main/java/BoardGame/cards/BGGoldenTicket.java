package BoardGame.cards;

import BoardGame.BoardGame;
import BoardGame.characters.BGColorless;
import basemod.AutoAdd;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import org.clapper.util.classutil.ClassInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import static BoardGame.BoardGame.makeCardPath;
import static com.megacrit.cardcrawl.core.CardCrawlGame.languagePack;

//TODO: exact wording of Golden Ticket card seems to have changed just before printing -- check physical copies when available
//TODO: Golden Ticket is actually a TICKET card, not a POWER card

public class BGGoldenTicket extends AbstractBGCard {

    /*
     * Wiki-page: https://github.com/daviscook477/BaseMod/wiki/Custom-Cards
     */

    // TEXT DECLARATION

    public static final String ID = BoardGame.makeID(BGGoldenTicket.class.getSimpleName());
    public static final String BASE_ID = BoardGame.makeID(BGGoldenTicket.class.getSimpleName());
    public static final String IMG = makeCardPath("GoldenTicket.png");

    // /TEXT DECLARATION/


    // STAT DECLARATION

    private static final CardRarity RARITY = CardRarity.RARE;
    private static final CardTarget TARGET = CardTarget.SELF;
    private static final CardType TYPE = CardType.POWER;
    public static final CardColor COLOR = BGColorless.Enums.CARD_COLOR;

    private static final int COST = -2;
    private static final int UPGRADE_COST = 1;

    private static final int MAGIC = 1;

    // /STAT DECLARATION/


    public BGGoldenTicket(String ID, CardColor color) {
        super(ID, languagePack.getCardStrings(BASE_ID).NAME, IMG, COST, languagePack.getCardStrings(BASE_ID).DESCRIPTION, TYPE, color, RARITY, TARGET);
    }

    public BGGoldenTicket() {
        this(ID, BGColorless.Enums.CARD_COLOR);
    }

    public void loadCardImage(String img) {
        passthroughLoadCardImage(img);
    }
    protected Texture getPortraitImage() {
        return passthroughGetPortraitImage();
    }


    // Actions the card should do.
    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {}

    //Upgraded stats.
    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            initializeDescription();
        }
    }

    public AbstractCard makeCopy() {
        return new BGGoldenTicket();
    }

    @SpirePatch2(clz= AutoAdd.class, method="findClasses",
            paramtypez={Class.class})
    public static class HideCardFromCompendiumPatch {
        @SpireInsertPatch(
                locator = BGGoldenTicket.HideCardFromCompendiumPatch.Locator.class,
                localvars = {"foundClasses"}
        )
        //public static <T> Collection<CtClass> Insert(AbstractCard _____instance, @ByRef Texture[] ___texture) {
        public static void Insert(@ByRef Collection<ClassInfo>[] ___foundClasses) {
            for (Iterator<ClassInfo> itr = ___foundClasses[0].iterator(); itr.hasNext(); ) {
                ClassInfo c = itr.next();
                if(c.getClassName().equals(BGGoldenTicket.class.getName())){
                    itr.remove();
                }
            }
        }
        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(Collection.class, "iterator");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
            }
        }
    }



}

