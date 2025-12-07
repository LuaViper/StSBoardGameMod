package CoopBoardGame.testutil;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Base test class providing common mock setup for Board Game mod tests.
 * Extend this class to get access to commonly mocked objects.
 */
@ExtendWith(MockitoExtension.class)
public abstract class TestBase {

    @Mock
    protected AbstractPlayer mockPlayer;

    @Mock
    protected AbstractMonster mockMonster;

    protected MockActionManager actionManager;

    @BeforeEach
    void baseSetUp() {
        actionManager = new MockActionManager();
    }
}
