

    //intent description in AbstractMonster.updateIntentTip
    //intent numbers in AbstractMonster.renderDamageRange
    //intent dmg is determined during AbstractMonster.calculateDamage(int damage)
    //attacking should be easy enough, just have a 3 dmg action then a delay action then a 2 dmg action


 package BoardGame.monsters.bgcity;
import BoardGame.monsters.AbstractBGMonster;
import BoardGame.monsters.DieControlledMoves;
import BoardGame.monsters.MixedAttacks;
import BoardGame.powers.BGWeakPower;
import BoardGame.thedie.TheDie;
import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Color;
 import com.badlogic.gdx.math.MathUtils;
 import com.esotericsoftware.spine.AnimationState;
 import com.megacrit.cardcrawl.actions.AbstractGameAction;
 import com.megacrit.cardcrawl.actions.animations.VFXAction;
 import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
 import com.megacrit.cardcrawl.actions.common.ChangeStateAction;
 import com.megacrit.cardcrawl.actions.common.DamageAction;
 import com.megacrit.cardcrawl.actions.common.RollMoveAction;
 import com.megacrit.cardcrawl.actions.utility.WaitAction;
 import com.megacrit.cardcrawl.cards.DamageInfo;
 import com.megacrit.cardcrawl.characters.AbstractPlayer;
 import com.megacrit.cardcrawl.core.AbstractCreature;
 import com.megacrit.cardcrawl.core.CardCrawlGame;
 import com.megacrit.cardcrawl.core.Settings;
 import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
 import com.megacrit.cardcrawl.localization.MonsterStrings;
 import com.megacrit.cardcrawl.monsters.AbstractMonster;
 import com.megacrit.cardcrawl.powers.AbstractPower;
 import com.megacrit.cardcrawl.powers.FrailPower;
 import com.megacrit.cardcrawl.powers.WeakPower;
 import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
 import com.megacrit.cardcrawl.vfx.combat.BiteEffect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

    public class BGSnakePlant extends AbstractBGMonster implements DieControlledMoves, MixedAttacks {

        private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("SnakePlant");
        public static final String ID = "BGSnakePlant";
        public static final String NAME = monsterStrings.NAME;
        public static final String[] MOVES = monsterStrings.MOVES;
        public static final String[] DIALOG = monsterStrings.DIALOG; private static final int HP_MIN = 75;
        private static final int HP_MAX = 79;
        private static final int A_2_HP_MIN = 78;
        private static final int A_2_HP_MAX = 82;
        private static final byte CHOMPY_CHOMPS = 1;
        private static final byte SPORES = 2;
        private static final int CHOMPY_AMT = 3;
        private static final int CHOMPY_DMG = 7;
        private static final int A_2_CHOMPY_DMG = 8;
        private int rainBlowsDmg;

        public String behavior="MMW";

        public BGSnakePlant(float x, float y) {
            super(NAME, "BGSnakePlant", 79, 0.0F, -44.0F, 350.0F, 360.0F, null, x, y + 50.0F);
            loadAnimation("images/monsters/theCity/snakePlant/skeleton.atlas", "images/monsters/theCity/snakePlant/skeleton.json", 1.0F);




            AnimationState.TrackEntry e = this.state.setAnimation(0, "Idle", true);
            e.setTime(e.getEndTime() * MathUtils.random());
            this.stateData.setMix("Hit", "Idle", 0.1F);
            e.setTimeScale(0.8F);

            setHp(17);



            this.damage.add(new DamageInfo((AbstractCreature)this, 3));
            this.damage.add(new DamageInfo((AbstractCreature)this, 2));

        }





        public void changeState(String stateName) {
            switch (stateName) {
                case "ATTACK":
                    this.state.setAnimation(0, "Attack", false);
                    this.state.addAnimation(0, "Idle", true, 0.0F);
                    break;
            }
        }


        public void damage(DamageInfo info) {
            super.damage(info);
            if (info.owner != null && info.type != DamageInfo.DamageType.THORNS && info.output > 0) {
                this.state.setAnimation(0, "Hit", false);
                this.state.addAnimation(0, "Idle", true, 0.0F);
            }
        }

        public void takeTurn() {
            int numBlows, i;
            AbstractPlayer abstractPlayer = AbstractDungeon.player;
            switch (this.nextMove) {
                case 1:
                    AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ChangeStateAction(this, "ATTACK"));
                    AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new WaitAction(0.5F));
                    AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new VFXAction((AbstractGameEffect)new BiteEffect(AbstractDungeon.player.hb.cX +
                            MathUtils.random(-50.0F, 50.0F) * Settings.scale, AbstractDungeon.player.hb.cY +
                            MathUtils.random(-50.0F, 50.0F) * Settings.scale, Color.CHARTREUSE
                            .cpy()), 0.2F));
                    AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)abstractPlayer, this.damage
                            .get(0), AbstractGameAction.AttackEffect.NONE, true));
                    AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)abstractPlayer, this.damage
                            .get(1), AbstractGameAction.AttackEffect.NONE, true));

                    break;
                case 2:
                    AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ApplyPowerAction((AbstractCreature)AbstractDungeon.player, (AbstractCreature)this, (AbstractPower)new BGWeakPower((AbstractCreature)AbstractDungeon.player, 2, true), 2));
                    break;
            }
            AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new RollMoveAction(this));
        }

        protected void getMove(int num) {
            setMove((byte) 0, AbstractMonster.Intent.NONE);
        }
        public void dieMove(int roll){
            final Logger logger = LogManager.getLogger(DieControlledMoves.class.getName());
            char move = '-';
            if (TheDie.monsterRoll == 1 || TheDie.monsterRoll == 2)
                move = this.behavior.charAt(0);
            else if (TheDie.monsterRoll == 3 || TheDie.monsterRoll == 4)
                move = this.behavior.charAt(1);
            else if (TheDie.monsterRoll == 5 || TheDie.monsterRoll == 6)
                move = this.behavior.charAt(2);

            if (move == 'M') {
                setMove((byte) 1, AbstractMonster.Intent.ATTACK, ((DamageInfo) this.damage.get(0)).base);
            } else if (move == 'W') {
                setMove((byte) 2, AbstractMonster.Intent.DEBUFF);
            }
        }

        public int intentDmg1(){
            ReflectionHacks.privateMethod(AbstractMonster.class, "calculateDamage", int.class).invoke((AbstractMonster)this,
                    3);
            return ReflectionHacks.getPrivate((AbstractMonster)this,AbstractMonster.class,"intentDmg");
        }
        public int intentDmg2(){
            ReflectionHacks.privateMethod(AbstractMonster.class, "calculateDamage", int.class).invoke((AbstractMonster)this,
                    2);
            return ReflectionHacks.getPrivate((AbstractMonster)this,AbstractMonster.class,"intentDmg");
        }
        public boolean isMixedAttacking(){
            return (this.nextMove==1);
        }
    }


