package org.babyfish.jimmer.sql.ast.query;

import org.babyfish.jimmer.lang.NewChain;
import org.babyfish.jimmer.sql.ast.ComparableExpression;
import org.babyfish.jimmer.sql.ast.NumericExpression;
import org.babyfish.jimmer.sql.ast.StringExpression;
import org.babyfish.jimmer.sql.ast.impl.query.MergedTypedSubQueryImpl;
import org.jetbrains.annotations.Nullable;

public interface ConfigurableSubQuery<R> extends TypedSubQuery<R> {

    @NewChain
    ConfigurableSubQuery<R> limit(int limit);

    @NewChain
    ConfigurableSubQuery<R> offset(long offset);

    @NewChain
    ConfigurableSubQuery<R> limit(int limit, long offset);

    @NewChain
    ConfigurableSubQuery<R> distinct();

    /**
     * Set the hint
     * @param hint Optional hint, both <b>/&#42;+ sth &#42;/</b> and <b>sth</b> are OK.
     * @return A new query object
     */
    ConfigurableSubQuery<R> hint(@Nullable String hint);

    interface Str extends ConfigurableSubQuery<String>, StringExpression {

        default TypedSubQuery.Str union(TypedSubQuery<String> other) {
            return (TypedSubQuery.Str) MergedTypedSubQueryImpl.of("union", this, other);
        }

        default TypedSubQuery.Str unionAll(TypedSubQuery<String> other) {
            return (TypedSubQuery.Str)MergedTypedSubQueryImpl.of("union all", this, other);
        }

        default TypedSubQuery.Str minus(TypedSubQuery<String> other) {
            return (TypedSubQuery.Str)MergedTypedSubQueryImpl.of("minus", this, other);
        }

        default TypedSubQuery.Str intersect(TypedSubQuery<String> other) {
            return (TypedSubQuery.Str)MergedTypedSubQueryImpl.of("intersect", this, other);
        }
    }

    interface Num<N extends Number & Comparable<N>> extends ConfigurableSubQuery<N>, NumericExpression<N> {

        default TypedSubQuery.Num<N> union(TypedSubQuery<N> other) {
            return (TypedSubQuery.Num<N>)MergedTypedSubQueryImpl.of("union", this, other);
        }

        default TypedSubQuery.Num<N> unionAll(TypedSubQuery<N> other) {
            return (TypedSubQuery.Num<N>)MergedTypedSubQueryImpl.of("union all", this, other);
        }

        default TypedSubQuery.Num<N> minus(TypedSubQuery<N> other) {
            return (TypedSubQuery.Num<N>)MergedTypedSubQueryImpl.of("minus", this, other);
        }

        default TypedSubQuery.Num<N> intersect(TypedSubQuery<N> other) {
            return (TypedSubQuery.Num<N>)MergedTypedSubQueryImpl.of("intersect", this, other);
        }
    }

    interface Cmp<T extends Comparable<?>> extends ConfigurableSubQuery<T>, ComparableExpression<T> {

        default TypedSubQuery.Cmp<T> union(TypedSubQuery<T> other) {
            return (TypedSubQuery.Cmp<T>)MergedTypedSubQueryImpl.of("union", this, other);
        }

        default TypedSubQuery.Cmp<T> unionAll(TypedSubQuery<T> other) {
            return (TypedSubQuery.Cmp<T>)MergedTypedSubQueryImpl.of("union all", this, other);
        }

        default TypedSubQuery.Cmp<T> minus(TypedSubQuery<T> other) {
            return (TypedSubQuery.Cmp<T>)MergedTypedSubQueryImpl.of("minus", this, other);
        }

        default TypedSubQuery.Cmp<T> intersect(TypedSubQuery<T> other) {
            return (TypedSubQuery.Cmp<T>)MergedTypedSubQueryImpl.of("intersect", this, other);
        }
    }
}
