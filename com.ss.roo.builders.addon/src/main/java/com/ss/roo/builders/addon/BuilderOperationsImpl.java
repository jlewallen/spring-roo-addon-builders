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

@Service
@Component
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
   }
}
