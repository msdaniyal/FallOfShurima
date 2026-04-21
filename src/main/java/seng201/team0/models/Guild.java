package seng201.team0.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the player's guild (warband).
 * Manages the main party, reserves, gold, and recruitment pool.
 * @author Mohammed, Xinyi
 */
public class Guild {

    private String name;
    private int gold;
    private Faction playerFaction;
    private boolean partyLocked;
    private List<Adventurer> mainParty;
    private List<Adventurer> reserves;
    private List<Adventurer> recruitPool;

    private static final int MAX_PARTY_SIZE = 5;
    private static final int MAX_RESERVE_SIZE = 5;

    /**
     * Constructs a Guild with a name, starting gold and the player's chosen faction.
     * @param name The guild's name (3-15 characters)
     * @param startingGold The amount of gold the guild starts with
     * @param playerFaction The MC faction chosen by the player (AATROX or XOLAANI)
     */
    public Guild(String name, int startingGold, Faction playerFaction) {
        this.name = name;
        this.gold = startingGold;
        this.playerFaction = playerFaction;
        this.partyLocked = false;
        this.mainParty = new ArrayList<>();
        this.reserves = new ArrayList<>();
        this.recruitPool = new ArrayList<>();
    }

    /**
     * @return The guild's name
     */
    public String getName() {
        return name;
    }

    /**
     * @return The guild's current gold
     */
    public int getGold() {
        return gold;
    }

    /**
     * @return The faction the player chose at setup
     */
    public Faction getPlayerFaction() {
        return playerFaction;
    }

    /**
     * @return True if the party is locked after Quest 5
     */
    public boolean isPartyLocked() {
        return partyLocked;
    }

    /**
     * Locks the party after Quest 5.
     * No recruiting or moving adventurers is allowed after this point.
     */
    public void lockParty() {
        this.partyLocked = true;
    }

    /**
     * @return The list of adventurers in the main party
     */
    public List<Adventurer> getMainParty() {
        return mainParty;
    }

    /**
     * @return The list of adventurers in reserves
     */
    public List<Adventurer> getReserves() {
        return reserves;
    }

    /**
     * @return The current recruit pool available for hiring
     */
    public List<Adventurer> getRecruitPool() {
        return recruitPool;
    }

    /**
     * Adds gold to the guild's treasury.
     * @param amount The amount of gold to add
     */
    public void addGold(int amount) {
        this.gold += amount;
    }

    /**
     * Spends gold from the guild's treasury.
     * @param amount The amount of gold to spend
     * @return True if the guild had enough gold, false otherwise
     */
    public boolean spendGold(int amount) {
        if (gold >= amount) {
            gold -= amount;
            return true;
        }
        return false;
    }

    /**
     * Adds an adventurer directly to the main party during setup.
     * Used for the three starting adventurers whose hiring cost is waived.
     * @param adventurer The adventurer to add
     * @return True if added successfully, false if party is full
     */
    public boolean addToMainParty(Adventurer adventurer) {
        if (mainParty.size() < MAX_PARTY_SIZE) {
            mainParty.add(adventurer);
            return true;
        }
        return false;
    }

    /**
     * Adds an adventurer to the reserves.
     * @param adventurer The adventurer to add
     * @return True if added successfully, false if reserves are full
     */
    public boolean addToReserves(Adventurer adventurer) {
        if (reserves.size() < MAX_RESERVE_SIZE) {
            reserves.add(adventurer);
            return true;
        }
        return false;
    }

    /**
     * Moves an adventurer from reserves to the main party.
     * Not allowed if party is locked.
     * @param adventurer The adventurer to move
     * @return True if moved successfully, false if locked, party full, or adventurer not in reserves
     */
    public boolean moveToMainParty(Adventurer adventurer) {
        if (partyLocked) return false;
        if (reserves.contains(adventurer) && mainParty.size() < MAX_PARTY_SIZE) {
            reserves.remove(adventurer);
            mainParty.add(adventurer);
            return true;
        }
        return false;
    }

    /**
     * Moves an adventurer from the main party to reserves.
     * The main party must always have at least one adventurer.
     * Not allowed if party is locked.
     * @param adventurer The adventurer to move
     * @return True if moved successfully, false if locked, would empty party, or reserves full
     */
    public boolean moveToReserves(Adventurer adventurer) {
        if (partyLocked) return false;
        if (mainParty.contains(adventurer) && mainParty.size() > 1
                && reserves.size() < MAX_RESERVE_SIZE) {
            mainParty.remove(adventurer);
            reserves.add(adventurer);
            return true;
        }
        return false;
    }

    /**
     * Recruits an adventurer from the recruit pool into the main party or reserves.
     * Deducts the adventurer's pay as a hiring fee.
     * Not allowed if party is locked.
     * @param adventurer The adventurer to recruit
     * @return True if recruited successfully, false if locked, not enough gold, or no space
     */
    public boolean recruit(Adventurer adventurer) {
        if (partyLocked) return false;
        if (!recruitPool.contains(adventurer)) return false;
        if (!spendGold(adventurer.getPay())) return false;
        recruitPool.remove(adventurer);
        if (mainParty.size() < MAX_PARTY_SIZE) {
            mainParty.add(adventurer);
        } else if (reserves.size() < MAX_RESERVE_SIZE) {
            reserves.add(adventurer);
        } else {
            return false;
        }
        return true;
    }

    /**
     * Retires an adventurer, removing them from the guild permanently.
     * Cannot retire if it would leave the main party empty.
     * Not allowed if party is locked.
     * @param adventurer The adventurer to retire
     * @return True if retired successfully, false otherwise
     */
    public boolean retire(Adventurer adventurer) {
        if (partyLocked) return false;
        if (mainParty.contains(adventurer)) {
            if (mainParty.size() <= 1) return false;
            mainParty.remove(adventurer);
            return true;
        }
        if (reserves.contains(adventurer)) {
            reserves.remove(adventurer);
            return true;
        }
        return false;
    }

    /**
     * Removes any adventurers who have abandoned the guild (loyalty reached 0).
     * Called after loyalty updates.
     */
    public void removeAbandoned() {
        mainParty.removeIf(Adventurer::getAbandoned);
        reserves.removeIf(Adventurer::getAbandoned);
    }

    /**
     * Collapses loyalty of all adventurers belonging to the opposing faction to 0.
     * Called at the end of Quest 5 (Faction War).
     * Triggers their abandoned flag and removes them from the guild.
     */
    public void collapseOpposingFaction() {
        for (Adventurer adventurer : mainParty) {
            if (adventurer.getFaction() != playerFaction
                    && adventurer.getFaction() != Faction.NEUTRAL) {
                adventurer.adjustLoyalty(-100);
            }
        }
        for (Adventurer adventurer : reserves) {
            if (adventurer.getFaction() != playerFaction
                    && adventurer.getFaction() != Faction.NEUTRAL) {
                adventurer.adjustLoyalty(-100);
            }
        }
        removeAbandoned();
    }

    /**
     * Sets the recruit pool for this guild.
     * Called by ShopService to refresh available recruits after each expedition.
     * @param pool The new list of recruitable adventurers
     */
    public void setRecruitPool(List<Adventurer> pool) {
        this.recruitPool = pool;
    }

    /**
     * Checks whether all adventurers in the main party have loyalty above the given threshold.
     * Used to determine whether the player faces Zoe or gets the true ending.
     * @param threshold The minimum loyalty required
     * @return True if all main party members meet the threshold
     */
    public boolean checkLoyaltyThreshold(int threshold) {
        return mainParty.stream().allMatch(a -> a.isLoyal(threshold));
    }

    /**
     * @return True if the main party is empty
     */
    public boolean isWiped() {
        return mainParty.isEmpty();
    }

    /**
     * Pays all adventurers in the main party their expedition fee.
     * @return True if the guild had enough gold to pay everyone, false otherwise
     */
    public boolean payParty() {
        int totalPay = mainParty.stream().mapToInt(Adventurer::getPay).sum();
        return spendGold(totalPay);
    }
}