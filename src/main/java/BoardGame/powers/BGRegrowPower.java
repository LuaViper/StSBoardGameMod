package BoardGame.powers;

import BoardGame.monsters.bgbeyond.BGDarkling;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BGRegrowPower extends AbstractBGPower {
    public static final String POWER_ID = "BGRegrowPower";
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings("BoardGame:BGRegrowPower");
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public BGRegrowPower(AbstractCreature owner) {
        Logger logger = LogManager.getLogger(BGRegrowPower.class.getName());
        if(!(owner instanceof BGDarkling)){
            logger.warn("tried to apply BGRegrowPower to something other than a Darkling; things will probably break");
        }
        this.name = NAME;
        this.ID = "BGRegrowPower";
        this.owner = owner;
        updateDescription();
        loadRegion("regrow");
        this.type = AbstractPower.PowerType.BUFF;
    }





    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }
}


