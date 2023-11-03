package ai.chat2db.server.web.api.controller.ai.azure.model;

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonCreator;

public class AzureChatRole extends AzureExpandableStringEnum<AzureChatRole> {

    /** The role that instructs or sets the behavior of the assistant. */
    public static final AzureChatRole SYSTEM = fromString("system");

    /** The role that provides responses to system-instructed, user-prompted input. */
    public static final AzureChatRole ASSISTANT = fromString("assistant");

    /** The role that provides input for chat completions. */
    public static final AzureChatRole USER = fromString("user");

    /**
     * Creates a new instance of ChatRole value.
     *
     * @deprecated Use the {@link #fromString(String)} factory method.
     */
    @Deprecated
    public AzureChatRole() {}

    /**
     * Creates or finds a ChatRole from its string representation.
     *
     * @param name a name to look for.
     * @return the corresponding ChatRole.
     */
    @JsonCreator
    public static AzureChatRole fromString(String name) {
        return fromString(name, AzureChatRole.class);
    }


    /**
     * Gets known ChatRole values.
     *
     * @return known ChatRole values.
     */
    public static Collection<AzureChatRole> values() {
        return values(AzureChatRole.class);
    }
}
