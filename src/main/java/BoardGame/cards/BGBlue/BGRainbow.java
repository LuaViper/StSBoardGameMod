package BoardGame.cards.BGBlue;
import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGDefect;
import BoardGame.orbs.BGDark;
import BoardGame.orbs.BGFrost;
import BoardGame.orbs.BGLightning;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.BetterDrawPileToHandAction;
import com.megacrit.cardcrawl.actions.defect.ChannelAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.RainbowCardEffect;

public class BGRainbow extends AbstractBGCard {
    public static final String ID = "BGRainbow";

    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGRainbow");

    public BGRainbow() {
        super("BGRainbow", cardStrings.NAME, "blue/skill/rainbow", 2, cardStrings.DESCRIPTION, CardType.SKILL, BGDefect.Enums.BG_BLUE, CardRarity.RARE, CardTarget.SELF);
        this.showEvokeValue = true;
        this.showEvokeOrbCount = 3;
        this.exhaust = true;
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot((AbstractGameAction)new VFXAction((AbstractGameEffect)new RainbowCardEffect()));
        addToBot((AbstractGameAction)new ChannelAction((AbstractOrb)new BGLightning()));
        addToBot((AbstractGameAction)new ChannelAction((AbstractOrb)new BGFrost()));
        addToBot((AbstractGameAction)new ChannelAction((AbstractOrb)new BGDark()));
    }

    public AbstractCard makeCopy() {
        return new BGRainbow();
    }

    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            this.exhaust = false;
            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            initializeDescription();
        }
    }
}