////
//// Source code recreated from a .class file by IntelliJ IDEA
//// (powered by FernFlower decompiler)
////
//
//package BoardGame.actions;
//
//import BoardGame.multicharacter.BGMultiCreature;
//import BoardGame.screen.TargetSelectScreen;
//import basemod.BaseMod;
//import com.badlogic.gdx.graphics.Color;
//import com.megacrit.cardcrawl.actions.AbstractGameAction;
//import com.megacrit.cardcrawl.actions.utility.WaitAction;
//import com.megacrit.cardcrawl.cards.DamageInfo;
//import com.megacrit.cardcrawl.characters.AbstractPlayer;
//import com.megacrit.cardcrawl.core.AbstractCreature;
//import com.megacrit.cardcrawl.core.Settings;
//import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
//import com.megacrit.cardcrawl.monsters.AbstractMonster;
//import com.megacrit.cardcrawl.powers.AbstractPower;
//import com.megacrit.cardcrawl.rooms.MonsterRoom;
//import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;
//
//import java.util.ArrayList;
//import java.util.Iterator;
//
////TODO: if we're fighting shieldspear, check whichRow
//public class DamageOneRowAction extends AbstractGameAction {
//    public int[] damage;
//    private String attackName="this attack";
//    private int baseDamage;
//    private int whichRow;
//    private boolean firstFrame;
//    private boolean utilizeBaseDamage;
//    private boolean alreadyCalledTargetSelect=false;
//
//    public DamageOneRowAction(String attackName,AbstractCreature source, AbstractCreature target, int[] amount, DamageInfo.DamageType type, AbstractGameAction.AttackEffect effect, boolean isFast) {
//        this.attackName=attackName;
//        this.firstFrame = true;
//        this.utilizeBaseDamage = false;
//        this.source = source;
//        this.whichRow=-1;   //will be calculated later
//        this.target=target;
//        this.damage = amount;
//        this.actionType = ActionType.DAMAGE;
//        this.damageType = type;
//        this.attackEffect = effect;
//        if (isFast) {
//            this.duration = Settings.ACTION_DUR_XFAST;
//        } else {
//            this.duration = Settings.ACTION_DUR_FAST;
//        }
//
//    }
//
//
//    public DamageOneRowAction(AbstractCreature source, AbstractCreature target, int[] amount, DamageInfo.DamageType type, AbstractGameAction.AttackEffect effect, boolean isFast) {
//        this("this attack",source,target, amount, type, effect, isFast);
//    }
//
//    public DamageOneRowAction(String attackName, AbstractCreature source, AbstractCreature target, int[] amount, DamageInfo.DamageType type, AbstractGameAction.AttackEffect effect) {
//        this(attackName, source, target, amount, type, effect, false);
//    }
//
//
//
//    public DamageOneRowAction(String attackName, AbstractPlayer player, AbstractCreature target, int baseDamage, DamageInfo.DamageType type, AbstractGameAction.AttackEffect effect) {
//        this(attackName, player,target, (int[])null, type, effect, false);
//        this.baseDamage = baseDamage;
//        this.utilizeBaseDamage = true;
//    }
//
//
//    public void update() {
//        if(target instanceof BGMultiCreature){
//            this.whichRow=((BGMultiCreature)target).getCurrentRow();
//        }
//
//        ArrayList<AbstractMonster> monsters=new ArrayList<>();
//        if(whichRow==-1){
//            if (AbstractDungeon.getCurrRoom() instanceof MonsterRoom && AbstractDungeon.lastCombatMetricKey.equals("BoardGame:Shield and Spear")) {
//                if(!alreadyCalledTargetSelect) {
//                    //TODO: this works for shieldspear, but in proper multiplayer we want the target select screen to autocomplete if there is only 1 row remaining (even if that row has multiple enemies)
//                    TargetSelectScreen.TargetSelectAction tssAction = (target) -> {
//                        if (target != null) {
//                            this.whichRow = 0;
//                            if (target instanceof BGMultiCreature) {
//                                this.whichRow = ((BGMultiCreature) target).getCurrentRow();
//                            }
//                        }
//                    };
//                    //addToTop((AbstractGameAction) new TargetSelectScreenAction(tssAction, )); //doesn't work -- softlock forever (current damage action is still active)
//                    BaseMod.openCustomScreen(TargetSelectScreen.Enum.TARGET_SELECT, tssAction, "Choose a target row for " + this.attackName + ".", false);
//                    alreadyCalledTargetSelect=true;
//                }
//                return;
//            }else{
//                monsters=AbstractDungeon.getMonsters().monsters;
//            }
//        }else{
//            for(AbstractMonster m : AbstractDungeon.getMonsters().monsters){
//                if(m instanceof BGMultiCreature){
//                    if(((BGMultiCreature) m).getCurrentRow()==whichRow){
//                        monsters.add(m);
//                    }
//                }
//            }
//        }
//
//        if (this.firstFrame) {
//            boolean playedMusic = false;
//            if (this.utilizeBaseDamage) {
//                //createDamageMatrix gets damage info for ALL monsters in EVERY row in the room
//                // (even if we don't want every row)
//                this.damage = DamageInfo.createDamageMatrix(this.baseDamage);
//            }
//
//            for(AbstractMonster m : monsters) {
//                if (!m.isDying && m.currentHealth > 0 && !m.isEscaping) {
//                    if (playedMusic) {
//                        AbstractDungeon.effectList.add(new FlashAtkImgEffect(m.hb.cX, m.hb.cY, this.attackEffect, true));
//                    } else {
//                        playedMusic = true;
//                        AbstractDungeon.effectList.add(new FlashAtkImgEffect(m.hb.cX, m.hb.cY, this.attackEffect));
//                    }
//                }
//            }
//
//            this.firstFrame = false;
//        }
//
//        this.tickDuration();
//        if (this.isDone) {
//            Iterator var4 = AbstractDungeon.player.powers.iterator();
//
//            while(var4.hasNext()) {
//                AbstractPower p = (AbstractPower)var4.next();
//                p.onDamageAllEnemies(this.damage);
//            }
//
//
//            for(int i=0;i<AbstractDungeon.getMonsters().monsters.size();i+=1) {
//                AbstractMonster m = AbstractDungeon.getMonsters().monsters.get(i);
//                if(monsters.contains(m)) {
//                    if (!m.isDeadOrEscaped()) {
//                        if (this.attackEffect == AttackEffect.POISON) {
//                            m.tint.color.set(Color.CHARTREUSE);
//                            m.tint.changeColor(Color.WHITE.cpy());
//                        } else if (this.attackEffect == AttackEffect.FIRE) {
//                            m.tint.color.set(Color.RED);
//                            m.tint.changeColor(Color.WHITE.cpy());
//                        }
//                        m.damage(new DamageInfo(this.source, this.damage[i], this.damageType));
//                    }
//                }
//            }
//
//            if (AbstractDungeon.getCurrRoom().monsters.areMonstersBasicallyDead()) {
//                AbstractDungeon.actionManager.clearPostCombatActions();
//            }
//
//            if (!Settings.FAST_MODE) {
//                this.addToTop(new WaitAction(0.1F));
//            }
//        }
//
//    }
//}
