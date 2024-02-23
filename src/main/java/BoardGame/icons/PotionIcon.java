package BoardGame.icons;

import BoardGame.util.TextureLoader;
import basemod.helpers.TooltipInfo;
import com.evacipated.cardcrawl.mod.stslib.icons.AbstractCustomIcon;

import java.util.ArrayList;
import java.util.List;

public class PotionIcon extends AbstractCustomIcon {
    public static final String ID = "BoardGame:Potion";    //reminder: "Icon" is automatically added
    private static PotionIcon singleton;

    public PotionIcon() {
        super(ID, TextureLoader.getTexture("BoardGameResources/images/icons/pot.png"));
    }

    public static PotionIcon get()
    {
        if (singleton == null) {
            singleton = new PotionIcon();
        }
        return singleton;
    }

    public List<TooltipInfo> getCustomTooltips() {
        List<TooltipInfo>list=new ArrayList<>();
        list.add(new TooltipInfo("[BoardGame:Potion] Potion","Draw a card from the potion deck. You may gain that potion or skip it."));
        return list;
    }
}