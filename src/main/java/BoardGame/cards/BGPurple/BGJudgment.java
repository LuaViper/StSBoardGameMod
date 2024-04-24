package BoardGame.cards.BGPurple;

import BoardGame.actions.BGJudgmentAction;
import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGWatcher;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.actions.watcher.JudgementAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.combat.GiantTextEffect;
import com.megacrit.cardcrawl.vfx.combat.WeightyImpactEffect;

//TODO: are any other for-loop multi-hit cards missing WEAKVULN_ZEROHITS?

public class BGJudgment extends AbstractBGCard {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGJudgment");
    public static final String ID = "BGJudgment";

    public BGJudgment() {
        super("BGJudgment", cardStrings.NAME, "purple/skill/judgment", 1, cardStrings.DESCRIPTION, CardType.SKILL, BGWatcher.Enums.BG_PURPLE, CardRarity.RARE, CardTarget.ENEMY);

        isEthereal=true;
        baseMagicNumber=7;
        magicNumber=baseMagicNumber;
    }



    public void use(AbstractPlayer p, AbstractMonster m) {
        if (m != null) {
            addToBot((AbstractGameAction)new VFXAction((AbstractGameEffect)new WeightyImpactEffect(m.hb.cX, m.hb.cY, Color.GOLD.cpy())));
            addToBot((AbstractGameAction)new WaitAction(0.8F));
            addToBot((AbstractGameAction)new VFXAction((AbstractGameEffect)new GiantTextEffect(m.hb.cX, m.hb.cY)));
        }
        //addToBot((AbstractGameAction)new BGJudgmentAction((AbstractCreature)m, this.magicNumber));
        //apparently Judgment DOES kill Nemesis
        addToBot((AbstractGameAction)new JudgementAction((AbstractCreature)m, this.magicNumber));
    }

    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            isEthereal=false;
            selfRetain=true;
            upgradeMagicNumber(1);
            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            initializeDescription();
        }
    }

    public AbstractCard makeCopy() {
        return new BGJudgment();
    }
}



