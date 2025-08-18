package BoardGame.powers;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;


public class BGPoisonProccerPower extends AbstractBGPower implements InvisiblePower {
    public static final String POWER_ID = "BGPoisonProccerPower";

    private static final int MAX_POISON_TOKENS = 30;
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings("BoardGame:BGPoison");

    public static final String NAME = powerStrings.NAME;

    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private AbstractCreature source;

    public BGPoisonProccerPower(AbstractCreature owner) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
    }
    @Override
    public void atEndOfTurnPreEndTurnCards(boolean isPlayer){
        for(AbstractMonster m : AbstractDungeon.getMonsters().monsters){
            AbstractPower p = m.getPower(BGPoisonPower.POWER_ID);
            if(p!=null){
                ((BGPoisonPower)p).proc();
            }
        }
    }


}
