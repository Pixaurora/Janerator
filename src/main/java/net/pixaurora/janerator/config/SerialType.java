package net.pixaurora.janerator.config;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

public record SerialType<A extends SpecifiesType<A>>(String name, Codec<? extends A> codec) {
    public static record Group<A extends SpecifiesType<A>>(String typeName, List<SerialType<A>> types) {
        public Codec<SerialType<? extends A>> typeCodec() {
            return Codec.STRING.comapFlatMap(this::lookupName, SerialType::name);
        }

        public Codec<A> dispatchCodec() {
            return this.typeCodec().dispatchStable(SpecifiesType::type, SerialType::codec);
        }

        public <T> DataResult<SerialType<A>> lookupBy(String lookupType, Function<SerialType<A>, T> getter, T lookupObject) {
            Optional<SerialType<A>> foundType = this.types.stream()
                .filter(type -> getter.apply(type).equals(lookupObject))
                .findFirst();

            try {
                return DataResult.success(foundType.get());
            } catch (NoSuchElementException typeNotFound) {
                return DataResult.error(() -> String.format("%s lookup did not find a type with %s = `%s`", this.typeName, lookupType, lookupObject.toString()));
            }
        }

        public DataResult<SerialType<A>> lookupName(String name) {
            return this.lookupBy("name", type -> type.name(), name);
        }
    }
}
