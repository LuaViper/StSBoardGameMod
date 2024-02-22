package BoardGame.icons;

import BoardGame.util.TextureLoader;
import basemod.helpers.TooltipInfo;
import com.evacipated.cardcrawl.mod.stslib.icons.AbstractCustomIcon;

import java.util.ArrayList;
import java.util.List;

public class HitIcon extends AbstractCustomIcon {
    public static final String ID = "BoardGame:Hit";    //reminder: "Icon" is automatically added
    private static HitIcon singleton;

    public HitIcon() {
        super(ID, TextureLoader.getTexture("BoardGameResources/images/icons/hit.png"));
    }

    public static HitIcon get()
    {
        if (singleton == null) {
            singleton = new HitIcon();
        }
        return singleton;
    }

    public List<TooltipInfo> getCustomTooltips() {
        List<TooltipInfo>list=new ArrayList<>();
        list.add(new TooltipInfo("[BoardGame:HitIcon] Hit","Hits deal damage."));
        return list;
    }
}