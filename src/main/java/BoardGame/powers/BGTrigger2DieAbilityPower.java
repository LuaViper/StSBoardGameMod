package BoardGame.powers;

import BoardGame.actions.BGUpdateDieRelicPulseAction;
import BoardGame.relics.BGNilrysCodex;
import BoardGame.thedie.TheDie;
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

public class BGTrigger2DieAbilityPower extends AbstractBGPower {
    public static final String POWER_ID = "BGTrigger2DieAbilityPower";
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings("BGTrigger2DieAbilityPower");
    private static final String thoughtbubble = "I can trigger a #rdie #rability!"; //TODO: move to localization
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    public boolean doNotActivateOnRemove=false;

    private static final Texture tex84 = TextureLoader.getTexture("BoardGameResources/images/powers/nilry_power84.png");
    private static final Texture tex32 = TextureLoader.getTexture("BoardGameResources/images/powers/nilry_power32.png");
    public BGTrigger2DieAbilityPower(AbstractCreature owner) {
        this.name = NAME;
        this.ID = "BGTrigger2DieAbilityPower";
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
        //mayhem fix
        //TODO: mayhem fix is still wrong -- player should have the chance to lock the roll + activate relics before playing mayhem (some cards change depending on roll)
        if(!card.isInAutoplay) {
            addToTop(new RemoveSpecificPowerAction(this.owner, this.owner, "BGTrigger2DieAbilityPower"));
        }
        //TODO: also trigger on use potion and on rightclick power
    }

    public void onInitialApplication() {
        if(!TheDie.forceLockInRoll) {
            AbstractDungeon.effectList.add(new ThoughtBubble(AbstractDungeon.player.dialogX, AbstractDungeon.player.dialogY, 3.0F, thoughtbubble, true));
            //TODO: move to DieControlledRelic static function
            for (AbstractRelic relic : AbstractDungeon.player.relics) {
                if (relic instanceof NilrysCodexCompatible) {
                    relic.beginLongPulse();
                }
            }
        }else{
            addToTop(new RemoveSpecificPowerAction(this.owner, this.owner, "BGTrigger2DieAbilityPower"));
        }
    }
    public void onRemove() {
        if(!doNotActivateOnRemove) {
            BGNilrysCodex codex = (BGNilrysCodex) AbstractDungeon.player.getRelic(BGNilrysCodex.ID);
            if (codex != null) {
                codex.activateDieAbility();
            }
        }
        addToBot((AbstractGameAction)new BGUpdateDieRelicPulseAction());

    }


    public void atEndOfTurn(boolean isPlayer) {
        addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, "BGTrigger2DieAbilityPower"));
    }

}