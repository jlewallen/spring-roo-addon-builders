package com.ss.roo.builders.addon;

import java.util.logging.Logger;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.springframework.roo.classpath.TypeManagementService;
import org.springframework.roo.classpath.scanner.MemberDetailsScanner;
import org.springframework.roo.metadata.MetadataService;
import org.springframework.roo.model.JavaType;
import org.springframework.roo.support.util.Assert;

@Component
@Service
public class BuilderOperationsImpl implements BuilderOperations {
   private Logger log = Logger.getLogger(getClass().getName());

   @Reference
   private TypeManagementService typeManagementService;
   @Reference
   private MetadataService metadataService;
   @Reference
   private MemberDetailsScanner memberDetailsScanner;

   public void newBuilder(JavaType bean) {
      Assert.notNull(bean, "Bean to produce a builder for");

      /*
      JavaType name = new JavaType(bean + "Builder");
      String declaredByMetadataId = PhysicalTypeIdentifier.createIdentifier(name, Path.SRC_TEST_JAVA);

      log.info("newBuilder: Starting: " + bean);

      if(metadataService.get(declaredByMetadataId) != null) {
         // The file already exists
         log.info("newBuilder: Already");
         return;
      }

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

      String key = BeanInfoMetadata.createIdentifier(bean, Path.SRC_MAIN_JAVA);
      BeanInfoMetadata beanInfo = (BeanInfoMetadata)metadataService.get(key);
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

      ClassOrInterfaceTypeDetailsBuilder typeDetailsBuilder = new ClassOrInterfaceTypeDetailsBuilder(declaredByMetadataId, Modifier.PUBLIC, name, PhysicalTypeCategory.CLASS);
      typeDetailsBuilder.setDeclaredFields(fields);
      typeDetailsBuilder.setDeclaredMethods(methods);
      typeDetailsBuilder.setDeclaredConstructors(constructors);
      */

      // typeManagementService.generateClassFile(typeDetailsBuilder.build());
   }

   /*
   private JavaSymbolName getBuilderMutatorMethodName(BeanInfoMetadata beanInfo, MethodMetadata method) {
      return new JavaSymbolName(StringUtils.uncapitalize(method.getMethodName().getSymbolName().replace("set", "")));
   }
   */
}
