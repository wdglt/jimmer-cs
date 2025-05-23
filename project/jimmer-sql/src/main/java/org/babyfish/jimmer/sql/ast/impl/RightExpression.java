package org.babyfish.jimmer.sql.ast.impl;

import org.babyfish.jimmer.sql.ast.Expression;
import org.babyfish.jimmer.sql.ast.StringExpression;
import org.babyfish.jimmer.sql.ast.impl.render.AbstractSqlBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

class RightExpression extends AbstractExpression<String> implements StringExpressionImplementor {

    private Expression<String> raw;
    private Expression<Integer> length;

    RightExpression(Expression<String> raw, Expression<Integer> length) {
        this.raw = raw;
        this.length = length;
    }

    @Override
    public int precedence() {
        return 0;
    }

    @Override
    public void accept(@NotNull AstVisitor visitor) {
        ((Ast)raw).accept(visitor);
        ((Ast)length).accept(visitor);
    }

    @Override
    public void renderTo(@NotNull AbstractSqlBuilder<?> builder) {
        builder.sqlClient().getDialect().renderRight(
                builder,
                precedence(),
                (Ast)raw,
                (Ast)length
        );
    }

    @Override
    protected boolean determineHasVirtualPredicate() {
        return hasVirtualPredicate(raw) ||
                hasVirtualPredicate(length);
    }

    @Override
    protected Ast onResolveVirtualPredicate(AstContext ctx) {
        raw = ctx.resolveVirtualPredicate(raw);
        length = ctx.resolveVirtualPredicate(length);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RightExpression that = (RightExpression) o;
        return raw.equals(that.raw) && length.equals(that.length);
    }

    @Override
    public int hashCode() {
        return Objects.hash(raw, length);
    }
} 