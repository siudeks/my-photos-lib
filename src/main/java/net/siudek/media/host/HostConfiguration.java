package net.siudek.media.host;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
// import org.springframework.shell.command.annotation.CommandScan;

@Configuration
// @CommandScan(basePackageClasses = HostConfiguration.class)
@ComponentScan(basePackageClasses = HostConfiguration.class)
class HostConfiguration {
  
}
