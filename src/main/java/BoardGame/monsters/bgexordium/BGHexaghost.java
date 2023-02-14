package BoardGame.monsters.bgexordium;

import BoardGame.cards.BGStatus.BGBurn;
import BoardGame.monsters.BGDamageIcons;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.ChangeStateAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDiscardAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import BoardGame.monsters.AbstractBGMonster;
import com.megacrit.cardcrawl.monsters.exordium.HexaghostBody;
import com.megacrit.cardcrawl.monsters.exordium.HexaghostOrb;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.BorderFlashEffect;
import com.megacrit.cardcrawl.vfx.combat.FireballEffect;
import com.megacrit.cardcrawl.vfx.combat.InflameEffect;
import com.megacrit.cardcrawl.vfx.combat.ScreenOnFireEffect;
import java.util.ArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BGHexaghost extends AbstractBGMonster implements BGDamageIcons {
    private static final Logger logger = LogManager.getLogger(BGHexaghost.class.getName());
    public static final String ID = "BGHexaghost";
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("Hexaghost");
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG; public static final String IMAGE = "images/monsters/theBottom/boss/ghost/core.png"; private static final int HP = 250;
    private static final int A_2_HP = 264;
    private static final int SEAR_DMG = 6;
    private static final int INFERNO_DMG = 2;
    private static final int FIRE_TACKLE_DMG = 5;
    private static final int STR_AMT = 2;
    private ArrayList<HexaghostOrb> orbs = new ArrayList<>(); private static final int A_4_INFERNO_DMG = 3;
    private static final int A_4_FIRE_TACKLE_DMG = 6;
    private static final int A_19_BURN_COUNT = 2;
    private static final int A_19_STR_AMT = 3;
    private int searDmg;
    private int strAmount;
    private int searBurnCount;
    private int strengthenBlockAmt = 5; private int fireTackleDmg;
    private int fireTackleCount = 2; private int infernoDmg;
    private int infernoHits = 2; private static final byte DIVIDER = 1;
    private static final byte TACKLE = 2;
    private static final byte INFLAME = 3;
    private static final String STRENGTHEN_NAME = MOVES[0]; private static final byte SEAR = 4; private static final byte ACTIVATE = 5; private static final byte INFERNO = 6; private static final String SEAR_NAME = MOVES[1]; private static final String BURN_NAME = MOVES[2];
    private static final String ACTIVATE_STATE = "Activate";
    private static final String ACTIVATE_ORB = "Activate Orb";
    private static final String DEACTIVATE_ALL_ORBS = "Deactivate";
    private boolean activated = false;
    private boolean burnUpgraded = false;
    private int orbActiveCount = 0;
    private HexaghostBody body;

    public BGHexaghost() {
        super(NAME, "BGHexaghost", 250, 20.0F, 0.0F, 450.0F, 450.0F, "images/monsters/theBottom/boss/ghost/core.png");
        this.type = AbstractMonster.EnemyType.BOSS;
        this.body = new HexaghostBody(this);
        this.disposables.add(this.body);
        createOrbs();


        setHp(36);



            this.strAmount = 1;
            this.searBurnCount = 1;
            this.fireTackleDmg = 5;
            this.infernoDmg = 2;


        this.searDmg = 6;
        this.damage.add(new DamageInfo((AbstractCreature)this, 1));
        this.damage.add(new DamageInfo((AbstractCreature)this, 2));
        this.damage.add(new DamageInfo((AbstractCreature)this, 3));
        this.damage.add(new DamageInfo((AbstractCreature)this, 2));
        this.damage.add(new DamageInfo((AbstractCreature)this, 3));
    }


    public void usePreBattleAction() {
        if (AbstractDungeon.getCurrRoom() instanceof com.megacrit.cardcrawl.rooms.MonsterRoomBoss) {
            CardCrawlGame.music.unsilenceBGM();
            AbstractDungeon.scene.fadeOutAmbiance();
            AbstractDungeon.getCurrRoom().playBgmInstantly("BOSS_BOTTOM");
        }
        UnlockTracker.markBossAsSeen("GHOST");
        //CardCrawlGame.music.precacheTempBgm("BOSS_BOTTOM");
        int i;
//        for (i = 0; i < 6; i++) {
//            AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new VFXAction((AbstractCreature)this, (AbstractGameEffect)new GhostIgniteEffect(AbstractDungeon.player.hb.cX +
//                    MathUtils.random(-120.0F, 120.0F) * Settings.scale, AbstractDungeon.player.hb.cY +
//                    MathUtils.random(-120.0F, 120.0F) * Settings.scale), 0.05F));
//
//            if (MathUtils.randomBoolean()) {
//                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SFXAction("GHOST_ORB_IGNITE_1", 0.3F));
//            } else {
//                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SFXAction("GHOST_ORB_IGNITE_2", 0.3F));
//            }
//
//            //not clear why the VG hits the player for -1 here...?
////                    AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
////                            .get(2), AbstractGameAction.AttackEffect.BLUNT_HEAVY, true));
//        }
        AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ChangeStateAction(this, "Deactivate"));
        AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ChangeStateAction(this, "Activate"));
        AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ChangeStateAction(this, "Activate Orb"));
        AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new RollMoveAction(this));

    }

    private void createOrbs() {
        this.orbs.add(new HexaghostOrb(-90.0F, 380.0F, this.orbs.size()));
        this.orbs.add(new HexaghostOrb(90.0F, 380.0F, this.orbs.size()));
        this.orbs.add(new HexaghostOrb(160.0F, 250.0F, this.orbs.size()));
        this.orbs.add(new HexaghostOrb(90.0F, 120.0F, this.orbs.size()));
        this.orbs.add(new HexaghostOrb(-90.0F, 120.0F, this.orbs.size()));
        this.orbs.add(new HexaghostOrb(-160.0F, 250.0F, this.orbs.size()));
    } public void takeTurn() {
        int d, i;
        BGBurn c;
        int j;
        switch (this.nextMove) {

            case 99:     //Divider (VG only)
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ChangeStateAction(this, "Activate"));
                d = AbstractDungeon.player.currentHealth / 12 + 1;

                ((DamageInfo)this.damage.get(2)).base = d;

                applyPowers();
                setMove((byte)1, AbstractMonster.Intent.ATTACK, ((DamageInfo)this.damage.get(2)).base, 6, true);
                return;


            case 0:     //activate (move to combat start!)
                return;

            case 1: //one hit, one burn ("sear")
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new VFXAction((AbstractGameEffect)new FireballEffect(this.hb.cX, this.hb.cY, AbstractDungeon.player.hb.cX, AbstractDungeon.player.hb.cY), 0.5F));

                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                        .get(0), AbstractGameAction.AttackEffect.FIRE));
                c = new BGBurn();
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new MakeTempCardInDiscardAction((AbstractCard)c, this.searBurnCount));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ChangeStateAction(this, "Activate Orb"));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new RollMoveAction(this));
                return;

            case 2: //two hits, one burn ("flame charge")
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new VFXAction((AbstractGameEffect)new BorderFlashEffect(Color.CHARTREUSE)));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new AnimateSlowAttackAction((AbstractCreature)this));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                        .get(1), AbstractGameAction.AttackEffect.FIRE));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                        .get(1), AbstractGameAction.AttackEffect.FIRE));
                c = new BGBurn();
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new MakeTempCardInDiscardAction((AbstractCard)c, 1));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ChangeStateAction(this, "Activate Orb"));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new RollMoveAction(this));
                return;

            case 3: //two burns
                c = new BGBurn();
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new MakeTempCardInDiscardAction((AbstractCard)c, 2));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ChangeStateAction(this, "Activate Orb"));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new RollMoveAction(this));
                return;

            case 4: //one hit, one block ("strengthen") (does not increase STR in BG)
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new VFXAction((AbstractCreature)this, (AbstractGameEffect)new InflameEffect((AbstractCreature)this), 0.5F));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new GainBlockAction((AbstractCreature)this, (AbstractCreature)this, this.strengthenBlockAmt));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                        .get(2), AbstractGameAction.AttackEffect.FIRE));

                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ChangeStateAction(this, "Activate Orb"));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new RollMoveAction(this));
                return;

            case 5: //one hit, one burn ("sear")
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new VFXAction((AbstractGameEffect)new FireballEffect(this.hb.cX, this.hb.cY, AbstractDungeon.player.hb.cX, AbstractDungeon.player.hb.cY), 0.5F));

                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                        .get(3), AbstractGameAction.AttackEffect.FIRE));
                c = new BGBurn();
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new MakeTempCardInDiscardAction((AbstractCard)c, this.searBurnCount));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ChangeStateAction(this, "Activate Orb"));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new RollMoveAction(this));
                return;





            case 6: //"inferno" (two hits, two burns, 1 STR)
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new VFXAction((AbstractCreature)this, (AbstractGameEffect)new ScreenOnFireEffect(), 1.0F));
                for (j = 0; j < this.infernoHits; j++) {
                    AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                            .get(4), AbstractGameAction.AttackEffect.FIRE));
                }
                c = new BGBurn();
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new MakeTempCardInDiscardAction((AbstractCard)c, 2));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ApplyPowerAction((AbstractCreature)this, (AbstractCreature)this, (AbstractPower)new StrengthPower((AbstractCreature)this, this.strAmount), this.strAmount));
//                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new BurnIncreaseAction());
//                if (!this.burnUpgraded) {
//                    this.burnUpgraded = true;
//                }

                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ChangeStateAction(this, "Deactivate"));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ChangeStateAction(this, "Activate Orb"));

                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new RollMoveAction(this));
                return;
        }
        logger.info("ERROR: Default Take Turn was called on " + this.name);
    }




    protected void getMove(int num) {
        if (!this.activated) {
            this.activated = true;
            //setMove((byte)5, AbstractMonster.Intent.UNKNOWN);
            setMove(SEAR_NAME, (byte)1, AbstractMonster.Intent.ATTACK_DEBUFF, ((DamageInfo)this.damage.get(0)).base);
        } else {

            switch (this.orbActiveCount) {
                case 0:

                    break;
                case 1:
                    setMove(SEAR_NAME, (byte)1, AbstractMonster.Intent.ATTACK_DEBUFF, ((DamageInfo)this.damage.get(0)).base);
                    break;
                case 2:
                    setMove((byte)2, AbstractMonster.Intent.ATTACK_DEBUFF, ((DamageInfo)this.damage.get(1)).base, 2, true);
                    break;
                case 3:
                    setMove((byte)3, AbstractMonster.Intent.DEBUFF);
                    break;
                case 4:
                    setMove(STRENGTHEN_NAME, (byte)4, AbstractMonster.Intent.ATTACK_DEFEND, ((DamageInfo)this.damage.get(2)).base);
                    break;
                case 5:
                    setMove(SEAR_NAME, (byte)5, AbstractMonster.Intent.ATTACK_DEBUFF, ((DamageInfo)this.damage.get(3)).base);
                    break;
                case 6:
                    setMove(BURN_NAME, (byte)6, AbstractMonster.Intent.ATTACK_DEBUFF, ((DamageInfo)this.damage.get(4)).base, 2, true);
                    break;
            }
        }
    }






    public void changeState(String stateName) {
        switch (stateName) {

            case "Activate":

//                for (HexaghostOrb orb : this.orbs) {
//                    orb.activate(this.drawX + this.animX, this.drawY + this.animY);
//                }
                //this.orbActiveCount = 6;
                this.body.targetRotationSpeed = 120.0F;
                break;


            case "Activate Orb":
                for (HexaghostOrb orb : this.orbs) {
                    if (!orb.activated) {
                        orb.activate(this.drawX + this.animX, this.drawY + this.animY);
                        break;
                    }
                }
                this.orbActiveCount++;
                if (this.orbActiveCount == 6) {
                    setMove(BURN_NAME, (byte)6, AbstractMonster.Intent.ATTACK_DEBUFF, ((DamageInfo)this.damage.get(4)).base, this.infernoHits, true);
                }
                break;



            case "Deactivate":
                for (HexaghostOrb orb : this.orbs) {
                    orb.deactivate();
                }
                CardCrawlGame.sound.play("CARD_EXHAUST", 0.2F);
                CardCrawlGame.sound.play("CARD_EXHAUST", 0.2F);
                this.orbActiveCount = 0;
                break;
        }
    }


    public void die() {
        useFastShakeAnimation(5.0F);
        CardCrawlGame.screenShake.rumble(4.0F);
        super.die();

        for (HexaghostOrb orb : this.orbs) {
            orb.hide();
        }

        onBossVictoryLogic();
        UnlockTracker.hardUnlockOverride("GHOST");
        UnlockTracker.unlockAchievement("GHOST_GUARDIAN");
    }





    public void update() {
        super.update();
        this.body.update();

        for (HexaghostOrb orb : this.orbs) {
            orb.update(this.drawX + this.animX, this.drawY + this.animY);
        }
    }





    public void render(SpriteBatch sb) {
        this.body.render(sb);
        super.render(sb);
    }
}


