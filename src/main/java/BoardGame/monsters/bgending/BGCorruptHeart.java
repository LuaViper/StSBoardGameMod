package BoardGame.monsters.bgending;
import BoardGame.cards.BGStatus.BGBurn;
import BoardGame.cards.BGStatus.BGSlimed;
import BoardGame.monsters.BGDamageIcons;
import BoardGame.powers.BGBeatOfDeathPower;
import BoardGame.powers.BGInvinciblePower;
import BoardGame.powers.BGVulnerablePower;
import BoardGame.powers.BGWeakPower;
import com.badlogic.gdx.graphics.Color;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.status.Burn;
import com.megacrit.cardcrawl.cards.status.Slimed;
import com.megacrit.cardcrawl.cards.status.VoidCard;
import com.megacrit.cardcrawl.cards.status.Wound;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.HeartAnimListener;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.ArtifactPower;
import com.megacrit.cardcrawl.powers.BeatOfDeathPower;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.InvinciblePower;
import com.megacrit.cardcrawl.powers.PainfulStabsPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.BorderFlashEffect;
import com.megacrit.cardcrawl.vfx.SpeechBubble;
import com.megacrit.cardcrawl.vfx.combat.BloodShotEffect;
import com.megacrit.cardcrawl.vfx.combat.HeartBuffEffect;
import com.megacrit.cardcrawl.vfx.combat.HeartMegaDebuffEffect;
import com.megacrit.cardcrawl.vfx.combat.ViceCrushEffect;

public class BGCorruptHeart extends AbstractMonster implements BGDamageIcons {
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("CorruptHeart");
    public static final String ID = "BGCorruptHeart";
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    public static final String VULN_WARNING = "@tu-thump@ @...@ @tu-thump@ @...@ @tu-thump@ @...@";
    private HeartAnimListener animListener = new HeartAnimListener();
    private static final byte BLOOD_SHOTS = 1;
    private static final byte ECHO_ATTACK = 2;
    private static final byte DEBILITATE = 3;
    private static final byte GAIN_ONE_STRENGTH = 4;
    public static final int DEBUFF_AMT = -1;
    private int bloodHitCount;
    private boolean isFirstMove = true;
    private int moveCount = 0, buffCount = 0;

    public BGCorruptHeart() {
        super(NAME, "BGCorruptHeart", 750, 30.0F, -30.0F, 476.0F, 410.0F, null, -50.0F, 30.0F);

        loadAnimation("images/npcs/heart/skeleton.atlas", "images/npcs/heart/skeleton.json", 1.0F);
        AnimationState.TrackEntry e = this.state.setAnimation(0, "idle", true);
        e.setTimeScale(1.5F);
        this.state.addListener((AnimationState.AnimationStateListener)this.animListener);
        this.type = AbstractMonster.EnemyType.BOSS;



        setHp((AbstractDungeon.ascensionLevel<11) ? 100 : 120);
        this.damage.add(new DamageInfo((AbstractCreature)this, 5));
        this.damage.add(new DamageInfo((AbstractCreature)this, 2));
        this.bloodHitCount = 3;

    }


    public void usePreBattleAction() {
        (AbstractDungeon.getCurrRoom()).rewardAllowed = false; //game is hardcoded to check for TheBeyond / TheEnding dungeons.  here's a workaround
        CardCrawlGame.music.unsilenceBGM();
        AbstractDungeon.scene.fadeOutAmbiance();
        AbstractDungeon.getCurrRoom().playBgmInstantly("BOSS_ENDING");
//        int invincibleAmt = 300;
//        if (AbstractDungeon.ascensionLevel >= 19) {
//            invincibleAmt -= 100;
//        }
//        int beatAmount = 1;
//        if (AbstractDungeon.ascensionLevel >= 19) {
//            beatAmount++;
//        }
        int invincibleAmt=(AbstractDungeon.ascensionLevel<11) ? 50 : 60;
        int beatAmount=1;
        AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ApplyPowerAction((AbstractCreature)this, (AbstractCreature)this, (AbstractPower)new BGInvinciblePower((AbstractCreature)this, invincibleAmt), invincibleAmt));
        AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ApplyPowerAction((AbstractCreature)this, (AbstractCreature)this, (AbstractPower)new BGBeatOfDeathPower((AbstractCreature)this, beatAmount), beatAmount));
    }


    public void takeTurn() {
        int additionalAmount, i;
        switch (this.nextMove) {
            case 0:
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new VFXAction((AbstractGameEffect) new HeartMegaDebuffEffect()));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new ApplyPowerAction((AbstractCreature) AbstractDungeon.player, (AbstractCreature) this, (AbstractPower) new BGWeakPower((AbstractCreature) AbstractDungeon.player, 1, true), 1));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new ApplyPowerAction((AbstractCreature) AbstractDungeon.player, (AbstractCreature) this, (AbstractPower) new BGVulnerablePower((AbstractCreature) AbstractDungeon.player, 1, true), 1));

                if(AbstractDungeon.ascensionLevel<11) {
                    AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new MakeTempCardInDrawPileAction((AbstractCard) new BGSlimed(), 5, true, true));
                }else{
                    AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new MakeTempCardInDrawPileAction((AbstractCard) new BGBurn(), 5, true, true));
                }
                //in VG we wouldn't shuffle here, but in BG we've just disrupted Scry/Rebound effects
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new ShuffleAction(AbstractDungeon.player.drawPile));
                break;

            case 1:
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new VFXAction((AbstractGameEffect) new ViceCrushEffect(AbstractDungeon.player.hb.cX, AbstractDungeon.player.hb.cY), 0.5F));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new DamageAction((AbstractCreature) AbstractDungeon.player, this.damage
                        .get(0), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
                break;

            case 2:
                if (Settings.FAST_MODE) {
                    AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new VFXAction((AbstractGameEffect) new BloodShotEffect(this.hb.cX, this.hb.cY, AbstractDungeon.player.hb.cX, AbstractDungeon.player.hb.cY, this.bloodHitCount), 0.25F));
                } else {
                    AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new VFXAction((AbstractGameEffect) new BloodShotEffect(this.hb.cX, this.hb.cY, AbstractDungeon.player.hb.cX, AbstractDungeon.player.hb.cY, this.bloodHitCount), 0.6F));
                }
                for (i = 0; i < this.bloodHitCount; i++) {
                    AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new DamageAction((AbstractCreature) AbstractDungeon.player, this.damage
                            .get(1), AbstractGameAction.AttackEffect.BLUNT_HEAVY, true));
                }
                break;

            case 3:
                additionalAmount = 0;

                AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new VFXAction((AbstractGameEffect) new BorderFlashEffect(new Color(0.8F, 0.5F, 1.0F, 1.0F))));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new VFXAction((AbstractGameEffect) new HeartBuffEffect(this.hb.cX, this.hb.cY)));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new ApplyPowerAction((AbstractCreature) this, (AbstractCreature) this, (AbstractPower) new StrengthPower((AbstractCreature) this, 2),2));
                int additionalBeatAmt=(AbstractDungeon.ascensionLevel<11) ? 1 : 2;
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new ApplyPowerAction((AbstractCreature) this, (AbstractCreature) this, (AbstractPower) new BGBeatOfDeathPower((AbstractCreature) this, additionalBeatAmt), additionalBeatAmt));
                if(this.hasPower("BGInvinciblePower")){
                    addToBot((AbstractGameAction) new RemoveSpecificPowerAction((AbstractCreature)this,(AbstractCreature)this,"BGInvinciblePower"));
                }
                this.buffCount++;
                break;
        }



        AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new RollMoveAction(this));
    }


    protected void getMove(int num) {
        if (this.isFirstMove) {
            setMove((byte)0, AbstractMonster.Intent.STRONG_DEBUFF);
            AbstractDungeon.effectList.add(new SpeechBubble(this.hb.cX + this.dialogX, this.hb.cY + this.dialogY, 2.5F, VULN_WARNING, false));
            this.isFirstMove = false;

            return;
        }
        switch (this.moveCount % 3) {
            case 0:
                setMove((byte)1, AbstractMonster.Intent.ATTACK, ((DamageInfo)this.damage.get(0)).base);
                break;

            case 1:
                setMove((byte)2, AbstractMonster.Intent.ATTACK, ((DamageInfo)this.damage.get(1)).base, this.bloodHitCount, true);
                break;

            default:
                setMove((byte)3, AbstractMonster.Intent.BUFF);
                break;
        }

        this.moveCount++;
    }


    public void die() {
        if (!(AbstractDungeon.getCurrRoom()).cannotLose) {
            super.die();
            this.state.removeListener((AnimationState.AnimationStateListener)this.animListener);
            onBossVictoryLogic();
            onFinalBossVictoryLogic();
            CardCrawlGame.stopClock = true;
        }
    }
}


