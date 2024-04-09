package BoardGame.cards.BGPurple;

import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGWatcher;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class BGSpiritShield extends AbstractBGCard {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGSpiritShield");
    public static final String ID = "BGSpiritShield";

    public BGSpiritShield() {
        super("BGSpiritShield", cardStrings.NAME, "purple/skill/spirit_shield", 2, cardStrings.DESCRIPTION, CardType.SKILL, BGWatcher.Enums.BG_PURPLE, CardRarity.RARE, CardTarget.SELF);
        this.exhaust = true;
        this.baseBlock=1;
        baseMagicNumber=0;
        magicNumber=baseMagicNumber;
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        super.applyPowers();
        int count = 0;
        for (AbstractCard c : AbstractDungeon.player.hand.group) {
            if (c != this)
                count++;
        }
        //TODO: this is blatantly redundant and can almost certainly be condensed
        for(int i=0;i<count;i+=1){
            addToBot(new GainBlockAction(p,block));
        }
    }

    public void applyPowers() {
        super.applyPowers();
        int count = 0;
        for (AbstractCard c : AbstractDungeon.player.hand.group) {
            if (c != this)
                count++;
        }
        baseMagicNumber=count*block;
        magicNumber=baseMagicNumber;
        if(!upgraded)
            this.rawDescription = cardStrings.DESCRIPTION + cardStrings.EXTENDED_DESCRIPTION[0];
        else
            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION + cardStrings.EXTENDED_DESCRIPTION[0];
        initializeDescription();
    }

    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            this.exhaust=false;
            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            initializeDescription();
        }
    }

    public AbstractCard makeCopy() {
        return new BGSpiritShield();
    }
}
