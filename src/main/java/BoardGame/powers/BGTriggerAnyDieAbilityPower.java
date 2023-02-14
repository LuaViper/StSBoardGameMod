package BoardGame.powers;

import BoardGame.actions.BGUpdateDieRelicPulseAction;
import BoardGame.relics.BGTheDieRelic;
import BoardGame.relics.DieControlledRelic;
import BoardGame.util.TextureLoader;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.ThoughtBubble;

public class BGTriggerAnyDieAbilityPower extends AbstractPower {
    public static final String POWER_ID = "BGTriggerAnyDieAbilityPower";
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings("BGTriggerAnyDieAbilityPower");
    private static final String thoughtbubble = "I can trigger a #rdie #rability!"; //TODO: move to localization
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    public boolean doNotActivateOnRemove=false;

    private static final Texture tex84 = TextureLoader.getTexture("BoardGameResources/images/powers/wildcard_power84.png");
    private static final Texture tex32 = TextureLoader.getTexture("BoardGameResources/images/powers/wildcard_power32.png");

    public BGTriggerAnyDieAbilityPower(AbstractCreature owner) {
        this.name = NAME;
        this.ID = "BGTriggerAnyDieAbilityPower";
        this.owner = owner;

        updateDescription();
        this.region128 = new TextureAtlas.AtlasRegion(tex84, 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(tex32, 0, 0, 32, 32);

        this.type = AbstractPower.PowerType.BUFF;

    }

    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }

    public void onUseCard(AbstractCard card, UseCardAction action) {
        addToTop(new RemoveSpecificPowerAction(this.owner, this.owner, "BGTriggerAnyDieAbilityPower"));
    }

    public void onInitialApplication() {
        AbstractDungeon.effectList.add(new ThoughtBubble(AbstractDungeon.player.dialogX, AbstractDungeon.player.dialogY, 3.0F, thoughtbubble, true));
        //TODO: move to DieControlledRelic static function
        for(AbstractRelic relic : AbstractDungeon.player.relics){
            if(relic instanceof DieControlledRelic){
                relic.beginLongPulse();
            }
        }

    }
    public void onRemove() {
        if(!doNotActivateOnRemove) {
            BGTheDieRelic die = (BGTheDieRelic) AbstractDungeon.player.getRelic(BGTheDieRelic.ID);
            if (die != null) {
                die.activateDieAbility();
            }
        }
        addToBot((AbstractGameAction)new BGUpdateDieRelicPulseAction());
    }


    public void atEndOfTurn(boolean isPlayer) {
        addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, "BGTriggerAnyDieAbilityPower"));
    }

}