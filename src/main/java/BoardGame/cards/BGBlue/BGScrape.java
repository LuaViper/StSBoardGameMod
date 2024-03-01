package BoardGame.cards.BGBlue;
import BoardGame.actions.BGScrapeAction;
import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGDefect;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.combat.ScrapeEffect;

public class BGScrape extends AbstractBGCard {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGScrape");
    public static final String ID = "BGScrape";


    public BGScrape() {
        super("BGScrape", cardStrings.NAME, "blue/attack/scrape", 1, cardStrings.DESCRIPTION, CardType.ATTACK, BGDefect.Enums.BG_BLUE, CardRarity.COMMON, CardTarget.ENEMY);
        this.baseDamage = 2;
    }


    public void triggerOnGlowCheck() {
        this
                .glowColor = shouldGlow() ? AbstractCard.GOLD_BORDER_GLOW_COLOR.cpy() : AbstractCard.BLUE_BORDER_GLOW_COLOR.cpy();
    }

    private boolean shouldGlow() {
        if(!AbstractDungeon.player.discardPile.group.isEmpty()) {
            AbstractCard c = AbstractDungeon.player.discardPile.group.get(AbstractDungeon.player.discardPile.group.size() - 1);
            if (c.cost == 0)
                return true;
        }
        return false;
    }



    public void use(AbstractPlayer p, AbstractMonster m) {
        if (m != null)
            addToBot((AbstractGameAction)new VFXAction((AbstractGameEffect)new ScrapeEffect(m.hb.cX, m.hb.cY), 0.1F));
        addToBot((AbstractGameAction)new DamageAction((AbstractCreature)m, new DamageInfo((AbstractCreature)p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.SLASH_HEAVY));
        addToBot((AbstractGameAction)new BGScrapeAction());

    }

    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeDamage(1);
        }
    }

    public AbstractCard makeCopy() {
        return new BGScrape();
    }
}

