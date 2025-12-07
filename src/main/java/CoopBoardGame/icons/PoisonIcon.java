package CoopBoardGame.icons;

import CoopBoardGame.util.TextureLoader;
import basemod.helpers.TooltipInfo;
import com.evacipated.cardcrawl.mod.stslib.icons.AbstractCustomIcon;
import java.util.ArrayList;
import java.util.List;

public class PoisonIcon extends AbstractCustomIcon {

    public static final String ID = "CoopBoardGame:Poison"; //reminder: "Icon" is automatically added
    private static PoisonIcon singleton;

    public PoisonIcon() {
        super(ID, TextureLoader.getTexture("CoopBoardGameResources/images/icons/poison.png"));
    }

    public static PoisonIcon get() {
        if (singleton == null) {
            singleton = new PoisonIcon();
        }
        return singleton;
    }

    public List<TooltipInfo> getCustomTooltips() {
        List<TooltipInfo> list = new ArrayList<>();
        list.add(
            new TooltipInfo(
                "[CoopBoardGame:PoisonIcon] Poison",
                "At the end of the player's turn, enemies with [CoopBoardGame:PoisonIcon] lose 1 HP per [CoopBoardGame:PoisonIcon] token they have."
            )
        );
        return list;
    }
}
