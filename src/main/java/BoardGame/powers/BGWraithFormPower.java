package BoardGame.powers;

import BoardGame.relics.BGTheDieRelic;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.ExhaustSpecificCardAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class BGWraithFormPower extends AbstractBGPower {
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings("BoardGame:BGWraithFormPower");
    public static final String POWER_ID = "BoardGame:BGWraithFormPower";
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static int wraithIdOffset;
    private AbstractCard originalcard;
    public BGWraithFormPower(AbstractCreature owner, int amount, AbstractCard originalcard) {
        this.name = NAME;
        this.ID = "BoardGame:BGWraithForm" + wraithIdOffset;
        wraithIdOffset++;
        this.owner = owner;
        this.amount = amount;
        this.originalcard=originalcard;

        updateDescription();
        //loadRegion("intangible");
        //this.priority = 75;
        loadRegion("wraithForm");
        this.priority = 75;
    }

    public void playApplyPowerSfx() {
        CardCrawlGame.sound.play("POWER_INTANGIBLE", 0.05F);
    }

    public void updateDescription() {
        if (this.amount <= 1) {
            this.description = DESCRIPTIONS[0];
        } else {
            this.description = DESCRIPTIONS[1] + this.amount + DESCRIPTIONS[2];
        }
    }


    public void atStartOfTurn() {
        addToBot((AbstractGameAction) new ReducePowerAction(this.owner, this.owner, this, 1));
        if (this.amount > 1) {
            //if amount is currently 1 (before ReducePowerAction is resolved), then it's going to 0 this turn and we don't reapply Invincible
            addToBot((AbstractGameAction) new ApplyPowerAction((AbstractCreature) this.owner, (AbstractCreature) this.owner, (AbstractPower) new BGInvinciblePlayerPower((AbstractCreature) this.owner, 1), 0));
        }else{
            AbstractCard card=originalcard.makeStatEquivalentCopy();
            AbstractDungeon.player.discardPile.addToBottom(card);
            addToBot(new ExhaustSpecificCardAction(card, AbstractDungeon.player.discardPile,true));
            //Logger logger = LogManager.getLogger(BGTheBombPower.class.getName());
            //logger.info("Awakened One check...");
            //this counts as losing a power card, so Awakened One loses 1 str
            for(AbstractMonster m : (AbstractDungeon.getCurrRoom()).monsters.monsters){
                AbstractPower p=m.getPower("BGCuriosityPower");
                if(p!=null){
                    addToBot(new ReducePowerAction((AbstractCreature)m,(AbstractCreature)m,"BGUncappedStrengthPower",1));
                }
            }
        }


    }




}


