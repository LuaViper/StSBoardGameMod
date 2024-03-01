package BoardGame.icons;

import BoardGame.util.TextureLoader;
import basemod.helpers.TooltipInfo;
import com.evacipated.cardcrawl.mod.stslib.icons.AbstractCustomIcon;

import java.util.ArrayList;
import java.util.List;

public class FakeDazedIcon extends AbstractCustomIcon {
    //Exactly the same as normal Dazed but without the "put on top of your draw pile" tooltip.

    public static final String ID = "BoardGame:FakeDazed";    //reminder: "Icon" is automatically added
    private static FakeDazedIcon singleton;

    public FakeDazedIcon() {
        super(ID, TextureLoader.getTexture("BoardGameResources/images/icons/daze.png"));
    }

    public static FakeDazedIcon get()
    {
        if (singleton == null) {
            singleton = new FakeDazedIcon();
        }
        return singleton;
    }

}