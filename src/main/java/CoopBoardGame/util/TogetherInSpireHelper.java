package CoopBoardGame.util;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for detecting and integrating with TogetherInSpire multiplayer mode.
 *
 * This class uses reflection to avoid hard dependencies on TogetherInSpire,
 * allowing the mod to work both with and without TogetherInSpire installed.
 */
public class TogetherInSpireHelper {

    private static final Logger logger = LogManager.getLogger(TogetherInSpireHelper.class.getName());

    // Cached reflection lookups
    private static Boolean togetherInSpireLoaded = null;
    private static Class<?> p2pManagerClass = null;
    private static Class<?> p2pPlayerClass = null;

    /**
     * Checks if TogetherInSpire mod is loaded.
     *
     * @return true if TogetherInSpire is available
     */
    public static boolean isTogetherInSpireLoaded() {
        if (togetherInSpireLoaded == null) {
            try {
                p2pManagerClass = Class.forName("spireTogether.network.P2P.P2PManager");
                p2pPlayerClass = Class.forName("spireTogether.network.P2P.P2PPlayer");
                togetherInSpireLoaded = true;
                logger.info("TogetherInSpire detected and loaded");
            } catch (ClassNotFoundException e) {
                togetherInSpireLoaded = false;
                logger.info("TogetherInSpire not detected");
            }
        }
        return togetherInSpireLoaded;
    }

    /**
     * Checks if we are currently in a multiplayer session.
     * Returns false if TogetherInSpire is not loaded.
     *
     * @return true if in an active multiplayer game
     */
    public static boolean isMultiplayerActive() {
        if (!isTogetherInSpireLoaded()) {
            return false;
        }

        try {
            // Check P2PManager.players.size() > 1
            java.lang.reflect.Field playersField = p2pManagerClass.getDeclaredField("players");
            playersField.setAccessible(true);
            Object players = playersField.get(null);

            if (players instanceof List) {
                int playerCount = ((List<?>) players).size();
                return playerCount > 1;
            }
        } catch (Exception e) {
            logger.warn("Failed to check multiplayer status: " + e.getMessage());
        }

        return false;
    }

    /**
     * Gets the number of players in the current multiplayer session.
     * Returns 1 if TogetherInSpire is not loaded or not in multiplayer.
     *
     * @return number of players (1 for single player)
     */
    public static int getPlayerCount() {
        if (!isTogetherInSpireLoaded()) {
            return 1;
        }

        try {
            java.lang.reflect.Field playersField = p2pManagerClass.getDeclaredField("players");
            playersField.setAccessible(true);
            Object players = playersField.get(null);

            if (players instanceof List) {
                return ((List<?>) players).size();
            }
        } catch (Exception e) {
            logger.warn("Failed to get player count: " + e.getMessage());
        }

        return 1;
    }

    /**
     * Gets the local player's ID in the multiplayer session.
     * Returns 0 if TogetherInSpire is not loaded or not in multiplayer.
     *
     * @return local player ID
     */
    public static int getLocalPlayerId() {
        if (!isTogetherInSpireLoaded()) {
            return 0;
        }

        try {
            java.lang.reflect.Field selfIdField = p2pManagerClass.getDeclaredField("selfID");
            selfIdField.setAccessible(true);
            Object selfId = selfIdField.get(null);

            if (selfId instanceof Integer) {
                return (Integer) selfId;
            }
        } catch (Exception e) {
            logger.warn("Failed to get local player ID: " + e.getMessage());
        }

        return 0;
    }

    /**
     * Gets the PlayerClass of each player in the multiplayer session.
     * This retrieves the character class that each player chose when starting the game.
     *
     * @return List of PlayerClass enums for all players, ordered by player ID.
     *         Returns empty list if not in multiplayer or if TogetherInSpire is not loaded.
     */
    public static List<AbstractPlayer.PlayerClass> getAllPlayerClasses() {
        List<AbstractPlayer.PlayerClass> result = new ArrayList<>();

        if (!isTogetherInSpireLoaded()) {
            return result;
        }

        try {
            java.lang.reflect.Field playersField = p2pManagerClass.getDeclaredField("players");
            playersField.setAccessible(true);
            Object players = playersField.get(null);

            if (players instanceof List) {
                List<?> playerList = (List<?>) players;

                for (Object p2pPlayer : playerList) {
                    if (p2pPlayer != null) {
                        // Get the playerClass field from P2PPlayer
                        java.lang.reflect.Field playerClassField = p2pPlayerClass.getDeclaredField("playerClass");
                        playerClassField.setAccessible(true);
                        Object playerClass = playerClassField.get(p2pPlayer);

                        if (playerClass instanceof AbstractPlayer.PlayerClass) {
                            result.add((AbstractPlayer.PlayerClass) playerClass);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("Failed to get player classes: " + e.getMessage());
        }

        return result;
    }

    /**
     * Checks if the current player is the host/leader of the multiplayer session.
     * The host is typically player ID 0 or the one who created the lobby.
     *
     * @return true if local player is the host, or if in single player mode
     */
    public static boolean isHost() {
        if (!isMultiplayerActive()) {
            return true; // Single player is always "host"
        }

        // In TogetherInSpire, the host is typically ID 0
        return getLocalPlayerId() == 0;
    }

    /**
     * Checks if we are in a multiplayer session with multiple Board Game characters.
     * This is used to determine if the grid/row system should be enabled.
     *
     * @return true if multiple BG players are in the session
     */
    public static boolean isMultiplayerBoardGameMode() {
        if (!isMultiplayerActive()) {
            return false;
        }

        List<AbstractPlayer.PlayerClass> playerClasses = getAllPlayerClasses();
        int bgPlayerCount = 0;

        for (AbstractPlayer.PlayerClass playerClass : playerClasses) {
            if (isBoardGameClass(playerClass)) {
                bgPlayerCount++;
            }
        }

        return bgPlayerCount > 1;
    }

    /**
     * Checks if a player class is a Board Game character class.
     *
     * @param playerClass The player class to check
     * @return true if it's a BG character class
     */
    public static boolean isBoardGameClass(AbstractPlayer.PlayerClass playerClass) {
        if (playerClass == null) {
            return false;
        }

        String className = playerClass.name();

        // Check for BG class names
        return className.equals("BG_IRONCLAD") ||
               className.equals("BG_SILENT") ||
               className.equals("BG_DEFECT") ||
               className.equals("BG_WATCHER") ||
               className.equals("BG_MULTICHARACTER");
    }

    /**
     * Gets the number of Board Game players in the current multiplayer session.
     *
     * @return count of BG players (0 if not in multiplayer or no BG players)
     */
    public static int getBoardGamePlayerCount() {
        if (!isMultiplayerActive()) {
            return 0;
        }

        List<AbstractPlayer.PlayerClass> playerClasses = getAllPlayerClasses();
        int bgPlayerCount = 0;

        for (AbstractPlayer.PlayerClass playerClass : playerClasses) {
            if (isBoardGameClass(playerClass)) {
                bgPlayerCount++;
            }
        }

        return bgPlayerCount;
    }

    /**
     * Gets the player IDs of all Board Game players in the current multiplayer session.
     *
     * @return list of player IDs who are using BG character classes
     */
    public static List<Integer> getBoardGamePlayerIds() {
        List<Integer> result = new ArrayList<>();

        if (!isMultiplayerActive()) {
            return result;
        }

        try {
            java.lang.reflect.Field playersField = p2pManagerClass.getDeclaredField("players");
            playersField.setAccessible(true);
            Object players = playersField.get(null);

            if (players instanceof List) {
                List<?> playerList = (List<?>) players;

                for (Object p2pPlayer : playerList) {
                    if (p2pPlayer != null) {
                        // Get player ID
                        java.lang.reflect.Field idField = p2pPlayerClass.getDeclaredField("id");
                        idField.setAccessible(true);
                        Object id = idField.get(p2pPlayer);

                        // Get player class
                        java.lang.reflect.Field playerClassField = p2pPlayerClass.getDeclaredField("playerClass");
                        playerClassField.setAccessible(true);
                        Object playerClass = playerClassField.get(p2pPlayer);

                        if (id instanceof Integer && playerClass instanceof AbstractPlayer.PlayerClass) {
                            if (isBoardGameClass((AbstractPlayer.PlayerClass) playerClass)) {
                                result.add((Integer) id);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("Failed to get BG player IDs: " + e.getMessage());
        }

        return result;
    }

    /**
     * Gets the game seed from TogetherInSpire's P2PClientData.
     * This seed is shared across all players for deterministic behavior.
     *
     * @return the game seed, or null if not available
     */
    public static Long getGameSeed() {
        if (!isTogetherInSpireLoaded()) {
            return null;
        }

        try {
            // Get P2PManager.data (P2PClientData)
            java.lang.reflect.Field dataField = p2pManagerClass.getDeclaredField("data");
            dataField.setAccessible(true);
            Object clientData = dataField.get(null);

            if (clientData != null) {
                // Get P2PClientData.gameSeed
                Class<?> clientDataClass = Class.forName("spireTogether.network.P2P.P2PClientData");
                java.lang.reflect.Field gameSeedField = clientDataClass.getDeclaredField("gameSeed");
                gameSeedField.setAccessible(true);
                Object gameSeed = gameSeedField.get(clientData);

                if (gameSeed instanceof Long) {
                    return (Long) gameSeed;
                }
            }
        } catch (Exception e) {
            logger.warn("Failed to get game seed: " + e.getMessage());
        }

        return null;
    }

    /**
     * Gets the username for a specific player ID.
     *
     * @param playerId the player ID to look up
     * @return the player's username, or "Player " + playerId if not found
     */
    public static String getPlayerUsername(int playerId) {
        if (!isTogetherInSpireLoaded()) {
            return "Player " + playerId;
        }

        try {
            java.lang.reflect.Field playersField = p2pManagerClass.getDeclaredField("players");
            playersField.setAccessible(true);
            Object players = playersField.get(null);

            if (players instanceof List) {
                List<?> playerList = (List<?>) players;

                for (Object p2pPlayer : playerList) {
                    if (p2pPlayer != null) {
                        // Get player ID
                        java.lang.reflect.Field idField = p2pPlayerClass.getDeclaredField("id");
                        idField.setAccessible(true);
                        Object id = idField.get(p2pPlayer);

                        if (id instanceof Integer && (Integer) id == playerId) {
                            // Get username
                            java.lang.reflect.Field usernameField = p2pPlayerClass.getDeclaredField("username");
                            usernameField.setAccessible(true);
                            Object username = usernameField.get(p2pPlayer);

                            if (username instanceof String) {
                                return (String) username;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("Failed to get player username: " + e.getMessage());
        }

        return "Player " + playerId;
    }

    /**
     * Gets all player IDs currently in the session.
     *
     * @return list of player IDs
     */
    public static List<Integer> getAllPlayerIds() {
        List<Integer> result = new ArrayList<>();

        if (!isTogetherInSpireLoaded()) {
            result.add(0);
            return result;
        }

        try {
            java.lang.reflect.Field playersField = p2pManagerClass.getDeclaredField("players");
            playersField.setAccessible(true);
            Object players = playersField.get(null);

            if (players instanceof List) {
                List<?> playerList = (List<?>) players;

                for (Object p2pPlayer : playerList) {
                    if (p2pPlayer != null) {
                        java.lang.reflect.Field idField = p2pPlayerClass.getDeclaredField("id");
                        idField.setAccessible(true);
                        Object id = idField.get(p2pPlayer);

                        if (id instanceof Integer) {
                            result.add((Integer) id);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("Failed to get player IDs: " + e.getMessage());
        }

        if (result.isEmpty()) {
            result.add(0);
        }

        return result;
    }

    /**
     * Checks if the given creature is a CharacterEntity (remote player) from TogetherInSpire.
     * CharacterEntity extends AbstractMonster but represents an allied player.
     *
     * @param creature The creature to check
     * @return true if it's a CharacterEntity (remote player)
     */
    public static boolean isCharacterEntity(Object creature) {
        if (!isTogetherInSpireLoaded()) {
            return false;
        }

        try {
            Class<?> characterEntityClass = Class.forName("spireTogether.ui.CharacterEntity");
            return characterEntityClass.isInstance(creature);
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    /**
     * Gets the player ID associated with a CharacterEntity.
     *
     * @param characterEntity The CharacterEntity to get the ID from
     * @return The player ID, or -1 if not found
     */
    public static int getCharacterEntityPlayerId(Object characterEntity) {
        if (!isCharacterEntity(characterEntity)) {
            return -1;
        }

        try {
            Class<?> characterEntityClass = Class.forName("spireTogether.ui.CharacterEntity");
            java.lang.reflect.Field playerIdField = characterEntityClass.getDeclaredField("playerID");
            playerIdField.setAccessible(true);
            Object playerId = playerIdField.get(characterEntity);

            if (playerId instanceof Integer) {
                return (Integer) playerId;
            }
        } catch (Exception e) {
            logger.warn("Failed to get CharacterEntity player ID: " + e.getMessage());
        }

        return -1;
    }
}
