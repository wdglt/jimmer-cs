package org.babyfish.jimmer.dto.compiler;

import org.antlr.v4.runtime.Token;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class AnnoParser {

    private final CompilerContext<?, ?> ctx;

    AnnoParser(CompilerContext<?, ?> ctx) {
        this.ctx = ctx;
    }

    public Anno parse(DtoParser.AnnotationContext ctx) {
        String typeName = parse(ctx.typeName);
        Map<String, Anno.Value> argumentMap;
        DtoParser.AnnotationArgumentsContext argsCtx = ctx.annotationArguments();
        if (argsCtx != null) {
            argumentMap = parse(argsCtx);
        } else {
            argumentMap = Collections.emptyMap();
        }
        return new Anno(typeName, argumentMap);
    }

    private Anno parse(DtoParser.NestedAnnotationContext ctx) {
        String typeName = parse(ctx.typeName);
        Map<String, Anno.Value> argumentMap;
        DtoParser.AnnotationArgumentsContext argsCtx = ctx.annotationArguments();
        if (argsCtx != null) {
            argumentMap = parse(argsCtx);
        } else {
            argumentMap = Collections.emptyMap();
        }
        return new Anno(typeName, argumentMap);
    }

    private String parse(DtoParser.QualifiedNameContext ctx) {
        String last = ctx.stop.getText();
        String typeName = this.ctx.resolve(ctx);
        switch (last) {
            case "Nullable":
                throw this.ctx.exception(
                        ctx.stop.getLine(),
                        ctx.stop.getCharPositionInLine(),
                        "Annotation whose simple name is \"Nullable\" " +
                                "is forbidden by DTO language"
                );
            case "Null":
                if (!typeName.equals("javax.validation.constraints.Null") &&
                !typeName.equals("jakarta.validation.constraints.Null")) {
                    throw this.ctx.exception(
                            ctx.stop.getLine(),
                            ctx.stop.getCharPositionInLine(),
                            "Annotation whose simple name is \"Null\" " +
                                    "but qualified name is neither \"javax.validation.constraints.Null\" and " +
                                    "\"jakarta.validation.constraints.Null\""
                    );
                }
                break;
            case "TNullable":
                if (!typeName.equals("org.babyfish.jimmer.client.TNullable")) {
                    throw this.ctx.exception(
                            ctx.stop.getLine(),
                            ctx.stop.getCharPositionInLine(),
                            "Annotation whose simple name is \"TNullable\" " +
                                    "but qualified name is not \"org.babyfish.jimmer.client.TNullable\""
                    );
                }
                break;
            case "NotNull":
                if (!typeName.equals("javax.validation.constraints.NotNull") &&
                !typeName.equals("jakarta.validation.constraints.NotNull")) {
                    throw this.ctx.exception(
                            ctx.stop.getLine(),
                            ctx.stop.getCharPositionInLine(),
                            "Annotation whose simple name is \"NotNull\" " +
                                    "but qualified name is neither \"javax.validation.constraints.NotNull\" " +
                                    "nor \"jakarta.validation.constraints.NotNull\" " +
                                    "is forbidden by DTO language"
                    );
                }
                break;
            case "NonNull":
                throw this.ctx.exception(
                        ctx.stop.getLine(),
                        ctx.stop.getCharPositionInLine(),
                        "Annotation whose simple name is \"NonNull\" " +
                                "is forbidden by DTO language"
                );
        }
        if (typeName.startsWith("org.babyfish.jimmer.") &&
                !typeName.startsWith("org.babyfish.jimmer.client.")
                && !typeName.startsWith("org.babyfish.jimmer.jackson.") &&
                !typeName.equals("org.babyfish.jimmer.kt.dto.KotlinDto")) {
            throw this.ctx.exception(
                    ctx.stop.getLine(),
                    ctx.stop.getCharPositionInLine(),
                    "Jimmer annotation \"" +
                            typeName +
                            "\" is forbidden by DTO language"
            );
        }
        return typeName;
    }

    private Map<String, Anno.Value> parse(DtoParser.AnnotationArgumentsContext ctx) {
        Map<String, Anno.Value> argumentMap = new LinkedHashMap<>();
        if (ctx.defaultArgument != null) {
            argumentMap.put("value", parse(ctx.defaultArgument));
        }
        for (DtoParser.AnnotationNamedArgumentContext namedCtx : ctx.namedArguments) {
            String name = namedCtx.name.getText();
            if (argumentMap.containsKey(name)) {
                throw this.ctx.exception(
                        namedCtx.name.getLine(),
                        namedCtx.name.getCharPositionInLine(),
                        "Duplicated annotation argument \"" +
                                name +
                                "\""
                );
            }
            argumentMap.put(name, parse(namedCtx.value));
        }
        return Collections.unmodifiableMap(argumentMap);
    }

    private Anno.Value parse(DtoParser.AnnotationValueContext ctx) {
        DtoParser.AnnotationArrayValueContext arrayCtx = ctx.annotationArrayValue();
        if (arrayCtx != null) {
            return new Anno.ArrayValue(
                    Collections.unmodifiableList(
                            arrayCtx.elements.stream().map(this::parse).collect(Collectors.toList())
                    )
            );
        }
        return parse(ctx.annotationSingleValue());
    }

    private Anno.Value parse(DtoParser.AnnotationSingleValueContext ctx) {
        if (ctx.annotationPart != null) {
            return new Anno.AnnoValue(parse(ctx.annotationPart));
        }
        if (ctx.nestedAnnotationPart != null) {
            return new Anno.AnnoValue(parse(ctx.nestedAnnotationPart));
        }
        if (!ctx.stringTokens.isEmpty()) {
            if (ctx.stringTokens.size() == 1) {
                return new Anno.LiteralValue(ctx.stringTokens.get(0).getText());
            }
            StringBuilder builder = new StringBuilder();
            for (Token token : ctx.stringTokens) {
                String text = token.getText();
                builder.append(text, 1, text.length() - 1);
            }
            return new Anno.LiteralValue('"' + builder.toString() + '"');
        }
        if (ctx.integerToken != null) {
            long l = Long.parseLong(ctx.integerToken.getText());
            if (ctx.negative != null) {
                l = -l;
            }
            return new Anno.LiteralValue(Long.toString(l));
        }
        if (ctx.floatingPointToken != null) {
            double d = Double.parseDouble(ctx.floatingPointToken.getText());
            if (ctx.negative != null) {
                d = -d;
            }
            return new Anno.LiteralValue(Double.toString(d));
        }
        if (ctx.qualifiedPart != null) {
            if (ctx.classSuffix() != null) {
                String qualifiedName = this.ctx.resolve(
                        ctx.qualifiedPart.parts.stream().map(Token::getText).collect(Collectors.joining(".")),
                        ctx.qualifiedPart.start.getLine(),
                        ctx.qualifiedPart.start.getCharPositionInLine()
                );
                boolean isBox = ctx.classSuffix().start.getText().equals("?");
                if (isBox && !TypeRef.PRIMITIVE_TNS.contains(qualifiedName)) {
                    throw this.ctx.exception(
                            ctx.classSuffix().start.getCharPositionInLine(),
                            ctx.classSuffix().start.getCharPositionInLine(),
                            "Illegal nullable suffix `?`, it can only be used for " + TypeRef.PRIMITIVE_TNS
                    );
                }
                return new Anno.TypeRefValue(
                        new TypeRef(
                                qualifiedName,
                                Collections.emptyList(),
                                isBox,
                                ctx.qualifiedPart.start.getLine(),
                                ctx.qualifiedPart.start.getCharPositionInLine()
                        )
                );
            }
            if (ctx.qualifiedPart.parts.size() == 1) {
                String text = ctx.qualifiedPart.parts.get(0).getText();
                if (text.equals("true") || text.equals("false")) {
                    return new Anno.LiteralValue(text);
                }
                throw this.ctx.exception(
                        ctx.qualifiedPart.parts.get(0).getLine(),
                        ctx.qualifiedPart.parts.get(0).getCharPositionInLine(),
                        "It looks like enum constant, '.' is expected"
                );
            }
            List<Token> enumParts = ctx.qualifiedPart.parts;
            enumParts = enumParts.subList(0, enumParts.size() - 1);
            String qualifiedName = this.ctx.resolve(
                    enumParts.stream().map(Token::getText).collect(Collectors.joining(".")),
                    enumParts.get(enumParts.size() - 1).getLine(),
                    enumParts.get(enumParts.size() - 1).getCharPositionInLine()
            );
            return new Anno.EnumValue(
                    qualifiedName,
                    ctx.qualifiedPart.parts.get(ctx.qualifiedPart.parts.size() - 1).getText()
            );
        }
        Token token = ctx.characterToken != null ? ctx.characterToken : ctx.booleanToken;
        return new Anno.LiteralValue(token.getText());
    }
}
