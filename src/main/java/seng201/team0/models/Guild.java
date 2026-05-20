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
    private Adventurer mainCharacter;
    private boolean partyLocked;
    private List<Adventurer> mainParty;
    private List<Adventurer> reserves;
    private List<Adventurer> recruitPool;

    private int smallPotionCount;
    private int partyPotionCount;
    private int fullRestoreCount;

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
        this.mainCharacter = createMainCharacter(playerFaction);
        this.partyLocked = false;
        this.mainParty = new ArrayList<>();
        this.reserves = new ArrayList<>();
        this.recruitPool = new ArrayList<>();
        this.mainParty.add(mainCharacter);
        this.smallPotionCount = 0;
        this.partyPotionCount = 0;
        this.fullRestoreCount = 0;
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
     * @return The chosen main character for this guild.
     */
    public Adventurer getMainCharacter() {
        if (mainCharacter == null) {
            mainCharacter = createMainCharacter(playerFaction);
        }
        return mainCharacter;
    }

    /**
     * @return True if the given adventurer is the chosen Aatrox/Xolaani main character.
     */
    public boolean isMainCharacter(Adventurer adventurer) {
        return adventurer != null && isMainCharacterName(adventurer.getName());
    }

    /**
     * @return True if this name belongs to the chosen main character.
     */
    public boolean isMainCharacterName(String name) {
        return name != null && getMainCharacter().getName().equals(name);
    }

    /**
     * Creates the player's fixed main character from the setup faction choice.
     * The player then adds 1 to 4 companions around this character.
     */
    private Adventurer createMainCharacter(Faction faction) {
        if (faction == Faction.XOLAANI) {
            return new Adventurer(
                    "Xolaani", 135, 18, 10, 0,
                    Faction.XOLAANI, faction,
                    "Your chosen main character. A blood-weaving Darkin leader who anchors the guild."
            );
        }

        return new Adventurer(
                "Aatrox", 145, 19, 9, 0,
                Faction.AATROX, faction,
                "Your chosen main character. The World Ender leads the guild into battle."
        );
    }

    /**
     * Makes sure the main character is present at the front of the main party.
     */
    public void ensureMainCharacterInParty() {
        Adventurer mc = getMainCharacter();
        mainParty.removeIf(member -> member.getName().equals(mc.getName()));
        mainParty.add(0, mc);

        while (mainParty.size() > MAX_PARTY_SIZE) {
            mainParty.remove(mainParty.size() - 1);
        }
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
     * @return The number of small potion owned
     */
    public int getSmallPotionCount() {
        return smallPotionCount;
    }


    /**
     * @return The number of party potion owned
     */
    public int getPartyPotionCount() {
        return partyPotionCount;
    }

    /**
     * @return The number of full-restore potion owned
     */
    public int getFullRestoreCount() {
        return fullRestoreCount;
    }

    public void addSmallPotions(int amount) {
        smallPotionCount += amount;
    }

    public void addPartyPotions(int amount) {
        partyPotionCount += amount;
    }

    public void addFullRestores(int amount) {
        fullRestoreCount += amount;
    }

    public boolean useSmallPotion() {
        if (smallPotionCount <= 0) {
            return false;
        }
        smallPotionCount--;
        return true;
    }

    public boolean usePartyPotion() {
        if (partyPotionCount <= 0) {
            return false;
        }
        partyPotionCount--;
        return true;
    }

    public boolean useFullRestore() {
        if (fullRestoreCount <= 0) {
            return false;
        }
        fullRestoreCount--;
        return true;
    }

    public void healMainPartyToFull() {
        for (Adventurer adventurer : mainParty) {
            adventurer.resetHealth();
        }
    }

    public boolean hasAnyHealingPotions() {
        return smallPotionCount > 0 || partyPotionCount > 0 || fullRestoreCount > 0;
    }

    public boolean hasInjuredMainPartyMember() {
        for (Adventurer adventurer : mainParty) {
            if (!adventurer.isDead() && adventurer.getCurrentHealth() < adventurer.getMaxHealth()) {
                return true;
            }
        }
        return false;
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
        if (adventurer == null || isInCurrentMainParty(adventurer)) {
            return false;
        }

        if (mainParty.size() < MAX_PARTY_SIZE) {
            if (isMainCharacter(adventurer)) {
                mainParty.add(0, getMainCharacter());
            } else {
                mainParty.add(adventurer);
            }
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
     * Calculates the gold required to confirm a new main-party selection.
     * Adventurers already in the current main party are free to keep; newly selected
     * adventurers cost their pay value. This keeps spending logic in the model.
     * @param selectedParty The proposed main party
     * @return Total recruitment cost for newly added adventurers
     */
    public int calculatePartySelectionCost(List<Adventurer> selectedParty) {
        int totalCost = 0;

        for (Adventurer adventurer : selectedParty) {
            if (!isMainCharacter(adventurer) && !isInCurrentMainParty(adventurer)) {
                totalCost += adventurer.getPay();
            }
        }

        return totalCost;
    }

    /**
     * @param selectedParty Proposed party selection
     * @return The gold remaining if this selection is confirmed
     */
    public int previewGoldAfterPartySelection(List<Adventurer> selectedParty) {
        return gold - calculatePartySelectionCost(selectedParty);
    }

    /**
     * Replaces the current main party and spends gold only for newly hired members.
     * This method is the single source of truth for party confirmation spending.
     * @param selectedParty The proposed main party
     * @return True if the party was replaced and payment succeeded
     */
    public boolean replaceMainPartyWithSelection(List<Adventurer> selectedParty) {
        if (partyLocked || selectedParty == null || !containsMainCharacter(selectedParty)) {
            return false;
        }

        int selectedSize = selectedParty.size();
        if (selectedSize < 2 || selectedSize > MAX_PARTY_SIZE) {
            return false;
        }

        int cost = calculatePartySelectionCost(selectedParty);
        if (!spendGold(cost)) {
            return false;
        }

        mainParty.clear();
        mainParty.add(getMainCharacter());

        for (Adventurer adventurer : selectedParty) {
            if (!isMainCharacter(adventurer)) {
                addToMainParty(adventurer);
            }
        }

        return mainParty.size() >= 2 && mainParty.size() <= MAX_PARTY_SIZE;
    }

    private boolean containsMainCharacter(List<Adventurer> selectedParty) {
        for (Adventurer adventurer : selectedParty) {
            if (isMainCharacter(adventurer)) {
                return true;
            }
        }
        return false;
    }

    private boolean isInCurrentMainParty(Adventurer adventurer) {
        if (adventurer == null) {
            return false;
        }

        for (Adventurer member : mainParty) {
            if (member.getName().equals(adventurer.getName())) {
                return true;
            }
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
     * Removes all dead adventurers from the guild.
     * Called in combat loops.
     */
    public void removeDeadAdventurers() {
        mainParty.removeIf(Adventurer::isDead);
        reserves.removeIf(Adventurer::isDead);
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