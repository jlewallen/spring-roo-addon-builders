package com.ss.roo.builders.addon;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.roo.addon.beaninfo.BeanInfoMetadata;
import org.springframework.roo.classpath.PhysicalTypeIdentifierNamingUtils;
import org.springframework.roo.classpath.PhysicalTypeMetadata;
import org.springframework.roo.classpath.details.ConstructorMetadataBuilder;
import org.springframework.roo.classpath.details.FieldMetadataBuilder;
import org.springframework.roo.classpath.details.MethodMetadata;
import org.springframework.roo.classpath.details.MethodMetadataBuilder;
import org.springframework.roo.classpath.details.annotations.AnnotatedJavaType;
import org.springframework.roo.classpath.details.annotations.AnnotationMetadataBuilder;
import org.springframework.roo.classpath.itd.AbstractItdTypeDetailsProvidingMetadataItem;
import org.springframework.roo.classpath.itd.InvocableMemberBodyBuilder;
import org.springframework.roo.metadata.MetadataIdentificationUtils;
import org.springframework.roo.model.JavaSymbolName;
import org.springframework.roo.model.JavaType;
import org.springframework.roo.project.Path;
import org.springframework.roo.support.style.ToStringCreator;
import org.springframework.roo.support.util.Assert;
import org.springframework.roo.support.util.StringUtils;

public class BuilderMetadata extends AbstractItdTypeDetailsProvidingMetadataItem {
   private Logger log = Logger.getLogger(getClass().getName());
   private static final String PROVIDES_TYPE_STRING = BuilderMetadata.class.getName();
   private static final String PROVIDES_TYPE = MetadataIdentificationUtils.create(PROVIDES_TYPE_STRING);

   public BuilderMetadata(String identifier, JavaType aspectName, PhysicalTypeMetadata governorPhysicalTypeMetadata, BeanInfoMetadata beanInfo) {
      super(identifier, aspectName, governorPhysicalTypeMetadata);
      Assert.isTrue(isValid(identifier), "Metadata identification string '" + identifier + "' does not appear to be a valid");

      if(!isValid()) {
         return;
      }

      JavaType bean = beanInfo.getJavaBean();
      JavaType builderName = new JavaType(bean + "Builder");

      {
         ConstructorMetadataBuilder ctor = new ConstructorMetadataBuilder(getId());
         InvocableMemberBodyBuilder bodyBuilder = new InvocableMemberBodyBuilder();
         bodyBuilder.appendFormalLine("super();");
         bodyBuilder.appendFormalLine("this.target = new " + bean + "();");
         ctor.setBodyBuilder(bodyBuilder);
         ctor.setModifier(Modifier.PUBLIC);
         builder.addConstructor(ctor);
      }

      for(MethodMetadata mutator : beanInfo.getPublicMutators()) {
         JavaSymbolName methodName = getBuilderMutatorMethodName(beanInfo, mutator);
         List<AnnotatedJavaType> parameterTypes = mutator.getParameterTypes();
         List<JavaSymbolName> parameterNames = new ArrayList<JavaSymbolName>(Arrays.asList(new JavaSymbolName("value")));
         InvocableMemberBodyBuilder bodyBuilder = new InvocableMemberBodyBuilder();
         bodyBuilder.appendFormalLine("this.target." + mutator.getMethodName() + "(value);");
         bodyBuilder.appendFormalLine("return this;");
         MethodMetadataBuilder methodBuilder = new MethodMetadataBuilder(getId(), Modifier.PUBLIC, methodName, builderName, parameterTypes, parameterNames, bodyBuilder);
         builder.addMethod(methodBuilder);
         log.info(bean + "." + methodName);
      }

      JavaSymbolName fieldName = new JavaSymbolName("target");
      FieldMetadataBuilder field = new FieldMetadataBuilder(getId(), Modifier.PRIVATE, new ArrayList<AnnotationMetadataBuilder>(), fieldName, bean);
      field.setFieldInitializer("new " + bean + "()");
      builder.addField(field);

      itdTypeDetails = builder.build();
   }

   public static final String getMetadataIdentiferType() {
      return PROVIDES_TYPE;
   }

   public String toString() {
      ToStringCreator tsc = new ToStringCreator(this);
      tsc.append("identifier", getId());
      tsc.append("valid", valid);
      tsc.append("aspectName", aspectName);
      tsc.append("destinationType", destination);
      tsc.append("governor", governorPhysicalTypeMetadata.getId());
      tsc.append("itdTypeDetails", itdTypeDetails);
      return tsc.toString();
   }

   public static final String createIdentifier(JavaType javaType, Path path) {
      return PhysicalTypeIdentifierNamingUtils.createIdentifier(PROVIDES_TYPE_STRING, javaType, path);
   }

   public static final JavaType getJavaType(String metadataIdentificationString) {
      return PhysicalTypeIdentifierNamingUtils.getJavaType(PROVIDES_TYPE_STRING, metadataIdentificationString);
   }

   public static final Path getPath(String metadataIdentificationString) {
      return PhysicalTypeIdentifierNamingUtils.getPath(PROVIDES_TYPE_STRING, metadataIdentificationString);
   }

   public static boolean isValid(String metadataIdentificationString) {
      return PhysicalTypeIdentifierNamingUtils.isValid(PROVIDES_TYPE_STRING, metadataIdentificationString);
   }

   private JavaSymbolName getBuilderMutatorMethodName(BeanInfoMetadata beanInfo, MethodMetadata method) {
      return new JavaSymbolName(StringUtils.uncapitalize(method.getMethodName().getSymbolName().replace("set", "")));
   }

}
