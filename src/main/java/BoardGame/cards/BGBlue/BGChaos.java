package BoardGame.cards.BGBlue;
import BoardGame.BoardGame;
import BoardGame.actions.BGChaosAction;
import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGDefect;
import BoardGame.orbs.BGDark;
import BoardGame.thedie.TheDie;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.defect.ChannelAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//TODO: can we update description while it's in draw/discard?
public class BGChaos extends AbstractBGCard {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGChaos");
    public static final String ID = "BGChaos";

    public BGChaos() {
        super("BGChaos", cardStrings.NAME, "blue/skill/chaos", 1, cardStrings.DESCRIPTION, CardType.SKILL, BGDefect.Enums.BG_BLUE, CardRarity.COMMON, CardTarget.SELF);
        this.showEvokeValue = true;
        this.showEvokeOrbCount = 1;
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot((AbstractGameAction)new BGChaosAction());
    }

    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeBaseCost(0);
        }
    }

    public void compileDescriptionBasedOnDieRoll(){
        //TODO: highlight currently active option in green, if it's possible ([#7FFF00] tags weren't working earlier)
        if (AbstractDungeon.player==null || TheDie.monsterRoll<=0)
            this.rawDescription = cardStrings.DESCRIPTION;
        else{
            this.rawDescription = cardStrings.EXTENDED_DESCRIPTION[0];
//            if(TheDie.monsterRoll==1 || TheDie.monsterRoll==2){
//                this.rawDescription += "[#7FFF00]";
//            }
            this.rawDescription += cardStrings.EXTENDED_DESCRIPTION[1];
//            if(TheDie.monsterRoll==3 || TheDie.monsterRoll==4){
//                this.rawDescription += "[#7FFF00]";
//            }
            this.rawDescription += cardStrings.EXTENDED_DESCRIPTION[2];
//            if(TheDie.monsterRoll==5 || TheDie.monsterRoll==6){
//                this.rawDescription += "[#7FFF00]";
//            }
            this.rawDescription += cardStrings.EXTENDED_DESCRIPTION[3];
            this.rawDescription += cardStrings.EXTENDED_DESCRIPTION[4];
            if(TheDie.monsterRoll==1 || TheDie.monsterRoll==2)
                this.rawDescription += cardStrings.EXTENDED_DESCRIPTION[5];
            else if(TheDie.monsterRoll==3 || TheDie.monsterRoll==4)
                this.rawDescription += cardStrings.EXTENDED_DESCRIPTION[6];
            else if(TheDie.monsterRoll==5 || TheDie.monsterRoll==6)
                this.rawDescription += cardStrings.EXTENDED_DESCRIPTION[7];
        }
        initializeDescription();
    }

    public void applyPowers() {
        super.applyPowers();
        compileDescriptionBasedOnDieRoll();
    }

    public void calculateCardDamage(AbstractMonster mo) {
        super.calculateCardDamage(mo);
        compileDescriptionBasedOnDieRoll();
    }

    public AbstractCard makeCopy() {
        return new BGChaos();
    }
}
