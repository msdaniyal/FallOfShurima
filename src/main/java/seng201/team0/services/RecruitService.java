// RecruitService.java (new file)
package seng201.team0.services;

import seng201.team0.models.Adventurer;
import seng201.team0.models.Faction;
import java.util.ArrayList;
import java.util.List;

public class RecruitService {
    public static List<Adventurer> generateRecruitPool(Faction playerFaction, int questIndex) {
        List<Adventurer> pool = new ArrayList<>();
        // Generate 3 random adventurers based on quest progression
        // (simplified example – expand as needed)
        pool.add(new Adventurer("Grom", 100, 15, 8, 25, Faction.NEUTRAL, playerFaction, "A wandering mercenary."));
        pool.add(new Adventurer("Lyra", 80, 20, 5, 30, Faction.XOLAANI, playerFaction, "Shadow mage."));
        pool.add(new Adventurer("Thorn", 120, 12, 12, 20, Faction.AATROX, playerFaction, "Shieldbearer."));
        return pool;
    }
}