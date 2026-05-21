package seng201.team0.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import seng201.team0.models.Adventurer;
import seng201.team0.models.Boss;
import seng201.team0.models.BossFight;
import seng201.team0.models.Game;
import seng201.team0.models.Guild;
import seng201.team0.models.ItemType;
import seng201.team0.models.Quest;
import seng201.team0.models.Quest1;
import seng201.team0.models.Quest5;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Generic battlefield controller for all boss fights.
 *
 * Menu:
 * - Attack: opens MemoryGameController.
 * - Change Member: opens a small party switch popup.
 * - Special Move: enabled only for the selected main character and once per fight.
 *
 * Defend button has been removed.
 * Boss attacks now automatically launch the DiceRollController after the player attacks.
 */
public class BossFightController implements GameDataReceiver {

    @FXML private AnchorPane rootPane;
    @FXML private Pane contentPane;
    @FXML private ImageView backgroundImage;

    @FXML private VBox partyBox;

    @FXML private Label bossNameLabel;
    @FXML private Label bossHpLabel;
    @FXML private ProgressBar bossHpBar;
    @FXML private ImageView bossImage;

    @FXML private Label activeMemberLabel;
    @FXML private Label activeMemberStatsLabel;
    @FXML private ProgressBar activeMemberHpBar;
    @FXML private ImageView activeMemberImage;

    @FXML private Label roundLabel;
    @FXML private Label dialogueLabel;

    @FXML private Button attackButton;
    @FXML private Button healButton;
    @FXML private Button changeMemberButton;
    @FXML private Button specialMoveButton;
    @FXML private Button restartQuestButton;

    @FXML private Pane changeMemberOverlay;
    @FXML private VBox changeMemberPopup;
    @FXML private VBox memberListBox;

    @FXML private VBox healPopup;
    @FXML private Label healPotionCountsLabel;
    @FXML private Button smallPotionHealButton;
    @FXML private Button partyPotionHealButton;
    @FXML private Button fullRestoreHealButton;

    private Game game;
    private Guild guild;
    private Quest currentQuest;
    private List<BossFight> bossFights = new ArrayList<>();

    private BossFight currentFight;
    private Adventurer currentAttacker;
    private Adventurer mainCharacter;

    private int fightIndex = 0;
    private int attackerIndex = 0;
    private boolean battleEnded = false;
    private boolean questFailedBecauseMainCharacterDied = false;

    private final List<Adventurer> questStartParty = new ArrayList<>();
    private final Map<String, Integer> questStartHealth = new HashMap<>();

    private String pendingAttackMessage = "";

    @FXML
    public void initialize() {
        ScreenUtil.setupStretch(rootPane, backgroundImage, contentPane);

        if (changeMemberOverlay != null) {
            changeMemberOverlay.setVisible(false);
        }

        if (changeMemberPopup != null) {
            changeMemberPopup.setVisible(false);
        }

        if (healPopup != null) {
            healPopup.setVisible(false);
        }

        if (restartQuestButton != null) {
            restartQuestButton.setVisible(false);
            restartQuestButton.setDisable(true);
        }
    }

    public void setGameData(Game game) {
        this.game = game;
        this.guild = game.getGuild();

        this.currentQuest = game.getCurrentQuest();
        if (this.currentQuest == null) {
            this.currentQuest = new Quest1(game.getDifficulty());
        }

        this.bossFights = currentQuest.getBossFights();
        this.fightIndex = currentQuest.getFirstUnfinishedFightIndex(guild);
        this.attackerIndex = 0;
        this.battleEnded = false;
        this.questFailedBecauseMainCharacterDied = false;

        this.mainCharacter = findMainCharacter();
        saveQuestStartState();

        startCurrentFight();
    }

    private Adventurer findMainCharacter() {
        return guild == null ? null : guild.getMainCharacter();
    }

    private void saveQuestStartState() {
        questStartParty.clear();
        questStartHealth.clear();

        if (guild == null) {
            return;
        }

        for (Adventurer member : guild.getMainParty()) {
            questStartParty.add(member);
            questStartHealth.put(member.getName(), member.getCurrentHealth());
        }
    }

    private void restoreQuestStartState() {
        if (guild == null) {
            return;
        }

        guild.getMainParty().clear();

        for (Adventurer member : questStartParty) {
            Integer savedHealth = questStartHealth.get(member.getName());
            if (savedHealth == null || savedHealth <= 0) {
                member.resetHealth();
            } else {
                member.setCurrentHealth(savedHealth);
            }
            guild.getMainParty().add(member);
        }

        guild.ensureMainCharacterInParty();
        guild.getRecruitPool().removeIf(candidate -> containsQuestStartMember(candidate.getName()));
    }

    private boolean containsQuestStartMember(String name) {
        for (Adventurer member : questStartParty) {
            if (member.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    private void startCurrentFight() {
        if (restartQuestButton != null) {
            restartQuestButton.setVisible(false);
            restartQuestButton.setDisable(true);
        }

        if (fightIndex >= bossFights.size()) {
            finishQuest();
            return;
        }

        currentFight = bossFights.get(fightIndex);
        attackerIndex = 0;

        chooseNextLivingAttacker();
        updateBattlefieldBackground();
        updateAllDisplays();

        dialogueLabel.getStyleClass().remove("special-effect-text");
        if (fightIndex > 0) {
            dialogueLabel.setText("Next phase: " + currentFight.getBoss().getName() + " enters the fight.");
        } else {
            dialogueLabel.setText(currentFight.getBoss().getName() + " stands before you.");
        }
        checkAndShowBossAbility();
    }

    private void chooseNextLivingAttacker() {
        List<Adventurer> party = guild.getMainParty();

        if (party.isEmpty()) {
            currentAttacker = null;
            return;
        }

        if (attackerIndex >= party.size()) {
            attackerIndex = 0;
        }

        for (int i = 0; i < party.size(); i++) {
            Adventurer candidate = party.get((attackerIndex + i) % party.size());

            if (!candidate.isDead() && currentFight.canAdventurerAct(candidate)) {
                currentAttacker = candidate;
                attackerIndex = (attackerIndex + i) % party.size();
                return;
            }
        }

        currentAttacker = currentFight.findWeakestTarget(party);
    }

    private void updateBattlefieldBackground() {
        String bossName = safeName(currentFight.getBoss().getName());

        if ("zilean".equals(bossName)) {
            setImageWithFallbacks(backgroundImage,
                    "/images/zilean_battlefield.png",
                    "/images/ruins_of_a_mystical_arena.png",
                    "/images/battlefield_zilean.png",
                    "/images/quest1_gates.png");
        } else if ("jax".equals(bossName)) {
            setImageWithFallbacks(backgroundImage,
                    "/images/jax_battlefield.png",
                    "/images/ruins_of_a_forgotten_battle_arena.png",
                    "/images/battlefield_jax.png",
                    "/images/quest1_gates.png");
        } else {
            setImageWithFallbacks(backgroundImage,
                    "/images/battlefield_" + bossName + ".png",
                    "/images/" + bossName + "_battlefield.png",
                    "/images/quest" + currentQuest.getId() + "_battle_intro.png",
                    "/images/quest1_gates.png");
        }
    }

    private void setImageWithFallbacks(ImageView imageView, String... paths) {
        for (String path : paths) {
            try {
                imageView.setImage(new Image(getClass().getResource(path).toExternalForm()));
                return;
            } catch (Exception ignored) {
                // Try next fallback.
            }
        }

        imageView.setImage(null);
    }

    private String safeName(String name) {
        if (name == null) {
            return "";
        }

        return name.toLowerCase().replaceAll("[^a-z0-9]", "");
    }

    private void updateAllDisplays() {
        updateBossDisplay();
        updatePartyDisplay();
        updateActiveMemberDisplay();
        updateCommandButtons();
    }

    private void updateBossDisplay() {
        Boss boss = currentFight.getBoss();

        bossNameLabel.setText(boss.getName());
        bossHpLabel.setText("HP " + boss.getCurrentHealth() + " / " + boss.getMaxHealth());

        double hpPercent = 0;
        if (boss.getMaxHealth() > 0) {
            hpPercent = Math.max(0, (double) boss.getCurrentHealth() / boss.getMaxHealth());
        }
        bossHpBar.setProgress(hpPercent);

        String bossName = safeName(boss.getName());

        if ("zilean".equals(bossName)) {
            setImageWithFallbacks(bossImage,
                    "/images/zilean_boss.png",
                    "/images/majestic_wizard_with_arcane_energy.png",
                    "/images/Zilean.png");
        } else if ("jax".equals(bossName)) {
            setImageWithFallbacks(bossImage,
                    "/images/jax_boss.png",
                    "/images/mystical_warrior_with_glowing_lamppost_staff.png",
                    "/images/Jax.png");
        } else {
            setImageWithFallbacks(bossImage,
                    "/images/boss_" + bossName + ".png",
                    "/images/" + bossName + "_boss.png",
                    "/images/" + boss.getName() + ".png",
                    "/images/boss.png");
        }

        roundLabel.setText("Round " + currentFight.getRoundNumber());
    }

    private void updatePartyDisplay() {
        partyBox.getChildren().clear();

        for (Adventurer member : guild.getMainParty()) {
            VBox card = createPartyCard(member);
            partyBox.getChildren().add(card);
        }
    }

    private VBox createPartyCard(Adventurer member) {
        VBox card = new VBox(4);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPrefWidth(170);
        card.setStyle("-fx-background-color: rgba(10,8,6,0.72);"
                + "-fx-border-color: #7b5a21;"
                + "-fx-border-width: 2;"
                + "-fx-border-radius: 8;"
                + "-fx-background-radius: 8;"
                + "-fx-padding: 6;");

        if (member.equals(currentAttacker)) {
            DropShadow glow = new DropShadow();
            glow.setColor(Color.GOLD);
            glow.setRadius(16);
            glow.setSpread(0.35);
            card.setEffect(glow);
        }

        Label name = new Label(member.getName());
        name.setStyle("-fx-text-fill: #ffd86a; -fx-font-size: 14px; -fx-font-weight: bold;");

        double hpPercent = member.getMaxHealth() <= 0 ? 0 : (double) member.getCurrentHealth() / member.getMaxHealth();
        ProgressBar hp = new ProgressBar(Math.max(0, hpPercent));
        hp.setPrefWidth(145);
        hp.setStyle("-fx-accent: #39d353;");

        Label hpText = new Label("HP " + member.getCurrentHealth() + "/" + member.getMaxHealth());
        hpText.setStyle("-fx-text-fill: white; -fx-font-size: 11px; -fx-font-weight: bold;");

        card.getChildren().addAll(name, hp, hpText);
        return card;
    }

    private void updateActiveMemberDisplay() {
        if (currentAttacker == null) {
            activeMemberLabel.setText("Active: None");
            activeMemberStatsLabel.setText("");
            if (activeMemberHpBar != null) {
                activeMemberHpBar.setProgress(0);
            }
            activeMemberImage.setImage(null);
            return;
        }

        activeMemberLabel.setText("Active: " + currentAttacker.getName());
        activeMemberStatsLabel.setText(
                "HP " + currentAttacker.getCurrentHealth() + "/" + currentAttacker.getMaxHealth()
        );

        if (activeMemberHpBar != null) {
            double hpPercent = currentAttacker.getMaxHealth() <= 0
                    ? 0
                    : (double) currentAttacker.getCurrentHealth() / currentAttacker.getMaxHealth();
            activeMemberHpBar.setProgress(Math.max(0, hpPercent));
        }

        String name = safeName(currentAttacker.getName());

        setImageWithFallbacks(activeMemberImage,
                "/images/" + currentAttacker.getName() + ".png",
                "/images/" + name + ".png",
                "/images/character_" + name + ".png",
                "/images/" + currentAttacker.getFaction().name().toLowerCase() + ".png",
                "/images/Aatrox.png",
                "/images/aatrox.png");

        activeMemberImage.setFitWidth(245);
        activeMemberImage.setFitHeight(225);
        activeMemberImage.setPreserveRatio(true);
        activeMemberImage.setSmooth(true);
        activeMemberImage.setTranslateY(0);
    }

    private void updateCommandButtons() {
        boolean canAct = !battleEnded
                && currentAttacker != null
                && !guild.isWiped()
                && !currentFight.getBoss().isDead()
                && currentFight.canAdventurerAct(currentAttacker);

        attackButton.setDisable(!canAct);
        boolean canChangeMember = canAct && currentFight.canChangeMember();
        changeMemberButton.setDisable(!canChangeMember);

        if (healButton != null) {
            healButton.setDisable(!canAct);
        }

        boolean canUseSpecial = canAct
                && currentAttacker != null
                && currentAttacker.equals(mainCharacter)
                && !currentFight.isSpecialMoveUsed();

        specialMoveButton.setDisable(!canUseSpecial);

        if (currentAttacker != null && currentAttacker.equals(mainCharacter)) {
            specialMoveButton.setText(currentFight.isSpecialMoveUsed() ? "SPECIAL USED" : "SPECIAL MOVE");
        } else {
            specialMoveButton.setText("SPECIAL MOVE\n(MC Only)");
        }
    }

    private void checkAndShowBossAbility() {
        String abilityMessage = currentFight.triggerBossAbilityIfNeeded(guild);

        if (abilityMessage != null && !abilityMessage.isBlank()) {
            dialogueLabel.setText(abilityMessage);
            dialogueLabel.getStyleClass().remove("special-effect-text");
            dialogueLabel.getStyleClass().add("special-effect-text");

            if (currentFight.isIsolationActive()) {
                currentAttacker = currentFight.getIsolatedTarget();
                attackerIndex = Math.max(0, guild.getMainParty().indexOf(currentAttacker));
            }

            updateAllDisplays();
        }

        if (handleMainCharacterDeathIfNeeded()) {
            return;
        }

        if (currentFight.isFightOver(guild)) {
            finishCurrentFightIfNeeded();
        }
    }

    @FXML
    public void onAttack() {
        if (currentAttacker == null) {
            return;
        }

        openMemoryGame();
    }

    private void openMemoryGame() {
        try {
            FXMLLoader loader = loadFirstAvailable("/fxml/MemoryGame.fxml", "/fxml/memorygame.fxml");
            Parent root = loader.load();

            MemoryGameController controller = loader.getController();
            controller.setFightData(currentFight, guild, currentAttacker);
            controller.setResultHandler(this::afterMemoryAttack);

            Stage popup = new Stage();
            popup.initOwner(rootPane.getScene().getWindow());
            popup.initModality(Modality.APPLICATION_MODAL);
            popup.setTitle("Memory Attack");
            popup.setResizable(false);
            popup.setScene(new Scene(root, 520, 430));
            popup.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            dialogueLabel.setText("Could not open memory game.");
        }
    }

    private FXMLLoader loadFirstAvailable(String primary, String fallback) {
        if (getClass().getResource(primary) != null) {
            return new FXMLLoader(getClass().getResource(primary));
        }

        return new FXMLLoader(getClass().getResource(fallback));
    }

    private void afterMemoryAttack(boolean success) {
        BossFight.AttackResult result = currentFight.resolvePlayerAttack(
                currentAttacker,
                success,
                currentAttacker != null && currentAttacker.equals(mainCharacter),
                guild
        );

        dialogueLabel.getStyleClass().remove("special-effect-text");
        pendingAttackMessage = result.getMessage();
        dialogueLabel.setText(pendingAttackMessage);
        updateAllDisplays();

        if (handleMainCharacterDeathIfNeeded()) {
            return;
        }

        if (finishCurrentFightIfNeeded()) {
            return;
        }

        openBossDiceRoll();
    }

    private void openBossDiceRoll() {
        if (currentAttacker == null || currentAttacker.isDead()) {
            if (!handleMainCharacterDeathIfNeeded()) {
                endTurnAfterBossAttack("No active fighter remains.");
            }
            return;
        }

        String bossLine = getBossAttackLine(currentFight.getBoss().getName());
        dialogueLabel.getStyleClass().remove("special-effect-text");
        dialogueLabel.setText(pendingAttackMessage + "\n" + bossLine);

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/diceroll.fxml"));
            Parent root = loader.load();

            DiceRollController controller = loader.getController();
            controller.setDiceData(currentFight, currentAttacker, this::afterBossDiceRoll);

            Stage popup = new Stage();
            popup.initOwner(rootPane.getScene().getWindow());
            popup.initModality(Modality.APPLICATION_MODAL);
            popup.setTitle(currentFight.getBoss().getName() + " Attacks");
            popup.setResizable(false);
            popup.setScene(new Scene(root, 560, 400));
            popup.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            BossFight.DefenseResult result = currentFight.resolveBossAttack(guild);
            endTurnAfterBossAttack(bossLine + "\n" + result.getMessage());
        }
    }

    private void afterBossDiceRoll(DiceRollController.DiceRollResult diceResult) {
        BossFight.DefenseResult result = currentFight.resolveDefend(
                currentAttacker,
                diceResult.getPlayerRoll(),
                diceResult.getBossRoll(),
                guild
        );

        String bossLine = getBossAttackLine(currentFight.getBoss().getName());
        String message = pendingAttackMessage + "\n" + bossLine + "\n" + result.getMessage();

        endTurnAfterBossAttack(message);
    }

    private String getBossAttackLine(String bossName) {
        if (bossName == null) {
            return "The enemy attacks!";
        }

        if ("Zilean".equalsIgnoreCase(bossName)) {
            return "Zilean: \"I've seen your death... it was painful.\"";
        }

        if ("Jax".equalsIgnoreCase(bossName)) {
            return "Jax: \"Word o' wisdom to the rest of ya: the secret to a long life? Not dying.\"";
        }

        if ("Vladimir".equalsIgnoreCase(bossName)) {
            return "Vladimir: \"The wound is only useful if someone learns from it.\"";
        }

        if ("Zoe".equalsIgnoreCase(bossName)) {
            return "Zoe: \"Sleepy time! Let's see what breaks.\"";
        }

        return bossName + " attacks!";
    }

    private void endTurnAfterBossAttack(String message) {
        dialogueLabel.getStyleClass().remove("special-effect-text");
        dialogueLabel.setText(message);
        endTurn();
    }

    @FXML
    public void onHeal() {
        if (currentFight == null || battleEnded) {
            return;
        }

        closeChangeMemberPopup();

        if (changeMemberOverlay != null) {
            changeMemberOverlay.setVisible(true);
        }

        updateHealPopupDisplay();
        healPopup.setVisible(true);
        healPopup.toFront();
    }

    private void updateHealPopupDisplay() {
        if (healPotionCountsLabel != null) {
            healPotionCountsLabel.setText(
                    "Silver x" + guild.getSmallPotionCount()
                            + "   Gold x" + guild.getPartyPotionCount()
                            + "   Purple x" + guild.getFullRestoreCount()
            );
        }

        boolean potionAlreadyUsed = currentFight != null && currentFight.isPotionUsedThisTurn();

        if (smallPotionHealButton != null) {
            smallPotionHealButton.setDisable(potionAlreadyUsed || guild.getSmallPotionCount() <= 0);
        }

        if (partyPotionHealButton != null) {
            partyPotionHealButton.setDisable(potionAlreadyUsed || guild.getPartyPotionCount() <= 0);
        }

        if (fullRestoreHealButton != null) {
            fullRestoreHealButton.setDisable(potionAlreadyUsed || guild.getFullRestoreCount() <= 0);
        }
    }

    @FXML
    public void onUseSmallPotion() {
        usePotion(ItemType.SINGLE);
    }

    @FXML
    public void onUsePartyPotion() {
        usePotion(ItemType.PARTY);
    }

    @FXML
    public void onUseFullRestore() {
        usePotion(ItemType.FULL);
    }

    private void usePotion(ItemType itemType) {
        if (currentFight == null) {
            return;
        }

        BossFight.PotionUseResult result = currentFight.usePotionThisTurn(guild, itemType, currentAttacker);

        closeHealPopup();
        dialogueLabel.getStyleClass().remove("special-effect-text");
        dialogueLabel.setText(result.getMessage());
        updateAllDisplays();
    }

    @FXML
    public void closeHealPopup() {
        if (healPopup != null) {
            healPopup.setVisible(false);
        }

        if (changeMemberOverlay != null && (changeMemberPopup == null || !changeMemberPopup.isVisible())) {
            changeMemberOverlay.setVisible(false);
        }
    }

    @FXML
    public void onChangeMember() {
        if (currentFight != null && !currentFight.canChangeMember()) {
            Adventurer isolated = currentFight.getIsolatedTarget();
            dialogueLabel.setText("Kha'Zix has isolated "
                    + (isolated == null ? "a fighter" : isolated.getName())
                    + ". This is a 1v1 until one of them falls.");
            updateCommandButtons();
            return;
        }

        if (changeMemberOverlay != null) {
            changeMemberOverlay.setVisible(true);
        }

        changeMemberPopup.setVisible(true);
        changeMemberPopup.toFront();

        memberListBox.getChildren().clear();

        for (Adventurer member : guild.getMainParty()) {
            if (member.isDead()) {
                continue;
            }

            Button button = new Button(member.getName() + "  HP " + member.getCurrentHealth() + "/" + member.getMaxHealth());
            button.setPrefWidth(260);
            button.getStyleClass().add("member-button");

            button.setOnAction(event -> {
                currentAttacker = member;
                closeChangeMemberPopup();
                dialogueLabel.setText(member.getName() + " steps forward.");
                updateAllDisplays();
            });

            memberListBox.getChildren().add(button);
        }
    }

    @FXML
    public void closeChangeMemberPopup() {
        if (changeMemberPopup != null) {
            changeMemberPopup.setVisible(false);
        }

        if (changeMemberOverlay != null && (healPopup == null || !healPopup.isVisible())) {
            changeMemberOverlay.setVisible(false);
        }
    }

    @FXML
    public void onSpecialMove() {
        if (currentAttacker == null || !currentAttacker.equals(mainCharacter)) {
            dialogueLabel.setText("Only the main character can use the special move.");
            return;
        }

        boolean activated = currentFight.activateSpecialMove();

        if (!activated) {
            dialogueLabel.setText("Special move has already been used in this fight.");
            updateCommandButtons();
            return;
        }

        String bossLine = getBossAttackLine(currentFight.getBoss().getName());

        BossFight.DefenseResult bossResult = currentFight.resolveBossAttack(guild);

        dialogueLabel.getStyleClass().remove("special-effect-text");
        dialogueLabel.getStyleClass().add("special-effect-text");
        dialogueLabel.setText(
                currentAttacker.getName() + " uses their special move.\n"
                        + bossLine + "\n"
                        + bossResult.getMessage()
        );

        endTurn();
    }

    private void endTurn() {
        if (handleMainCharacterDeathIfNeeded()) {
            return;
        }

        guild.removeDeadAdventurers();
        updateAllDisplays();

        if (handleMainCharacterDeathIfNeeded()) {
            return;
        }

        if (finishCurrentFightIfNeeded()) {
            return;
        }

        if (guild.getMainParty().isEmpty() || guild.isWiped()) {
            showBattleEnded("Your party has fallen.");
            return;
        }

        int currentIndex = guild.getMainParty().indexOf(currentAttacker);

        if (currentIndex < 0) {
            attackerIndex = 0;
        } else {
            attackerIndex = (currentIndex + 1) % guild.getMainParty().size();
        }

        chooseNextLivingAttacker();
        updateAllDisplays();
        checkAndShowBossAbility();
    }

    private boolean handleMainCharacterDeathIfNeeded() {
        if (guild == null || mainCharacter == null) {
            return false;
        }

        if (!mainCharacter.isDead()) {
            return false;
        }

        questFailedBecauseMainCharacterDied = true;
        showQuestRestartOption(mainCharacter.getName() + " has fallen. The quest ends here. Restart the quest or return to the map to rebuild the guild.");
        return true;
    }

    private void showQuestRestartOption(String message) {
        battleEnded = true;
        dialogueLabel.getStyleClass().remove("special-effect-text");
        dialogueLabel.setText(message);

        attackButton.setDisable(true);
        if (healButton != null) {
            healButton.setDisable(true);
        }
        changeMemberButton.setDisable(true);
        specialMoveButton.setDisable(true);

        if (restartQuestButton != null) {
            restartQuestButton.setVisible(true);
            restartQuestButton.setDisable(false);
        }
    }

    @FXML
    public void onRestartQuest() {
        if (currentQuest == null) {
            return;
        }

        restoreQuestStartState();
        currentQuest.resetBossFights();
        if (currentQuest instanceof Quest5) {
            ((Quest5) currentQuest).prepareEnemyGuild(guild);
        }

        this.bossFights = currentQuest.getBossFights();
        this.fightIndex = 0;
        this.attackerIndex = 0;
        this.battleEnded = false;
        this.questFailedBecauseMainCharacterDied = false;
        this.pendingAttackMessage = "";
        this.mainCharacter = guild.getMainCharacter();

        if (restartQuestButton != null) {
            restartQuestButton.setVisible(false);
            restartQuestButton.setDisable(true);
        }

        closeHealPopup();
        closeChangeMemberPopup();
        startCurrentFight();
    }

    private boolean finishCurrentFightIfNeeded() {
        if (handleMainCharacterDeathIfNeeded()) {
            return true;
        }

        if (!currentFight.isFightOver(guild)) {
            return false;
        }

        currentFight.finishFightIfOver(guild);

        if (!currentFight.isPlayerWon()) {
            showBattleEnded("You were defeated by " + currentFight.getBoss().getName() + ".");
            return true;
        }

        fightIndex++;

        if (fightIndex >= bossFights.size()) {
            finishQuest();
            return true;
        }

        startCurrentFight();
        return true;
    }

    private void finishQuest() {
        battleEnded = true;

        if (!currentQuest.isCompleted()) {
            currentQuest.runEvents(guild);
            currentQuest.updateCharacters(guild);
            guild.healMainPartyToFull();

            game.advanceToNextQuest(currentQuest.getId() - 1);
        }

        if (game.isGameOver()) {
            openEndScreen();
            return;
        }

        showBattleEnded("Quest complete. Return to the map.");
    }


    private void openEndScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/end.fxml"));
            Parent root = loader.load();

            GameDataReceiver controller = loader.getController();
            controller.setGameData(game);

            Stage stage = (Stage) rootPane.getScene().getWindow();
            ScreenUtil.switchScene(stage, root);
            stage.setTitle("The Fall of Shurima — Final Result");

        } catch (Exception e) {
            e.printStackTrace();
            showBattleEnded("Game finished, but final result screen could not be loaded.");
        }
    }

    private void showBattleEnded(String message) {
        battleEnded = true;
        dialogueLabel.getStyleClass().remove("special-effect-text");
        dialogueLabel.setText(message);

        attackButton.setDisable(true);
        if (healButton != null) {
            healButton.setDisable(true);
        }
        changeMemberButton.setDisable(true);
        specialMoveButton.setDisable(true);

        if (restartQuestButton != null && !questFailedBecauseMainCharacterDied) {
            restartQuestButton.setVisible(false);
            restartQuestButton.setDisable(true);
        }
    }

    @FXML
    public void onReturnToMap() {
        if (questFailedBecauseMainCharacterDied) {
            guild.reviveMainCharacterForMenu();
            currentQuest.resetBossFights();
            if (currentQuest instanceof Quest5) {
                ((Quest5) currentQuest).prepareEnemyGuild(guild);
            }
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/mainmenu.fxml"));
            Parent root = loader.load();

            MainMenuController controller = loader.getController();
            controller.setGameData(game);

            Stage stage = (Stage) rootPane.getScene().getWindow();
            ScreenUtil.switchScene(stage, root);
            stage.setTitle("The Fall of Shurima — Map Hub");

        } catch (Exception e) {
            e.printStackTrace();
            dialogueLabel.setText("Error returning to map.");
        }
    }
}
