// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: core/serialization/src/builtins.proto

package org.jetbrains.jet.descriptors.serialization;

public final class BuiltInsProtoBuf {
  private BuiltInsProtoBuf() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
    registry.add(org.jetbrains.jet.descriptors.serialization.BuiltInsProtoBuf.className);
    registry.add(org.jetbrains.jet.descriptors.serialization.BuiltInsProtoBuf.classAnnotation);
    registry.add(org.jetbrains.jet.descriptors.serialization.BuiltInsProtoBuf.callableAnnotation);
    registry.add(org.jetbrains.jet.descriptors.serialization.BuiltInsProtoBuf.parameterAnnotation);
  }
  public static final int CLASS_NAME_FIELD_NUMBER = 150;
  /**
   * <code>extend .org.jetbrains.jet.descriptors.serialization.Package { ... }</code>
   */
  public static final
    com.google.protobuf.GeneratedMessageLite.GeneratedExtension<
      org.jetbrains.jet.descriptors.serialization.ProtoBuf.Package,
      java.util.List<java.lang.Integer>> className = com.google.protobuf.GeneratedMessageLite
          .newRepeatedGeneratedExtension(
        org.jetbrains.jet.descriptors.serialization.ProtoBuf.Package.getDefaultInstance(),
        null,
        null,
        150,
        com.google.protobuf.WireFormat.FieldType.INT32,
        true);
  public static final int CLASS_ANNOTATION_FIELD_NUMBER = 150;
  /**
   * <code>extend .org.jetbrains.jet.descriptors.serialization.Class { ... }</code>
   */
  public static final
    com.google.protobuf.GeneratedMessageLite.GeneratedExtension<
      org.jetbrains.jet.descriptors.serialization.ProtoBuf.Class,
      java.util.List<org.jetbrains.jet.descriptors.serialization.ProtoBuf.Annotation>> classAnnotation = com.google.protobuf.GeneratedMessageLite
          .newRepeatedGeneratedExtension(
        org.jetbrains.jet.descriptors.serialization.ProtoBuf.Class.getDefaultInstance(),
        org.jetbrains.jet.descriptors.serialization.ProtoBuf.Annotation.getDefaultInstance(),
        null,
        150,
        com.google.protobuf.WireFormat.FieldType.MESSAGE,
        false);
  public static final int CALLABLE_ANNOTATION_FIELD_NUMBER = 150;
  /**
   * <code>extend .org.jetbrains.jet.descriptors.serialization.Callable { ... }</code>
   */
  public static final
    com.google.protobuf.GeneratedMessageLite.GeneratedExtension<
      org.jetbrains.jet.descriptors.serialization.ProtoBuf.Callable,
      java.util.List<org.jetbrains.jet.descriptors.serialization.ProtoBuf.Annotation>> callableAnnotation = com.google.protobuf.GeneratedMessageLite
          .newRepeatedGeneratedExtension(
        org.jetbrains.jet.descriptors.serialization.ProtoBuf.Callable.getDefaultInstance(),
        org.jetbrains.jet.descriptors.serialization.ProtoBuf.Annotation.getDefaultInstance(),
        null,
        150,
        com.google.protobuf.WireFormat.FieldType.MESSAGE,
        false);
  public static final int PARAMETER_ANNOTATION_FIELD_NUMBER = 150;
  /**
   * <code>extend .org.jetbrains.jet.descriptors.serialization.Callable.ValueParameter { ... }</code>
   */
  public static final
    com.google.protobuf.GeneratedMessageLite.GeneratedExtension<
      org.jetbrains.jet.descriptors.serialization.ProtoBuf.Callable.ValueParameter,
      java.util.List<org.jetbrains.jet.descriptors.serialization.ProtoBuf.Annotation>> parameterAnnotation = com.google.protobuf.GeneratedMessageLite
          .newRepeatedGeneratedExtension(
        org.jetbrains.jet.descriptors.serialization.ProtoBuf.Callable.ValueParameter.getDefaultInstance(),
        org.jetbrains.jet.descriptors.serialization.ProtoBuf.Annotation.getDefaultInstance(),
        null,
        150,
        com.google.protobuf.WireFormat.FieldType.MESSAGE,
        false);

  static {
  }

  // @@protoc_insertion_point(outer_class_scope)
}