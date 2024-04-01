package BoardGame.cards.BGColorless;

import BoardGame.actions.BGGainMiracleAction;
import BoardGame.cards.AbstractBGAttackCardChoice;
import BoardGame.characters.BGColorless;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.BorderFlashEffect;
import com.megacrit.cardcrawl.vfx.RainingGoldEffect;
import com.megacrit.cardcrawl.vfx.SpotlightPlayerEffect;
import com.megacrit.cardcrawl.vfx.combat.MiracleEffect;

public class BGFameAndFortune extends AbstractBGAttackCardChoice {
    public static final String ID = "BGFameAndFortune";
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGFameAndFortune");

    public BGFameAndFortune() {
        super("BGFameAndFortune", cardStrings.NAME, "colorless/skill/fame_and_fortune", -2, cardStrings.DESCRIPTION, CardType.STATUS, BGColorless.Enums.CARD_COLOR, CardRarity.SPECIAL, CardTarget.NONE);
        this.baseMagicNumber=4;
        magicNumber=baseMagicNumber;
    }



    public void use(AbstractPlayer p, AbstractMonster m) {
        onChoseThisOption();
    }
    public void onChoseThisOption() {
        AbstractDungeon.effectList.add(new RainingGoldEffect(this.magicNumber * 20, true));
        AbstractDungeon.effectsQueue.add(new SpotlightPlayerEffect());
        addToBot((AbstractGameAction)new BGGainMiracleAction(this.magicNumber));
    }


    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeBlock(1);
            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            initializeDescription();
        }
    }

    public AbstractCard makeCopy() {
        return new BGFameAndFortune();
    }
}

