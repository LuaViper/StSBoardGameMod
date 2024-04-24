//package BoardGame.actions;
//
//import BoardGame.cards.AbstractBGCard;
//import BoardGame.powers.BGTripleAttackPower;
//import BoardGame.screen.TargetSelectScreen;
//import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
//import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
//import com.megacrit.cardcrawl.actions.AbstractGameAction;
//import com.megacrit.cardcrawl.actions.utility.NewQueueCardAction;
//import com.megacrit.cardcrawl.actions.utility.ShowCardAndPoofAction;
//import com.megacrit.cardcrawl.cards.AbstractCard;
//import com.megacrit.cardcrawl.core.Settings;
//import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
//import com.megacrit.cardcrawl.monsters.AbstractMonster;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//
//public class BGCopyCardAction
//        extends AbstractGameAction {
//
//    private AbstractCard card;
//    private boolean tripleAttack;
//
//
//    public BGCopyCardAction(AbstractCard originalCard, boolean tripleAttack) {
//        this.card=originalCard;
//        this.tripleAttack=tripleAttack;
//    }
//
//    public void update() {
//        AbstractMonster m = null;
//
//        AbstractCard tmp = card.makeSameInstanceOf();
//        AbstractDungeon.player.limbo.addToBottom(tmp);
//        tmp.current_x = card.current_x;
//        tmp.current_y = card.current_y;
//        tmp.target_x = Settings.WIDTH / 2.0F - 300.0F * Settings.scale;
//        tmp.target_y = Settings.HEIGHT / 2.0F;
//
//        tmp.purgeOnUse = true;
//
//        Logger logger = LogManager.getLogger(BGTripleAttackPower.class.getName());
//        //logger.info("DoubleAttackPower instanceof check");
//        if (card instanceof AbstractBGCard) {
//            //logger.info("set old card's copy reference: "+tmp);
//            ((AbstractBGCard) card).copiedCard = (AbstractBGCard) tmp;
//            if(tripleAttack) {
//                ((AbstractBGCard) tmp).originalCard = ((AbstractBGCard) card);
//                ((AbstractBGCard) tmp).copyOriginalCardAgain = true;
//            }
//        }
//
//        //AbstractDungeon.actionManager.addCardQueueItem(new CardQueueItem(tmp, m, card.energyOnUse, true, true), true);
//
//        //logger.info("DoubleTap card target type: "+card.target);
//        if (card.target == AbstractCard.CardTarget.ENEMY || card.target == AbstractCard.CardTarget.SELF_AND_ENEMY) {
//            TargetSelectScreen.TargetSelectAction tssAction = (target) -> {
//                //logger.info("DoubleTap tssAction.execute");
//                if (target != null) {
//                    tmp.calculateCardDamage(target);
//                }
//                //logger.info("DoubleTap final target: "+target);
//                addToBot((AbstractGameAction) new NewQueueCardAction(tmp, target, true, true));
//            };
//            //logger.info("DoubleTap addToTop");
//            addToBot((AbstractGameAction) new TargetSelectScreenAction(tssAction, "Choose a target for the copy of " + card.name + "."));
//        } else {
//            addToBot((AbstractGameAction) new NewQueueCardAction(tmp, null, true, true));
//        }
//        this.isDone = true;
//    }
//
//    @SpirePatch2(clz = ShowCardAndPoofAction.class, method = "update",
//            paramtypez={})
//    public static class TripleAttackPatch {
//        @SpirePostfixPatch
//        public static void update(ShowCardAndPoofAction __instance, AbstractCard ___card) {
//            if(__instance.isDone) {
//                if (___card instanceof AbstractBGCard) {
//                    if (((AbstractBGCard) ___card).copyOriginalCardAgain) {
//                        AbstractDungeon.actionManager.addToBottom(new BGCopyCardAction(((AbstractBGCard) ___card).originalCard, false));
//                    }
//                }
//            }
//        }
//    }
//
//
//}
//
//
