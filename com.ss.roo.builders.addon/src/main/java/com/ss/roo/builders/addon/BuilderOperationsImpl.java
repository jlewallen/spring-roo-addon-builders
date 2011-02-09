package com.ss.roo.builders.addon;

import java.io.File;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.springframework.roo.classpath.MutablePhysicalTypeMetadataProvider;
import org.springframework.roo.classpath.PhysicalTypeCategory;
import org.springframework.roo.classpath.PhysicalTypeIdentifier;
import org.springframework.roo.classpath.PhysicalTypeMetadata;
import org.springframework.roo.classpath.TypeManagementService;
import org.springframework.roo.classpath.details.ClassOrInterfaceTypeDetailsBuilder;
import org.springframework.roo.classpath.details.DefaultPhysicalTypeMetadata;
import org.springframework.roo.classpath.details.MethodMetadataBuilder;
import org.springframework.roo.classpath.details.annotations.AnnotationAttributeValue;
import org.springframework.roo.classpath.details.annotations.AnnotationMetadataBuilder;
import org.springframework.roo.classpath.details.annotations.ClassAttributeValue;
import org.springframework.roo.classpath.scanner.MemberDetailsScanner;
import org.springframework.roo.metadata.MetadataService;
import org.springframework.roo.model.JavaSymbolName;
import org.springframework.roo.model.JavaType;
import org.springframework.roo.process.manager.FileManager;
import org.springframework.roo.project.Path;
import org.springframework.roo.project.PathResolver;
import org.springframework.roo.project.ProjectMetadata;
import org.springframework.roo.support.util.Assert;

@Service
@Component
public class BuilderOperationsImpl implements BuilderOperations {
   private Logger log = Logger.getLogger(getClass().getName());

   @Reference
   private FileManager fileManager;
   @Reference
   private PathResolver pathResolver;
   @Reference
   private TypeManagementService typeManagementService;
   @Reference
   private MetadataService metadataService;
   @Reference
   private MemberDetailsScanner memberDetailsScanner;
   @Reference
   private MutablePhysicalTypeMetadataProvider physicalTypeMetadataProvider;

   public void newBuilder(JavaType builderType, JavaType beanType) {
      Assert.notNull(builderType, "Builder to produce");
      Assert.notNull(beanType, "Bean to produce a builder for");

      String declaredByMetadataId = PhysicalTypeIdentifier.createIdentifier(builderType, Path.SRC_TEST_JAVA);

      List<MethodMetadataBuilder> methods = new ArrayList<MethodMetadataBuilder>();
      ClassOrInterfaceTypeDetailsBuilder typeDetailsBuilder = new ClassOrInterfaceTypeDetailsBuilder(declaredByMetadataId, Modifier.PUBLIC, builderType, PhysicalTypeCategory.CLASS);
      typeDetailsBuilder.setDeclaredMethods(methods);

      List<AnnotationAttributeValue<?>> builderAnnotation = new ArrayList<AnnotationAttributeValue<?>>();
      builderAnnotation.add(new ClassAttributeValue(new JavaSymbolName("bean"), beanType));

      List<AnnotationMetadataBuilder> annotations = new ArrayList<AnnotationMetadataBuilder>();
      annotations.add(new AnnotationMetadataBuilder(new JavaType(RooBuilder.class.getName()), builderAnnotation));
      typeDetailsBuilder.setAnnotations(annotations);

      // Determine the canonical filename
      String physicalLocationCanonicalPath = getPhysicalLocationCanonicalPath(declaredByMetadataId);

      // Check the file doesn't already exist
      Assert.isTrue(!fileManager.exists(physicalLocationCanonicalPath), getPathResolver().getFriendlyName(physicalLocationCanonicalPath) + " already exists");

      // Compute physical location
      PhysicalTypeMetadata toCreate = new DefaultPhysicalTypeMetadata(declaredByMetadataId, physicalLocationCanonicalPath, typeDetailsBuilder.build());

      physicalTypeMetadataProvider.createPhysicalType(toCreate);
   }

   private String getPhysicalLocationCanonicalPath(String physicalTypeIdentifier) {
      Assert.isTrue(PhysicalTypeIdentifier.isValid(physicalTypeIdentifier), "Physical type identifier is invalid");
      PathResolver pathResolver = getPathResolver();
      Assert.notNull(pathResolver, "Cannot computed metadata ID of a type because the path resolver is presently unavailable");
      JavaType javaType = PhysicalTypeIdentifier.getJavaType(physicalTypeIdentifier);
      Path path = PhysicalTypeIdentifier.getPath(physicalTypeIdentifier);
      String relativePath = javaType.getFullyQualifiedTypeName().replace('.', File.separatorChar) + ".java";
      String physicalLocationCanonicalPath = pathResolver.getIdentifier(path, relativePath);
      return physicalLocationCanonicalPath;
   }

   private PathResolver getPathResolver() {
      ProjectMetadata projectMetadata = (ProjectMetadata)metadataService.get(ProjectMetadata.getProjectIdentifier());
      if(projectMetadata == null) {
         return null;
      }
      return projectMetadata.getPathResolver();
   }

}
