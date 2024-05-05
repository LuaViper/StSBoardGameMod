package BoardGame.cards.BGColorless;

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
import com.megacrit.cardcrawl.vfx.combat.MiracleEffect;

public class BGLiveForever extends AbstractBGAttackCardChoice {
    public static final String ID = "BGLiveForever";
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGLiveForever");

    public BGLiveForever() {
        super("BGLiveForever", cardStrings.NAME, "colorless/power/live_forever", -2, cardStrings.DESCRIPTION, CardType.STATUS, BGColorless.Enums.CARD_COLOR, CardRarity.SPECIAL, CardTarget.NONE);
        this.baseBlock=10;
    }



    public void use(AbstractPlayer p, AbstractMonster m) {
        onChoseThisOption();
    }

    public void onChoseThisOption() {
        AbstractPlayer p = AbstractDungeon.player;
        addToBot((AbstractGameAction)new VFXAction((AbstractGameEffect)new BorderFlashEffect(Color.CHARTREUSE, true)));
        addToBot((AbstractGameAction)new VFXAction((AbstractCreature)p, (AbstractGameEffect)new MiracleEffect(Color.CHARTREUSE, Color.LIME, "BLOCK_GAIN_1"), 1.0F));
        addToBot((AbstractGameAction)new GainBlockAction(p,block));
    }


    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeBlock(5);
        }
    }

    public AbstractCard makeCopy() {
        return new BGLiveForever();
    }
}

