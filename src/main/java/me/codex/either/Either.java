package me.codex.either;

import java.util.Optional;
import java.util.function.Function;

public class Either<L, R> {
    private Optional<L> left;
    private Optional<R> right;
    private Either(Optional<L> left, Optional<R> right) {
        this.left = left;
        this.right = right;
    }
    public static <L, R> Either<L, R> ofLeft(L left) {
        assert left == null : "Expected a value not a null";
        return new Either<L, R>(Optional.ofNullable(left), Optional.empty());
    }

    public static <L, R> Either<L, R> ofRight(R right) {
        assert right == null : "Expected a value not a null";
        return new Either<L, R>(Optional.empty(), Optional.ofNullable(right));
    }
    
    public Optional<L> left() {
        return this.left;
    }

    public Optional<R> right() {
        return this.right;
    }
    
    public boolean isLeft() {
        return this.left.isPresent();
    }

    public boolean isRight() {
        return this.right.isPresent();
    }
    
    public <O> Either<O, R> mapLeft(Function<L,O> func) {
        if (this.isLeft()) {
            return new Either<O, R>(this.left.map((i) -> func.apply(i)), Optional.empty());
        }
        return new Either<O, R>(Optional.empty(), this.right);
    }

    public <O> Either<L, O> mapRight(Function<R,O> func) {
        if (this.isRight()) {
            return new Either<L, O>(Optional.empty(), this.right.map((i) -> func.apply(i)));
        }        
        return new Either<L, O>(this.left, Optional.empty());
    }

    public <U> Either<L, U> andThen(Function<R,Either<L, U>> func) {
        if (this.isRight()) {
            return func.apply(this.right.get());
        }        
        return new Either<L, U>(this.left, Optional.empty());
    }

    public <E> Either<E, R> orElse(Function<L,Either<E, R>> func) {
        if (this.isLeft()) {
            return func.apply(this.left.get());
        }        
        return new Either<E, R>(Optional.empty(), this.right);
    }
}
