package BoardGame.icons;

import BoardGame.util.TextureLoader;
import basemod.helpers.TooltipInfo;
import com.evacipated.cardcrawl.mod.stslib.icons.AbstractCustomIcon;

import java.util.ArrayList;
import java.util.List;

public class DazedIcon extends AbstractCustomIcon {
    public static final String ID = "BoardGame:Dazed";    //reminder: "Icon" is automatically added
    private static DazedIcon singleton;

    public DazedIcon() {
        super(ID, TextureLoader.getTexture("BoardGameResources/images/icons/daze.png"));
    }

    public static DazedIcon get()
    {
        if (singleton == null) {
            singleton = new DazedIcon();
        }
        return singleton;
    }

    public List<TooltipInfo> getCustomTooltips() {
        List<TooltipInfo>list=new ArrayList<>();
        list.add(new TooltipInfo("[BoardGame:DazedIcon] Daze","Put a Daze on top of your draw pile."));
        return list;
    }
}