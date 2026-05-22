package seng201.team76.models;

/**
 * Shows the faction allegiance of an adventurer.
 * Determines starting loyalty based on the player's chosen MC.
 * @author Mohammed
 */
public enum Faction {
    /** Aatrox's faction. */
    AATROX(80, 30),

    /** Xolaani's faction. */
    XOLAANI(80, 30),

    /** Neutral faction with no strong side. */
    NEUTRAL(50, 50);

    private final int loyaltyIfAllied;
    private final int loyaltyIfOpposed;

    /**
     * Constructs a Faction with loyalty values for both scenarios.
     * @param loyaltyIfAllied Starting loyalty when the player chose this faction
     * @param loyaltyIfOpposed Starting loyalty when the player chose the opposing faction
     */
    Faction(int loyaltyIfAllied, int loyaltyIfOpposed) {
        this.loyaltyIfAllied = loyaltyIfAllied;
        this.loyaltyIfOpposed = loyaltyIfOpposed;
    }

    /**
     * Returns the starting loyalty for an adventurer of this faction
     * based on the player's chosen MC faction.
     * @param playerFaction The faction the player chose at setup
     * @return The starting loyalty value
     */
    public int getStartingLoyalty(Faction playerFaction) {
        return this == playerFaction ? loyaltyIfAllied : loyaltyIfOpposed;
    }
}
