package CoopBoardGame.icons;

import CoopBoardGame.util.TextureLoader;
import basemod.helpers.TooltipInfo;
import com.evacipated.cardcrawl.mod.stslib.icons.AbstractCustomIcon;
import java.util.ArrayList;
import java.util.List;

public class CardpackIcon extends AbstractCustomIcon {

    public static final String ID = "CoopBoardGame:Cardpack"; //reminder: "Icon" is automatically added
    private static CardpackIcon singleton;

    public CardpackIcon() {
        super(ID, TextureLoader.getTexture("CoopBoardGameResources/images/icons/cardpack.png"));
    }

    public static CardpackIcon get() {
        if (singleton == null) {
            singleton = new CardpackIcon();
        }
        return singleton;
    }

    public List<TooltipInfo> getCustomTooltips() {
        List<TooltipInfo> list = new ArrayList<>();
        list.add(
            new TooltipInfo(
                "[CoopBoardGame:CardpackIcon] Card Reward",
                "Reveal 3 card rewards. Add 1 to your deck or skip."
            )
        );
        return list;
    }
}
