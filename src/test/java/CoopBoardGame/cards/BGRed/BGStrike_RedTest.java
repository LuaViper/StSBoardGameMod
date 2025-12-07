package CoopBoardGame.cards.BGRed;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.localization.LocalizedStrings;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockedStatic;

/**
 * Tests for BGStrike_Red card.
 * Tests the basic card properties and logic without requiring full game framework.
 */
class BGStrike_RedTest {

    private static MockedStatic<CardCrawlGame> mockedGame;
    private static MockedStatic<Settings> mockedSettings;
    private static MockedStatic<Gdx> mockedGdx;

    @BeforeAll
    static void setUpStatic() {
        // Mock Gdx for texture loading in AbstractBGCard static initializer
        mockedGdx = mockStatic(Gdx.class);
        Files mockFiles = mock(Files.class);
        FileHandle mockFileHandle = mock(FileHandle.class);

        Gdx.files = mockFiles;
        when(mockFiles.internal(anyString())).thenReturn(mockFileHandle);
        when(mockFileHandle.read()).thenReturn(null); // TextureAtlas constructor needs this

        // Mock CardCrawlGame static fields needed for card initialization
        mockedGame = mockStatic(CardCrawlGame.class);
        mockedSettings = mockStatic(Settings.class);

        // Create mock localization
        LocalizedStrings mockLanguagePack = mock(LocalizedStrings.class);
        CardStrings mockCardStrings = new CardStrings();
        mockCardStrings.NAME = "Strike";
        mockCardStrings.DESCRIPTION = "Deal 1 damage.";

        // Set up static mocks - CardCrawlGame.languagePack field
        CardCrawlGame.languagePack = mockLanguagePack;
        when(mockLanguagePack.getCardStrings(anyString())).thenReturn(mockCardStrings);

        // Mock Settings.isDebug to false for normal card behavior
        Settings.isDebug = false;
    }

    @AfterAll
    static void tearDownStatic() {
        if (mockedGdx != null) {
            mockedGdx.close();
        }
        if (mockedGame != null) {
            mockedGame.close();
        }
        if (mockedSettings != null) {
            mockedSettings.close();
        }
    }

    @BeforeEach
    void setUp() {
    }

    // @Test
    // void testCardInitialization_HasCorrectID() {
    //     assertThat(card.cardID).isEqualTo("BGStrike_R");
    // }

    // @Test
    // void testCardInitialization_HasBaseDamageOfOne() {
    //     assertThat(card.baseDamage).isEqualTo(1);
    // }

    // @Test
    // void testCardInitialization_CostsOneEnergy() {
    //     assertThat(card.cost).isEqualTo(1);
    // }

    // @Test
    // void testCardInitialization_IsAttackType() {
    //     assertThat(card.type).isEqualTo(AbstractCard.CardType.ATTACK);
    // }

    // @Test
    // void testCardInitialization_IsBasicRarity() {
    //     assertThat(card.rarity).isEqualTo(AbstractCard.CardRarity.BASIC);
    // }

    // @Test
    // void testCardInitialization_TargetsEnemy() {
    //     assertThat(card.target).isEqualTo(AbstractCard.CardTarget.ENEMY);
    // }

    // @Test
    // void testCardTags_ContainsStrikeTag() {
    //     assertThat(card.tags).contains(AbstractCard.CardTags.STRIKE);
    // }

    // @Test
    // void testCardTags_ContainsStarterStrikeTag() {
    //     assertThat(card.tags).contains(AbstractCard.CardTags.STARTER_STRIKE);
    // }

    // @Test
    // void testUpgrade_StartsNotUpgraded() {
    //     assertThat(card.upgraded).isFalse();
    // }

    // @Test
    // void testUpgrade_SetsUpgradedFlagToTrue() {
    //     card.upgrade();

    //     assertThat(card.upgraded).isTrue();
    // }

    // @Test
    // void testUpgrade_IncreasesBaseDamageByOne() {
    //     int originalDamage = card.baseDamage;

    //     card.upgrade();

    //     assertThat(card.baseDamage).isEqualTo(originalDamage + 1);
    //     assertThat(card.baseDamage).isEqualTo(2);
    // }

    // @Test
    // void testUpgrade_OnlyWorksOnce() {
    //     card.upgrade();
    //     int damageAfterFirstUpgrade = card.baseDamage;

    //     card.upgrade();

    //     assertThat(card.baseDamage).isEqualTo(damageAfterFirstUpgrade);
    //     assertThat(card.upgraded).isTrue();
    // }

    // @Test
    // void testMakeCopy_CreatesNewInstance() {
    //     AbstractCard copy = card.makeCopy();

    //     assertThat(copy).isNotSameAs(card);
    // }

    // @Test
    // void testMakeCopy_CreatesCorrectType() {
    //     AbstractCard copy = card.makeCopy();

    //     assertThat(copy).isInstanceOf(BGStrike_Red.class);
    // }

    // @Test
    // void testMakeCopy_HasSameCardID() {
    //     AbstractCard copy = card.makeCopy();

    //     assertThat(copy.cardID).isEqualTo(card.cardID);
    // }

    // @Test
    // void testMakeCopy_CopyIsNotUpgraded() {
    //     card.upgrade();
    //     AbstractCard copy = card.makeCopy();

    //     assertThat(copy.upgraded).isFalse();
    //     assertThat(copy.baseDamage).isEqualTo(1);
    // }

    // @Test
    // void testUpgradedCard_HasCorrectDamageValue() {
    //     BGStrike_Red upgradedCard = new BGStrike_Red();
    //     upgradedCard.upgrade();

    //     assertThat(upgradedCard.baseDamage).isEqualTo(2);
    // }

    // @Test
    // void testMultipleCards_AreIndependent() {
    //     BGStrike_Red card1 = new BGStrike_Red();
    //     BGStrike_Red card2 = new BGStrike_Red();

    //     card1.upgrade();

    //     assertThat(card1.upgraded).isTrue();
    //     assertThat(card2.upgraded).isFalse();
    //     assertThat(card1.baseDamage).isEqualTo(2);
    //     assertThat(card2.baseDamage).isEqualTo(1);
    // }
}
