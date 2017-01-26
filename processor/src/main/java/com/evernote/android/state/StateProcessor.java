/* *****************************************************************************
 * Copyright (c) 2017 Evernote Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ralf Wondratschek - initial version
 *******************************************************************************/
package com.evernote.android.state;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.SparseArray;
import android.view.View;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import com.squareup.javapoet.WildcardTypeName;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

/**
 * @author rwondratschek
 */
@SuppressWarnings("unused")
@AutoService(Processor.class)
public class StateProcessor extends AbstractProcessor {

    private static final Map<String, String> TYPE_MAPPING = new HashMap<String, String>() {{
        put("boolean", "Boolean");
        put("boolean[]", "BooleanArray");
        put("java.lang.Boolean", "BoxedBoolean");
        put("byte", "Byte");
        put("byte[]", "ByteArray");
        put("java.lang.Byte", "BoxedByte");
        put("char", "Char");
        put("char[]", "CharArray");
        put("java.lang.Character", "BoxedChar");
        put("double", "Double");
        put("double[]", "DoubleArray");
        put("java.lang.Double", "BoxedDouble");
        put("float", "Float");
        put("float[]", "FloatArray");
        put("java.lang.Float", "BoxedFloat");
        put("int", "Int");
        put("int[]", "IntArray");
        put("java.lang.Integer", "BoxedInt");
        put("long", "Long");
        put("long[]", "LongArray");
        put("java.lang.Long", "BoxedLong");
        put("short", "Short");
        put("short[]", "ShortArray");
        put("java.lang.Short", "BoxedShort");
        put("java.lang.CharSequence", "CharSequence");
        put("java.lang.CharSequence[]", "CharSequenceArray");
        put("java.lang.String", "String");
        put("java.lang.String[]", "StringArray");
        put("java.util.ArrayList<java.lang.CharSequence>", "CharSequenceArrayList");
        put("java.util.ArrayList<java.lang.Integer>", "IntegerArrayList");
        put("java.util.ArrayList<java.lang.String>", "StringArrayList");
        put("android.os.Bundle", "Bundle");
        put("android.os.Parcelable[]", "ParcelableArray");
    }};

    private static final Comparator<Element> COMPARATOR = new Comparator<Element>() {
        @Override
        public int compare(Element o1, Element o2) {
            return o1.asType().toString().compareTo(o2.asType().toString());
        }
    };

    private static final String STATE_CLASS_NAME = State.class.getName();
    private static final String OBJECT_CLASS_NAME = Object.class.getName();
    private static final String PARCELABLE_CLASS_NAME = Parcelable.class.getName();
    private static final String SERIALIZABLE_CLASS_NAME = Serializable.class.getName();
    private static final String ARRAY_LIST_CLASS_NAME = ArrayList.class.getName();
    private static final String SPARSE_ARRAY_CLASS_NAME = SparseArray.class.getName();

    private static final Set<String> IGNORED_TYPE_DECLARATIONS = Collections.unmodifiableSet(new HashSet<String>() {{
        add(Bundle.class.getName());
        add(String.class.getName());
        add(Byte.class.getName());
        add(Short.class.getName());
        add(Integer.class.getName());
        add(Long.class.getName());
        add(Float.class.getName());
        add(Double.class.getName());
        add(Character.class.getName());
        add(Boolean.class.getName());
    }});

    private Types mTypeUtils;
    private Elements mElementUtils;
    private Filer mFiler;
    private Messager mMessager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mTypeUtils = processingEnv.getTypeUtils();
        mElementUtils = processingEnv.getElementUtils();
        mFiler = processingEnv.getFiler();
        mMessager = processingEnv.getMessager();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(State.class.getName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
        final Set<? extends Element> annotatedFields = env.getElementsAnnotatedWith(State.class);
        final Map<Element, BundlerWrapper> bundlers = new HashMap<>();

        for (Element field : annotatedFields) {
            if (field.getModifiers().contains(Modifier.STATIC)) {
                mMessager.printMessage(Diagnostic.Kind.ERROR, "Field must not be static", field);
                return true;
            }
            if (field.getModifiers().contains(Modifier.FINAL)) {
                mMessager.printMessage(Diagnostic.Kind.ERROR, "Field must not be final", field);
                return true;
            }

            BundlerWrapper bundlerWrapper = getBundlerWrapper(field);
            String key = field.asType().toString();
            if (bundlerWrapper == null && TYPE_MAPPING.get(key) == null) {
                CompatibilityType compatibilityType = getCompatibilityType(field);
                if (compatibilityType == null) {
                    mMessager.printMessage(Diagnostic.Kind.ERROR, "Don't know how to put " + field.asType() + " into a bundle", field);
                    return true;
                }

                TYPE_MAPPING.put(key, compatibilityType.mMapping); // this caches the class
            }
            if (bundlerWrapper != null) {
                Element privateClass = getPrivateClass(mElementUtils.getTypeElement(bundlerWrapper.mGenericName.toString()));
                if (privateClass != null) {
                    mMessager.printMessage(Diagnostic.Kind.ERROR, "Class must not be private", privateClass);
                    return true;
                }
                privateClass = getPrivateClass(mElementUtils.getTypeElement(bundlerWrapper.mBundlerName.toString()));
                if (privateClass != null) {
                    mMessager.printMessage(Diagnostic.Kind.ERROR, "Class must not be private", privateClass);
                    return true;
                }

                bundlers.put(field, bundlerWrapper);
            }
        }

        Map<Element, Set<Element>> classes = getClassElements(annotatedFields);
        Set<Element> allClassElements = classes.keySet();

        for (Element classElement : allClassElements) {
            Element privateClass = getPrivateClass(classElement);
            if (privateClass != null) {
                mMessager.printMessage(Diagnostic.Kind.ERROR, "Class must not be private", privateClass);
                return true;
            }
        }

        for (Element classElement : allClassElements) {
            final List<? extends Element> fields = sorted(classes.get(classElement));
            final Element packageElement = findElement(classElement, ElementKind.PACKAGE);

            final String packageName = packageElement.asType().toString();
            final String className = getClassName(classElement);
            final boolean isView = isAssignable(classElement, View.class);

            final TypeVariableName genericType = TypeVariableName.get("T", TypeName.get(classElement.asType()));

            final TypeName superTypeName;
            final TypeMirror superType = getSuperType(classElement.asType(), allClassElements);
            if (superType == null) {
                superTypeName = ParameterizedTypeName.get(ClassName.get(isView ? Injector.View.class : Injector.Object.class), genericType);
            } else {
                superTypeName = ParameterizedTypeName.get(ClassName.bestGuess(superType.toString() + StateSaver.SUFFIX), genericType);
            }

            MethodSpec.Builder saveMethodBuilder = MethodSpec.methodBuilder("save")
                    .addAnnotation(Override.class)
                    .addAnnotation(
                            AnnotationSpec
                                    .builder(SuppressWarnings.class)
                                    .addMember("value", "$S", "unchecked")
                                    .build()
                    )
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(genericType, "target");

            MethodSpec.Builder restoreMethodBuilder = MethodSpec.methodBuilder("restore")
                    .addAnnotation(Override.class)
                    .addAnnotation(
                            AnnotationSpec
                                    .builder(SuppressWarnings.class)
                                    .addMember("value", "$S", "unchecked")
                                    .build()
                    )
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(genericType, "target");

            if (isView) {
                saveMethodBuilder = saveMethodBuilder.returns(Parcelable.class).addParameter(Parcelable.class, "p");
                restoreMethodBuilder = restoreMethodBuilder.returns(Parcelable.class).addParameter(Parcelable.class, "p");

                if (superType != null) {
                    saveMethodBuilder = saveMethodBuilder.addStatement("$T state = HELPER.putParent(super.save(target, p))", Bundle.class);
                } else {
                    saveMethodBuilder = saveMethodBuilder.addStatement("$T state = HELPER.putParent(p)", Bundle.class);
                }
                restoreMethodBuilder = restoreMethodBuilder.addStatement("$T state = ($T) p", Bundle.class, Bundle.class);

            } else {
                saveMethodBuilder = saveMethodBuilder.returns(void.class).addParameter(Bundle.class, "state");
                restoreMethodBuilder = restoreMethodBuilder.returns(void.class).addParameter(Bundle.class, "state");

                if (superType != null) {
                    saveMethodBuilder = saveMethodBuilder.addStatement("super.save(target, state)");
                    restoreMethodBuilder = restoreMethodBuilder.addStatement("super.restore(target, state)");
                }
            }

            CodeBlock.Builder staticInitBlock = CodeBlock.builder();

            for (Element field : fields) {
                String fieldTypeString = field.asType().toString();
                String mapping = TYPE_MAPPING.get(fieldTypeString);

                FieldType fieldType = getFieldType(field);
                String fieldName = fieldType.getFieldName(field);

                BundlerWrapper bundler = bundlers.get(field);
                if (bundler != null) {
                    staticInitBlock = staticInitBlock.addStatement("BUNDLERS.put($S, new $T())", fieldName, bundler.mBundlerName);
                    mapping = "WithBundler";
                }

                switch (fieldType) {
                    case FIELD:
                        saveMethodBuilder = saveMethodBuilder.addStatement("HELPER.put$N(state, $S, target.$N)", mapping, fieldName, fieldName);
                        restoreMethodBuilder = restoreMethodBuilder.addStatement("target.$N = HELPER.get$N(state, $S)", fieldName, mapping, fieldName);
                        break;

                    case BOOLEAN_PROPERTY:
                    case PROPERTY:
                        saveMethodBuilder.addStatement("HELPER.put$N(state, $S, target.$N$N())", mapping, fieldName,
                                fieldType == FieldType.BOOLEAN_PROPERTY ? "is" : "get", fieldName);

                        if (bundler != null) {
                            restoreMethodBuilder = restoreMethodBuilder.addStatement("target.set$N(HELPER.<$T>get$N(state, $S))", fieldName,
                                    bundler.mGenericName, mapping, fieldName);
                        } else {
                            TypeMirror insertedType = getInsertedType(field, true);
                            if (insertedType != null) {
                                restoreMethodBuilder = restoreMethodBuilder.addStatement("target.set$N(HELPER.<$T>get$N(state, $S))",
                                        fieldName, ClassName.get(insertedType), mapping, fieldName);
                            } else {
                                restoreMethodBuilder = restoreMethodBuilder.addStatement("target.set$N(HELPER.get$N(state, $S))",
                                        fieldName, mapping, fieldName);
                            }
                        }
                        break;

                    case FIELD_REFLECTION:
                        String reflectionMapping = isPrimitiveMapping(mapping) ? mapping : "";

                        saveMethodBuilder = saveMethodBuilder
                                .beginControlFlow("try")
                                .addStatement("$T field = target.getClass().getDeclaredField($S)", Field.class, fieldName)
                                .addStatement("boolean accessible = field.isAccessible()")
                                .beginControlFlow("if (!accessible)")
                                .addStatement("field.setAccessible(true)")
                                .endControlFlow()
                                .addStatement("HELPER.put$N(state, $S, ($N) field.get$N(target))", mapping, fieldName, fieldTypeString, reflectionMapping)
                                .beginControlFlow("if (!accessible)")
                                .addStatement("field.setAccessible(false)")
                                .endControlFlow()
                                .nextControlFlow("catch (Exception e)")
                                .addStatement("throw new $T(e)", RuntimeException.class)
                                .endControlFlow();

                        restoreMethodBuilder = restoreMethodBuilder
                                .beginControlFlow("try")
                                .addStatement("$T field = target.getClass().getDeclaredField($S)", Field.class, fieldName)
                                .addStatement("boolean accessible = field.isAccessible()")
                                .beginControlFlow("if (!accessible)")
                                .addStatement("field.setAccessible(true)")
                                .endControlFlow()
                                .addStatement("field.set$N(target, HELPER.get$N(state, $S))", reflectionMapping, mapping, fieldName)
                                .beginControlFlow("if (!accessible)")
                                .addStatement("field.setAccessible(false)")
                                .endControlFlow()
                                .nextControlFlow("catch (Exception e)")
                                .addStatement("throw new $T(e)", RuntimeException.class)
                                .endControlFlow();
                        break;

                    case NOT_SUPPORTED:
                    default:
                        mMessager.printMessage(Diagnostic.Kind.ERROR, "Field must be either non-private or provide a getter and setter method", field);
                        return true;
                }
            }

            if (isView) {
                saveMethodBuilder = saveMethodBuilder.addStatement("return state");
                if (superType != null) {
                    restoreMethodBuilder = restoreMethodBuilder.addStatement("return super.restore(target, HELPER.getParent(state))");
                } else {
                    restoreMethodBuilder = restoreMethodBuilder.addStatement("return HELPER.getParent(state)");
                }
            }

            TypeName bundlerType = ParameterizedTypeName.get(ClassName.get(Bundler.class), WildcardTypeName.subtypeOf(Object.class));
            TypeName bundlerMap = ParameterizedTypeName.get(ClassName.get(HashMap.class), ClassName.get(String.class), bundlerType);

            TypeSpec classBuilder = TypeSpec.classBuilder(className + StateSaver.SUFFIX)
                    .addModifiers(Modifier.PUBLIC)
                    .superclass(superTypeName)
                    .addTypeVariable(genericType)
                    .addField(
                            FieldSpec.builder(bundlerMap, "BUNDLERS")
                                    .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                                    .initializer("new $T()", bundlerMap)
                                    .build()
                    )
                    .addStaticBlock(staticInitBlock.build())
                    .addField(
                            FieldSpec.builder(InjectionHelper.class, "HELPER")
                                    .addModifiers(Modifier.FINAL, Modifier.STATIC, Modifier.PRIVATE)
                                    .initializer("new $T($S, $N)", InjectionHelper.class, packageName + '.' + className + StateSaver.SUFFIX, "BUNDLERS")
                                    .build()
                    )
                    .addMethod(saveMethodBuilder.build())
                    .addMethod(restoreMethodBuilder.build())
                    .build();

            JavaFile javaFile = JavaFile.builder(packageName, classBuilder).build();
            try {
                javaFile.writeTo(mFiler);
            } catch (IOException e) {
                mMessager.printMessage(Diagnostic.Kind.ERROR, "Couldn't generate classes");
                return true;
            }
        }

        return true;
    }

    private Map<Element, Set<Element>> getClassElements(Collection<? extends Element> annotatedFields) {
        Map<Element, Set<Element>> result = new HashMap<>();
        for (Element field : annotatedFields) {
            Element classElement = findElement(field, ElementKind.CLASS);
            Set<Element> elements = result.get(classElement);
            if (elements == null) {
                elements = new HashSet<>();
            }
            elements.add(field);
            result.put(classElement, elements);
        }
        return result;
    }

    private Element findElement(Element element, ElementKind kind) {
        Element enclosingElement = element.getEnclosingElement();
        if (enclosingElement == null) {
            return element;
        }
        return element.getKind() == kind ? element : findElement(enclosingElement, kind);
    }

    private Element getPrivateClass(Element classElement) {
        if (classElement == null || classElement.getKind() != ElementKind.CLASS) {
            return null;
        } else if (classElement.getModifiers().contains(Modifier.PRIVATE)) {
            return classElement;
        } else {
            return getPrivateClass(classElement.getEnclosingElement());
        }
    }

    private String getClassName(Element classElement) {
        String className = classElement.getSimpleName().toString();
        Element enclosingElement = classElement.getEnclosingElement();
        while (enclosingElement != null && enclosingElement.getKind() == ElementKind.CLASS) {
            className = enclosingElement.getSimpleName() + "$" + className;
            enclosingElement = enclosingElement.getEnclosingElement();
        }
        return className;
    }

    private static String getPropertyFieldName(Element field) {
        String fieldName = field.getSimpleName().toString();
        if (fieldName.length() >= 2 && fieldName.startsWith("m") && Character.isUpperCase(fieldName.charAt(1))) {
            fieldName = fieldName.substring(1);
        }
        return Character.toUpperCase(fieldName.charAt(0)) + (fieldName.length() > 1 ? fieldName.substring(1) : "");
    }

    private FieldType getFieldType(Element field) {
        if (!field.getModifiers().contains(Modifier.PRIVATE)) {
            return FieldType.FIELD;
        }

        if (useReflection(field)) {
            return FieldType.FIELD_REFLECTION;
        }

        String fieldName = getPropertyFieldName(field);
        String getterName = "get" + fieldName;
        String isGetterName = "is" + fieldName;
        String setterName = "set" + fieldName;

        List<? extends Element> elements = field.getEnclosingElement().getEnclosedElements();
        boolean hasGetter = false;
        boolean hasIsGetter = false;
        boolean hasSetter = false;

        for (Element element : elements) {
            if (element.getKind() != ElementKind.METHOD) {
                continue;
            }
            String elementName = element.getSimpleName().toString();
            if (!hasGetter && getterName.equals(elementName) && !element.getModifiers().contains(Modifier.PRIVATE)) {
                hasGetter = true;
                if (hasSetter) {
                    break;
                }
            }
            if (!hasIsGetter && isGetterName.equals(elementName) && !element.getModifiers().contains(Modifier.PRIVATE)) {
                hasIsGetter = true;
                if (hasSetter) {
                    break;
                }
            }

            if (!hasSetter && setterName.equals(elementName) && !element.getModifiers().contains(Modifier.PRIVATE)) {
                hasSetter = true;
                if (hasGetter) {
                    break;
                }
            }
        }

        if (hasIsGetter && hasSetter) {
            return FieldType.BOOLEAN_PROPERTY;
        } else if (hasGetter && hasSetter) {
            return FieldType.PROPERTY;
        } else {
            return FieldType.NOT_SUPPORTED;
        }
    }

    private enum FieldType {
        FIELD, FIELD_REFLECTION, PROPERTY, BOOLEAN_PROPERTY, NOT_SUPPORTED;

        public String getFieldName(Element field) {
            switch (this) {
                case FIELD:
                case FIELD_REFLECTION:
                    return field.getSimpleName().toString();
                case PROPERTY:
                case BOOLEAN_PROPERTY:
                    return getPropertyFieldName(field);
                case NOT_SUPPORTED:
                default:
                    return null;
            }
        }
    }

    private BundlerWrapper getBundlerWrapper(Element field) {
        for (AnnotationMirror annotationMirror : field.getAnnotationMirrors()) {
            if (!STATE_CLASS_NAME.equals(annotationMirror.getAnnotationType().toString())) {
                continue;
            }

            Map<? extends ExecutableElement, ? extends AnnotationValue> elementValues = annotationMirror.getElementValues();
            for (ExecutableElement executableElement : elementValues.keySet()) {
                if ("value".equals(executableElement.getSimpleName().toString())) {
                    Object value = elementValues.get(executableElement).getValue(); // bundler class
                    if (value == null) {
                        continue;
                    }
                    TypeName bundlerName = ClassName.get(mElementUtils.getTypeElement(value.toString()));
                    TypeName genericName = null;

                    try {
                        // gets the generic type Data from `class MyBundler implements Bundler<Data> {}`
                        List<? extends TypeMirror> interfaces = mElementUtils.getTypeElement(value.toString()).getInterfaces();
                        for (TypeMirror anInterface : interfaces) {
                            if (isAssignable(mTypeUtils.erasure(anInterface), Bundler.class)) {
                                List<? extends TypeMirror> typeArguments = ((DeclaredType) anInterface).getTypeArguments();
                                if (typeArguments != null && typeArguments.size() >= 1) {
                                    genericName = ClassName.get(typeArguments.get(0));
                                }
                            }
                        }
                    } catch (Exception ignored) {
                    }
                    return new BundlerWrapper(bundlerName, genericName == null ? ClassName.get(Object.class) : genericName);
                }
            }
        }
        return null;
    }

    private boolean useReflection(Element field) {
        for (AnnotationMirror annotationMirror : field.getAnnotationMirrors()) {
            if (!STATE_CLASS_NAME.equals(annotationMirror.getAnnotationType().toString())) {
                continue;
            }

            Map<? extends ExecutableElement, ? extends AnnotationValue> elementValues = annotationMirror.getElementValues();
            for (ExecutableElement executableElement : elementValues.keySet()) {
                if ("reflection".equals(executableElement.getSimpleName().toString())) {
                    Object value = elementValues.get(executableElement).getValue(); // bundler class
                    if (value == null) {
                        continue;
                    }
                    return "true".equals(value.toString());
                }
            }
        }
        return false;
    }

    private boolean isPrimitiveMapping(String mapping) {
        switch (mapping) {
            case "Int":
            case "Short":
            case "Byte":
            case "Long":
            case "Char":
            case "Boolean":
            case "Double":
            case "Float":
                return true;
            default:
                return false;
        }
    }

    private static final class BundlerWrapper {
        final TypeName mBundlerName;
        final TypeName mGenericName;

        private BundlerWrapper(TypeName bundlerName, TypeName genericName) {
            mBundlerName = bundlerName;
            mGenericName = genericName;
        }
    }

    @SuppressWarnings("unused")
    private enum CompatibilityType {
        PARCELABLE("Parcelable", Parcelable.class, null),
        PARCELABLE_LIST("ParcelableArrayList", ArrayList.class, Parcelable.class),
        SPARSE_PARCELABLE_ARRAY("SparseParcelableArray", SparseArray.class, Parcelable.class),
        SERIALIZABLE("Serializable", Serializable.class, null);

        final String mMapping;
        final Class<?> mClass;
        final Class<?> mGenericClass;

        CompatibilityType(String mapping, Class<?> clazz, Class<?> genericClass) {
            mMapping = mapping;
            mClass = clazz;
            mGenericClass = genericClass;
        }
    }

    private CompatibilityType getCompatibilityType(Element field) {
        TypeMirror typeMirror = field.asType();
        for (CompatibilityType compatibilityType : CompatibilityType.values()) {
            if (compatibilityType.mGenericClass == null) {
                if (isAssignable(typeMirror, compatibilityType.mClass)) {
                    return compatibilityType;
                }

            } else if (typeMirror.getKind() != TypeKind.WILDCARD) {
                if (isAssignable(mTypeUtils.erasure(field.asType()), compatibilityType.mClass)) {
                    List<? extends TypeMirror> typeArguments = ((DeclaredType) field.asType()).getTypeArguments();
                    if (typeArguments != null && typeArguments.size() >= 1 && isAssignable(typeArguments.get(0), compatibilityType.mGenericClass)) {
                        return compatibilityType;
                    }
                }
            }
        }
        return null;
    }

    private boolean isAssignable(Element element, Class<?> clazz) {
        return isAssignable(element.asType(), clazz);
    }

    private boolean isAssignable(TypeMirror typeMirror, Class<?> clazz) {
        return mTypeUtils.isAssignable(typeMirror, mElementUtils.getTypeElement(clazz.getName()).asType());
    }

    private static List<? extends Element> sorted(Collection<? extends Element> collection) {
        List<Element> result = new ArrayList<>(collection);
        Collections.sort(result, COMPARATOR);
        return result;
    }

    private TypeMirror getSuperType(TypeMirror classElement, Set<Element> allClassElements) {
        List<? extends TypeMirror> typeMirrors = mTypeUtils.directSupertypes(classElement);
        while (typeMirrors != null && !typeMirrors.isEmpty()) {
            TypeMirror superClass = typeMirrors.get(0); // interfaces are at the end
            if (OBJECT_CLASS_NAME.equals(superClass.toString())) {
                break;
            }
            if (allClassElements.contains(mTypeUtils.asElement(superClass))) {
                return superClass;
            }
            typeMirrors = mTypeUtils.directSupertypes(superClass);
        }
        return null;
    }

    private TypeMirror getInsertedType(Element field, boolean checkIgnoredTypes) {
        if (field == null) {
            return null;
        }
        TypeMirror fieldType = field.asType();

        if (fieldType instanceof DeclaredType) {
            // generic type, return the generic value
            List<? extends TypeMirror> typeArguments = ((DeclaredType) fieldType).getTypeArguments();
            if (typeArguments != null && !typeArguments.isEmpty()) {
                return getInsertedType(mElementUtils.getTypeElement(typeArguments.get(0).toString()), false);
            }
        }

        TypeElement classElement = mElementUtils.getTypeElement(fieldType.toString());
        if (classElement == null) {
            return null;
        }

        if (checkIgnoredTypes && IGNORED_TYPE_DECLARATIONS.contains(classElement.toString())) {
            return null;
        }

        List<? extends TypeMirror> typeMirrors = mTypeUtils.directSupertypes(classElement.asType());
        if (typeMirrors != null) {
            for (TypeMirror superType : typeMirrors) {
                String superTypeString = superType.toString();
                if (PARCELABLE_CLASS_NAME.equals(superTypeString)) {
                    return fieldType;
                }
                if (SERIALIZABLE_CLASS_NAME.equals(superTypeString)) {
                    return fieldType;
                }
                TypeMirror result = getInsertedType(mElementUtils.getTypeElement(superTypeString), checkIgnoredTypes);
                if (result != null) {
                    // always return the passed in type and not any super type
                    return fieldType;
                }
            }
        }
        return null;
    }
}
