/*
 * JBoss, Home of Professional Open Source
 * Copyright 2005, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.osgi.deployment.deployer;


import org.jboss.logging.Logger;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.startlevel.StartLevel;

/**
 * A {@link DeployerService} that installs/uninstalls the bundles directly on the OSGi framework.
 *
 * @author thomas.diesler@jboss.com
 * @since 27-May-2009
 */
public class SystemDeployerService implements DeployerService
{
   // Provide logging
   private static final Logger log = Logger.getLogger(SystemDeployerService.class);

   private final BundleContext context;
   private final DeploymentRegistryService registry;
   private final StartLevel startLevel;

   public SystemDeployerService(BundleContext context, DeploymentRegistryService registry)
   {
      if (context == null)
         throw new IllegalArgumentException("Null context");
      if (registry == null)
         throw new IllegalArgumentException("Null registry");

      this.context = context;
      this.registry = registry;

      ServiceReference sref = context.getServiceReference(StartLevel.class.getName());
      this.startLevel = sref != null ? (StartLevel)context.getService(sref) : null;
   }

   @Override
   public Bundle deploy(Deployment dep) throws BundleException
   {
      Bundle bundle = deployInternal(dep);
      startInternal(dep);
      return bundle;
   }

   @Override
   public Bundle undeploy(Deployment dep) throws BundleException
   {
      return undeployInternal(dep);
   }

   @Override
   public void deploy(Deployment[] depArr) throws BundleException
   {
      // Install bundle deployments
      for (Deployment dep : depArr)
         deployInternal(dep);

      // Start the installed bundles
      for (Deployment dep : depArr)
         startInternal(dep);
   }

   @Override
   public void undeploy(Deployment[] depArr) throws BundleException
   {
      for (Deployment dep : depArr)
         undeployInternal(dep);
   }

   protected Bundle installBundle(Deployment dep) throws BundleException
   {
      return context.installBundle(dep.getLocation());
   }

   private Bundle deployInternal(Deployment dep) throws BundleException
   {
      log.debugf("Deploy: %s", dep);
      Bundle bundle = installBundle(dep);
      dep.addAttachment(Bundle.class, bundle);

      Integer level = dep.getStartLevel();
      if (startLevel != null && level != null && level > 0)
         startLevel.setBundleStartLevel(bundle, level);

      registry.registerDeployment(dep);
      return bundle;
   }

   private void startInternal(Deployment dep) throws BundleException
   {
      Bundle bundle = dep.getAttachment(Bundle.class);
      if (bundle != null && dep.isAutoStart())
      {
         log.debugf("Start: %s", bundle);

         // Added support for Bundle.START_ACTIVATION_POLICY on start
         // http://issues.apache.org/jira/browse/FELIX-1317
         // bundle.start(Bundle.START_ACTIVATION_POLICY);

         bundle.start();
      }
   }
   
   private Bundle undeployInternal(Deployment dep)
   {
      log.debugf("Undeploy: %s", dep);
      Bundle bundle = dep.getAttachment(Bundle.class);
      if (bundle == null)
      {
         log.warnf("Cannot obtain bundle for: %s", dep);
         return null;
      }

      try
      {
         registry.unregisterDeployment(dep);
         if (bundle.getState() != Bundle.UNINSTALLED)
         {
            log.debugf("Uninstall: %s", bundle);
            bundle.uninstall();
         }
      }
      catch (Throwable ex)
      {
         log.warnf(ex, "Cannot uninstall: %s", dep);
      }
      return bundle;
   }
}