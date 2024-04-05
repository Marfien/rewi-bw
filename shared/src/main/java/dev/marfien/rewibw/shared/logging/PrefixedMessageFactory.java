package dev.marfien.rewibw.shared.logging;

import lombok.Getter;
import org.apache.logging.log4j.message.AbstractMessageFactory;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.ParameterizedMessage;

@Getter
public class PrefixedMessageFactory extends AbstractMessageFactory {

    private final String prefix;

    public PrefixedMessageFactory(String prefix) {
        if (prefix.contains("{") || prefix.contains("}")) {
            throw new IllegalArgumentException("Prefix cannot contain curly braces");
        }

        this.prefix = prefix;
    }

    @Override
    public Message newMessage(String s, Object... objects) {
        return new ParameterizedMessage(this.prefix + " " + s, objects);
    }

}
