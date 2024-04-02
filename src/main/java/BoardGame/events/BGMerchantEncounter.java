package BoardGame.events;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BGMerchantEncounter extends AbstractImageEvent {
    public static final String ID = "BGMerchantEncounter";

    private static final Logger logger = LogManager.getLogger(BGMerchantEncounter.class.getName());
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString("BoardGame:BGPlaceholderEvent");
    public static final String NAME = eventStrings.NAME;

    public static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;

    public static final String[] OPTIONS = eventStrings.OPTIONS;

    public BGMerchantEncounter() {
        super(NAME, "DNT: Merchant Encounter Placeholder", "");
    }

    protected void buttonEffect(int buttonPressed){}
}


