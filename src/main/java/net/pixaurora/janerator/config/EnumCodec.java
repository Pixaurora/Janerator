package net.pixaurora.janerator.config;

import java.util.Locale;
import java.util.Optional;
import java.util.stream.Stream;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.PrimitiveCodec;

public class EnumCodec<E extends Enum<E>> implements PrimitiveCodec<E> {
    private final String enumName;
    private final E[] enumValues;

    public EnumCodec(String enumName, E[] enumValues) {
        this.enumName = enumName;
        this.enumValues = enumValues;
    }

    @Override
    public <T> T write(DynamicOps<T> ops, E value) {
        return ops.createString(value.toString());
    }

    @Override
    public <T> DataResult<E> read(DynamicOps<T> ops, T input) {
        Optional<String> value = ops.getStringValue(input).get().left();

        if (value.isEmpty()) {
            return DataResult.error(() -> String.format("%s must not be empty!", this.enumName));
        }

        String uppercasedInput = value.get().toUpperCase(Locale.ROOT);
        Optional<E> foundMember = Stream.of(this.enumValues).filter(enumMember -> enumMember.toString().equals(uppercasedInput)).findFirst();

        if (foundMember.isEmpty()) {
            return DataResult.error(
                () -> String.format(
                    "No %s found, valid members include `%s`",
                    this.enumName,
                    String.join(", ", Stream.of(this.enumValues).map(Object::toString).toList())
                )
            );
        }

        return DataResult.success(foundMember.get());
    }
}
