package net.siudek.media.host;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Configuration;
// import org.springframework.shell.command.annotation.CommandScan;

import net.siudek.media.RootPackageMarker;

@Configuration
// @CommandScan(basePackageClasses = RootPackageMarker.class)
@ComponentScan(basePackageClasses = RootPackageMarker.class)
class HostConfiguration {
  
}
