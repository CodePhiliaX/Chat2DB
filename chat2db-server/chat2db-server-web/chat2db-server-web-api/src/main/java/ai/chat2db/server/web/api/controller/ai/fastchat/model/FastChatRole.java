package ai.chat2db.server.web.api.controller.ai.fastchat.model;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Collection;

public class FastChatRole extends FastChatExpandableStringEnum<FastChatRole> {

    /** The role that instructs or sets the behavior of the assistant. */
    public static final FastChatRole SYSTEM = fromString("system");

    /** The role that provides responses to system-instructed, user-prompted input. */
    public static final FastChatRole ASSISTANT = fromString("assistant");

    /** The role that provides input for chat completions. */
    public static final FastChatRole USER = fromString("user");

    /**
     * Creates a new instance of ChatRole value.
     *
     * @deprecated Use the {@link #fromString(String)} factory method.
     */
    @Deprecated
    public FastChatRole() {}

    /**
     * Creates or finds a ChatRole from its string representation.
     *
     * @param name a name to look for.
     * @return the corresponding ChatRole.
     */
    @JsonCreator
    public static FastChatRole fromString(String name) {
        return fromString(name, FastChatRole.class);
    }


    /**
     * Gets known ChatRole values.
     *
     * @return known ChatRole values.
     */
    public static Collection<FastChatRole> values() {
        return values(FastChatRole.class);
    }
}
