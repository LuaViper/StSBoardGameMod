package BoardGame.icons;

import BoardGame.util.TextureLoader;
import basemod.BaseMod;
import basemod.helpers.TooltipInfo;
import com.evacipated.cardcrawl.mod.stslib.icons.AbstractCustomIcon;

import java.util.ArrayList;
import java.util.List;
//TODO NEXT NEXT: game crash on viewing CERTAIN shiv cards in compendium -- inconsistent between steam and intellij, apparently
public class ShivIcon extends AbstractCustomIcon {
    public static final String ID = "BoardGame:Shiv";    //reminder: "Icon" is automatically added
    private static ShivIcon singleton;

    public ShivIcon() {
        super(ID, TextureLoader.getTexture("BoardGameResources/images/icons/shiv.png"));
    }

    public static ShivIcon get()
    {
        if (singleton == null) {
            singleton = new ShivIcon();
        }
        return singleton;
    }

    public List<TooltipInfo> getCustomTooltips() {
        List<TooltipInfo>list=new ArrayList<>();

        list.add(new TooltipInfo(BaseMod.getKeywordTitle("boardgame:shiv"),BaseMod.getKeywordDescription("boardgame:shiv")));
        return list;
    }
}