package BoardGame.cards.BGGreen;

import BoardGame.actions.BGBouncingFlaskAction;
import BoardGame.actions.TargetSelectScreenAction;
import BoardGame.cards.AbstractBGCard;
import BoardGame.screen.TargetSelectScreen;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.combat.PotionBounceEffect;

public class BGBouncingFlask extends AbstractBGCard {
    public static final String ID = "BGBouncingFlask";

    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGBouncingFlask");

    public BGBouncingFlask() {
        super("BGBouncingFlask", cardStrings.NAME, "green/skill/bouncing_flask", 2, cardStrings.DESCRIPTION, CardType.SKILL, CardColor.GREEN, CardRarity.UNCOMMON, CardTarget.NONE);
        this.baseMagicNumber = 2;
        this.magicNumber = this.baseMagicNumber;
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        TargetSelectScreen.TargetSelectAction tssAction = (target) -> {
            if(target!=null) {
                addToBot((AbstractGameAction) new VFXAction((AbstractGameEffect) new PotionBounceEffect(p.hb.cX, p.hb.cY, target.hb.cX, this.hb.cY), 0.4F));
                addToBot((AbstractGameAction) new BGBouncingFlaskAction((AbstractCreature) target, 1, this.magicNumber));
            }
        };
        addToTop((AbstractGameAction) new TargetSelectScreenAction(tssAction, cardStrings.EXTENDED_DESCRIPTION[0]));

    }


    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeMagicNumber(1);
            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            initializeDescription();

        }
    }

    public AbstractCard makeCopy() {
        return new BGBouncingFlask();
    }
}




