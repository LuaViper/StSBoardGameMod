package CoopBoardGame.icons;

import CoopBoardGame.util.TextureLoader;
import com.evacipated.cardcrawl.mod.stslib.icons.AbstractCustomIcon;

public class FakeDazedIcon extends AbstractCustomIcon {

    //Exactly the same as normal Dazed but without the "put on top of your draw pile" tooltip.

    public static final String ID = "CoopBoardGame:FakeDazed"; //reminder: "Icon" is automatically added
    private static FakeDazedIcon singleton;

    public FakeDazedIcon() {
        super(ID, TextureLoader.getTexture("CoopBoardGameResources/images/icons/daze.png"));
    }

    public static FakeDazedIcon get() {
        if (singleton == null) {
            singleton = new FakeDazedIcon();
        }
        return singleton;
    }
}
