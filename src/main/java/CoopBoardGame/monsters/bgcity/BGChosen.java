package CoopBoardGame.monsters.bgcity;

import CoopBoardGame.cards.BGStatus.BGDazed;
import CoopBoardGame.monsters.AbstractBGMonster;
import CoopBoardGame.monsters.BGDamageIcons;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.animations.FastShakeAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.ChangeStateAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;

public class BGChosen extends AbstractBGMonster implements BGDamageIcons {

    public static final String ID = "BGChosen";
    private static final MonsterStrings monsterStrings =
        CardCrawlGame.languagePack.getMonsterStrings("Chosen");
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    public BGChosen() {
        this(0.0F, 0.0F, 14);
    }

    public BGChosen(float x, float y, int hp) {
        super(NAME, "BGChosen", 99, 5.0F, -10.0F, 200.0F, 280.0F, null, x, -20.0F + y);
        this.dialogX = -30.0F * Settings.scale;
        this.dialogY = 50.0F * Settings.scale;

        setHp(hp);

        this.damage.add(new DamageInfo((AbstractCreature) this, 3));
        this.damage.add(new DamageInfo((AbstractCreature) this, 5));
        this.damage.add(new DamageInfo((AbstractCreature) this, 1));

        loadAnimation(
            "images/monsters/theCity/chosen/skeleton.atlas",
            "images/monsters/theCity/chosen/skeleton.json",
            1.0F
        );

        AnimationState.TrackEntry e = this.state.setAnimation(0, "Idle", true);
        e.setTime(e.getEndTime() * MathUtils.random());
        this.stateData.setMix("Hit", "Idle", 0.2F);
        this.stateData.setMix("Attack", "Idle", 0.2F);
        this.state.setTimeScale(0.8F);
    }

    public void takeTurn() {
        switch (this.nextMove) {
            case 1:
                AbstractDungeon.actionManager.addToBottom(
                    (AbstractGameAction) new TalkAction((AbstractCreature) this, DIALOG[0])
                );
                AbstractDungeon.actionManager.addToBottom(
                    (AbstractGameAction) new ChangeStateAction(this, "ATTACK")
                );
                AbstractDungeon.actionManager.addToBottom(
                    (AbstractGameAction) new WaitAction(0.2F)
                );
                AbstractDungeon.actionManager.addToBottom(
                    (AbstractGameAction) new FastShakeAction((AbstractCreature) this, 0.3F, 0.5F)
                );
                AbstractDungeon.actionManager.addToBottom(
                    (AbstractGameAction) new DamageAction(
                        (AbstractCreature) AbstractDungeon.player,
                        this.damage.get(2),
                        AbstractGameAction.AttackEffect.FIRE
                    )
                );
                addToBot(
                    (AbstractGameAction) new MakeTempCardInDrawPileAction(
                        (AbstractCard) new BGDazed(),
                        1,
                        false,
                        true
                    )
                );
                setMove((byte) 2, AbstractMonster.Intent.ATTACK_DEBUFF, this.damage.get(0).base);
                break;
            case 2:
                AbstractDungeon.actionManager.addToBottom(
                    (AbstractGameAction) new FastShakeAction((AbstractCreature) this, 0.3F, 0.5F)
                );
                AbstractDungeon.actionManager.addToBottom(
                    (AbstractGameAction) new DamageAction(
                        (AbstractCreature) AbstractDungeon.player,
                        this.damage.get(0),
                        AbstractGameAction.AttackEffect.FIRE
                    )
                );
                addToBot(
                    (AbstractGameAction) new MakeTempCardInDrawPileAction(
                        (AbstractCard) new BGDazed(),
                        2,
                        false,
                        true
                    )
                );
                setMove((byte) 3, AbstractMonster.Intent.ATTACK_BUFF, this.damage.get(1).base);

                break;
            case 3:
                AbstractDungeon.actionManager.addToBottom(
                    (AbstractGameAction) new AnimateSlowAttackAction((AbstractCreature) this)
                );
                AbstractDungeon.actionManager.addToBottom(
                    (AbstractGameAction) new DamageAction(
                        (AbstractCreature) AbstractDungeon.player,
                        this.damage.get(1),
                        AbstractGameAction.AttackEffect.SLASH_HEAVY
                    )
                );
                AbstractDungeon.actionManager.addToBottom(
                    (AbstractGameAction) new ApplyPowerAction(
                        (AbstractCreature) this,
                        (AbstractCreature) this,
                        (AbstractPower) new StrengthPower((AbstractCreature) this, 1),
                        1
                    )
                );
                setMove((byte) 2, AbstractMonster.Intent.ATTACK_DEBUFF, this.damage.get(0).base);
                break;
        }
    }

    public void changeState(String key) {
        switch (key) {
            case "ATTACK":
                this.state.setAnimation(0, "Attack", false);
                this.state.addAnimation(0, "Idle", true, 0.0F);
                break;
        }
    }

    protected void getMove(int num) {
        setMove((byte) 1, AbstractMonster.Intent.ATTACK_DEBUFF, this.damage.get(2).base);
    }

    public void damage(DamageInfo info) {
        super.damage(info);
        if (info.owner != null && info.type != DamageInfo.DamageType.THORNS && info.output > 0) {
            this.state.setAnimation(0, "Hit", false);
            this.state.setTimeScale(0.8F);
            this.state.addAnimation(0, "Idle", true, 0.0F);
        }
    }

    public void die() {
        super.die();
        CardCrawlGame.sound.play("CHOSEN_DEATH");
    }
}
