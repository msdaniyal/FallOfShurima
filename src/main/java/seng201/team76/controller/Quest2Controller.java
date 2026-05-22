package seng201.team76.controller;

public class Quest2Controller extends QuestChoiceController {
    @Override
    protected int getQuestIndex() {
        return 1;
    }

    @Override
    protected String getQuestScreenTitle() {
        return "Quest 2: Into the Void";
    }
}
