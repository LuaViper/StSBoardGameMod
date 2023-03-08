//package BoardGame.actions;
//
//import com.badlogic.gdx.Gdx;
//import com.badlogic.gdx.graphics.Color;
//import com.badlogic.gdx.graphics.g2d.SpriteBatch;
//import com.badlogic.gdx.math.Interpolation;
//import com.megacrit.cardcrawl.cards.AbstractCard;
//import com.megacrit.cardcrawl.cards.CardGroup;
//import com.megacrit.cardcrawl.core.CardCrawlGame;
//import com.megacrit.cardcrawl.core.Settings;
//import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
//import com.megacrit.cardcrawl.helpers.ImageMaster;
//import com.megacrit.cardcrawl.localization.UIStrings;
//import com.megacrit.cardcrawl.rooms.AbstractRoom;
//import com.megacrit.cardcrawl.rooms.CampfireUI;
//import com.megacrit.cardcrawl.rooms.RestRoom;
//import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
//import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;
//
//
//
//public class BGCampfireTokeEffect
//        extends AbstractGameEffect
//{
//    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString("CampfireTokeEffect");
//    public static final String[] TEXT = uiStrings.TEXT;
//
//    private static final float DUR = 1.5F;
//    private boolean openedScreen = false;
//    private Color screenColor = AbstractDungeon.fadeColor.cpy();
//
//    public BGCampfireTokeEffect() {
//        this.duration = 1.5F;
//        this.screenColor.a = 0.0F;
//        //AbstractDungeon.overlayMenu.proceedButton.hide();
//    }
//
//
//    public void update() {
//        if (!AbstractDungeon.isScreenUp) {
//            this.duration -= Gdx.graphics.getDeltaTime();
//            updateBlackScreenColor();
//        }
//
//
//        if (!AbstractDungeon.isScreenUp && !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty() && AbstractDungeon.gridSelectScreen.forPurge) {
//            AbstractCard card = AbstractDungeon.gridSelectScreen.selectedCards.get(0);
//            CardCrawlGame.metricData.addCampfireChoiceData("PURGE", card.getMetricID());
//            CardCrawlGame.sound.play("CARD_EXHAUST");
//            AbstractDungeon.topLevelEffects.add(new PurgeCardEffect(card, (Settings.WIDTH / 2), (Settings.HEIGHT / 2)));
//            AbstractDungeon.player.masterDeck.removeCard(card);
//            AbstractDungeon.gridSelectScreen.selectedCards.clear();
//        }
//
//
//        if (this.duration < 1.0F && !this.openedScreen) {
//            this.openedScreen = true;
//            AbstractDungeon.gridSelectScreen.open(
//                    CardGroup.getGroupWithoutBottledCards(AbstractDungeon.player.masterDeck.getPurgeableCards()), 1, TEXT[0], false, false, true, true);
//        }
//
//
//
//
//
//
//
//
//        if (this.duration < 0.0F) {
//            this.isDone = true;
//            if (CampfireUI.hidden) {
//                AbstractRoom.waitTimer = 0.0F;
//                (AbstractDungeon.getCurrRoom()).phase = AbstractRoom.RoomPhase.COMPLETE;
//                ((RestRoom)AbstractDungeon.getCurrRoom()).cutFireSound();
//            }
//        }
//    }
//
//
//
//
//    private void updateBlackScreenColor() {
//        if (this.duration > 1.0F) {
//            this.screenColor.a = Interpolation.fade.apply(1.0F, 0.0F, (this.duration - 1.0F) * 2.0F);
//        } else {
//            this.screenColor.a = Interpolation.fade.apply(0.0F, 1.0F, this.duration / 1.5F);
//        }
//    }
//
//
//    public void render(SpriteBatch sb) {
//        sb.setColor(this.screenColor);
//        sb.draw(ImageMaster.WHITE_SQUARE_IMG, 0.0F, 0.0F, Settings.WIDTH, Settings.HEIGHT);
//
//        if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.GRID)
//            AbstractDungeon.gridSelectScreen.render(sb);
//    }
//
//    public void dispose() {}
//}
//
//
