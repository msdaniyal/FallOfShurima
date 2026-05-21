package seng201.team0.models;

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

    private int smallPotionCount;
    private int partyPotionCount;
    private int fullRestoreCount;

    private static final int MAX_PARTY_SIZE = 5;
    private static final int MAX_RESERVE_SIZE = 5;

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
        this.mainParty.add(mainCharacter);
        this.smallPotionCount = 0;
        this.partyPotionCount = 0;
        this.fullRestoreCount = 0;
    }

    public String getName() {
        return name;
    }

    public int getGold() {
        return gold;
    }

    public Faction getPlayerFaction() {
        return playerFaction;
    }

    public Adventurer getMainCharacter() {
        if (mainCharacter == null) {
            mainCharacter = createMainCharacter(playerFaction);
        }
        return mainCharacter;
    }

    public boolean isMainCharacter(Adventurer adventurer) {
        return adventurer != null && isMainCharacterName(adventurer.getName());
    }

    public boolean isMainCharacterName(String name) {
        return name != null && getMainCharacter().getName().equals(name);
    }

    /**
     * True when a companion has permanently left the game by death or abandonment.
     * These characters must not appear in Character Select, Shop, Barracks, or Quest 5 rival pools again.
     */
    public boolean isPermanentlyUnavailable(String name) {
        return name != null && permanentlyUnavailableCharacterNames.contains(name);
    }

    public boolean isPermanentlyUnavailable(Adventurer adventurer) {
        return adventurer != null && isPermanentlyUnavailable(adventurer.getName());
    }

    public List<String> getPermanentlyUnavailableCharacterNames() {
        return new ArrayList<>(permanentlyUnavailableCharacterNames);
    }

    private void markPermanentlyUnavailable(Adventurer adventurer) {
        if (adventurer == null || isMainCharacter(adventurer)) {
            return;
        }

        String name = adventurer.getName();
        if (!permanentlyUnavailableCharacterNames.contains(name)) {
            permanentlyUnavailableCharacterNames.add(name);
        }

        recruitPool.removeIf(candidate -> candidate.getName().equals(name));
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

    public void ensureMainCharacterInParty() {
        Adventurer mc = getMainCharacter();
        mainParty.removeIf(member -> member.getName().equals(mc.getName()));
        mainParty.add(0, mc);

        while (mainParty.size() > MAX_PARTY_SIZE) {
            mainParty.remove(mainParty.size() - 1);
        }
    }

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

    public boolean isPartyLocked() {
        return partyLocked;
    }

    public void lockParty() {
        this.partyLocked = true;
    }

    public List<Adventurer> getMainParty() {
        return mainParty;
    }

    public List<Adventurer> getReserves() {
        return reserves;
    }

    public List<Adventurer> getRecruitPool() {
        return recruitPool;
    }

    public int getSmallPotionCount() {
        return smallPotionCount;
    }

    public int getPartyPotionCount() {
        return partyPotionCount;
    }

    public int getFullRestoreCount() {
        return fullRestoreCount;
    }

    public void addSmallPotions(int amount) {
        smallPotionCount += Math.max(0, amount);
    }

    public void addPartyPotions(int amount) {
        partyPotionCount += Math.max(0, amount);
    }

    public void addFullRestores(int amount) {
        fullRestoreCount += Math.max(0, amount);
    }

    public boolean useSmallPotion() {
        if (smallPotionCount <= 0) return false;
        smallPotionCount--;
        return true;
    }

    public boolean usePartyPotion() {
        if (partyPotionCount <= 0) return false;
        partyPotionCount--;
        return true;
    }

    public boolean useFullRestore() {
        if (fullRestoreCount <= 0) return false;
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

    public void addGold(int amount) {
        this.gold += amount;
    }

    public boolean spendGold(int amount) {
        if (amount <= 0) return true;
        if (gold >= amount) {
            gold -= amount;
            return true;
        }
        return false;
    }

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

    public boolean addToReserves(Adventurer adventurer) {
        if (adventurer == null || reserves.size() >= MAX_RESERVE_SIZE || isPermanentlyUnavailable(adventurer)) return false;
        if (containsByName(reserves, adventurer.getName()) || containsByName(mainParty, adventurer.getName())) return false;
        reserves.add(adventurer);
        return true;
    }

    public boolean moveToMainParty(Adventurer adventurer) {
        if (partyLocked || adventurer == null || isMainCharacter(adventurer)) return false;
        if (reserves.contains(adventurer) && mainParty.size() < MAX_PARTY_SIZE) {
            reserves.remove(adventurer);
            mainParty.add(adventurer);
            return true;
        }
        return false;
    }

    public boolean moveToReserves(Adventurer adventurer) {
        if (partyLocked || adventurer == null || isMainCharacter(adventurer)) return false;
        if (mainParty.contains(adventurer) && mainParty.size() > 2 && reserves.size() < MAX_RESERVE_SIZE) {
            mainParty.remove(adventurer);
            reserves.add(adventurer);
            return true;
        }
        return false;
    }

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

    public int calculatePartySelectionCost(List<Adventurer> selectedParty) {
        int totalCost = 0;
        for (Adventurer adventurer : selectedParty) {
            if (!isMainCharacter(adventurer) && !isInCurrentMainParty(adventurer) && !isPermanentlyUnavailable(adventurer)) {
                totalCost += adventurer.getPay();
            }
        }
        return totalCost;
    }

    public int previewGoldAfterPartySelection(List<Adventurer> selectedParty) {
        return gold - calculatePartySelectionCost(selectedParty);
    }

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

    public void removeAbandoned() {
        Iterator<Adventurer> mainIterator = mainParty.iterator();
        while (mainIterator.hasNext()) {
            Adventurer adventurer = mainIterator.next();
            if (!isMainCharacter(adventurer) && adventurer.getAbandoned()) {
                markPermanentlyUnavailable(adventurer);
                mainIterator.remove();
            }
        }

        Iterator<Adventurer> reserveIterator = reserves.iterator();
        while (reserveIterator.hasNext()) {
            Adventurer adventurer = reserveIterator.next();
            if (adventurer.getAbandoned()) {
                markPermanentlyUnavailable(adventurer);
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
                markPermanentlyUnavailable(adventurer);
                mainIterator.remove();
            }
        }

        Iterator<Adventurer> reserveIterator = reserves.iterator();
        while (reserveIterator.hasNext()) {
            Adventurer adventurer = reserveIterator.next();
            if (adventurer.isDead()) {
                markPermanentlyUnavailable(adventurer);
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

    public void setRecruitPool(List<Adventurer> pool) {
        this.recruitPool = new ArrayList<>();
        if (pool == null) {
            return;
        }
        for (Adventurer adventurer : pool) {
            addToRecruitPoolIfMissing(adventurer);
        }
    }

    public boolean checkLoyaltyThreshold(int threshold) {
        return mainParty.stream().filter(a -> !a.isDead()).allMatch(a -> a.isLoyal(threshold));
    }

    public boolean isWiped() {
        for (Adventurer member : mainParty) {
            if (!member.isDead()) return false;
        }
        return true;
    }

    public boolean payParty() {
        int totalPay = mainParty.stream().mapToInt(Adventurer::getPay).sum();
        return spendGold(totalPay);
    }
}
