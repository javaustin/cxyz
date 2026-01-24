package com.carrotguy69.cxyz.messages.utils;

import java.util.List;

public class SimpleTextComponent {

    private final String content;
    private final List<Action> actions;


    public SimpleTextComponent(String content, List<Action> actions) {
        this.content = content;
        this.actions = actions;
    }

    public void addAction(Action action) {
        this.actions.add(action);
    }

    public void removeAction(Action action) {
        this.actions.remove(action);
    }

    public void clearActions() {
        this.actions.clear();
    }

    public List<Action> getActions() {
        return actions;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "SimpleTextComponent{" +
                "content=" + content + ", " +
                "actions=" + actions + "}";
    }

    public static class Action {
        public enum ActionType {
            RUN_COMMAND,
            SUGGEST_COMMAND,
            COPY_TO_CLIPBOARD,
            OPEN_URL,
            HOVER
        }

        private final ActionType actionType;
        private final String actionLine;

        public Action(ActionType actionType, String actionLine) {
            this.actionType = actionType;
            this.actionLine = actionLine;
        }

        public String getActionLine() {
            return actionLine;
        }

        public ActionType getType() {
            return actionType;
        }

        @Override
        public String toString() {
            return "Action{" +
                    "type=" + actionType + ", " +
                    "line=\"" + actionLine + "\"}";
        }
    }


}
