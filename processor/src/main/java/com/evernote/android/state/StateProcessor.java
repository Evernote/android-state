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

import java.io.Serializable;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
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
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

/**
 * @author rwondratschek
 */
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
            return o1.getSimpleName().toString().compareTo(o2.getSimpleName().toString());
        }
    };

    private static final String STATE_CLASS_NAME = State.class.getName();
    private static final String STATE_REFLECTION_CLASS_NAME = StateReflection.class.getName();
    private static final String OBJECT_CLASS_NAME = Object.class.getName();
    private static final String PARCELABLE_CLASS_NAME = Parcelable.class.getName();
    private static final String PARCELABLE_ARRAY_CLASS_NAME = Parcelable[].class.getCanonicalName();
    private static final String SERIALIZABLE_CLASS_NAME = Serializable.class.getName();

    private static final Set<String> GENERIC_SUPER_TYPES = Collections.unmodifiableSet(new HashSet<String>() {{
        add(PARCELABLE_CLASS_NAME);
        add(SERIALIZABLE_CLASS_NAME);
    }});

    private static final Set<String> GENERIC_SUPER_TYPE_SERIALIZABLE = Collections.singleton(SERIALIZABLE_CLASS_NAME);

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

    private HashMap<String, List<Element>> mMapGeneratedFileToOriginatingElements = new LinkedHashMap<>();

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
        Set<String> annotations = new HashSet<>();
        annotations.add(State.class.getName());
        annotations.add(StateReflection.class.getName());
        return Collections.unmodifiableSet(annotations);
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
        final Set<Element> annotatedFields = new HashSet<>();
        annotatedFields.addAll(env.getElementsAnnotatedWith(State.class));
        annotatedFields.addAll(env.getElementsAnnotatedWith(StateReflection.class));

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
            if (bundlerWrapper == null && TYPE_MAPPING.get(field.asType().toString()) == null) {
                CompatibilityType compatibilityType = getCompatibilityType(field);
                if (compatibilityType == null) {
                    mMessager.printMessage(Diagnostic.Kind.ERROR, "Don't know how to put " + field.asType() + " into a bundle", field);
                    return true;
                }

                /*
                 * Something really weird happens here. In Kotlin inner classes use a '$' sign instead of a '.'as delimiter,
                 * e.g. field.asType().toString() returns "com.evernote.android.state.test.YNABFormats$Currency"
                 *
                 * After the getCompatibilityType() method the same method call on the same object returns
                 * "com.evernote.android.state.test.YNABFormats.Currency", what is actually expected.
                 *
                 * I don't get why that's the case. But definitely use the new value as key in the map.
                 */
                TYPE_MAPPING.put(field.asType().toString(), compatibilityType.mMapping); // this caches the class
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

            String packageName = packageElement.asType().toString();
            if ("package".equals(packageName)) {
                // that's a weird bug in Jack where the package name is always "package"
                String packageJackCompiler = findPackageJackCompiler(classElement);
                packageName = packageJackCompiler == null ? packageName : packageJackCompiler;
            }

            final String className = getClassName(classElement);
            final boolean isView = isAssignable(classElement, View.class);

            final TypeVariableName genericType = TypeVariableName.get("T", TypeName.get(eraseGenericIfNecessary(classElement.asType())));

            final TypeName superTypeName;
            final TypeMirror superType = getSuperType(classElement.asType(), allClassElements);
            if (superType == null) {
                superTypeName = ParameterizedTypeName.get(ClassName.get(isView ? Injector.View.class : Injector.Object.class), genericType);
            } else {
                ClassName rawType = ClassName.bestGuess(eraseGenericIfNecessary(superType).toString() + StateSaver.SUFFIX);
                if (!rawType.toString().equals(rawType.reflectionName())) {
                    rawType = ClassName.bestGuess(rawType.reflectionName());
                }
                superTypeName = ParameterizedTypeName.get(rawType, genericType);
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

                FieldType fieldType = getFieldType(field, false);
                if (fieldType == FieldType.NOT_SUPPORTED && isHungarianNotation(field)) {
                    fieldType = getFieldType(field, true);
                }

                String fieldName = fieldType.getFieldName(field);

                String castedType = null;
                CompatibilityType compatibilityType = getCompatibilityType(field);
                if (compatibilityType == CompatibilityType.PARCELABLE_ARRAY && !PARCELABLE_ARRAY_CLASS_NAME.equals(fieldTypeString)) {
                    mMessager.printMessage(Diagnostic.Kind.NOTE, "Field " + fieldTypeString + " " + PARCELABLE_ARRAY_CLASS_NAME);
                    castedType = fieldTypeString;
                }

                BundlerWrapper bundler = bundlers.get(field);
                if (bundler != null) {
                    staticInitBlock = staticInitBlock.addStatement("BUNDLERS.put($S, new $T())", fieldName, bundler.mBundlerName);
                    mapping = "WithBundler";
                }

                switch (fieldType) {
                    case FIELD:
                        saveMethodBuilder = saveMethodBuilder.addStatement("HELPER.put$N(state, $S, target.$N)", mapping, fieldName, fieldName);
                        if (castedType != null) {
                            restoreMethodBuilder = restoreMethodBuilder.addStatement("target.$N = ($N) HELPER.get$N(state, $S)", fieldName, castedType, mapping, fieldName);
                        } else {
                            restoreMethodBuilder = restoreMethodBuilder.addStatement("target.$N = HELPER.get$N(state, $S)", fieldName, mapping, fieldName);
                        }
                        break;

                    case BOOLEAN_PROPERTY:
                    case BOOLEAN_PROPERTY_KOTLIN:
                    case PROPERTY_HUNGARIAN:
                    case PROPERTY:
                        if (fieldType == FieldType.BOOLEAN_PROPERTY || fieldType == FieldType.BOOLEAN_PROPERTY_KOTLIN) {
                            saveMethodBuilder.addStatement("HELPER.put$N(state, $S, target.$N$N())", mapping, fieldName, "is", fieldName);
                        } else {
                            saveMethodBuilder.addStatement("HELPER.put$N(state, $S, target.$N$N())", mapping, fieldName, "get", fieldName);
                        }

                        if (bundler != null) {
                            TypeName genericName = bundler.mGenericName;
                            if (fieldType == FieldType.PROPERTY) {
                                // maybe Kotlin, double check what the method accepts
                                TypeMirror parameterType = findParameterType(field);
                                if (parameterType != null) {
                                    genericName = ClassName.get(parameterType);
                                }
                            }

                            restoreMethodBuilder = restoreMethodBuilder.addStatement("target.set$N(HELPER.<$T>get$N(state, $S))", fieldName,
                                    genericName, mapping, fieldName);
                        } else {
                            InsertedTypeResult insertedType = getInsertedType(field, true);
                            if (insertedType != null) {
                                restoreMethodBuilder = restoreMethodBuilder.addStatement("target.set$N(HELPER.<$T>get$N(state, $S))",
                                        fieldName, ClassName.get(insertedType.mTypeMirror), mapping, fieldName);
                            } else if (castedType != null) {
                                restoreMethodBuilder = restoreMethodBuilder.addStatement("target.set$N(($N) HELPER.get$N(state, $S))",
                                        fieldName, castedType, mapping, fieldName);
                            } else {
                                restoreMethodBuilder = restoreMethodBuilder.addStatement("target.set$N(HELPER.get$N(state, $S))",
                                        fieldName, mapping, fieldName);
                            }
                        }
                        break;

                    case FIELD_REFLECTION:
                        String reflectionMapping = isPrimitiveMapping(mapping) ? mapping : "";

                        InsertedTypeResult insertedType = getInsertedType(field, true);
                        if (compatibilityType != null && insertedType != null) {
                            // either serializable or parcelable, this could be a private inner class, so don't use the concrete type
                            fieldTypeString = compatibilityType.mClass.getName();
                        }

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
                        mMessager.printMessage(Diagnostic.Kind.ERROR, "Field " + field.getSimpleName()
                                + " must be either non-private or provide a getter and setter method", field);
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
                    .addOriginatingElement(classElement)
                    .build();

            JavaFile javaFile = JavaFile.builder(packageName, classBuilder).build();
            if (!writeJavaFile(javaFile)) {
                return true; // failed, stop processor here
            }
            mMapGeneratedFileToOriginatingElements.put(javaFile.toJavaFileObject().getName(), javaFile.typeSpec.originatingElements);
        }

        return true;
    }

    private boolean writeJavaFile(JavaFile javaFile) {
        StringBuilder builder = new StringBuilder();

        JavaFileObject filerSourceFile = null;

        try {
            builder.append(LICENSE_HEADER);
            javaFile.writeTo(builder);

            String fileName = javaFile.packageName.isEmpty() ? javaFile.typeSpec.name : javaFile.packageName + "." + javaFile.typeSpec.name;
            List<Element> originatingElements = javaFile.typeSpec.originatingElements;
            filerSourceFile = mFiler.createSourceFile(fileName, originatingElements.toArray(new Element[0]));

            try (Writer writer = filerSourceFile.openWriter()) {
                writer.write(builder.toString());
            }
            return true;

        } catch (Exception e) {
            mMessager.printMessage(Diagnostic.Kind.ERROR, "Couldn't generate classes " + javaFile.packageName + '.' + javaFile.typeSpec.name);
            e.printStackTrace();

            if (filerSourceFile != null) {
                filerSourceFile.delete();
            }

            return false;
        }
    }

    /*package*/ HashMap<String, List<Element>> getMapGeneratedFileToOriginatingElements() {
        return mMapGeneratedFileToOriginatingElements;
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

    private String findPackageJackCompiler(Element element) {
        Element enclosingElement = element.getEnclosingElement();
        if (enclosingElement != null && enclosingElement.getKind() == ElementKind.PACKAGE && element.getKind() == ElementKind.CLASS) {
            String className = element.asType().toString();
            int index = className.lastIndexOf('.');
            if (index <= 0) {
                mMessager.printMessage(Diagnostic.Kind.WARNING, "Couldn't find package name with Jack compiler");
                return null;
            }
            return className.substring(0, index);
        }

        return enclosingElement != null ? findPackageJackCompiler(enclosingElement) : null;
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
        StringBuilder className = new StringBuilder(classElement.getSimpleName().toString());
        Element enclosingElement = classElement.getEnclosingElement();
        while (enclosingElement != null && enclosingElement.getKind() == ElementKind.CLASS) {
            className.insert(0, enclosingElement.getSimpleName() + "$");
            enclosingElement = enclosingElement.getEnclosingElement();
        }
        return className.toString();
    }

    private static boolean isHungarianNotation(Element field) {
        String fieldName = field.getSimpleName().toString();
        return fieldName.length() >= 2 && fieldName.startsWith("m") && Character.isUpperCase(fieldName.charAt(1));
    }

    private static String getPropertyFieldName(Element field, boolean ignoreHungarianNotation) {
        String fieldName = field.getSimpleName().toString();
        if (!ignoreHungarianNotation && isHungarianNotation(field)) {
            fieldName = fieldName.substring(1);
        }
        return Character.toUpperCase(fieldName.charAt(0)) + (fieldName.length() > 1 ? fieldName.substring(1) : "");
    }

    private TypeMirror findParameterType(Element field) {
        TypeMirror result = findParameterType(field, true);
        return result != null ? result : findParameterType(field, false);
    }

    private TypeMirror findParameterType(Element field, boolean ignoreHungarianNotation) {
        String fieldName = getPropertyFieldName(field, ignoreHungarianNotation);
        String setterName = "set" + fieldName;

        List<? extends Element> elements = field.getEnclosingElement().getEnclosedElements();

        for (Element element : elements) {
            if (element.getKind() != ElementKind.METHOD || !(element instanceof ExecutableElement)) {
                continue;
            }

            String elementName = element.getSimpleName().toString();

            if (setterName.equals(elementName) && !element.getModifiers().contains(Modifier.PRIVATE)) {
                List<? extends VariableElement> parameters = ((ExecutableElement) element).getParameters();
                if (parameters == null || parameters.isEmpty()) {
                    continue;
                }
                VariableElement variableElement = parameters.get(0);
                return variableElement.asType();
            }
        }

        return null;
    }

    private FieldType getFieldType(Element field, boolean ignoreHungarianNotation) {
        if (!field.getModifiers().contains(Modifier.PRIVATE)) {
            return FieldType.FIELD;
        }

        if (useReflection(field)) {
            return FieldType.FIELD_REFLECTION;
        }

        String fieldName = getPropertyFieldName(field, ignoreHungarianNotation);
        String getterName = "get" + fieldName;
        String isGetterName = "is" + fieldName;
        String setterName = "set" + fieldName;

        String isGetterNameKotlin = null;
        String setterNameKotlin = null;

        if (fieldName.length() > 2 && fieldName.startsWith("Is") && Character.isUpperCase(fieldName.charAt(2))) {
            // cut off the "is"
            String substring = fieldName.substring(2);
            isGetterNameKotlin = "is" + substring;
            setterNameKotlin = "set" + substring;
        }


        List<? extends Element> elements = field.getEnclosingElement().getEnclosedElements();
        boolean hasGetter = false;
        boolean hasIsGetter = false;
        boolean hasSetter = false;
        boolean isKotlinBooleanProperty = false;

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
            if (!hasGetter && isGetterNameKotlin != null && isGetterNameKotlin.equals(elementName) && !element.getModifiers().contains(Modifier.PRIVATE)) {
                hasIsGetter = true;
                isKotlinBooleanProperty = true;
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
            if (!hasSetter && setterNameKotlin != null && setterNameKotlin.equals(elementName) && !element.getModifiers().contains(Modifier.PRIVATE)) {
                hasSetter = true;
                if (hasGetter) {
                    break;
                }
            }
        }

        if (isKotlinBooleanProperty && hasSetter) {
            return FieldType.BOOLEAN_PROPERTY_KOTLIN;
        } else if (hasIsGetter && hasSetter) {
            return FieldType.BOOLEAN_PROPERTY;
        } else if (hasGetter && hasSetter) {
            return ignoreHungarianNotation ? FieldType.PROPERTY_HUNGARIAN : FieldType.PROPERTY;
        } else {
            return FieldType.NOT_SUPPORTED;
        }
    }

    private enum FieldType {
        FIELD, FIELD_REFLECTION, PROPERTY, BOOLEAN_PROPERTY, BOOLEAN_PROPERTY_KOTLIN, PROPERTY_HUNGARIAN, NOT_SUPPORTED;

        public String getFieldName(Element field) {
            switch (this) {
                case FIELD:
                case FIELD_REFLECTION:
                    return field.getSimpleName().toString();
                case PROPERTY:
                case BOOLEAN_PROPERTY:
                    return getPropertyFieldName(field, false);
                case PROPERTY_HUNGARIAN:
                    return getPropertyFieldName(field, true);
                case BOOLEAN_PROPERTY_KOTLIN:
                    return field.getSimpleName().toString().substring(2);
                case NOT_SUPPORTED:
                default:
                    return null;
            }
        }
    }

    private BundlerWrapper getBundlerWrapper(Element field) {
        for (AnnotationMirror annotationMirror : field.getAnnotationMirrors()) {
            if (!isStateAnnotation(annotationMirror)) {
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
                                    TypeMirror genericTypeMirror = typeArguments.get(0);
                                    String genericString = genericTypeMirror.toString();

                                    // this check is necessary for returned types like: List<? extends String> -> remove "? extends"
                                    if (genericString.contains("<? extends ")) {
                                        String innerType = genericString.substring(genericString.indexOf("<? extends ") + 11, genericString.length() - 1);

                                        // if it's a Parcelable, then we need to know the correct type for the bundler, e.g. List<ParcelableImpl> for parcelable list bundler
                                        if (PARCELABLE_CLASS_NAME.equals(innerType)) {
                                            InsertedTypeResult insertedType = getInsertedType(field, true);
                                            if (insertedType != null) {
                                                innerType = insertedType.mTypeMirror.toString();
                                            }
                                        }

                                        ClassName erasureClassName = ClassName.bestGuess(mTypeUtils.erasure(genericTypeMirror).toString());
                                        genericName = ParameterizedTypeName.get(erasureClassName, ClassName.bestGuess(innerType));
                                    } else {
                                        genericName = ClassName.get(genericTypeMirror);
                                    }
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
            if (STATE_REFLECTION_CLASS_NAME.equals(annotationMirror.getAnnotationType().toString())) {
                return true;
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
        PARCELABLE_ARRAY("ParcelableArray", Parcelable[].class, null),
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
            if (compatibilityType == CompatibilityType.PARCELABLE_ARRAY) {
                TypeMirror arrayType = getArrayType(field);
                if (arrayType != null && isAssignable(arrayType, Parcelable.class)) {
                    return CompatibilityType.PARCELABLE_ARRAY;
                }

            } else if (compatibilityType.mGenericClass == null) {
                if (isAssignable(typeMirror, compatibilityType.mClass)) {
                    return compatibilityType;
                }

            } else if (typeMirror.getKind() != TypeKind.WILDCARD) {
                if (isAssignable(mTypeUtils.erasure(typeMirror), compatibilityType.mClass)) {
                    List<? extends TypeMirror> typeArguments = ((DeclaredType) typeMirror).getTypeArguments();
                    if (typeArguments != null && typeArguments.size() >= 1 && isAssignable(typeArguments.get(0), compatibilityType.mGenericClass)) {
                        return compatibilityType;
                    }
                }
            }
        }
        return null;
    }

    private TypeMirror getArrayType(Element field) {
        TypeMirror typeMirror = field.asType();
        if (typeMirror instanceof ArrayType) {
            return ((ArrayType) typeMirror).getComponentType();
        } else {
            return null;
        }
    }

    @SuppressWarnings("SameParameterValue")
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

    private TypeMirror eraseGenericIfNecessary(TypeMirror typeMirror) {
        // is there a better way to detect a generic type?
        return (typeMirror.toString().endsWith(">")) ? mTypeUtils.erasure(typeMirror) : typeMirror;
    }

    private TypeMirror eraseCovarianceAndInvariance(TypeMirror typeMirror) {
        String string = typeMirror.toString();
        if (string.startsWith("? extends") || string.startsWith("? super")) {
            return mTypeUtils.erasure(typeMirror);
        } else {
            return typeMirror;
        }
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

    private InsertedTypeResult getInsertedType(Element field, @SuppressWarnings("SameParameterValue") boolean checkIgnoredTypes) {
        if (field == null) {
            return null;
        }
        return getInsertedType(field.asType(), checkIgnoredTypes);
    }

    private InsertedTypeResult getInsertedType(TypeMirror fieldType, boolean checkIgnoredTypes) {
        fieldType = eraseCovarianceAndInvariance(fieldType);

        TypeElement classElement = mElementUtils.getTypeElement(eraseGenericIfNecessary(fieldType).toString());
        List<? extends TypeMirror> superTypes = classElement == null ? null : mTypeUtils.directSupertypes(classElement.asType());

        if (fieldType instanceof DeclaredType) {
            List<? extends TypeMirror> typeArguments = ((DeclaredType) fieldType).getTypeArguments();
            if (typeArguments != null && !typeArguments.isEmpty()) {
                // generic type, return the generic value

                if (superTypes != null && isSuperType(superTypes, GENERIC_SUPER_TYPES) && !fieldType.toString().startsWith(ArrayList.class.getName())) {
                    // if this is generic Parcelable or Serializable, then use the type, ignore ArrayList, which also implements Serializable
                    return new InsertedTypeResult(fieldType, isSuperType(superTypes, GENERIC_SUPER_TYPE_SERIALIZABLE));
                }

                InsertedTypeResult insertedType = getInsertedType(typeArguments.get(0), false);
                if (insertedType != null && insertedType.mSerializable && fieldType.toString().startsWith(ArrayList.class.getName())) {
                    // because ArrayList was excluded above we need to check again, if it's an ArrayList containing Serializable, then
                    // just serialize the ArrayList
                    return new InsertedTypeResult(fieldType, true);
                }

                return insertedType;
            }
        }

        if (classElement == null || OBJECT_CLASS_NAME.equals(classElement.toString())) {
            return null;
        }

        if (checkIgnoredTypes && IGNORED_TYPE_DECLARATIONS.contains(classElement.toString())) {
            return null;
        }

        if (superTypes != null) {
            if (isSuperType(superTypes, GENERIC_SUPER_TYPES)) {
                // either instance of Serializable or Parcelable
                return new InsertedTypeResult(fieldType, isSuperType(superTypes, GENERIC_SUPER_TYPE_SERIALIZABLE));
            }

            for (TypeMirror superType : superTypes) {
                InsertedTypeResult result = getInsertedType(eraseGenericIfNecessary(superType), checkIgnoredTypes);
                if (result != null) {
                    // always return the passed in type and not any super type
                    return new InsertedTypeResult(fieldType, result.mSerializable);
                }
            }
        }
        return null;
    }

    private boolean isSuperType(List<? extends TypeMirror> typeMirrors, Collection<String> superTypes) {
        for (TypeMirror superType : typeMirrors) {
            if (superTypes.contains(superType.toString())) {
                return true;
            }
        }
        return false;
    }

    private boolean isStateAnnotation(AnnotationMirror annotationMirror) {
        String string = annotationMirror.getAnnotationType().toString();
        return STATE_CLASS_NAME.equals(string) || STATE_REFLECTION_CLASS_NAME.equals(string);
    }

    private static final class InsertedTypeResult {
        private final TypeMirror mTypeMirror;
        private final boolean mSerializable;

        private InsertedTypeResult(TypeMirror typeMirror, boolean serializable) {
            mTypeMirror = typeMirror;
            mSerializable = serializable;
        }
    }

    private static final String LICENSE_HEADER = "/* *****************************************************************************\n"
            + " * Copyright (c) 2017 Evernote Corporation.\n"
            + " * This software was generated by Evernote’s Android-State code generator\n"
            + " * (available at https://github.com/evernote/android-state), which is made\n"
            + " * available under the terms of the Eclipse Public License v1.0\n"
            + " * (available at http://www.eclipse.org/legal/epl-v10.html).\n"
            + " * For clarification, code generated by the Android-State code generator is\n"
            + " * not subject to the Eclipse Public License and can be used subject to the\n"
            + " * following terms:\n"
            + " *\n"
            + " * Permission is hereby granted, free of charge, to any person obtaining a copy\n"
            + " * of this software and associated documentation files (the \"Software\"), to deal\n"
            + " * in the Software without restriction, including without limitation the rights\n"
            + " * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell\n"
            + " * copies of the Software, and to permit persons to whom the Software is\n"
            + " * furnished to do so, subject to the following conditions:\n"
            + " *\n"
            + " * The above copyright notice and this permission notice shall be included in all\n"
            + " * copies or substantial portions of the Software.\n"
            + " *\n"
            + " * THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR\n"
            + " * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,\n"
            + " * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE\n"
            + " * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER\n"
            + " * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,\n"
            + " * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE\n"
            + " * SOFTWARE.\n"
            + " *******************************************************************************/\n";
}
