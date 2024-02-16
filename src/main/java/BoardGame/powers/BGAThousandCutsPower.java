package BoardGame.powers;

import BoardGame.dungeons.AbstractBGDungeon;
import BoardGame.relics.BGTheDieRelic;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.actions.common.EmptyDeckShuffleAction;
import com.megacrit.cardcrawl.actions.common.ShuffleAction;
import com.megacrit.cardcrawl.actions.defect.ShuffleAllAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.combat.CleaveEffect;

public class BGAThousandCutsPower extends AbstractBGPower {
    public static final String POWER_ID = "BoardGame:BGAThousandCutsPower";
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings("BoardGame:BGAThousandCutsPower");
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public BGAThousandCutsPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = "BoardGame:BGAThousandCutsPower";
        this.owner = owner;
        this.amount = amount;
        updateDescription();
        loadRegion("thousandCuts");
    }


    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }


    public void onShuffle(){
        flash();
        addToBot((AbstractGameAction)new SFXAction("ATTACK_HEAVY"));
        if (Settings.FAST_MODE) {
            addToBot((AbstractGameAction)new VFXAction((AbstractGameEffect)new CleaveEffect()));
        } else {
            addToBot((AbstractGameAction)new VFXAction(this.owner, (AbstractGameEffect)new CleaveEffect(), 0.2F));
        }
        addToBot((AbstractGameAction)new DamageAllEnemiesAction(this.owner,
                DamageInfo.createDamageMatrix(this.amount, true), DamageInfo.DamageType.THORNS, AbstractGameAction.AttackEffect.NONE, true));
    }





    @SpirePatch2(clz = ShuffleAction.class, method = "update",
            paramtypez={})
    public static class ShuffleActionPatch {
        @SpirePostfixPatch
        public static void Postfix() {
            if(!(CardCrawlGame.dungeon instanceof AbstractBGDungeon)) return;
            for(AbstractPower p:AbstractDungeon.player.powers){
                if(p instanceof AbstractBGPower){
                    ((AbstractBGPower)p).onShuffle();
                }
            }
        }
    }

    @SpirePatch2(clz = EmptyDeckShuffleAction.class, method = SpirePatch.CONSTRUCTOR,
            paramtypez={})
    public static class EmptyDeckShuffleActionPatch {
        @SpirePostfixPatch
        public static void Postfix() {
            if(!(CardCrawlGame.dungeon instanceof AbstractBGDungeon)) return;
            for(AbstractPower p:AbstractDungeon.player.powers){
                if(p instanceof AbstractBGPower){
                    ((AbstractBGPower)p).onShuffle();
                }
            }
        }
    }

    @SpirePatch2(clz = ShuffleAllAction.class, method = SpirePatch.CONSTRUCTOR,
            paramtypez={})
    public static class ShuffleAllActionPatch {
        @SpirePostfixPatch
        public static void Postfix() {
            if(!(CardCrawlGame.dungeon instanceof AbstractBGDungeon)) return;
            for(AbstractPower p:AbstractDungeon.player.powers){
                if(p instanceof AbstractBGPower){
                    ((AbstractBGPower)p).onShuffle();
                }
            }
        }
    }



}


