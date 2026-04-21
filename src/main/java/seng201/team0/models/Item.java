package seng201.team0.models;

/**
 * Represents the items purchasable items for the shop
 * Three types:
 * 1. Single Adventurer
 * 2. Whole Party
 * 3. Full Health Full Party
 * @author Mohammed, Xinyi
 */

public class Item {

    private String name;
    private ItemType type;
    private int healAmount;
    private int cost;
    private String description;

    /**
     * Constructs a Potion.
     * @param name The potion's display name
     * @param type The tier of potion (SINGLE, PARTY, FULL)
     * @param healAmount The amount of health restored (ignored for FULL type)
     * @param cost The gold cost in the shop
     * @param description Short description shown in the shop UI
     */
    public Item(String name, ItemType type, int healAmount, int cost, String description) {
        this.name = name;
        this.type = type;
        this.healAmount = healAmount;
        this.cost = cost;
        this.description = description;
    }

    /**
     * @return The potion's display name
     */
    public String getName() {
        return name;
    }

    /**
     * @return The potion's type
     */
    public ItemType getType() {
        return type;
    }

    /**
     * @return The amount of health this potion restores
     */
    public int getHealAmount() {
        return healAmount;
    }

    /**
     * @return The gold cost of this potion
     */
    public int getCost() {
        return cost;
    }

    /**
     * @return The potion's description
     */
    public String getDescription() {
        return description;
    }

    public void use(Guild guild, Adventurer target) {
        switch (type) {
            case SINGLE:
                if (target != null) {
                    target.setCurrentHealth(target.getCurrentHealth() + healAmount);
                }
                break;
            case PARTY:
                for (Adventurer adventurer : guild.getMainParty()) {
                    adventurer.setCurrentHealth(adventurer.getCurrentHealth() + healAmount);
                }
                break;
            case FULL:
                for (Adventurer adventurer : guild.getMainParty()) {
                    adventurer.resetHealth();
                }
                break;
        }
    }
}

