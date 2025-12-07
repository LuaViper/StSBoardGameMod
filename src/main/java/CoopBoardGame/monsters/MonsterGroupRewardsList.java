package CoopBoardGame.monsters;

import CoopBoardGame.monsters.bgexordium.BGCultist;
import java.util.HashMap;

public class MonsterGroupRewardsList {

    public static class MonsterGroupRewards {

        public int gold = 0;
        public boolean potion = false;
        public boolean relic = false;
        public boolean upgrade = false;

        public MonsterGroupRewards(int gold, boolean potion, boolean relic, boolean upgrade) {
            this.gold = gold;
            this.potion = potion;
            this.relic = relic;
            this.upgrade = upgrade;
        }

        public MonsterGroupRewards(int gold) {
            this(gold, false, false, false);
        }

        public MonsterGroupRewards(int gold, boolean potion) {
            this(gold, potion, false, false);
        }

        public MonsterGroupRewards(int gold, boolean potion, boolean relic) {
            this(gold, potion, relic, false);
        }
    }

    public static HashMap<String, MonsterGroupRewards> rewards;

    static {
        rewards = new HashMap<>();
        rewards.put(BGCultist.ID, new MonsterGroupRewards(1));
        rewards.put("CoopBoardGame:Jaw Worm (Easy)", new MonsterGroupRewards(1, true));
        rewards.put("CoopBoardGame:2 Louse", new MonsterGroupRewards(1));
        rewards.put("CoopBoardGame:Easy Small Slimes", new MonsterGroupRewards(0, true));

        rewards.put("CoopBoardGame:Cultist and SpikeSlime", new MonsterGroupRewards(1, true));
        rewards.put("CoopBoardGame:Cultist and Louse", new MonsterGroupRewards(1));
        rewards.put("CoopBoardGame:Fungi Beasts", new MonsterGroupRewards(1, true));
        rewards.put("CoopBoardGame:Slime Trio", new MonsterGroupRewards(1));
        rewards.put("CoopBoardGame:3 Louse (Hard)", new MonsterGroupRewards(1));
        rewards.put("CoopBoardGame:Large Slime", new MonsterGroupRewards(1, true));
        rewards.put("CoopBoardGame:Sneaky Gremlin Team", new MonsterGroupRewards(1, true));
        rewards.put("CoopBoardGame:Angry Gremlin Team", new MonsterGroupRewards(2));
        rewards.put("CoopBoardGame:Blue Slaver", new MonsterGroupRewards(2));
        rewards.put("CoopBoardGame:Red Slaver", new MonsterGroupRewards(1));
        rewards.put("CoopBoardGame:Looter", new MonsterGroupRewards(0, true));
        rewards.put("CoopBoardGame:Jaw Worm (Medium)", new MonsterGroupRewards(1));
        rewards.put("CoopBoardGame:A7 Looter and Acid Slime", new MonsterGroupRewards(0, true));
        rewards.put("CoopBoardGame:A7 Jaw Worm and Spike Slime", new MonsterGroupRewards(1));

        rewards.put("CoopBoardGame:Lagavulin", new MonsterGroupRewards(2, false, true));
        rewards.put("CoopBoardGame:Gremlin Nob", new MonsterGroupRewards(2, false, true));
        rewards.put("CoopBoardGame:3 Sentries", new MonsterGroupRewards(2, false, true));

        rewards.put("The Guardian", new MonsterGroupRewards(3));
        rewards.put("Hexaghost", new MonsterGroupRewards(3));
        rewards.put("Slime Boss", new MonsterGroupRewards(3));

        rewards.put("CoopBoardGame:Centurion A", new MonsterGroupRewards(1));
        rewards.put("CoopBoardGame:Centurion B", new MonsterGroupRewards(1, true));
        rewards.put("CoopBoardGame:Looter (Hard)", new MonsterGroupRewards(0, true));
        rewards.put("CoopBoardGame:Another Looter (Hard)", new MonsterGroupRewards(0, true));
        rewards.put("CoopBoardGame:Chosen and Cultist", new MonsterGroupRewards(1, true));
        rewards.put("CoopBoardGame:Chosen and Byrd", new MonsterGroupRewards(2));
        rewards.put("CoopBoardGame:3 Cultists", new MonsterGroupRewards(2));
        rewards.put("CoopBoardGame:3 Byrds", new MonsterGroupRewards(1));
        rewards.put("CoopBoardGame:SphericGuardian", new MonsterGroupRewards(1));
        rewards.put("CoopBoardGame:SnakePlant", new MonsterGroupRewards(1));
        rewards.put("CoopBoardGame:Snecko", new MonsterGroupRewards(1));
        rewards.put("CoopBoardGame:Shelled Parasite", new MonsterGroupRewards(0, true));
        rewards.put(
            "CoopBoardGame:A7 Shelled Parasite and Fungi Beast",
            new MonsterGroupRewards(0, true)
        );
        rewards.put("CoopBoardGame:A7 Spheric Guardian and Sentry A", new MonsterGroupRewards(1));

        rewards.put("CoopBoardGame:Book of Stabbing", new MonsterGroupRewards(2, false, true));
        rewards.put("CoopBoardGame:Gremlin Leader", new MonsterGroupRewards(2, false, true));
        rewards.put("CoopBoardGame:Taskmaster", new MonsterGroupRewards(2, false, true));

        rewards.put("Automaton", new MonsterGroupRewards(3));
        rewards.put("Champ", new MonsterGroupRewards(3));
        rewards.put("Collector", new MonsterGroupRewards(3));

        rewards.put("CoopBoardGame:Jaw Worms (Hard)", new MonsterGroupRewards(2));
        rewards.put("CoopBoardGame:Spire Growth", new MonsterGroupRewards(1, true));
        rewards.put("CoopBoardGame:Exploder and Friends", new MonsterGroupRewards(1));
        rewards.put("CoopBoardGame:Repulsor and Friends", new MonsterGroupRewards(0, true));
        rewards.put("CoopBoardGame:Orb Walker v2.3", new MonsterGroupRewards(1));
        rewards.put("CoopBoardGame:Orb Walker v3.2", new MonsterGroupRewards(1));
        rewards.put("CoopBoardGame:Transient", new MonsterGroupRewards(2));
        rewards.put("CoopBoardGame:Maw", new MonsterGroupRewards(1));
        rewards.put("CoopBoardGame:Writhing Mass", new MonsterGroupRewards(0, true));
        rewards.put("CoopBoardGame:3 Darklings", new MonsterGroupRewards(0, true));
        rewards.put("CoopBoardGame:Giant Head", new MonsterGroupRewards(3, false, true));
        rewards.put("CoopBoardGame:Nemesis", new MonsterGroupRewards(3, false, true));
        rewards.put("CoopBoardGame:Reptomancer", new MonsterGroupRewards(3, false, true));
        rewards.put("CoopBoardGame:A7 Exploder and Friends", new MonsterGroupRewards(1));
        rewards.put("CoopBoardGame:A7 Repulsor and Friends", new MonsterGroupRewards(0, true));

        rewards.put("CoopBoardGame:Shield and Spear", new MonsterGroupRewards(0, false, true));

        rewards.put("Time Eater", new MonsterGroupRewards(0));
        rewards.put("Awakened One", new MonsterGroupRewards(0));
        rewards.put("Donu and Deca", new MonsterGroupRewards(0));

        rewards.put("The Heart", new MonsterGroupRewards(0));
    }
}
