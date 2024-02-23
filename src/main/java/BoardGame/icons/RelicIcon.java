package BoardGame.icons;

import BoardGame.util.TextureLoader;
import basemod.helpers.TooltipInfo;
import com.evacipated.cardcrawl.mod.stslib.icons.AbstractCustomIcon;

import java.util.ArrayList;
import java.util.List;

public class RelicIcon extends AbstractCustomIcon {
    public static final String ID = "BoardGame:Relic";    //reminder: "Icon" is automatically added
    private static RelicIcon singleton;

    public RelicIcon() {
        super(ID, TextureLoader.getTexture("BoardGameResources/images/icons/relic.png"));
    }

    public static RelicIcon get()
    {
        if (singleton == null) {
            singleton = new RelicIcon();
        }
        return singleton;
    }

    public List<TooltipInfo> getCustomTooltips() {
        List<TooltipInfo>list=new ArrayList<>();
        list.add(new TooltipInfo("[BoardGame:Relic] Relic","Draw a card from the relic deck. You may gain that relic or skip it."));
        return list;
    }
}