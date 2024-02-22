package BoardGame.icons;

import BoardGame.util.TextureLoader;
import com.evacipated.cardcrawl.mod.stslib.icons.AbstractCustomIcon;

public class Die5Icon extends AbstractCustomIcon {
    public static final String ID = "BoardGame:Die5";    //reminder: "Icon" is automatically added
    private static Die5Icon singleton;

    public Die5Icon() {
        super(ID, TextureLoader.getTexture("BoardGameResources/images/icons/die5.png"));
    }

    public static Die5Icon get()
    {
        if (singleton == null) {
            singleton = new Die5Icon();
        }
        return singleton;
    }

}