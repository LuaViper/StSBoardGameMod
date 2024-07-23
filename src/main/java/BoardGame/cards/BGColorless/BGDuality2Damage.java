package BoardGame.cards.BGColorless;

import BoardGame.actions.TargetSelectScreenAction;
import BoardGame.cards.AbstractBGAttackCardChoice;
import BoardGame.characters.BGColorless;
import BoardGame.screen.TargetSelectScreen;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class BGDuality2Damage extends AbstractBGAttackCardChoice {
    public static final String ID = "BGDuality2Damage";
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGDuality2Damage");

    public BGDuality2Damage() {
        super("BGDuality2Damage", cardStrings.NAME, "purple/attack/strike", -2, cardStrings.DESCRIPTION, CardType.STATUS, BGColorless.Enums.CARD_COLOR, CardRarity.SPECIAL, CardTarget.NONE);
        this.baseDamage=2;
    }



    public void use(AbstractPlayer p, AbstractMonster m) {}



    public void onChoseThisOption() {
        TargetSelectScreen.TargetSelectAction tssAction = (target) -> {
            if(target==null)return;
            addToBot((AbstractGameAction) new DamageAction(target,
                    new DamageInfo(AbstractDungeon.player,2, DamageInfo.DamageType.THORNS),
                    AbstractGameAction.AttackEffect.BLUNT_LIGHT));
        };
        addToBot((AbstractGameAction)new TargetSelectScreenAction(tssAction,"Choose a target for Duality (2 damage)."));
    }



    public void upgrade() {}


    public AbstractCard makeCopy() {
        return new BGDuality2Damage();
    }
}


