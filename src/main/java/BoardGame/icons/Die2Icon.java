package BoardGame.icons;

import BoardGame.util.TextureLoader;
import com.evacipated.cardcrawl.mod.stslib.icons.AbstractCustomIcon;

public class Die2Icon extends AbstractCustomIcon {
    public static final String ID = "BoardGame:Die2";    //reminder: "Icon" is automatically added
    private static Die2Icon singleton;

    public Die2Icon() {
        super(ID, TextureLoader.getTexture("BoardGameResources/images/icons/die2.png"));
    }

    public static Die2Icon get()
    {
        if (singleton == null) {
            singleton = new Die2Icon();
        }
        return singleton;
    }

}