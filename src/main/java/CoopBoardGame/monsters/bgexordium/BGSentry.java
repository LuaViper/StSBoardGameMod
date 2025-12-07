package CoopBoardGame.monsters.bgexordium;

import CoopBoardGame.cards.BGStatus.BGDazed;
import CoopBoardGame.monsters.AbstractBGMonster;
import CoopBoardGame.monsters.BGDamageIcons;
import CoopBoardGame.monsters.DieControlledMoves;
import CoopBoardGame.thedie.TheDie;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.FastShakeAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.BorderFlashEffect;
import com.megacrit.cardcrawl.vfx.combat.ShockWaveEffect;
import com.megacrit.cardcrawl.vfx.combat.SmallLaserEffect;

public class BGSentry extends AbstractBGMonster implements BGDamageIcons, DieControlledMoves {

    private static final MonsterStrings monsterStrings =
        CardCrawlGame.languagePack.getMonsterStrings("Sentry");
    public static final String ID = "Sentry";
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;
    public static final String ENC_NAME = "Sentries";

    public BGSentry(float x, float y, String behavior) {
        super(NAME, "BGSentry", 42, 0.0F, -5.0F, 180.0F, 310.0F, null, x, y);
        this.behavior = behavior;

        this.type = AbstractMonster.EnemyType.ELITE;

        if (
            behavior.equals("D3") || (behavior.equals("2D") && AbstractDungeon.ascensionLevel == 0)
        ) setHp(7);
        else if (behavior.equals("2D") && AbstractDungeon.ascensionLevel >= 12) setHp(9);
        else setHp(8);
        //note -- on A1+, sentry C's behavior is changed to "--" later

        this.damage.add(new DamageInfo((AbstractCreature) this, 2));
        this.damage.add(new DamageInfo((AbstractCreature) this, 3));

        loadAnimation(
            "images/monsters/theBottom/sentry/skeleton.atlas",
            "images/monsters/theBottom/sentry/skeleton.json",
            1.0F
        );

        AnimationState.TrackEntry e = this.state.setAnimation(0, "idle", true);
        e.setTimeScale(2.0F);
        e.setTime(e.getEndTime() * MathUtils.random());
        this.stateData.setMix("idle", "attack", 0.1F);
        this.stateData.setMix("idle", "spaz1", 0.1F);
        this.stateData.setMix("idle", "hit", 0.1F);
    }

    public void usePreBattleAction() {
        //AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ApplyPowerAction((AbstractCreature)this, (AbstractCreature)this, (AbstractPower)new ArtifactPower((AbstractCreature)this, 1)));
    }

    public void takeTurn() {
        switch (this.nextMove) {
            case 3: //daze
                AbstractDungeon.actionManager.addToBottom(
                    (AbstractGameAction) new SFXAction("THUNDERCLAP")
                );
                if (!Settings.FAST_MODE) {
                    AbstractDungeon.actionManager.addToBottom(
                        (AbstractGameAction) new VFXAction(
                            (AbstractCreature) this,
                            (AbstractGameEffect) new ShockWaveEffect(
                                this.hb.cX,
                                this.hb.cY,
                                Color.ROYAL,
                                ShockWaveEffect.ShockWaveType.ADDITIVE
                            ),
                            0.5F
                        )
                    );
                    AbstractDungeon.actionManager.addToBottom(
                        (AbstractGameAction) new FastShakeAction(
                            (AbstractCreature) AbstractDungeon.player,
                            0.6F,
                            0.2F
                        )
                    );
                } else {
                    AbstractDungeon.actionManager.addToBottom(
                        (AbstractGameAction) new VFXAction(
                            (AbstractCreature) this,
                            (AbstractGameEffect) new ShockWaveEffect(
                                this.hb.cX,
                                this.hb.cY,
                                Color.ROYAL,
                                ShockWaveEffect.ShockWaveType.ADDITIVE
                            ),
                            0.1F
                        )
                    );
                    AbstractDungeon.actionManager.addToBottom(
                        (AbstractGameAction) new FastShakeAction(
                            (AbstractCreature) AbstractDungeon.player,
                            0.6F,
                            0.15F
                        )
                    );
                }
                //TODO: on ascension 12, single daze is AOE
                addToBot(
                    (AbstractGameAction) new MakeTempCardInDrawPileAction(
                        (AbstractCard) new BGDazed(),
                        1,
                        false,
                        true
                    )
                );
                AbstractDungeon.actionManager.addToBottom(
                    (AbstractGameAction) new RollMoveAction(this)
                );
                break;
            case 4: //2 dmg
                AbstractDungeon.actionManager.addToBottom(
                    (AbstractGameAction) new ChangeStateAction(this, "ATTACK")
                );
                AbstractDungeon.actionManager.addToBottom(
                    (AbstractGameAction) new SFXAction("ATTACK_MAGIC_BEAM_SHORT", 0.5F)
                );
                AbstractDungeon.actionManager.addToBottom(
                    (AbstractGameAction) new VFXAction(
                        (AbstractGameEffect) new BorderFlashEffect(Color.SKY)
                    )
                );
                if (Settings.FAST_MODE) {
                    AbstractDungeon.actionManager.addToBottom(
                        (AbstractGameAction) new VFXAction(
                            (AbstractGameEffect) new SmallLaserEffect(
                                AbstractDungeon.player.hb.cX,
                                AbstractDungeon.player.hb.cY,
                                this.hb.cX,
                                this.hb.cY
                            ),
                            0.1F
                        )
                    );
                } else {
                    AbstractDungeon.actionManager.addToBottom(
                        (AbstractGameAction) new VFXAction(
                            (AbstractGameEffect) new SmallLaserEffect(
                                AbstractDungeon.player.hb.cX,
                                AbstractDungeon.player.hb.cY,
                                this.hb.cX,
                                this.hb.cY
                            ),
                            0.3F
                        )
                    );
                }
                AbstractDungeon.actionManager.addToBottom(
                    (AbstractGameAction) new DamageAction(
                        (AbstractCreature) AbstractDungeon.player,
                        this.damage.get(0),
                        AbstractGameAction.AttackEffect.NONE,
                        Settings.FAST_MODE
                    )
                );
                AbstractDungeon.actionManager.addToBottom(
                    (AbstractGameAction) new RollMoveAction(this)
                );
                break;
            case 5: //3 dmg
                AbstractDungeon.actionManager.addToBottom(
                    (AbstractGameAction) new ChangeStateAction(this, "ATTACK")
                );
                AbstractDungeon.actionManager.addToBottom(
                    (AbstractGameAction) new SFXAction("ATTACK_MAGIC_BEAM_SHORT", 0.5F)
                );
                AbstractDungeon.actionManager.addToBottom(
                    (AbstractGameAction) new VFXAction(
                        (AbstractGameEffect) new BorderFlashEffect(Color.SKY)
                    )
                );
                if (Settings.FAST_MODE) {
                    AbstractDungeon.actionManager.addToBottom(
                        (AbstractGameAction) new VFXAction(
                            (AbstractGameEffect) new SmallLaserEffect(
                                AbstractDungeon.player.hb.cX,
                                AbstractDungeon.player.hb.cY,
                                this.hb.cX,
                                this.hb.cY
                            ),
                            0.1F
                        )
                    );
                } else {
                    AbstractDungeon.actionManager.addToBottom(
                        (AbstractGameAction) new VFXAction(
                            (AbstractGameEffect) new SmallLaserEffect(
                                AbstractDungeon.player.hb.cX,
                                AbstractDungeon.player.hb.cY,
                                this.hb.cX,
                                this.hb.cY
                            ),
                            0.3F
                        )
                    );
                }
                AbstractDungeon.actionManager.addToBottom(
                    (AbstractGameAction) new DamageAction(
                        (AbstractCreature) AbstractDungeon.player,
                        this.damage.get(1),
                        AbstractGameAction.AttackEffect.NONE,
                        Settings.FAST_MODE
                    )
                );
                AbstractDungeon.actionManager.addToBottom(
                    (AbstractGameAction) new RollMoveAction(this)
                );
                break;
            case 6: //A1 daze
                AbstractDungeon.actionManager.addToBottom(
                    (AbstractGameAction) new SFXAction("THUNDERCLAP")
                );
                if (!Settings.FAST_MODE) {
                    AbstractDungeon.actionManager.addToBottom(
                        (AbstractGameAction) new VFXAction(
                            (AbstractCreature) this,
                            (AbstractGameEffect) new ShockWaveEffect(
                                this.hb.cX,
                                this.hb.cY,
                                Color.ROYAL,
                                ShockWaveEffect.ShockWaveType.ADDITIVE
                            ),
                            0.5F
                        )
                    );
                    AbstractDungeon.actionManager.addToBottom(
                        (AbstractGameAction) new FastShakeAction(
                            (AbstractCreature) AbstractDungeon.player,
                            0.6F,
                            0.2F
                        )
                    );
                } else {
                    AbstractDungeon.actionManager.addToBottom(
                        (AbstractGameAction) new VFXAction(
                            (AbstractCreature) this,
                            (AbstractGameEffect) new ShockWaveEffect(
                                this.hb.cX,
                                this.hb.cY,
                                Color.ROYAL,
                                ShockWaveEffect.ShockWaveType.ADDITIVE
                            ),
                            0.1F
                        )
                    );
                    AbstractDungeon.actionManager.addToBottom(
                        (AbstractGameAction) new FastShakeAction(
                            (AbstractCreature) AbstractDungeon.player,
                            0.6F,
                            0.15F
                        )
                    );
                }
                addToBot(
                    (AbstractGameAction) new MakeTempCardInDrawPileAction(
                        (AbstractCard) new BGDazed(),
                        1,
                        false,
                        true
                    )
                );
                AbstractDungeon.actionManager.addToBottom(
                    (AbstractGameAction) new SetMoveAction(
                        this,
                        (byte) 7,
                        AbstractMonster.Intent.ATTACK,
                        ((DamageInfo) this.damage.get(0)).base
                    )
                );
                break;
            case 7: //A0 2 dmg
                AbstractDungeon.actionManager.addToBottom(
                    (AbstractGameAction) new ChangeStateAction(this, "ATTACK")
                );
                AbstractDungeon.actionManager.addToBottom(
                    (AbstractGameAction) new SFXAction("ATTACK_MAGIC_BEAM_SHORT", 0.5F)
                );
                AbstractDungeon.actionManager.addToBottom(
                    (AbstractGameAction) new VFXAction(
                        (AbstractGameEffect) new BorderFlashEffect(Color.SKY)
                    )
                );
                if (Settings.FAST_MODE) {
                    AbstractDungeon.actionManager.addToBottom(
                        (AbstractGameAction) new VFXAction(
                            (AbstractGameEffect) new SmallLaserEffect(
                                AbstractDungeon.player.hb.cX,
                                AbstractDungeon.player.hb.cY,
                                this.hb.cX,
                                this.hb.cY
                            ),
                            0.1F
                        )
                    );
                } else {
                    AbstractDungeon.actionManager.addToBottom(
                        (AbstractGameAction) new VFXAction(
                            (AbstractGameEffect) new SmallLaserEffect(
                                AbstractDungeon.player.hb.cX,
                                AbstractDungeon.player.hb.cY,
                                this.hb.cX,
                                this.hb.cY
                            ),
                            0.3F
                        )
                    );
                }
                AbstractDungeon.actionManager.addToBottom(
                    (AbstractGameAction) new DamageAction(
                        (AbstractCreature) AbstractDungeon.player,
                        this.damage.get(0),
                        AbstractGameAction.AttackEffect.NONE,
                        Settings.FAST_MODE
                    )
                );
                AbstractDungeon.actionManager.addToBottom(
                    (AbstractGameAction) new SetMoveAction(
                        this,
                        (byte) 8,
                        AbstractMonster.Intent.DEBUFF
                    )
                );
                break;
            case 8: //A1 double daze
                AbstractDungeon.actionManager.addToBottom(
                    (AbstractGameAction) new SFXAction("THUNDERCLAP")
                );
                if (!Settings.FAST_MODE) {
                    AbstractDungeon.actionManager.addToBottom(
                        (AbstractGameAction) new VFXAction(
                            (AbstractCreature) this,
                            (AbstractGameEffect) new ShockWaveEffect(
                                this.hb.cX,
                                this.hb.cY,
                                Color.ROYAL,
                                ShockWaveEffect.ShockWaveType.ADDITIVE
                            ),
                            0.5F
                        )
                    );
                    AbstractDungeon.actionManager.addToBottom(
                        (AbstractGameAction) new FastShakeAction(
                            (AbstractCreature) AbstractDungeon.player,
                            0.6F,
                            0.2F
                        )
                    );
                } else {
                    AbstractDungeon.actionManager.addToBottom(
                        (AbstractGameAction) new VFXAction(
                            (AbstractCreature) this,
                            (AbstractGameEffect) new ShockWaveEffect(
                                this.hb.cX,
                                this.hb.cY,
                                Color.ROYAL,
                                ShockWaveEffect.ShockWaveType.ADDITIVE
                            ),
                            0.1F
                        )
                    );
                    AbstractDungeon.actionManager.addToBottom(
                        (AbstractGameAction) new FastShakeAction(
                            (AbstractCreature) AbstractDungeon.player,
                            0.6F,
                            0.15F
                        )
                    );
                }
                addToBot(
                    (AbstractGameAction) new MakeTempCardInDrawPileAction(
                        (AbstractCard) new BGDazed(),
                        2,
                        false,
                        true
                    )
                );
                AbstractDungeon.actionManager.addToBottom(
                    (AbstractGameAction) new SetMoveAction(
                        this,
                        (byte) 7,
                        AbstractMonster.Intent.ATTACK,
                        ((DamageInfo) this.damage.get(0)).base
                    )
                );
                break;
        }
    }

    public void damage(DamageInfo info) {
        super.damage(info);
        if (info.owner != null && info.type != DamageInfo.DamageType.THORNS && info.output > 0) {
            this.state.setAnimation(0, "hit", false);
            this.state.addAnimation(0, "idle", true, 0.0F);
        }
    }

    public void changeState(String stateName) {
        switch (stateName) {
            case "ATTACK":
                this.state.setAnimation(0, "attack", false);
                this.state.addAnimation(0, "idle", true, 0.0F);
                break;
        }
    }

    public void dieMove(int roll) {
        if (behavior.equals("2D") && AbstractDungeon.ascensionLevel >= 1) return;
        char move = '-';
        if (TheDie.monsterRoll == 1 || TheDie.monsterRoll == 2 || TheDie.monsterRoll == 3) move =
            this.behavior.charAt(0);
        else if (
            TheDie.monsterRoll == 4 || TheDie.monsterRoll == 5 || TheDie.monsterRoll == 6
        ) move = this.behavior.charAt(1);

        if (move == 'D') {
            setMove((byte) 3, AbstractMonster.Intent.DEBUFF);
        } else if (move == '2') {
            setMove(
                (byte) 4,
                AbstractMonster.Intent.ATTACK,
                ((DamageInfo) this.damage.get(0)).base
            );
        } else if (move == '3') {
            setMove(
                (byte) 5,
                AbstractMonster.Intent.ATTACK,
                ((DamageInfo) this.damage.get(1)).base
            );
        }
    }

    protected void getMove(int num) {
        if (behavior.equals("2D") && AbstractDungeon.ascensionLevel >= 1) {
            behavior = "--";
            setMove((byte) 6, AbstractMonster.Intent.DEBUFF);
        } else {
            setMove((byte) 0, AbstractMonster.Intent.NONE);
        }
    }

    //    protected void getMove(int num) {
    //        if (this.firstMove) {
    //            if ((AbstractDungeon.getMonsters()).monsters.lastIndexOf(this) % 2 == 0) {
    //                setMove((byte)3, AbstractMonster.Intent.DEBUFF);
    //            } else {
    //                setMove((byte)4, AbstractMonster.Intent.ATTACK, ((DamageInfo)this.damage.get(0)).base);
    //            }
    //            this.firstMove = false;
    //
    //            return;
    //        }
    //        if (lastMove((byte)4)) {
    //            setMove((byte)3, AbstractMonster.Intent.DEBUFF);
    //        } else {
    //            setMove((byte)4, AbstractMonster.Intent.ATTACK, ((DamageInfo)this.damage.get(0)).base);
    //        }
    //    }
}
