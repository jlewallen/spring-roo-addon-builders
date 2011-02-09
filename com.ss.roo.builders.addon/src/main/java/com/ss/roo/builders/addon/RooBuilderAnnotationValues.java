package com.ss.roo.builders.addon;

import org.springframework.roo.classpath.PhysicalTypeMetadata;
import org.springframework.roo.classpath.details.annotations.populator.AbstractAnnotationValues;
import org.springframework.roo.classpath.details.annotations.populator.AutoPopulate;
import org.springframework.roo.classpath.details.annotations.populator.AutoPopulationUtils;
import org.springframework.roo.model.JavaType;

public class RooBuilderAnnotationValues extends AbstractAnnotationValues {

   public RooBuilderAnnotationValues(PhysicalTypeMetadata governorPhysicalTypeMetadata) {
      super(governorPhysicalTypeMetadata, new JavaType(RooBuilder.class.getName()));
      AutoPopulationUtils.populate(this, annotationMetadata);
   }

   @AutoPopulate
   private JavaType bean;

   public JavaType getBean() {
      return bean;
   }

}
