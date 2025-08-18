package BoardGame.cards.BGGreen;

import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGSilent;
import BoardGame.powers.BGNoxiousFumesAOEPower;
import BoardGame.powers.BGNoxiousFumesPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.rooms.MonsterRoom;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BGNoxiousFumes extends AbstractBGCard {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGNoxiousFumes");
    public static final String ID = "BGNoxiousFumes";

    private AbstractMonster target;

    static Logger logger = LogManager.getLogger(BGNoxiousFumes.class.getName());
    public BGNoxiousFumes() {
        super("BGNoxiousFumes", cardStrings.NAME, "green/power/noxious_fumes", 1, cardStrings.DESCRIPTION, CardType.POWER, BGSilent.Enums.BG_GREEN, CardRarity.UNCOMMON, CardTarget.SELF);

        this.baseMagicNumber = 1;
        this.magicNumber=this.baseMagicNumber;
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        if(!this.upgraded)
            addToBot((AbstractGameAction)new ApplyPowerAction((AbstractCreature)p, (AbstractCreature)p, (AbstractPower)new BGNoxiousFumesPower((AbstractCreature)p, this.magicNumber), this.magicNumber));
        else
            if (!(AbstractDungeon.getCurrRoom() instanceof MonsterRoom && AbstractDungeon.lastCombatMetricKey.equals("BoardGame:Shield and Spear")))
                addToBot((AbstractGameAction)new ApplyPowerAction((AbstractCreature)p, (AbstractCreature)p, (AbstractPower)new BGNoxiousFumesAOEPower((AbstractCreature)p, this.magicNumber), this.magicNumber));
            else
                //if this is Shield and Spear, they're in different rows, so just apply regular non-AOE power
                //TODO: eventually we will need to prompt AOE power for which row to hit, at which point we can remove this check
                addToBot((AbstractGameAction)new ApplyPowerAction((AbstractCreature)p, (AbstractCreature)p, (AbstractPower)new BGNoxiousFumesPower((AbstractCreature)p, this.magicNumber), this.magicNumber));
    }

    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            initializeDescription();
            //we're using the this.upgraded flag itself to track whether the power has a different effect
        }
    }

    public AbstractCard makeCopy() {
        return new BGNoxiousFumes();
    }
}


