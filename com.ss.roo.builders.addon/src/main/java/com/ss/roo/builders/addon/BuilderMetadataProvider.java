package com.ss.roo.builders.addon;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.osgi.service.component.ComponentContext;
import org.springframework.roo.addon.beaninfo.BeanInfoMetadata;
import org.springframework.roo.addon.beaninfo.BeanInfoMetadataProvider;
import org.springframework.roo.classpath.PhysicalTypeDetails;
import org.springframework.roo.classpath.PhysicalTypeIdentifier;
import org.springframework.roo.classpath.PhysicalTypeMetadata;
import org.springframework.roo.classpath.TypeLocationService;
import org.springframework.roo.classpath.details.ClassOrInterfaceTypeDetails;
import org.springframework.roo.classpath.details.FieldMetadata;
import org.springframework.roo.classpath.details.MemberFindingUtils;
import org.springframework.roo.classpath.details.annotations.AnnotationMetadata;
import org.springframework.roo.classpath.itd.AbstractItdMetadataProvider;
import org.springframework.roo.classpath.itd.ItdTypeDetailsProvidingMetadataItem;
import org.springframework.roo.model.JavaType;
import org.springframework.roo.project.Path;
import org.springframework.roo.project.ProjectMetadata;

import com.ss.roo.builders.addon.RooBuildable;
import com.ss.roo.builders.addon.RooBuilder;

@Service
@Component(immediate = true)
public final class BuilderMetadataProvider extends AbstractItdMetadataProvider {
   private Logger log = Logger.getLogger(getClass().getName());

   @Reference
   private BeanInfoMetadataProvider beanInfoMetadataProvider;
   @Reference
   private TypeLocationService typeLocationService;

   protected void activate(ComponentContext context) {
      metadataDependencyRegistry.registerDependency(PhysicalTypeIdentifier.getMetadataIdentiferType(), getProvidesType());
      beanInfoMetadataProvider.addMetadataTrigger(new JavaType(RooBuilder.class.getName()));
      addMetadataTrigger(new JavaType(RooBuilder.class.getName()));
      log.info("BuilderMetadataProvider.activated");
   }

   protected void deactivate(ComponentContext context) {
      metadataDependencyRegistry.deregisterDependency(PhysicalTypeIdentifier.getMetadataIdentiferType(), getProvidesType());
      beanInfoMetadataProvider.removeMetadataTrigger(new JavaType(RooBuilder.class.getName()));
      removeMetadataTrigger(new JavaType(RooBuilder.class.getName()));
      log.info("BuilderMetadataProvider.deactivated");
   }

   public String getItdUniquenessFilenameSuffix() {
      return "Builder";
   }

   public String getProvidesType() {
      return BuilderMetadata.getMetadataIdentiferType();
   }

   @Override
   protected String createLocalIdentifier(JavaType javaType, Path path) {
      return BuilderMetadata.createIdentifier(javaType, path);
   }

   @Override
   protected String getGovernorPhysicalTypeIdentifier(String metadataIdentificationString) {
      JavaType javaType = BuilderMetadata.getJavaType(metadataIdentificationString);
      Path path = BuilderMetadata.getPath(metadataIdentificationString);
      return PhysicalTypeIdentifier.createIdentifier(javaType, path);
   }

   @Override
   protected ItdTypeDetailsProvidingMetadataItem getMetadata(String metadataIdentificationString, JavaType aspectName, PhysicalTypeMetadata governorPhysicalTypeMetadata, String itdFilename) {
      ProjectMetadata projectMetadata = (ProjectMetadata)metadataService.get(ProjectMetadata.getProjectIdentifier());
      if(projectMetadata == null || !projectMetadata.isValid()) {
         log.info("no project metadata");
         return null;
      }

      log.info("checking...");

      Map<FieldMetadata, Boolean> declaredFields = new LinkedHashMap<FieldMetadata, Boolean>();
      PhysicalTypeDetails physicalTypeDetails = governorPhysicalTypeMetadata.getMemberHoldingTypeDetails();
      if(physicalTypeDetails != null && physicalTypeDetails instanceof ClassOrInterfaceTypeDetails) {
         ClassOrInterfaceTypeDetails governorTypeDetails = (ClassOrInterfaceTypeDetails)physicalTypeDetails;
         for(FieldMetadata field : governorTypeDetails.getDeclaredFields()) {
            declaredFields.put(field, (projectMetadata.isGaeEnabled() && isGaeInterested(field)));
         }

         String key = BeanInfoMetadata.createIdentifier(governorTypeDetails.getName(), Path.SRC_MAIN_JAVA);
         BeanInfoMetadata beanInfo = (BeanInfoMetadata)beanInfoMetadataProvider.get(key);
         return new BuilderMetadata(metadataIdentificationString, aspectName, governorPhysicalTypeMetadata, declaredFields, beanInfo);
      }

      return null;
   }

   private boolean isGaeInterested(FieldMetadata field) {
      try {
         ClassOrInterfaceTypeDetails classOrInterfaceTypeDetails = typeLocationService.getClassOrInterface(field.getFieldType());
         AnnotationMetadata annotation = MemberFindingUtils.getTypeAnnotation(classOrInterfaceTypeDetails, new JavaType("com.ss.roo.addon.annotations.RooBuilder"));
         return annotation != null;
      }
      catch(Exception e) {
         // Don't need to know what happened so just return false;
         return false;
      }
   }

}
