package seng201.team76.models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Represents the player's guild (warband).
 * Manages the main party, reserves, gold, and recruitment pool.
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
    private List<String> permanentlyUnavailableCharacterNames;
    private List<DepartedAdventurerRecord> departedAdventurers;

    private int smallPotionCount;
    private int partyPotionCount;
    private int fullRestoreCount;

    private static final int MAX_PARTY_SIZE = 5;
    private static final int MAX_RESERVE_SIZE = 5;

    /** Fate used when an adventurer died. */
    public static final String FATE_FELL = "FELL";

    /** Fate used when an adventurer abandoned or left the guild. */
    public static final String FATE_LEFT = "LEFT";

    /**
     * Snapshot of a companion who permanently left the story through death or abandonment.
     * Kept so the final screen can describe everyone who was part of the journey,
     * not only the surviving party members.
     */
    public static class DepartedAdventurerRecord {
        private final String name;
        private final String fate;
        private final int loyalty;
        private final int madness;

        /**
         * Creates a departed adventurer record.
         *
         * @param name The adventurer name
         * @param fate How the adventurer left
         * @param loyalty Loyalty when they left
         * @param madness Madness when they left
         */
        public DepartedAdventurerRecord(String name, String fate, int loyalty, int madness) {
            this.name = name;
            this.fate = fate;
            this.loyalty = loyalty;
            this.madness = madness;
        }

        /**
         * Gets the adventurer name.
         *
         * @return The adventurer name
         */
        public String getName() {
            return name;
        }

        /**
         * Gets how the adventurer left.
         *
         * @return The fate value
         */
        public String getFate() {
            return fate;
        }

        /**
         * Gets the loyalty value when the adventurer left.
         *
         * @return The loyalty value
         */
        public int getLoyalty() {
            return loyalty;
        }

        /**
         * Gets the madness value when the adventurer left.
         *
         * @return The madness value
         */
        public int getMadness() {
            return madness;
        }
    }

    /**
     * Creates the player's guild and adds the main character.
     *
     * @param name The guild name
     * @param startingGold The amount of gold the guild starts with
     * @param playerFaction The faction chosen by the player
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
        this.permanentlyUnavailableCharacterNames = new ArrayList<>();
        this.departedAdventurers = new ArrayList<>();
        this.mainParty.add(mainCharacter);
        this.smallPotionCount = 0;
        this.partyPotionCount = 0;
        this.fullRestoreCount = 0;
    }

    /**
     * Gets the guild name.
     *
     * @return The guild name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the guild's current gold.
     *
     * @return The current gold
     */
    public int getGold() {
        return gold;
    }

    /**
     * Gets the player's chosen faction.
     *
     * @return The player faction
     */
    public Faction getPlayerFaction() {
        return playerFaction;
    }

    /**
     * Gets the main character, recreating it if needed.
     *
     * @return The main character
     */
    public Adventurer getMainCharacter() {
        if (mainCharacter == null) {
            mainCharacter = createMainCharacter(playerFaction);
        }
        return mainCharacter;
    }

    /**
     * Checks whether an adventurer is the main character.
     *
     * @param adventurer The adventurer to check
     * @return true if the adventurer is the main character
     */
    public boolean isMainCharacter(Adventurer adventurer) {
        return adventurer != null && isMainCharacterName(adventurer.getName());
    }

    /**
     * Checks whether a name belongs to the main character.
     *
     * @param name The name to check
     * @return true if the name is the main character's name
     */
    public boolean isMainCharacterName(String name) {
        return name != null && getMainCharacter().getName().equals(name);
    }

    /**
     * True when a companion has permanently left the game by death or abandonment.
     * These characters must not appear in Character Select, Shop, Barracks, or Quest 5 rival pools again.
     *
     * @param name The character name to check
     * @return true if the character is permanently unavailable
     */
    public boolean isPermanentlyUnavailable(String name) {
        return name != null && permanentlyUnavailableCharacterNames.contains(name);
    }

    /**
     * Checks whether an adventurer is permanently unavailable.
     *
     * @param adventurer The adventurer to check
     * @return true if the adventurer is permanently unavailable
     */
    public boolean isPermanentlyUnavailable(Adventurer adventurer) {
        return adventurer != null && isPermanentlyUnavailable(adventurer.getName());
    }

    /**
     * Gets the names of characters who cannot return.
     *
     * @return A copy of permanently unavailable character names
     */
    public List<String> getPermanentlyUnavailableCharacterNames() {
        return new ArrayList<>(permanentlyUnavailableCharacterNames);
    }

    private void markPermanentlyUnavailable(Adventurer adventurer) {
        markPermanentlyUnavailable(adventurer, FATE_LEFT);
    }

    private void markPermanentlyUnavailable(Adventurer adventurer, String fate) {
        if (adventurer == null || isMainCharacter(adventurer)) {
            return;
        }

        String name = adventurer.getName();
        if (!permanentlyUnavailableCharacterNames.contains(name)) {
            permanentlyUnavailableCharacterNames.add(name);
        }

        if (!hasDepartedRecord(name)) {
            departedAdventurers.add(new DepartedAdventurerRecord(
                    name,
                    fate,
                    adventurer.getLoyalty(),
                    adventurer.getMadness()
            ));
        }

        recruitPool.removeIf(candidate -> candidate.getName().equals(name));
    }

    private boolean hasDepartedRecord(String name) {
        for (DepartedAdventurerRecord record : departedAdventurers) {
            if (record.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets departed adventurer records for the ending screen.
     *
     * @return A copy of departed adventurer records
     */
    public List<DepartedAdventurerRecord> getDepartedAdventurers() {
        return new ArrayList<>(departedAdventurers);
    }

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
     * Makes sure the main character is first in the main party.
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
     * Checks whether the main character has fallen.
     *
     * @return true if the main character is dead
     */
    public boolean isMainCharacterDead() {
        return getMainCharacter().isDead();
    }

    /**
     * Used when the player returns to the map after the MC falls.
     * The quest itself is not completed, but the player is not soft-locked.
     */
    public void reviveMainCharacterForMenu() {
        getMainCharacter().resetHealth();
        ensureMainCharacterInParty();
    }

    /**
     * Checks whether party editing is locked.
     *
     * @return true if the party is locked
     */
    public boolean isPartyLocked() {
        return partyLocked;
    }

    /**
     * Locks party editing.
     */
    public void lockParty() {
        this.partyLocked = true;
    }

    /**
     * Gets the main party.
     *
     * @return The main party list
     */
    public List<Adventurer> getMainParty() {
        return mainParty;
    }

    /**
     * Gets the reserve adventurers.
     *
     * @return The reserve list
     */
    public List<Adventurer> getReserves() {
        return reserves;
    }

    /**
     * Gets the recruit pool.
     *
     * @return The recruit pool
     */
    public List<Adventurer> getRecruitPool() {
        return recruitPool;
    }

    /**
     * Gets the number of Silver Potions.
     *
     * @return The Silver Potion count
     */
    public int getSmallPotionCount() {
        return smallPotionCount;
    }

    /**
     * Gets the number of Gold Potions.
     *
     * @return The Gold Potion count
     */
    public int getPartyPotionCount() {
        return partyPotionCount;
    }

    /**
     * Gets the number of Purple Potions.
     *
     * @return The Purple Potion count
     */
    public int getFullRestoreCount() {
        return fullRestoreCount;
    }

    /**
     * Adds Silver Potions to the guild inventory.
     *
     * @param amount The amount to add
     */
    public void addSmallPotions(int amount) {
        smallPotionCount += Math.max(0, amount);
    }

    /**
     * Adds Gold Potions to the guild inventory.
     *
     * @param amount The amount to add
     */
    public void addPartyPotions(int amount) {
        partyPotionCount += Math.max(0, amount);
    }

    /**
     * Adds Purple Potions to the guild inventory.
     *
     * @param amount The amount to add
     */
    public void addFullRestores(int amount) {
        fullRestoreCount += Math.max(0, amount);
    }

    /**
     * Uses one Silver Potion if available.
     *
     * @return true if a potion was used
     */
    public boolean useSmallPotion() {
        if (smallPotionCount <= 0) return false;
        smallPotionCount--;
        return true;
    }

    /**
     * Uses one Gold Potion if available.
     *
     * @return true if a potion was used
     */
    public boolean usePartyPotion() {
        if (partyPotionCount <= 0) return false;
        partyPotionCount--;
        return true;
    }

    /**
     * Uses one Purple Potion if available.
     *
     * @return true if a potion was used
     */
    public boolean useFullRestore() {
        if (fullRestoreCount <= 0) return false;
        fullRestoreCount--;
        return true;
    }

    /**
     * Fully heals every adventurer in the main party.
     */
    public void healMainPartyToFull() {
        for (Adventurer adventurer : mainParty) {
            adventurer.resetHealth();
        }
    }

    /**
     * Checks whether the guild has any healing potions.
     *
     * @return true if at least one potion is available
     */
    public boolean hasAnyHealingPotions() {
        return smallPotionCount > 0 || partyPotionCount > 0 || fullRestoreCount > 0;
    }

    /**
     * Checks whether a living main party member is injured.
     *
     * @return true if someone can be healed
     */
    public boolean hasInjuredMainPartyMember() {
        for (Adventurer adventurer : mainParty) {
            if (!adventurer.isDead() && adventurer.getCurrentHealth() < adventurer.getMaxHealth()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Adds gold to the guild.
     *
     * @param amount The amount of gold to add
     */
    public void addGold(int amount) {
        this.gold += amount;
    }

    /**
     * Spends gold if the guild can afford it.
     *
     * @param amount The amount of gold to spend
     * @return true if the gold was spent
     */
    public boolean spendGold(int amount) {
        if (amount <= 0) return true;
        if (gold >= amount) {
            gold -= amount;
            return true;
        }
        return false;
    }

    /**
     * Adds an adventurer to the main party if there is room.
     *
     * @param adventurer The adventurer to add
     * @return true if the adventurer was added
     */
    public boolean addToMainParty(Adventurer adventurer) {
        if (adventurer == null || isInCurrentMainParty(adventurer) || isPermanentlyUnavailable(adventurer)) return false;
        if (mainParty.size() >= MAX_PARTY_SIZE) return false;

        if (isMainCharacter(adventurer)) {
            mainParty.add(0, getMainCharacter());
        } else {
            mainParty.add(adventurer);
        }
        return true;
    }

    /**
     * Adds an adventurer to reserves if there is room.
     *
     * @param adventurer The adventurer to add
     * @return true if the adventurer was added
     */
    public boolean addToReserves(Adventurer adventurer) {
        if (adventurer == null || reserves.size() >= MAX_RESERVE_SIZE || isPermanentlyUnavailable(adventurer)) return false;
        if (containsByName(reserves, adventurer.getName()) || containsByName(mainParty, adventurer.getName())) return false;
        reserves.add(adventurer);
        return true;
    }

    /**
     * Moves an adventurer from reserves to the main party.
     *
     * @param adventurer The adventurer to move
     * @return true if the move succeeded
     */
    public boolean moveToMainParty(Adventurer adventurer) {
        if (partyLocked || adventurer == null || isMainCharacter(adventurer)) return false;
        if (reserves.contains(adventurer) && mainParty.size() < MAX_PARTY_SIZE) {
            reserves.remove(adventurer);
            mainParty.add(adventurer);
            return true;
        }
        return false;
    }

    /**
     * Moves an adventurer from the main party to reserves.
     *
     * @param adventurer The adventurer to move
     * @return true if the move succeeded
     */
    public boolean moveToReserves(Adventurer adventurer) {
        if (partyLocked || adventurer == null || isMainCharacter(adventurer)) return false;
        if (mainParty.contains(adventurer) && mainParty.size() > 2 && reserves.size() < MAX_RESERVE_SIZE) {
            mainParty.remove(adventurer);
            reserves.add(adventurer);
            return true;
        }
        return false;
    }

    /**
     * Recruits an adventurer from the recruit pool.
     *
     * @param adventurer The adventurer to recruit
     * @return true if recruitment succeeded
     */
    public boolean recruit(Adventurer adventurer) {
        if (partyLocked || adventurer == null || isPermanentlyUnavailable(adventurer)) return false;
        if (!recruitPool.contains(adventurer)) return false;
        if (!spendGold(adventurer.getPay())) return false;

        recruitPool.remove(adventurer);
        adventurer.resetHealth();

        if (mainParty.size() < MAX_PARTY_SIZE) {
            mainParty.add(adventurer);
        } else if (reserves.size() < MAX_RESERVE_SIZE) {
            reserves.add(adventurer);
        } else {
            addToRecruitPoolIfMissing(adventurer);
            return false;
        }
        return true;
    }

    /**
     * Removes an adventurer from the guild if allowed.
     *
     * @param adventurer The adventurer to retire
     * @return true if the adventurer was retired
     */
    public boolean retire(Adventurer adventurer) {
        if (partyLocked || adventurer == null || isMainCharacter(adventurer)) return false;
        if (mainParty.contains(adventurer)) {
            if (mainParty.size() <= 2) return false;
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
     * Calculates the cost of a selected party.
     *
     * @param selectedParty The selected party preview
     * @return The total recruitment cost
     */
    public int calculatePartySelectionCost(List<Adventurer> selectedParty) {
        int totalCost = 0;
        for (Adventurer adventurer : selectedParty) {
            if (!isMainCharacter(adventurer) && !isInCurrentMainParty(adventurer) && !isPermanentlyUnavailable(adventurer)) {
                totalCost += adventurer.getPay();
            }
        }
        return totalCost;
    }

    /**
     * Previews gold after buying new selected party members.
     *
     * @param selectedParty The selected party preview
     * @return Gold remaining after the selection
     */
    public int previewGoldAfterPartySelection(List<Adventurer> selectedParty) {
        return gold - calculatePartySelectionCost(selectedParty);
    }

    /**
     * Replaces the main party with the selected adventurers.
     *
     * @param selectedParty The new selected party
     * @return true if the replacement succeeded
     */
    public boolean replaceMainPartyWithSelection(List<Adventurer> selectedParty) {
        if (partyLocked || selectedParty == null || !containsMainCharacter(selectedParty)) return false;

        int selectedSize = selectedParty.size();
        if (selectedSize < 2 || selectedSize > MAX_PARTY_SIZE) return false;

        for (Adventurer adventurer : selectedParty) {
            if (!isMainCharacter(adventurer) && isPermanentlyUnavailable(adventurer)) {
                return false;
            }
        }

        int cost = calculatePartySelectionCost(selectedParty);
        if (!spendGold(cost)) return false;

        mainParty.clear();
        mainParty.add(getMainCharacter());

        for (Adventurer adventurer : selectedParty) {
            if (!isMainCharacter(adventurer)) {
                addToMainParty(adventurer);
                recruitPool.removeIf(candidate -> candidate.getName().equals(adventurer.getName()));
            }
        }

        return mainParty.size() >= 2 && mainParty.size() <= MAX_PARTY_SIZE;
    }

    private boolean containsMainCharacter(List<Adventurer> selectedParty) {
        for (Adventurer adventurer : selectedParty) {
            if (isMainCharacter(adventurer)) return true;
        }
        return false;
    }

    private boolean isInCurrentMainParty(Adventurer adventurer) {
        if (adventurer == null) return false;
        return containsByName(mainParty, adventurer.getName());
    }

    /**
     * Checks whether the guild already has a member with this name.
     *
     * @param name The character name
     * @return true if the name is in the main party or reserves
     */
    public boolean hasMemberNamed(String name) {
        return containsByName(mainParty, name) || containsByName(reserves, name);
    }

    private boolean containsByName(List<Adventurer> list, String name) {
        if (name == null) return false;
        for (Adventurer adventurer : list) {
            if (name.equals(adventurer.getName())) return true;
        }
        return false;
    }

    /**
     * Removes companions who have abandoned the guild.
     */
    public void removeAbandoned() {
        Iterator<Adventurer> mainIterator = mainParty.iterator();
        while (mainIterator.hasNext()) {
            Adventurer adventurer = mainIterator.next();
            if (!isMainCharacter(adventurer) && adventurer.getAbandoned()) {
                markPermanentlyUnavailable(adventurer, FATE_LEFT);
                mainIterator.remove();
            }
        }

        Iterator<Adventurer> reserveIterator = reserves.iterator();
        while (reserveIterator.hasNext()) {
            Adventurer adventurer = reserveIterator.next();
            if (adventurer.getAbandoned()) {
                markPermanentlyUnavailable(adventurer, FATE_LEFT);
                reserveIterator.remove();
            }
        }

        ensureMainCharacterInParty();
    }

    /**
     * Dead companions are permanently gone. They leave the active party/reserves
     * and are removed from the recruit pool so they cannot be hired again.
     * The main character is not removed; the controller stops the quest immediately
     * when the MC hits 0 HP and lets the player restart the quest.
     */
    public void removeDeadAdventurers() {
        Iterator<Adventurer> mainIterator = mainParty.iterator();
        while (mainIterator.hasNext()) {
            Adventurer adventurer = mainIterator.next();
            if (adventurer.isDead() && !isMainCharacter(adventurer)) {
                markPermanentlyUnavailable(adventurer, FATE_FELL);
                mainIterator.remove();
            }
        }

        Iterator<Adventurer> reserveIterator = reserves.iterator();
        while (reserveIterator.hasNext()) {
            Adventurer adventurer = reserveIterator.next();
            if (adventurer.isDead()) {
                markPermanentlyUnavailable(adventurer, FATE_FELL);
                reserveIterator.remove();
            }
        }
    }

    private void addToRecruitPoolIfMissing(Adventurer adventurer) {
        if (adventurer == null || isMainCharacter(adventurer) || isPermanentlyUnavailable(adventurer)) return;
        if (!containsByName(recruitPool, adventurer.getName()) && !hasMemberNamed(adventurer.getName())) {
            recruitPool.add(adventurer);
        }
    }

    /**
     * Drops loyalty for companions from the opposing faction.
     */
    public void collapseOpposingFaction() {
        for (Adventurer adventurer : mainParty) {
            if (!isMainCharacter(adventurer)
                    && adventurer.getFaction() != playerFaction
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
     * Replaces the recruit pool, skipping unavailable characters.
     *
     * @param pool The new recruit pool
     */
    public void setRecruitPool(List<Adventurer> pool) {
        this.recruitPool = new ArrayList<>();
        if (pool == null) {
            return;
        }
        for (Adventurer adventurer : pool) {
            addToRecruitPoolIfMissing(adventurer);
        }
    }

    /**
     * Checks whether every living party member is loyal enough.
     *
     * @param threshold The required loyalty value
     * @return true if every living member meets the threshold
     */
    public boolean checkLoyaltyThreshold(int threshold) {
        return mainParty.stream().filter(a -> !a.isDead()).allMatch(a -> a.isLoyal(threshold));
    }

    /**
     * Checks whether all main party members are dead.
     *
     * @return true if the party is wiped
     */
    public boolean isWiped() {
        for (Adventurer member : mainParty) {
            if (!member.isDead()) return false;
        }
        return true;
    }

    /**
     * Pays the party's expedition cost.
     *
     * @return true if the guild had enough gold
     */
    public boolean payParty() {
        int totalPay = mainParty.stream().mapToInt(Adventurer::getPay).sum();
        return spendGold(totalPay);
    }
}
