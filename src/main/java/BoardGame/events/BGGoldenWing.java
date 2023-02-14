package BoardGame.events;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.events.GenericEventDialog;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.vfx.RainingGoldEffect;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;

public class BGGoldenWing
        extends AbstractImageEvent {
    public static final String ID = "BGGolden Wing";
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString("BoardGame:BGGolden Wing");
    public static final String NAME = eventStrings.NAME;
    public static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    public static final String[] OPTIONS = eventStrings.OPTIONS;

    private int damage = 2;
    private static final String INTRO = DESCRIPTIONS[0];
    private static final String AGREE_DIALOG = DESCRIPTIONS[1];
    private static final String SPECIAL_OPTION = DESCRIPTIONS[2];
    private static final String DISAGREE_DIALOG = DESCRIPTIONS[3];

    private boolean canAttack;

    private boolean purgeResult = false;
    private static final int MIN_GOLD = 2;
    private CUR_SCREEN screen = CUR_SCREEN.INTRO; private static final int MAX_GOLD = 2; private static final int REQUIRED_DAMAGE = 0;
    private int goldAmount;

    private enum CUR_SCREEN { INTRO, PURGE, MAP; }


    public BGGoldenWing() {
        super(NAME, INTRO, "images/events/goldenWing.jpg");

        this.canAttack = true;
        this.imageEventText.setDialogOption(OPTIONS[0] + this.damage + OPTIONS[1]);
        this.imageEventText.setDialogOption(OPTIONS[2] + "2" + OPTIONS[4]);
    }

    public void update() {
        super.update();
        purgeLogic();

        if (this.waitForInput) {
            buttonEffect(GenericEventDialog.getSelectedOption());
        }
    }

    protected void buttonEffect(int buttonPressed) {
        switch (this.screen) {
            case INTRO:
                switch (buttonPressed) {
                    case 0:
                        this.imageEventText.updateBodyText(AGREE_DIALOG);
                        AbstractDungeon.player.damage(new DamageInfo((AbstractCreature)AbstractDungeon.player, this.damage));
                        AbstractDungeon.effectList.add(new FlashAtkImgEffect(AbstractDungeon.player.hb.cX, AbstractDungeon.player.hb.cY, AbstractGameAction.AttackEffect.FIRE));




                        this.screen = CUR_SCREEN.PURGE;
                        this.imageEventText.updateDialogOption(0, OPTIONS[8]);
                        this.imageEventText.removeDialogOption(1);
                        return;
                    case 1:
                        if (this.canAttack) {
                            this.goldAmount = 2;
                            int fakeGoldAmount = AbstractDungeon.miscRng.random(50, 80);
                            AbstractDungeon.effectList.add(new RainingGoldEffect(fakeGoldAmount));
                            AbstractDungeon.player.gainGold(this.goldAmount);
                            AbstractEvent.logMetricGainGold("Golden Wing", "Gained Gold", this.goldAmount);
                            this.imageEventText.updateBodyText(SPECIAL_OPTION);
                            this.screen = CUR_SCREEN.MAP;
                            this.imageEventText.updateDialogOption(0, OPTIONS[7]);
                            this.imageEventText.removeDialogOption(1);
                        }
                        return;
                }
                this.imageEventText.updateBodyText(DISAGREE_DIALOG);
                AbstractEvent.logMetricIgnored("Golden Wing");
                this.screen = CUR_SCREEN.MAP;
                this.imageEventText.updateDialogOption(0, OPTIONS[7]);
                this.imageEventText.removeDialogOption(1);
                this.imageEventText.removeDialogOption(1);
                return;



            case PURGE:
                AbstractDungeon.gridSelectScreen.open(
                        CardGroup.getGroupWithoutBottledCards(AbstractDungeon.player.masterDeck.getPurgeableCards()), 1, OPTIONS[9], false, false, false, true);






                this.imageEventText.updateDialogOption(0, OPTIONS[7]);
                this.purgeResult = true;
                this.screen = CUR_SCREEN.MAP;
                return;

            case MAP:
                openMap();
                return;
        }

        openMap();
    }



    private void purgeLogic() {
        if (this.purgeResult && !AbstractDungeon.isScreenUp && !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            AbstractCard c = AbstractDungeon.gridSelectScreen.selectedCards.get(0);
            AbstractDungeon.topLevelEffects.add(new PurgeCardEffect(c, (Settings.WIDTH / 2), (Settings.HEIGHT / 2)));
            AbstractEvent.logMetricCardRemovalAndDamage("Golden Wing", "Card Removal", c, this.damage);
            AbstractDungeon.player.masterDeck.removeCard(c);
            AbstractDungeon.gridSelectScreen.selectedCards.clear();

            this.purgeResult = false;
        }
    }
}


