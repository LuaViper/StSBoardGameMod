package BoardGame.monsters.bgbeyond; 
 import BoardGame.monsters.BGDamageIcons;
import BoardGame.monsters.AbstractBGMonster;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ChangeStateAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDiscardAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.status.Wound;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class BGSnakeDagger extends AbstractBGMonster implements BGDamageIcons {
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("Dagger");
    public static final String ID = "BGSnakeDagger";
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG; private static final int HP_MIN = 20; private static final int HP_MAX = 25;

    public boolean firstMove = true;

    public BGSnakeDagger(float x, float y) {
        super(NAME, "BGSnakeDagger", AbstractDungeon.monsterHpRng.random(20, 25), 0.0F, -50.0F, 140.0F, 130.0F, null, x, y);
        initializeAnimation();

        setHp(5);

        this.damage.add(new DamageInfo((AbstractCreature)this, 2));
        this.damage.add(new DamageInfo((AbstractCreature)this, 5));

    }

    public void initializeAnimation() {
        loadAnimation("images/monsters/theForest/mage_dagger/skeleton.atlas", "images/monsters/theForest/mage_dagger/skeleton.json", 1.0F);




        AnimationState.TrackEntry e = this.state.setAnimation(0, "Idle", true);
        e.setTime(e.getEndTime() * MathUtils.random());
    }


    public void takeTurn() {
        switch (this.nextMove) {
            case 1:
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ChangeStateAction(this, "ATTACK"));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new WaitAction(0.3F));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                        .get(0), AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));
                //AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new MakeTempCardInDiscardAction((AbstractCard)new Wound(), 1));
                break;
            case 2:
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ChangeStateAction(this, "SUICIDE"));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new WaitAction(0.4F));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                        .get(1), AbstractGameAction.AttackEffect.SLASH_HEAVY));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new LoseHPAction((AbstractCreature)this, (AbstractCreature)this, this.currentHealth));
                break;
        }
        AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new RollMoveAction(this));
    }


    public void damage(DamageInfo info) {
        super.damage(info);
        if (info.owner != null && info.type != DamageInfo.DamageType.THORNS && info.output > 0) {
            this.state.setAnimation(0, "Hurt", false);
            this.state.addAnimation(0, "Idle", true, 0.0F);
            this.stateData.setMix("Hurt", "Idle", 0.1F);
            this.stateData.setMix("Idle", "Hurt", 0.1F);
        }
    }


    protected void getMove(int num) {
        if (this.firstMove) {
            this.firstMove = false;
            setMove((byte)1, AbstractMonster.Intent.ATTACK, 2);

            return;
        }
        setMove((byte)2, AbstractMonster.Intent.ATTACK, 5);
    }


    public void changeState(String key) {
        switch (key) {
            case "ATTACK":
                this.state.setAnimation(0, "Attack", false);
                this.state.addAnimation(0, "Idle", true, 0.0F);
                break;
            case "SUICIDE":
                this.state.setAnimation(0, "Attack2", false);
                this.state.addAnimation(0, "Idle", true, 0.0F);
                break;
        }
    }
}


