package dev.marfien.rewibw.shared.logging;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.message.AbstractMessageFactory;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.ParameterizedMessage;

@Getter
@RequiredArgsConstructor
public class PrefixedMessageFactory extends AbstractMessageFactory {

    private final String prefix;

    @Override
    public Message newMessage(String s, Object... objects) {
        return new ParameterizedMessage(this.prefix + " " + s, objects);
    }

}
