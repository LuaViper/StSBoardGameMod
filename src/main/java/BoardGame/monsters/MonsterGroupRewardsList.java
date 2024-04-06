
package BoardGame.monsters;

import BoardGame.monsters.bgexordium.BGCultist;

import java.util.HashMap;

public class MonsterGroupRewardsList {
    public static class MonsterGroupRewards{
        public int gold=0;
        public boolean potion=false;
        public boolean relic=false;
        public boolean upgrade=false;
        public MonsterGroupRewards(int gold, boolean potion, boolean relic, boolean upgrade){
            this.gold=gold;this.potion=potion;this.relic=relic;this.upgrade=upgrade;
        }
        public MonsterGroupRewards(int gold){
            this(gold,false,false,false);
        }
        public MonsterGroupRewards(int gold, boolean potion){
            this(gold,potion,false,false);
        }
        public MonsterGroupRewards(int gold, boolean potion, boolean relic){
            this(gold,potion,relic,false);
        }
    }
    public static HashMap<String, MonsterGroupRewards> rewards;
    static {
        rewards=new HashMap<>();
        rewards.put(BGCultist.ID,new MonsterGroupRewards(1));
        rewards.put("BoardGame:Jaw Worm (Easy)",new MonsterGroupRewards(1,true));
        rewards.put("BoardGame:2 Louse",new MonsterGroupRewards(1));
        rewards.put("BoardGame:Easy Small Slimes",new MonsterGroupRewards(0,true));

        rewards.put("BoardGame:Cultist and SpikeSlime",new MonsterGroupRewards(1,true));
        rewards.put("BoardGame:Cultist and Louse",new MonsterGroupRewards(1));
        rewards.put("BoardGame:Fungi Beasts",new MonsterGroupRewards(1,true));
        rewards.put("BoardGame:Slime Trio",new MonsterGroupRewards(1));
        rewards.put("BoardGame:3 Louse (Hard)",new MonsterGroupRewards(1));
        rewards.put("BoardGame:Large Slime",new MonsterGroupRewards(1, true));
        rewards.put("BoardGame:Sneaky Gremlin Team",new MonsterGroupRewards(1, true));
        rewards.put("BoardGame:Angry Gremlin Team",new MonsterGroupRewards(2));
        rewards.put("BoardGame:Blue Slaver",new MonsterGroupRewards(2));
        rewards.put("BoardGame:Red Slaver",new MonsterGroupRewards(1));
        rewards.put("BoardGame:Looter",new MonsterGroupRewards(0,true));
        rewards.put("BoardGame:Jaw Worm (Medium)",new MonsterGroupRewards(1));
        rewards.put("BoardGame:A7 Looter and Acid Slime",new MonsterGroupRewards(0,true));
        rewards.put("BoardGame:A7 Jaw Worm and Spike Slime",new MonsterGroupRewards(1));

        rewards.put("BoardGame:Lagavulin",new MonsterGroupRewards(2,false,true));
        rewards.put("BoardGame:Gremlin Nob",new MonsterGroupRewards(2,false,true));
        rewards.put("BoardGame:3 Sentries",new MonsterGroupRewards(2,false,true));

        rewards.put("The Guardian",new MonsterGroupRewards(3));
        rewards.put("Hexaghost",new MonsterGroupRewards(3));
        rewards.put("Slime Boss",new MonsterGroupRewards(3));

        rewards.put("BoardGame:Centurion A",new MonsterGroupRewards(1));
        rewards.put("BoardGame:Centurion B",new MonsterGroupRewards(1,true));
        rewards.put("BoardGame:Looter (Hard)",new MonsterGroupRewards(0,true));
        rewards.put("BoardGame:Another Looter (Hard)",new MonsterGroupRewards(0,true));
        rewards.put("BoardGame:Chosen and Cultist",new MonsterGroupRewards(1,true));
        rewards.put("BoardGame:Chosen and Byrd",new MonsterGroupRewards(2));
        rewards.put("BoardGame:3 Cultists",new MonsterGroupRewards(2));
        rewards.put("BoardGame:3 Byrds",new MonsterGroupRewards(1));
        rewards.put("BoardGame:SphericGuardian",new MonsterGroupRewards(1));
        rewards.put("BoardGame:SnakePlant",new MonsterGroupRewards(1));
        rewards.put("BoardGame:Snecko",new MonsterGroupRewards(1));
        rewards.put("BoardGame:Shelled Parasite",new MonsterGroupRewards(0,true));
        rewards.put("BoardGame:A7 Shelled Parasite and Fungi Beast",new MonsterGroupRewards(0,true));
        rewards.put("BoardGame:A7 Spheric Guardian and Sentry A",new MonsterGroupRewards(1));

        rewards.put("BoardGame:Book of Stabbing",new MonsterGroupRewards(2,false, true));
        rewards.put("BoardGame:Gremlin Leader",new MonsterGroupRewards(2,false, true));
        rewards.put("BoardGame:Taskmaster",new MonsterGroupRewards(2,false, true));

        rewards.put("Automaton",new MonsterGroupRewards(3));
        rewards.put("Champ",new MonsterGroupRewards(3));
        rewards.put("Collector",new MonsterGroupRewards(3));

        rewards.put("BoardGame:Jaw Worms (Hard)",new MonsterGroupRewards(2));
        rewards.put("BoardGame:Spire Growth",new MonsterGroupRewards(1,true));
        rewards.put("BoardGame:Exploder and Friends",new MonsterGroupRewards(1));
        rewards.put("BoardGame:Repulsor and Friends",new MonsterGroupRewards(0,true));
        rewards.put("BoardGame:Orb Walker v2.3",new MonsterGroupRewards(1));
        rewards.put("BoardGame:Orb Walker v3.2",new MonsterGroupRewards(1));
        rewards.put("BoardGame:Transient",new MonsterGroupRewards(2));
        rewards.put("BoardGame:Maw",new MonsterGroupRewards(1));
        rewards.put("BoardGame:Writhing Mass",new MonsterGroupRewards(0,true));
        rewards.put("BoardGame:3 Darklings",new MonsterGroupRewards(0,true));
        rewards.put("BoardGame:Giant Head",new MonsterGroupRewards(3,false, true));
        rewards.put("BoardGame:Nemesis",new MonsterGroupRewards(3,false, true));
        rewards.put("BoardGame:Reptomancer",new MonsterGroupRewards(3,false, true));
        rewards.put("BoardGame:A7 Exploder and Friends",new MonsterGroupRewards(1));
        rewards.put("BoardGame:A7 Repulsor and Friends",new MonsterGroupRewards(0,true));

        rewards.put("Time Eater",new MonsterGroupRewards(0));
        rewards.put("Awakened One",new MonsterGroupRewards(0));
        rewards.put("Donu and Deca",new MonsterGroupRewards(0));

        rewards.put("The Heart",new MonsterGroupRewards(0));

    }
}
