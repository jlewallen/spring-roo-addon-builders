package com.ss.roo.builders.addon;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.springframework.roo.addon.beaninfo.BeanInfoMetadata;
import org.springframework.roo.classpath.PhysicalTypeIdentifier;
import org.springframework.roo.classpath.PhysicalTypeIdentifierNamingUtils;
import org.springframework.roo.classpath.PhysicalTypeMetadata;
import org.springframework.roo.classpath.details.ConstructorMetadataBuilder;
import org.springframework.roo.classpath.details.FieldMetadata;
import org.springframework.roo.classpath.details.FieldMetadataBuilder;
import org.springframework.roo.classpath.details.MemberFindingUtils;
import org.springframework.roo.classpath.details.MethodMetadata;
import org.springframework.roo.classpath.details.MethodMetadataBuilder;
import org.springframework.roo.classpath.details.annotations.AnnotatedJavaType;
import org.springframework.roo.classpath.details.annotations.AnnotationMetadata;
import org.springframework.roo.classpath.details.annotations.AnnotationMetadataBuilder;
import org.springframework.roo.classpath.details.annotations.populator.AutoPopulationUtils;
import org.springframework.roo.classpath.itd.AbstractItdTypeDetailsProvidingMetadataItem;
import org.springframework.roo.classpath.itd.InvocableMemberBodyBuilder;
import org.springframework.roo.metadata.MetadataIdentificationUtils;
import org.springframework.roo.model.JavaSymbolName;
import org.springframework.roo.model.JavaType;
import org.springframework.roo.project.Path;
import org.springframework.roo.support.style.ToStringCreator;
import org.springframework.roo.support.util.Assert;
import org.springframework.roo.support.util.StringUtils;

import com.ss.roo.builders.addon.RooBuildable;

public class BuilderMetadata extends AbstractItdTypeDetailsProvidingMetadataItem {
   private Logger log = Logger.getLogger(getClass().getName());
   private static final String PROVIDES_TYPE_STRING = BuilderMetadata.class.getName();
   private static final String PROVIDES_TYPE = MetadataIdentificationUtils.create(PROVIDES_TYPE_STRING);

   public BuilderMetadata(String identifier, JavaType aspectName, PhysicalTypeMetadata governorPhysicalTypeMetadata, Map<FieldMetadata, Boolean> declaredFields, BeanInfoMetadata beanInfo) {
      super(identifier, aspectName, governorPhysicalTypeMetadata);
      Assert.isTrue(isValid(identifier), "Metadata identification string '" + identifier + "' does not appear to be a valid");

      if(!isValid()) {
         return;
      }

      // Process values from the annotation, if present
      AnnotationMetadata annotation = MemberFindingUtils.getDeclaredTypeAnnotation(governorTypeDetails, new JavaType(RooBuildable.class.getName()));
      if(annotation != null) {
         AutoPopulationUtils.populate(this, annotation);
      }

      JavaType bean = governorTypeDetails.getName();
      JavaType name = new JavaType(bean + "Builder");
      String declaredByMetadataId = PhysicalTypeIdentifier.createIdentifier(name, Path.SRC_TEST_JAVA);

      List<ConstructorMetadataBuilder> constructors = new ArrayList<ConstructorMetadataBuilder>();
      {
         ConstructorMetadataBuilder ctor = new ConstructorMetadataBuilder(declaredByMetadataId);
         InvocableMemberBodyBuilder bodyBuilder = new InvocableMemberBodyBuilder();
         bodyBuilder.appendFormalLine("super();");
         bodyBuilder.appendFormalLine("this.target = new " + bean + "();");
         ctor.setBodyBuilder(bodyBuilder);
         ctor.setModifier(Modifier.PUBLIC);
         constructors.add(ctor);
      }

      List<MethodMetadataBuilder> methods = new ArrayList<MethodMetadataBuilder>();

      if(beanInfo != null) {
         for(MethodMetadata mutator : beanInfo.getPublicMutators()) {
            log.info("newBuilder: Mutator: " + mutator.getMethodName());
            JavaSymbolName methodName = getBuilderMutatorMethodName(beanInfo, mutator);
            List<AnnotatedJavaType> parameterTypes = mutator.getParameterTypes();
            List<JavaSymbolName> parameterNames = new ArrayList<JavaSymbolName>(Arrays.asList(new JavaSymbolName("value")));
            InvocableMemberBodyBuilder bodyBuilder = new InvocableMemberBodyBuilder();
            bodyBuilder.appendFormalLine("this.target." + mutator.getMethodName() + "(value);");
            bodyBuilder.appendFormalLine("return this;");
            MethodMetadataBuilder methodBuilder = new MethodMetadataBuilder(declaredByMetadataId, Modifier.PUBLIC, methodName, name, parameterTypes, parameterNames, bodyBuilder);
            methods.add(methodBuilder);
         }
      }
      else {
         log.warning("newBuilder: No metadata.");
      }

      JavaSymbolName fieldName = new JavaSymbolName("target");
      List<FieldMetadataBuilder> fields = new ArrayList<FieldMetadataBuilder>();
      FieldMetadataBuilder field = new FieldMetadataBuilder(declaredByMetadataId, Modifier.PRIVATE, new ArrayList<AnnotationMetadataBuilder>(), fieldName, bean);
      field.setFieldInitializer("new " + bean + "()");
      fields.add(field);

      /*
      ClassOrInterfaceTypeDetailsBuilder typeDetailsBuilder = new ClassOrInterfaceTypeDetailsBuilder(declaredByMetadataId, Modifier.PUBLIC, name, PhysicalTypeCategory.CLASS);
      typeDetailsBuilder.setDeclaredFields(fields);
      typeDetailsBuilder.setDeclaredMethods(methods);
      typeDetailsBuilder.setDeclaredConstructors(constructors);
      */

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
