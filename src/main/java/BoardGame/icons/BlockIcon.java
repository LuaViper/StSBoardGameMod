package BoardGame.icons;

import BoardGame.util.TextureLoader;
import basemod.helpers.TooltipInfo;
import com.evacipated.cardcrawl.mod.stslib.icons.AbstractCustomIcon;

import java.util.ArrayList;
import java.util.List;

public class BlockIcon extends AbstractCustomIcon {
    public static final String ID = "BoardGame:Block";    //reminder: "Icon" is automatically added
    private static BlockIcon singleton;

    public BlockIcon() {
        super(ID, TextureLoader.getTexture("BoardGameResources/images/icons/block.png"));
    }

    public static BlockIcon get()
    {
        if (singleton == null) {
            singleton = new BlockIcon();
        }
        return singleton;
    }

    public List<TooltipInfo> getCustomTooltips() {
        List<TooltipInfo>list=new ArrayList<>();
        list.add(new TooltipInfo("[BoardGame:BlockIcon] Block","Block prevents damage."));
        return list;
    }
}