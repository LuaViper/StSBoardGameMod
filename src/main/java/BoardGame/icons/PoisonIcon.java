package BoardGame.icons;

import BoardGame.util.TextureLoader;
import basemod.helpers.TooltipInfo;
import com.evacipated.cardcrawl.mod.stslib.icons.AbstractCustomIcon;

import java.util.ArrayList;
import java.util.List;

public class PoisonIcon extends AbstractCustomIcon {
    public static final String ID = "BoardGame:Poison";    //reminder: "Icon" is automatically added
    private static PoisonIcon singleton;

    public PoisonIcon() {
        super(ID, TextureLoader.getTexture("BoardGameResources/images/icons/poison.png"));
    }

    public static PoisonIcon get()
    {
        if (singleton == null) {
            singleton = new PoisonIcon();
        }
        return singleton;
    }

    public List<TooltipInfo> getCustomTooltips() {
        List<TooltipInfo>list=new ArrayList<>();
        list.add(new TooltipInfo("[BoardGame:PoisonIcon] Poison","At the end of the player's turn, enemies with [BoardGame:PoisonIcon] lose 1 HP per [BoardGame:PoisonIcon] token they have."));
        return list;
    }
}