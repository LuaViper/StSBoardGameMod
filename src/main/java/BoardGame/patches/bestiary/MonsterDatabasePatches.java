package BoardGame.patches.bestiary;

import Bestiary.database.MonsterDatabase;
import Bestiary.database.MonsterInfo;
import BoardGame.BoardGame;
import BoardGame.multicharacter.grid.GridTile;
import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.google.gson.*;
import com.megacrit.cardcrawl.localization.RelicStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static BoardGame.BoardGame.getModID;

public class MonsterDatabasePatches {


    private static String resourceStreamToString(InputStream in) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String read;
        while ((read = br.readLine()) != null)
            sb.append(read);
        br.close();
        return sb.toString();
    }


    @SpirePatch2(clz = MonsterDatabase.class,method="load",
            optional = true)
    public static class MonsterConstructorPostfix {
        @SpirePostfixPatch
        public static void Foo(MonsterDatabase __instance) {
            InputStream in = __instance.getClass().getClassLoader().getResourceAsStream(
                    "BoardGameResources/localization/eng/Bestiary-Monsters.json");
            if (in == null) {
                System.out.println("ERROR: failed to load monsters.json (not found?)");
                return;
            }
            try {
                String content = resourceStreamToString(in);
                JsonObject o = (new JsonParser()).parse(content).getAsJsonObject();
                Gson gson = new Gson();
                if (o.has("monsters") && o.get("monsters").isJsonArray()) {
                    JsonArray arr = o.getAsJsonArray("monsters");
                    for (JsonElement elt : arr)
                        __instance.insert((MonsterInfo)gson.fromJson(elt, MonsterInfo.class));
                    //System.out.println("Monsters.size is " + __instance.monsters.size());
                }
            } catch (IOException e) {
                System.out.println("Failed to load resource stream to string");
                e.printStackTrace();
            }
        }
    }


}
