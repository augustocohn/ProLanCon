package plc.project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Environment {

    public static final PlcObject NIL = new PlcObject(Type.NIL, new Scope(null), new Object() {

        @Override
        public String toString() {
            return "nil";
        }

    });

    private static final Map<String, Type> TYPES = new HashMap<>();

    public static Type getType(String name) {
        if (!TYPES.containsKey(name)) {
            throw new RuntimeException("Unknown type " + name + ".");
        }
        return TYPES.get(name);
    }

    public static void registerType(Type type) {
        if (TYPES.containsKey(type.getName())) {
            throw new IllegalArgumentException("Duplicate registration of type " + type.getName() + ".");
        }
        TYPES.put(type.getName(), type);
    }

    public static PlcObject create(Object value) {
        return new PlcObject(new Scope(null), value);
    }

    public static final class Type {

        public static final Type ANY = new Type("Any", "Object", new Scope(null));
        public static final Type NIL = new Type("Nil", "Void", new Scope(ANY.scope));
        public static final Type INTEGER_ITERABLE = new Type("IntegerIterable", "Iterable<Integer>", new Scope(ANY.scope));
        public static final Type COMPARABLE = new Type("Comparable", "Comparable", new Scope(ANY.scope));
        public static final Type BOOLEAN = new Type("Boolean", "boolean", new Scope(ANY.scope));
        public static final Type INTEGER = new Type("Integer", "int", new Scope(COMPARABLE.scope));
        public static final Type DECIMAL = new Type("Decimal", "double", new Scope(COMPARABLE.scope));
        public static final Type CHARACTER = new Type("Character", "char", new Scope(COMPARABLE.scope));
        public static final Type STRING = new Type("String", "String", new Scope(COMPARABLE.scope));

        private final String name;
        private final String jvmName;
        private final Scope scope;

        public Type(String name, String jvmName, Scope scope) {
            this.name = name;
            this.jvmName = jvmName;
            this.scope = scope;
        }

        public String getName() {
            return name;
        }

        public String getJvmName() {
            return jvmName;
        }

        public Scope getScope() {
            return this.scope;
        }

        public Variable getField(String name) {
            return scope.lookupVariable(name);
        }

        public Function getMethod(String name, int arity) {
            return scope.lookupFunction(name, arity + 1);
        }

        @Override
        public String toString() {
            return "Type{" +
                    "name='" + name + '\'' +
                    ", jvmName='" + jvmName + '\'' +
                    '}';
        }

    }

    public static final class PlcObject {

        private final Type type;
        private final Scope scope;
        private final Object value;

        public PlcObject(Scope scope, Object value) {
            this(new Type("Unknown", "Unknown", scope), scope, value);
        }

        public PlcObject(Type type, Scope scope, Object value) {
            this.type = type;
            this.scope = scope;
            this.value = value;
        }

        public Type getType() {
            return type;
        }

        public Variable getField(String name) {
            return scope.lookupVariable(name);
        }

        public void setField(String name, PlcObject value) {
            scope.lookupVariable(name).setValue(value);
        }

        public PlcObject callMethod(String name, List<PlcObject> arguments) {
            Function function = type.getMethod(name, arguments.size());
            arguments = new ArrayList<>(arguments);
            arguments.add(0, this);
            return function.invoke(arguments);
        }

        public Object getValue() {
            return value;
        }

        @Override
        public String toString() {
            return "Object{" +
                    "type=" + type +
                    ", scope=" + scope +
                    ", value=" + value +
                    '}';
        }

    }

    public static final class Variable {

        private final String name;
        private final String jvmName;
        private final Type type;
        private PlcObject value;

        public Variable(String name, PlcObject value) {
            this(name, name, Type.ANY, value);
        }

        public Variable(String name, String jvmName, Type type, PlcObject value) {
            this.name = name;
            this.jvmName = jvmName;
            this.type = type;
            this.value = value;
        }

        public Type getType() {
            return type;
        }

        public String getName() {
            return name;
        }

        public String getJvmName() {
            return jvmName;
        }

        public PlcObject getValue() {
            return value;
        }

        public void setValue(PlcObject value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Variable &&
                    name.equals(((Variable) obj).name) &&
                    jvmName.equals(((Variable) obj).jvmName) &&
                    type.equals(((Variable) obj).type);
        }

        @Override
        public String toString() {
            return "Variable{" +
                    "type=" + type +
                    ", name='" + name + '\'' +
                    ", value=" + value +
                    '}';
        }

    }

    public static final class Function {

        private final String name;
        private final String jvmName;
        private final List<Type> parameterTypes;
        private final Type returnType;
        private final java.util.function.Function<List<PlcObject>, PlcObject> function;

        public Function(String name, int arity, java.util.function.Function<List<PlcObject>, PlcObject> function) {
            this(name, name, new ArrayList<>(), Type.ANY, function);
            for (int i = 0; i < arity; i++) {
                this.parameterTypes.add(Type.ANY);
            }
        }

        public Function(String name, String jvmName, List<Type> parameterTypes, Type returnType, java.util.function.Function<List<PlcObject>, PlcObject> function) {
            this.name = name;
            this.jvmName = jvmName;
            this.parameterTypes = parameterTypes;
            this.returnType = returnType;
            this.function = function;
        }

        public String getName() {
            return name;
        }

        public String getJvmName() {
            return jvmName;
        }

        public List<Type> getParameterTypes() {
            return parameterTypes;
        }

        public Type getReturnType() {
            return returnType;
        }

        public PlcObject invoke(List<PlcObject> arguments) {
            return function.apply(arguments);
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Function &&
                    name.equals(((Function) obj).name) &&
                    jvmName.equals(((Function) obj).jvmName) &&
                    parameterTypes.equals(((Function) obj).parameterTypes) &&
                    returnType.equals(((Function) obj).returnType);
        }

        @Override
        public String toString() {
            return "Function{" +
                    "name='" + name + '\'' +
                    ", arity=" + parameterTypes.size() +
                    ", jvmName='" + jvmName + '\'' +
                    ", parameterTypes=" + parameterTypes +
                    ", returnType=" + returnType +
                    ", function=" + function +
                    '}';
        }

    }

    static {
        registerType(Type.ANY);
        registerType(Type.BOOLEAN);
        registerType(Type.INTEGER);
        registerType(Type.DECIMAL);
        registerType(Type.STRING);
        registerType(Type.NIL);
    }

}
