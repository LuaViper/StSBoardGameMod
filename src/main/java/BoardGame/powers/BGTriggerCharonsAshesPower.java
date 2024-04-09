package BoardGame.powers;

import BoardGame.actions.BGCheckEndPlayerStartTurnPhaseAction;
import BoardGame.relics.BGCharonsAshes;
import BoardGame.thedie.TheDie;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.ThoughtBubble;

public class BGTriggerCharonsAshesPower extends AbstractBGPower implements ManualStartTurnPhasePower{
    public static final String POWER_ID = "BGTriggerCharonsAshesPower";
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings("BGTriggerCharonsAshesPower");
    private static final String thoughtbubble = "I can trigger Charon's Ashes!"; //TODO: move to localization
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    public boolean doNotActivateOnRemove=false;


    public BGTriggerCharonsAshesPower(AbstractCreature owner) {
        this.name = NAME;
        this.ID = "BGTriggerCharonsAshesPower";
        this.owner = owner;

        updateDescription();

        this.loadRegion("combust");

        this.type = PowerType.BUFF;

    }

    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }

    public void onAboutToUseCard(AbstractCard card) {
        //mayhem fix
        //TODO: mayhem fix is still wrong -- player should have the chance to lock the roll + activate relics before playing mayhem (some cards change depending on roll)
        if(!card.isInAutoplay) {
            addToTop(new RemoveSpecificPowerAction(this.owner, this.owner, "BGTriggerCharonsAshesPower"));
        }
        //TODO: also trigger on use potion and on rightclick power
    }

    public void onInitialApplication() {
        if(!TheDie.forceLockInRoll) {
            if(!AbstractDungeon.player.hasRelic("BGDollysMirror") || TheDie.monsterRoll==2)
                AbstractDungeon.effectList.add(new ThoughtBubble(AbstractDungeon.player.dialogX, AbstractDungeon.player.dialogY, 3.0F,
                        //TODO: localization
                        thoughtbubble, true));
            //TODO: move to DieControlledRelic static function
            for (AbstractRelic relic : AbstractDungeon.player.relics) {
                if (relic instanceof BGCharonsAshes) {
                    relic.beginLongPulse();
                }
            }
        }else{
            addToTop(new RemoveSpecificPowerAction(this.owner, this.owner, "BGTriggerCharonsAshesPower"));
        }
    }
    public void onRemove() {
        for (AbstractRelic relic : AbstractDungeon.player.relics) {
            if (relic instanceof BGCharonsAshes) {
                relic.stopPulse();
            }
        }
        addToBot(new BGCheckEndPlayerStartTurnPhaseAction());
    }


    public void atEndOfTurn(boolean isPlayer) {
        addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, "BGTriggerCharonsAshesPower"));
    }

}