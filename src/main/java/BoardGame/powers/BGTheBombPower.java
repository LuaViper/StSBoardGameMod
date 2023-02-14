package BoardGame.powers;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.actions.common.ExhaustSpecificCardAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class BGTheBombPower extends AbstractPower {
    public static final String POWER_ID = "BGTheBomb";
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings("TheBomb");
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private int damage;
    private static int bombIdOffset;
    private AbstractCard originalcard;
    public BGTheBombPower(AbstractCreature owner, int turns, int damage, AbstractCard originalcard) {
        this.name = NAME;
        this.ID = "BGTheBomb" + bombIdOffset;
        bombIdOffset++;
        this.owner = owner;
        this.amount = turns;
        this.damage = damage;
        this.originalcard=originalcard;
        updateDescription();
        loadRegion("the_bomb");
    }


    public void atEndOfTurn(boolean isPlayer) {
        if (!AbstractDungeon.getMonsters().areMonstersBasicallyDead()) {
            addToBot((AbstractGameAction)new ReducePowerAction(this.owner, this.owner, this, 1));
            if (this.amount == 1) {
                addToBot((AbstractGameAction)new DamageAllEnemiesAction(null,


                        DamageInfo.createDamageMatrix(this.damage, true), DamageInfo.DamageType.THORNS, AbstractGameAction.AttackEffect.FIRE));

                AbstractCard card=originalcard.makeStatEquivalentCopy();

                AbstractDungeon.player.discardPile.addToBottom(card);
                addToBot(new ExhaustSpecificCardAction(card, AbstractDungeon.player.discardPile,true));
            }
        }
    }




    public void updateDescription() {
        if (this.amount == 1) {
            this.description = String.format(DESCRIPTIONS[1], new Object[] { Integer.valueOf(this.damage) });
        } else {
            this.description = String.format(DESCRIPTIONS[0], new Object[] { Integer.valueOf(this.amount), Integer.valueOf(this.damage) });
        }
    }
}


