package CoopBoardGame.patches.mainmenu;

import CoopBoardGame.characters.AbstractBGPlayer;
import CoopBoardGame.characters.BGDefect;
import CoopBoardGame.characters.BGIronclad;
import CoopBoardGame.characters.BGSilent;
import CoopBoardGame.characters.BGWatcher;
import CoopBoardGame.multicharacter.MultiCharacter;
import basemod.BaseMod;
import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.screens.charSelect.CharacterOption;
import com.megacrit.cardcrawl.screens.charSelect.CharacterSelectScreen;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;
import com.megacrit.cardcrawl.screens.mainMenu.MenuButton;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Adds a "Board Game" button to the main menu that opens character selection
 * with only Board Game characters visible.
 */
public class BoardGameMenuButton {
    private static final Logger logger = LogManager.getLogger(BoardGameMenuButton.class.getName());

    // Track whether we're in board game mode for character filtering
    public static boolean isBoardGameMode = false;

    // Custom menu button for Board Game mode
    private static Hitbox boardGameHb;
    private static float boardGameY;
    private static final String BOARD_GAME_TEXT = "Board Game";
    private static boolean boardGameHovered = false;

    /**
     * Patch to add the Board Game button to the main menu
     */
    @SpirePatch2(
        clz = MainMenuScreen.class,
        method = "setMainMenuButtons"
    )
    public static class AddBoardGameButtonPatch {
        @SpirePostfixPatch
        public static void Postfix(MainMenuScreen __instance) {
            // Initialize the board game button hitbox
            // Position it below the existing buttons using the hitbox Y position
            ArrayList<MenuButton> buttons = ReflectionHacks.getPrivate(__instance, MainMenuScreen.class, "buttons");
            if (buttons != null && !buttons.isEmpty()) {
                // Find the lowest button and position our button below it
                float lowestY = Float.MAX_VALUE;
                for (MenuButton btn : buttons) {
                    // MenuButton has a hb (Hitbox) field, use its cY (center Y)
                    if (btn.hb != null && btn.hb.cY < lowestY) {
                        lowestY = btn.hb.cY;
                    }
                }
                boardGameY = lowestY - 70.0f * Settings.scale;
            } else {
                boardGameY = Settings.HEIGHT / 2.0f - 200.0f * Settings.scale;
            }

            float buttonWidth = 400.0f * Settings.scale;
            float buttonHeight = 50.0f * Settings.scale;
            boardGameHb = new Hitbox(
                Settings.WIDTH / 2.0f - buttonWidth / 2.0f,
                boardGameY - buttonHeight / 2.0f,
                buttonWidth,
                buttonHeight
            );

            logger.info("Board Game button initialized at Y=" + boardGameY);
        }
    }

    /**
     * Patch to update the Board Game button
     */
    @SpirePatch2(
        clz = MainMenuScreen.class,
        method = "update"
    )
    public static class UpdateBoardGameButtonPatch {
        @SpirePostfixPatch
        public static void Postfix(MainMenuScreen __instance) {
            if (boardGameHb == null) return;
            if (__instance.screen != MainMenuScreen.CurScreen.MAIN_MENU) return;

            boardGameHb.update();
            boardGameHovered = boardGameHb.hovered;

            if (boardGameHb.justHovered) {
                CardCrawlGame.sound.playA("UI_HOVER", -0.3f);
            }

            if (boardGameHb.hovered && InputHelper.justClickedLeft) {
                boardGameHb.clickStarted = true;
            }

            if (boardGameHb.clicked || (boardGameHb.hovered && CInputActionSet.select.isJustPressed())) {
                boardGameHb.clicked = false;
                CardCrawlGame.sound.playA("UI_CLICK_1", -0.4f);

                // Set board game mode flag before transitioning
                isBoardGameMode = true;
                logger.info("Board Game mode selected - transitioning to character select");

                // Transition to character select
                __instance.screen = MainMenuScreen.CurScreen.CHAR_SELECT;
                __instance.charSelectScreen = new CharacterSelectScreen();

                // Reset the flag after a short delay (handled in character select filtering)
            }
        }
    }

    /**
     * Patch to render the Board Game button
     */
    @SpirePatch2(
        clz = MainMenuScreen.class,
        method = "render"
    )
    public static class RenderBoardGameButtonPatch {
        @SpirePostfixPatch
        public static void Postfix(MainMenuScreen __instance, SpriteBatch sb) {
            if (boardGameHb == null) return;
            if (__instance.screen != MainMenuScreen.CurScreen.MAIN_MENU) return;

            // Draw button background
            Color bgColor = boardGameHovered ? new Color(0.3f, 0.3f, 0.4f, 0.8f) : new Color(0.2f, 0.2f, 0.3f, 0.6f);
            sb.setColor(bgColor);
            float padding = 10.0f * Settings.scale;
            sb.draw(
                ImageMaster.WHITE_SQUARE_IMG,
                boardGameHb.x - padding,
                boardGameHb.y - padding,
                boardGameHb.width + padding * 2,
                boardGameHb.height + padding * 2
            );

            // Draw button text
            Color textColor = boardGameHovered ? Settings.GOLD_COLOR : Settings.CREAM_COLOR;
            FontHelper.renderFontCentered(
                sb,
                FontHelper.buttonLabelFont,
                BOARD_GAME_TEXT,
                Settings.WIDTH / 2.0f,
                boardGameY,
                textColor
            );

            // Draw hitbox for debugging (optional)
            // boardGameHb.render(sb);
        }
    }

    /**
     * Patch CharacterSelectScreen to filter characters based on mode
     * In Board Game mode: show only BG characters (not MultiCharacter)
     * In normal mode: hide BG characters
     */
    @SpirePatch2(
        clz = CharacterSelectScreen.class,
        method = SpirePatch.CONSTRUCTOR
    )
    public static class FilterCharactersPatch {
        @SpirePostfixPatch
        public static void Postfix(CharacterSelectScreen __instance) {
            ArrayList<CharacterOption> options = ReflectionHacks.getPrivate(
                __instance, CharacterSelectScreen.class, "options"
            );

            if (options == null) return;

            if (isBoardGameMode) {
                logger.info("Filtering characters for Board Game mode");

                // Remove non-BG characters and MultiCharacter, keep only individual BG characters
                ArrayList<CharacterOption> bgOptions = new ArrayList<>();
                for (CharacterOption option : options) {
                    AbstractPlayer player = option.c;
                    // Keep only individual BG characters (not MultiCharacter)
                    if (player instanceof AbstractBGPlayer && !(player instanceof MultiCharacter)) {
                        bgOptions.add(option);
                        logger.info("Keeping BG character: " + player.getClass().getSimpleName());
                    }
                }

                // Replace options with only BG characters
                options.clear();
                options.addAll(bgOptions);

                logger.info("Board Game mode: showing " + bgOptions.size() + " characters");
            } else {
                logger.info("Normal mode - removing BG characters from selection");

                // Remove all BG characters from normal play mode
                Iterator<CharacterOption> iterator = options.iterator();
                while (iterator.hasNext()) {
                    CharacterOption option = iterator.next();
                    if (option.c instanceof AbstractBGPlayer) {
                        logger.info("Removing BG character from normal mode: " + option.c.getClass().getSimpleName());
                        iterator.remove();
                    }
                }
            }

            // Recalculate positions for remaining options
            repositionOptions(options);
        }

        private static void repositionOptions(ArrayList<CharacterOption> options) {
            if (options.isEmpty()) return;

            float totalWidth = options.size() * 220.0f * Settings.scale;
            float startX = (Settings.WIDTH - totalWidth) / 2.0f + 110.0f * Settings.scale;

            for (int i = 0; i < options.size(); i++) {
                CharacterOption option = options.get(i);
                float x = startX + i * 220.0f * Settings.scale;
                ReflectionHacks.setPrivate(option, CharacterOption.class, "selectX", x);
                option.hb.move(x, option.hb.cY);
            }
        }
    }

    /**
     * Reset board game mode flag when returning to main menu
     */
    @SpirePatch2(
        clz = CharacterSelectScreen.class,
        method = "update"
    )
    public static class ResetModeOnBackPatch {
        @SpirePostfixPatch
        public static void Postfix(CharacterSelectScreen __instance) {
            // Check if user pressed back/cancel
            if (InputHelper.pressedEscape || CInputActionSet.cancel.isJustPressed()) {
                if (isBoardGameMode) {
                    logger.info("Exiting Board Game character select - resetting mode flag");
                    isBoardGameMode = false;
                }
            }
        }
    }

    /**
     * Reset board game mode flag when game starts (character is selected and embark is clicked)
     */
    @SpirePatch2(
        clz = CardCrawlGame.class,
        method = "startOver"
    )
    public static class ResetModeOnGameStartPatch {
        @SpirePostfixPatch
        public static void Postfix() {
            if (isBoardGameMode) {
                logger.info("Game starting - resetting board game mode flag");
                isBoardGameMode = false;
            }
        }
    }

    /**
     * Also reset when returning to main menu from anywhere
     */
    @SpirePatch2(
        clz = MainMenuScreen.class,
        method = SpirePatch.CONSTRUCTOR,
        paramtypez = { boolean.class }
    )
    public static class ResetModeOnMainMenuPatch {
        @SpirePostfixPatch
        public static void Postfix() {
            if (isBoardGameMode) {
                logger.info("Returning to main menu - resetting board game mode flag");
                isBoardGameMode = false;
            }
        }
    }
}
