package com.ss.roo.builders.addon;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.springframework.roo.model.JavaType;
import org.springframework.roo.project.ProjectOperations;
import org.springframework.roo.shell.CliAvailabilityIndicator;
import org.springframework.roo.shell.CliCommand;
import org.springframework.roo.shell.CliOption;
import org.springframework.roo.shell.CommandMarker;

@Component
@Service
public class BuilderCommands implements CommandMarker {
   @Reference
   private BuilderOperations builderOperations;
   @Reference
   private ProjectOperations projectOperations;

   @CliAvailabilityIndicator({ "builder" })
   public boolean isAvailable() {
      return projectOperations.isProjectAvailable();
   }

   @CliCommand(value = "builder", help = "Creates a builder for the specified class")
   public void newBuilder(
         @CliOption(key = "class", mandatory = false, unspecifiedDefaultValue = "*", optionContext = "update,project", help = "The name of the class to generate a builder for") JavaType bean) {

      builderOperations.newBuilder(bean);
   }

}
