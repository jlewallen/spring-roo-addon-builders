package com.ss.roo.builders.addon;

import java.util.logging.Logger;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.osgi.service.component.ComponentContext;
import org.springframework.roo.addon.beaninfo.BeanInfoMetadata;
import org.springframework.roo.addon.beaninfo.BeanInfoMetadataProvider;
import org.springframework.roo.classpath.PhysicalTypeIdentifier;
import org.springframework.roo.classpath.PhysicalTypeMetadata;
import org.springframework.roo.classpath.TypeLocationService;
import org.springframework.roo.classpath.itd.AbstractItdMetadataProvider;
import org.springframework.roo.classpath.itd.ItdTypeDetailsProvidingMetadataItem;
import org.springframework.roo.model.JavaType;
import org.springframework.roo.project.Path;
import org.springframework.roo.project.ProjectMetadata;

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
   }

   protected void deactivate(ComponentContext context) {
      metadataDependencyRegistry.deregisterDependency(PhysicalTypeIdentifier.getMetadataIdentiferType(), getProvidesType());
      beanInfoMetadataProvider.removeMetadataTrigger(new JavaType(RooBuilder.class.getName()));
      removeMetadataTrigger(new JavaType(RooBuilder.class.getName()));
   }

   public String getItdUniquenessFilenameSuffix() {
      return "Builder";
   }

   public String getProvidesType() {
      return BuilderMetadata.getMetadataIdentiferType();
   }

   @Override
   protected String createLocalIdentifier(JavaType javaType, Path path) {
      // log.info("Local Identifier: " + javaType + " " + path);
      return BuilderMetadata.createIdentifier(javaType, path);
   }

   @Override
   protected String getGovernorPhysicalTypeIdentifier(String metadataIdentificationString) {
      JavaType javaType = BuilderMetadata.getJavaType(metadataIdentificationString);
      Path path = BuilderMetadata.getPath(metadataIdentificationString);
      // log.info("Governor Identifier: " + javaType + " " + path);
      return PhysicalTypeIdentifier.createIdentifier(javaType, path);
   }

   @Override
   protected ItdTypeDetailsProvidingMetadataItem getMetadata(String metadataIdentificationString, JavaType aspectName, PhysicalTypeMetadata governorPhysicalTypeMetadata, String itdFilename) {
      ProjectMetadata projectMetadata = (ProjectMetadata)metadataService.get(ProjectMetadata.getProjectIdentifier());
      if(projectMetadata == null || !projectMetadata.isValid()) {
         log.info(itdFilename + ": No project metadata");
         return null;
      }

      RooBuilderAnnotationValues annotationValues = new RooBuilderAnnotationValues(governorPhysicalTypeMetadata);
      if(!annotationValues.isAnnotationFound() || annotationValues.getBean() == null) {
         log.info(itdFilename + ": Requires annotation");
         return null;
      }

      JavaType bean = annotationValues.getBean();
      String beanInfoMetadataKey = BeanInfoMetadata.createIdentifier(bean, Path.SRC_MAIN_JAVA);
      BeanInfoMetadata beanInfoMetadata = (BeanInfoMetadata)metadataService.get(beanInfoMetadataKey);

      if(beanInfoMetadata == null || !beanInfoMetadata.isValid()) {
         return null;
      }

      metadataDependencyRegistry.registerDependency(beanInfoMetadataKey, metadataIdentificationString);

      return new BuilderMetadata(metadataIdentificationString, aspectName, governorPhysicalTypeMetadata, beanInfoMetadata);
   }

}
