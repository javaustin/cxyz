package com.carrotguy69.cxyz.messages;

import com.carrotguy69.cxyz.cmd.admin.Debug;
import com.carrotguy69.cxyz.messages.utils.SimpleTextComponent;
import com.carrotguy69.cxyz.other.Logger;
import com.carrotguy69.cxyz.other.utils.ObjectUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.carrotguy69.cxyz.CXYZ.enabledDebugs;
import static com.carrotguy69.cxyz.CXYZ.f;
import static com.carrotguy69.cxyz.messages.MessageUtils.forceColor;
import static com.carrotguy69.cxyz.messages.MessageUtils.formatPlaceholders;

public class MessageParser {

    // Created custom exception class that hopefully provides helpful messages
    public class MessageParseException extends RuntimeException {
        private final int index;
        private final String description;

        public MessageParseException(String description, int index) {
            this.index = index;
            this.description = description;

            if (!unparsed.contains("\n")) {
                Logger.info(unparsed);
                Logger.info(" ".repeat(index) + "^" + " " + String.format("at index=%d", index));
            }
            else {
                Logger.info("at index=" + index);
            }
        }

        public MessageParseException(String description) {
            this.description = description;
            this.index = -1;
        }

        public String getOriginalText() {
            return unparsed;
        }

        public String getDescription() {
            return this.description;
        }

        public int getIndex() {
            return this.index;
        }

        public String getMessage() {
            return this.description;
        }
    }

    private final String unparsed;
    private final Map<String, Object> formatMap;

    public MessageParser(String s, Map<String, Object> formatMap) {

        if (s.endsWith("\n"))
            this.unparsed = s.substring(0, s.length() - 1);

        else
            this.unparsed = s;


        this.formatMap = formatMap;
    }

    public boolean isInteractive() {
        for (SimpleTextComponent textComponent : parse()) {
            if (!textComponent.getActions().isEmpty()) {
                return true;
            }
        }

        return false;
    }

    public static String unescape(String s) {
        // Converts escaped characters (like doubled brackets) into literal strings.
        // E.g. "Hello ((these)) are escaped characters" -> "Hello (these) are escaped characters"

        if (s == null || s.isEmpty()) return s;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (c == '(' && i + 1 < s.length() && s.charAt(i + 1) == '(') {
                sb.append('(');
                i++;
            }
            else if (c == ')' && i + 1 < s.length() && s.charAt(i + 1) == ')') {
                sb.append(')');
                i++;
            }
            else if (c == '[' && i + 1 < s.length() && s.charAt(i + 1) == '[') {
                sb.append('[');
                i++;
            }
            else if (c == ']' && i + 1 < s.length() && s.charAt(i + 1) == ']') {
                sb.append(']');
                i++;
            }
            else if (c == '\\' && i + 1 < s.length() && s.charAt(i + 1) == '\\') {
                sb.append('\\');
                i++;
            }
            else {
                sb.append(c);
            }
        }
        return sb.toString();

    }

    public static String getStrippedText(List<SimpleTextComponent> components) {

        StringBuilder sb = new StringBuilder();

        for (SimpleTextComponent component : components) {
            sb.append(component.getContent());
        }

        return sb.toString();
    }

    public List<SimpleTextComponent> parse() throws MessageParseException {
        List<SimpleTextComponent> components = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < unparsed.length(); i++) {
            char character = unparsed.charAt(i);
            char nextCharacter = unparsed.length() == i + 1 ? ' ' : unparsed.charAt(i + 1); // If we are not at the end of the sequence, we get the upcoming character from the index.

            if (character == '(' && nextCharacter != '(') { // Opening character (Not doubled/escaped)

                if (!stringBuilder.toString().isEmpty()){
                    components.add(new SimpleTextComponent(stringBuilder.toString(), List.of()));
                    stringBuilder = new StringBuilder(); // cleared the string builder for new text
                }

                int openingParenthesis = i;
                int closingParenthesis = getClosingParenthesis(openingParenthesis, i);

                // Both parentheses exist

                String textSubstring = unparsed.substring(openingParenthesis + 1, closingParenthesis);

                textSubstring = unescape(textSubstring);

                // Initialized textComponent with no action.
                SimpleTextComponent textComponent = new SimpleTextComponent(
                        textSubstring,
                        new ArrayList<>()
                );

                int start = closingParenthesis + 1;

                while (true) {
                    if (start + 1 > unparsed.length()) {
                        // We are out of action blocks at this point
                        i = start - 1; // After we break this while loop, we will return to the top of the outer loop, starting at `i=start` (because of incrementation.)
                        break;
                    }

                    else if (unparsed.charAt(start) == '[' && unparsed.charAt(start + 1) != '[') { // Valid opening bracket character.
                        int openingBracket = start;
                        int closingBracket = -1;

                        for (int j = openingBracket; j < unparsed.length(); j++) { // Immediately find closing character

                            if (unparsed.length() == j + 1 && unparsed.charAt(j) == ']') {
                                closingBracket = j;
                                break;
                            }

                            if (unparsed.charAt(j) == ']' && unparsed.charAt(j + 1) != ']') { // Closing character
                                closingBracket = j;
                                break;
                            }
                        }

                        if (closingBracket == -1) {// No matching closing bracket found for this opening bracket
                            throw new MessageParseException(String.format("Expected closing bracket ']' to match opening bracket at index %d in action block!", openingBracket), openingBracket);
                        }


                        // Both brackets exist

                        String actionSubstring = unparsed.substring(openingBracket + 1, closingBracket);
                        String[] indexes = actionSubstring.split(":", 2);

                        try {
                            SimpleTextComponent.Action.ActionType type = SimpleTextComponent.Action.ActionType.valueOf(indexes[0].toUpperCase());
                            String line = unescape(indexes[1]);

                            SimpleTextComponent.Action action = new SimpleTextComponent.Action(type, line);

                            textComponent.addAction(action);
                        }
                        catch (IllegalArgumentException ex) {
                            throw new MessageParseException(String.format("'%s' is not a valid Action!", indexes[0].toUpperCase()), openingBracket + 1);
                        }

                        start = closingBracket + 1; // This will get the next action block if it exists.
                    }
                    else {
                        // We are out of action blocks or no action block exists
                        i = start - 1; // After we break this while loop, we will return to the top of the outer loop, starting at `i=start` (because of incrementation.)
                        break;
                    }
                }
                // Once we've exited the while loop, then I am confident that we have all our actions associated with that block.
                components.add(textComponent);
            }

            // Everything below this line assumes we are outside of text or action blocks


            else if (
                    (character == '[' && nextCharacter == '[')
                            || (character == ']' && nextCharacter == ']')
                            || character == '('
                            || character == ')' && nextCharacter == ')'
            ) { // We want to be sure we are escaping doubled up brackets.
                stringBuilder.append(character);
                i += 1; // Since we are at the position i, i + 1 is the position of the second escaped bracket. Therefore, the iterator will skip the brackets we already covered.
            }

            else if (character == '[') {
                throw new MessageParseException(
                        String.format("Unexpected '[' at index %d. Brackets must directly follow a text block (e.g. (text)[ACTION]). If you intended a literal '[', escape it by doubling: '[['", i), i
                );
            }


            else if (character == ']') {
                throw new MessageParseException(
                        String.format("Unexpected ']' at index %d. Brackets must directly follow a text block (e.g. (text)[ACTION]). If you intended a literal ']]', escape it by doubling: ']]'", i), i
                );
            }

            else if (character == ')') {
                throw new MessageParseException(
                        String.format("Unexpected ')' at index %d. Closing parentheses must match an opening '(' that starts a text block. If you intended a literal ')', escape it by doubling: '))'", i), i
                );
            }

            else { // character is not an opening parenthesis, we will treat it as a literal String. This assumes we are outside of text or action blocks.
                stringBuilder.append(character);
            }

        }

        if (!stringBuilder.toString().isEmpty()) {
            components.add(new SimpleTextComponent(stringBuilder.toString(), List.of()));
        }

        return components;
    }

    private int getClosingParenthesis(int openingParenthesis, int i) {
        int closingParenthesis = -1;

        for (int j = openingParenthesis; j < unparsed.length(); j++) { // Immediately find closing character

            if (j != i && unparsed.charAt(j) == '(' && unparsed.charAt(j + 1) == '(') { // characters are escaped - increment by j += 2 (here and in the loop definition)
                j++;
                continue;
            }

            if (j != i && unparsed.charAt(j) == ')' && unparsed.charAt(j + 1) == ')') { // characters are escaped - increment by j += 2 (here and in the loop definition)
                j++;
                continue;
            }

            if (j != i && unparsed.charAt(j) == '(' && unparsed.charAt(j + 1) != '(') { // There is an unexpected parenthesis that is not escaped
                throw new MessageParseException(
                        String.format("Unexpected '(' at index %d. If you intended a literal '(', escape it by doubling: '(('", j)
                        , j);
            }

            if (unparsed.charAt(j) == ')' && unparsed.charAt(j + 1) != ')') { // Closing character (Not doubled/escaped)
                closingParenthesis = j;
                break;
            }
        }

        if (closingParenthesis == -1) { // Closing parenthesis does not exist. Throw error!
            throw new MessageParseException(String.format("Expected closing character to match opening character at %d in text block!", i));
        }
        return closingParenthesis;
    }

    public List<SimpleTextComponent> applyPlaceholders(List<SimpleTextComponent> components) {
        // Applies placeholders to the content of the component (not to the actions)

        List<SimpleTextComponent> results = new ArrayList<>();

        for (SimpleTextComponent stc : components) {
            String content = stc.getContent();

            content = formatPlaceholders(content, formatMap);

            results.add(new SimpleTextComponent(content, stc.getActions()));
        }

        return results;
    }


    public TextComponent toTextComponent(List<SimpleTextComponent> components) {
        // The reason some text components work perfectly and others don't is because we are trying to fulfill the colorSTC map before we have all of our colors loaded from the placeholders.
        // To solve this we must format all of our placeholders before, and then create the colorMap.
        // This shouldn't conflict with bracket detection since the parser that gave us a List<SimpleTextComponents> already handled it.

        components = applyPlaceholders(components);

        // SimpleTextComponents are handy because a SimpleTextComponent will only have text and actions. It will not have embedded TextComponents like spigot/bungee would.
        // So we can assume that we have access to STC.getContent() which is just a String and STC.getActions() which is a list of Actions.
        try {
            TextComponent tc = new TextComponent();

            LinkedHashMap<SimpleTextComponent, ChatColor> colorSTCMap = new LinkedHashMap<>();

            for (SimpleTextComponent simple : components) {
                // Assume we have "&aAn &bexample &x&F&F&0&0&0&0string" as our content.
                // Bungee has no idea what to do with that last color code unless we put ChatColor.of("#FF0000") and set the components color to it.

                // So we want new TC("&An &example "), and then new TC("string").setColor(ChatColor.of("#FF0000"))

                // To achieve this, we need to the range find where this "&x&F&F..." thing is located, and where it extends to (which is to the end or the next RGB code).
                // Once we find that, we will split this content with the delimiter being the start of the rgb codes, saving the content of each slice and its color (no color if no rgb)

                // This should result is in having at least one text component, and color that we will put in the map, and with the text component we will set it to the specified color in the next loop.
                // Finally, when constructing bungee TextComponents, we need to add the actions back to the text component, and set the color of the strings given the ranges we've acquired

                StringBuilder lastString = new StringBuilder();

                int size = simple.getContent().length();

                for (int i = 0; i < size; i++) {

                    char c0 = simple.getContent().charAt(i);

                    if (i + 1 >= size) {
                        lastString.append(c0);
                        continue;
                    }

                    char c1 = simple.getContent().charAt(i + 1); // This is why we set the loop condition to `i < size`

                    if (c0 == '&' && c1 == 'x' && i + 13 < size) {

                        // Add all the characters of the last string and clear the builder.
                        if (lastString.length() > 0) {
                            SimpleTextComponent newComponent = new SimpleTextComponent(lastString.toString(), simple.getActions());
                            colorSTCMap.put(newComponent, ChatColor.WHITE);
                            lastString.setLength(0);
                        }


                        // This signifies a rgb color code if the rest of the message is at least 14 chars long.

                        StringBuilder hexBuilder = new StringBuilder();

                        for (int j = 2; j < 14; j++) {
                            char ch = simple.getContent().charAt(i + j);

                            if (ch == '&' || ch == 'x')
                                continue;

                            hexBuilder.append(ch);
                        }

                        int startPos = i + 14;
                        int endPos = size;

                        for (int k = i + 2; k < size - 1; k++) {
                            // Now that we have a valid hex color, we want to find the range of the string that this hex color affects (until the end of string or a new &x code).

                            if (simple.getContent().charAt(k) == '&' && simple.getContent().charAt(k + 1) == 'x') {
                                endPos = k; // Cuts off just before the next color code so the '&' character of the next portion is not included.
                                break;
                            }
                        }


                        ChatColor color;

                        try {
                            color = ChatColor.of("#" + hexBuilder);

                            if (color == null)
                                color = ChatColor.WHITE;
                        }

                        catch (Exception gen) {
                            color = ChatColor.WHITE;
                        }

                        // So finally we have our start value, our end value, and the color of each value.
                        String affectedString = simple.getContent().substring(startPos, endPos);

                        SimpleTextComponent newComponent = new SimpleTextComponent(affectedString, simple.getActions());

                        colorSTCMap.put(newComponent, color);

                        i = endPos - 1;
                    }

                    else {
                        lastString.append(c0);
                    }

                }

                if (lastString.length() > 0) {
                    colorSTCMap.put(
                            new SimpleTextComponent(lastString.toString(), simple.getActions()),
                            ChatColor.WHITE
                    );
                }

            }

            for (Map.Entry<SimpleTextComponent, ChatColor> entry : colorSTCMap.entrySet()) {

                SimpleTextComponent simple = entry.getKey();


                String content = simple.getContent();
                ChatColor color = entry.getValue();

                if (simple.getActions().isEmpty()) {

                    TextComponent component = new TextComponent(f(forceColor(content)));
                    component.setColor(color);

                    tc.addExtra(component);
                }

                else {

                    String[] lines = content.split("\n");

                    for (String split : lines) {
                        TextComponent component = new TextComponent(f(forceColor(split)));

                        for (SimpleTextComponent.Action action : simple.getActions()) {
                            String actionLine = formatPlaceholders(action.getActionLine(), formatMap);

                            if (action.getType() == SimpleTextComponent.Action.ActionType.HOVER && component.getHoverEvent() == null) {
                                component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(f(forceColor(actionLine)))));
                            }
                            else if (action.getType() != SimpleTextComponent.Action.ActionType.HOVER && component.getClickEvent() == null) {
                                component.setClickEvent(new ClickEvent(ClickEvent.Action.valueOf(action.getType().name()), ChatColor.stripColor(actionLine)));
                            }
                        }

                        component.setColor(color);
                        tc.addExtra(component);

                    }

                }
            }

            Logger.debugMessage("unparsed text: " + unparsed);
            Logger.debugMessage("formatMap(size): " + formatMap.size());
            Logger.debugMessage("colors: " + colorSTCMap);

            if (ObjectUtils.containsIgnoreCase(enabledDebugs, Debug.DebugValue.MESSAGE_PARSER.name())) {
                TextComponent debugComponent = new TextComponent("final textComponent: ");
                debugComponent.addExtra(tc);

                Bukkit.getConsoleSender().spigot().sendMessage(debugComponent);
            }

            return tc;
        }

        catch (Exception ex) {
            Logger.warning("Failed to parse message! Perhaps you've inputted a bad RGB code or blank text after a color code! " + ex.getMessage());
            Logger.logStackTrace(ex);
            return new TextComponent(unparsed);
        }
    }



}