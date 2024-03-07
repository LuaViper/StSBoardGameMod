package BoardGame.powers;

import BoardGame.cards.BGColorless.BGShivSurrogate;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

//TODO: does this cooperate with Cunning Potion?
public class BGTimeWarpPower extends AbstractBGPower {
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings("BoardGame:BGTimeWarpPower");
    public static final String POWER_ID = "BGTimeWarpPower";
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public int initialAmount=5;

    public BGTimeWarpPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = "BGTimeWarpPower";
        this.owner = owner;
        this.amount = amount;
        this.initialAmount=amount;
        updateDescription();
        loadRegion("time");
        this.type = AbstractPower.PowerType.BUFF;
    }
    public void stackPower(int stackAmount) {
        this.fontScale = 8.0F;
        this.amount=stackAmount;
        this.initialAmount=stackAmount;
    }

    public void playApplyPowerSfx() {
        CardCrawlGame.sound.play("POWER_TIME_WARP", 0.05F);
    }

    public boolean canPlayCard(AbstractCard card) {
        if(this.amount<=0){
            //card.cantUseMessage = DESC[4]+this.initialAmount+DESC[5];     //doesn't work as expected.
            return false;
        }
        return true;

    }

    public void updateDescription() {
        if (this.amount > 1 ) {
            this.description = DESCRIPTIONS[0] + (this.amount) + DESCRIPTIONS[1];
        }else if(this.amount==1){
            this.description = DESCRIPTIONS[0] + (this.amount) + DESCRIPTIONS[2];
        }else{
            this.description = DESCRIPTIONS[3];
        }
    }


    public void onAfterUseCard(AbstractCard card, UseCardAction action) {
        if(!(card instanceof BGShivSurrogate)){
            flashWithoutSound();
            this.amount--;
            updateDescription();
        }
    }

//    @Override
//    public void renderAmount(SpriteBatch sb, float x, float y, Color c) {
//        FontHelper.renderFontRightTopAligned(sb, FontHelper.powerAmountFont,
//                Integer.toString(this.amount), x, y, this.fontScale, c);
////        Logger logger = LogManager.getLogger(BoardGame.class.getName());
////        logger.info("TimeWarp render: "+x+" "+y+" "+c+" "+fontScale+" "+this.amount);
//        super.renderAmount(sb,x,y,c);
//    }
}


