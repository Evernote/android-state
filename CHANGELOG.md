## 1.1.4

* Add a small optimization, if the object doesn't contain any state variable, see #31
* Fix potential `ClassCastException`, see #30

## 1.1.3 (2017-07-24)

* Fix wrong handling of properties in Kotlin in Hungarian notation, see #26

## 1.1.2 (2017-07-11)

* Fix nullable lists containing `Parcelable` elements in Kotlin, see #25

## 1.1.1 (2017-06-22)

* Add a Proguard rule to keep the class name if the class contains a `State` or `StateReflection annoation`, see #23
* Handle a generic `Serializable` or `Parcelable` properly, see #24

## 1.1.0 (2017-06-05)

* Remove unnecessary Proguard rules prevent proper obfuscation, see #15
* Add new annotation `StateReflection` to properly support obfuscation even though reflection is being used, see #15
* Bundle library classes in annotation processor, see #13
* Detect correct getter and setter for boolean properties with a "is" prefix in Kotlin, see #18
* Add a Lint check to find matching save and restore calls to ensure a proper usage of the library

## 1.0.6 (2017-04-07)

* Fix inner classes in Kotlin, see #11
* Add a license header to each generated file, see #10

## 1.0.5 (2017-03-02)

* Fix a Stackoverflow error when using Kotlin generics, see #9

## 1.0.4 (2017-02-24)

* Fix a Stackoverflow error when using Kotlin enums, see #8

## 1.0.3 (2017-02-15)

* Provide some `Bundler` implementations for lists, see #4
* Add a workaround for the Jack compiler where the package name is always "package", may fix #5
* Erase generics of extended classes, fixes #6

## 1.0.2 (2017-02-15)

* NOT AVAILABLE because of Maven Central sync issues

## 1.0.1 (2017-01-27)

* Insert missing casted type for `Parcelable` or `Serializable` getters
* Allow `is` as getter prefix for boolean getters

## 1.0.0 (2017-01-26)

* Initial release