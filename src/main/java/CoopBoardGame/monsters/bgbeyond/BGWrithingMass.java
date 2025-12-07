package CoopBoardGame.monsters.bgbeyond;

import CoopBoardGame.cards.BGStatus.BGDazed;
import CoopBoardGame.monsters.AbstractBGMonster;
import CoopBoardGame.monsters.BGDamageIcons;
import CoopBoardGame.monsters.DieControlledMoves;
import CoopBoardGame.powers.BGReactivePower;
import CoopBoardGame.powers.BGVulnerablePower;
import CoopBoardGame.powers.BGWeakPower;
import CoopBoardGame.thedie.TheDie;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateFastAttackAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.animations.FastShakeAction;
import com.megacrit.cardcrawl.actions.animations.ShoutAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

//TODO: when hit with Skewer, Writhing Mass *repeatedly* switched to vulnerable and made several overlapping Ominous Noises
public class BGWrithingMass extends AbstractBGMonster implements DieControlledMoves, BGDamageIcons {

    public static final String ID = "BGWrithingMass";
    private static final MonsterStrings monsterStrings =
        CardCrawlGame.languagePack.getMonsterStrings("WrithingMass");
    public static final String NAME = monsterStrings.NAME;

    public BGWrithingMass() {
        super(NAME, "BGWrithingMass", 160, 5.0F, -26.0F, 450.0F, 310.0F, null, 0.0F, 15.0F);
        loadAnimation(
            "images/monsters/theForest/spaghetti/skeleton.atlas",
            "images/monsters/theForest/spaghetti/skeleton.json",
            1.0F
        );

        AnimationState.TrackEntry e = this.state.setAnimation(0, "Idle", true);
        e.setTime(e.getEndTime() * MathUtils.random());
        this.stateData.setMix("Hit", "Idle", 0.1F);

        setHp(27);
        this.damage.add(new DamageInfo((AbstractCreature) this, 5));
        this.damage.add(new DamageInfo((AbstractCreature) this, 0));
        this.damage.add(new DamageInfo((AbstractCreature) this, 3));
        this.damage.add(new DamageInfo((AbstractCreature) this, 0));
        this.damage.add(new DamageInfo((AbstractCreature) this, 7));
        this.damage.add(new DamageInfo((AbstractCreature) this, 4));
    }


    public void usePreBattleAction() {
        AbstractDungeon.actionManager.addToBottom(
            (AbstractGameAction) new ApplyPowerAction(
                (AbstractCreature) this,
                (AbstractCreature) this,
                (AbstractPower) new BGReactivePower((AbstractCreature) this)
            )
        );
    }

    public void takeTurn() {
        int i;
        switch (this.nextMove) {
            case 0:
                AbstractDungeon.actionManager.addToBottom(
                    (AbstractGameAction) new AnimateFastAttackAction((AbstractCreature) this)
                );
                AbstractDungeon.actionManager.addToBottom(
                    (AbstractGameAction) new DamageAction(
                        (AbstractCreature) AbstractDungeon.player,
                        this.damage.get(0),
                        AbstractGameAction.AttackEffect.BLUNT_HEAVY
                    )
                );
                AbstractDungeon.actionManager.addToBottom(
                    (AbstractGameAction) new GainBlockAction(
                        (AbstractCreature) this,
                        (AbstractCreature) this,
                        5
                    )
                );
                break;
            case 1:
                //this.usedMegaDebuff = true;
                //AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new FastShakeAction((AbstractCreature)this, 0.5F, 0.2F));
                AbstractDungeon.actionManager.addToBottom(
                    (AbstractGameAction) new MakeTempCardInDrawPileAction(
                        (AbstractCard) new BGDazed(),
                        2,
                        false,
                        true
                    )
                );
                break;
            case 2:
                AbstractDungeon.actionManager.addToBottom(
                    (AbstractGameAction) new AnimateSlowAttackAction((AbstractCreature) this)
                );
                for (i = 0; i < 2; i++) {
                    AbstractDungeon.actionManager.addToBottom(
                        (AbstractGameAction) new DamageAction(
                            (AbstractCreature) AbstractDungeon.player,
                            this.damage.get(2),
                            AbstractGameAction.AttackEffect.BLUNT_LIGHT
                        )
                    );
                }
                break;
            case 3:
                //this.usedMegaDebuff = true;
                AbstractDungeon.actionManager.addToBottom(
                    (AbstractGameAction) new FastShakeAction((AbstractCreature) this, 0.5F, 0.2F)
                );
                AbstractDungeon.actionManager.addToBottom(
                    (AbstractGameAction) new ApplyPowerAction(
                        (AbstractCreature) AbstractDungeon.player,
                        (AbstractCreature) this,
                        (AbstractPower) new BGVulnerablePower(
                            (AbstractCreature) AbstractDungeon.player,
                            1,
                            true
                        ),
                        1
                    )
                );
                break;
            case 4:
                AbstractDungeon.actionManager.addToBottom(
                    (AbstractGameAction) new ChangeStateAction(this, "ATTACK")
                );
                AbstractDungeon.actionManager.addToBottom(
                    (AbstractGameAction) new WaitAction(0.4F)
                );
                AbstractDungeon.actionManager.addToBottom(
                    (AbstractGameAction) new DamageAction(
                        (AbstractCreature) AbstractDungeon.player,
                        this.damage.get(4),
                        AbstractGameAction.AttackEffect.SLASH_HEAVY
                    )
                );
                break;
            case 5:
                AbstractDungeon.actionManager.addToBottom(
                    (AbstractGameAction) new DamageAction(
                        (AbstractCreature) AbstractDungeon.player,
                        this.damage.get(5),
                        AbstractGameAction.AttackEffect.BLUNT_HEAVY
                    )
                );
                AbstractDungeon.actionManager.addToBottom(
                    (AbstractGameAction) new ApplyPowerAction(
                        (AbstractCreature) AbstractDungeon.player,
                        (AbstractCreature) this,
                        (AbstractPower) new BGWeakPower(
                            (AbstractCreature) AbstractDungeon.player,
                            1,
                            true
                        ),
                        1
                    )
                );

                AbstractDungeon.actionManager.addToBottom(
                    (AbstractGameAction) new AnimateFastAttackAction((AbstractCreature) this)
                );
        }

        AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new RollMoveAction(this));
    }

    public void damage(DamageInfo info) {
        if (info.owner != null && info.type != DamageInfo.DamageType.THORNS && info.output > 0) {
            this.state.setAnimation(0, "Hit", false);
            this.state.addAnimation(0, "Idle", true, 0.0F);
        }

        super.damage(info);
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
        setMove((byte) 0, AbstractMonster.Intent.NONE);
    }

    public void dieMove(int roll) {
        //TODO: reorder if-else from 1 to 6 after checking physical card
        if (TheDie.monsterRoll == 4) {
            setMove(
                (byte) 0,
                AbstractMonster.Intent.ATTACK_DEFEND,
                ((DamageInfo) this.damage.get(0)).base
            );
        } else if (TheDie.monsterRoll == 2) {
            setMove((byte) 1, AbstractMonster.Intent.DEBUFF);
        } else if (TheDie.monsterRoll == 3) {
            setMove(
                (byte) 2,
                AbstractMonster.Intent.ATTACK,
                ((DamageInfo) this.damage.get(2)).base,
                2,
                true
            );
        } else if (TheDie.monsterRoll == 1) {
            AbstractDungeon.actionManager.addToBottom(
                (AbstractGameAction) new ShoutAction(
                    (AbstractCreature) this,
                    "@(Ominous@ @noises)@",
                    1.0F,
                    2.0F
                )
            ); //TODO: localization
            setMove((byte) 3, AbstractMonster.Intent.STRONG_DEBUFF);
        } else if (TheDie.monsterRoll == 5) {
            setMove(
                (byte) 4,
                AbstractMonster.Intent.ATTACK,
                ((DamageInfo) this.damage.get(4)).base
            );
        } else if (TheDie.monsterRoll == 6) {
            setMove(
                (byte) 5,
                AbstractMonster.Intent.ATTACK_DEBUFF,
                ((DamageInfo) this.damage.get(5)).base
            );
        }
    }
}
