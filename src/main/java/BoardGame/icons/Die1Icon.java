package BoardGame.icons;

import BoardGame.util.TextureLoader;
import basemod.helpers.TooltipInfo;
import com.evacipated.cardcrawl.mod.stslib.icons.AbstractCustomIcon;

import java.util.ArrayList;
import java.util.List;

public class Die1Icon extends AbstractCustomIcon {
    public static final String ID = "BoardGame:Die1";    //reminder: "Icon" is automatically added
    private static Die1Icon singleton;

    public Die1Icon() {
        super(ID, TextureLoader.getTexture("BoardGameResources/images/icons/die1.png"));
    }

    public static Die1Icon get()
    {
        if (singleton == null) {
            singleton = new Die1Icon();
        }
        return singleton;
    }

}