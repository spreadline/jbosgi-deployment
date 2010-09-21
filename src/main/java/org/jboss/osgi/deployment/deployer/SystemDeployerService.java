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

//$Id$

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.jboss.logging.Logger;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;
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

   private BundleContext context;
   private DeploymentRegistryService registry;

   public SystemDeployerService(BundleContext context)
   {
      if (context == null)
         throw new IllegalArgumentException("Null context");

      this.context = context;
   }

   @Override
   public Bundle deploy(Deployment dep) throws BundleException
   {
      Bundle bundle = installBundle(dep);
      return bundle;
   }

   @Override
   public Bundle undeploy(Deployment dep) throws BundleException
   {
      Bundle bundle = uninstallBundle(dep);
      return bundle;
   }

   @Override
   public void deploy(Deployment[] depArr) throws BundleException
   {
      Map<Deployment, Bundle> bundleMap = new HashMap<Deployment, Bundle>();
      for (Deployment dep : depArr)
      {
         Bundle bundle = installBundle(dep);
         bundleMap.put(dep, bundle);
      }

      // Start the installed bundles
      for (Entry<Deployment, Bundle> entry : bundleMap.entrySet())
      {
         Deployment dep = entry.getKey();
         Bundle bundle = entry.getValue();
         if (dep.isAutoStart())
         {
            try
            {
               log.debug("Start: " + bundle);

               // Added support for Bundle.START_ACTIVATION_POLICY on start
               // http://issues.apache.org/jira/browse/FELIX-1317
               // bundle.start(Bundle.START_ACTIVATION_POLICY);

               bundle.start();
            }
            catch (BundleException ex)
            {
               log.error("Cannot start bundle: " + bundle, ex);
            }
         }
      }
   }

   @Override
   public void undeploy(Deployment[] depArr) throws BundleException
   {
      for (Deployment dep : depArr)
      {
         uninstallBundle(dep);
      }
   }

   protected Bundle installBundleInternal(Deployment dep) throws BundleException
   {
      return context.installBundle(dep.getLocation());
   }

   private Bundle installBundle(Deployment dep) throws BundleException
   {
      log.debug("Install: " + dep.getLocation());
      Bundle bundle = installBundleInternal(dep);

      Integer level = dep.getStartLevel();
      StartLevel startLevel = getStartLevel();
      if (level != null && level > 0)
         startLevel.setBundleStartLevel(bundle, level);

      DeploymentRegistryService registry = getDeploymentRegistryService();
      registry.registerDeployment(dep);

      return bundle;
   }

   private Bundle uninstallBundle(Deployment dep) throws BundleException
   {
      Bundle bundle = getBundle(dep);
      if (bundle != null)
      {
         log.debug("Uninstall: " + bundle);
         DeploymentRegistryService registry = getDeploymentRegistryService();
         registry.unregisterDeployment(dep);
         bundle.uninstall();
      }
      else
      {
         log.warn("Cannot obtain bundle for: " + dep);
      }
      return bundle;
   }

   private Bundle getBundle(Deployment dep)
   {
      String symbolicName = dep.getSymbolicName();
      Version version = Version.parseVersion(dep.getVersion());

      Bundle bundle = null;
      for (Bundle aux : context.getBundles())
      {
         if (aux.getSymbolicName().equals(symbolicName))
         {
            Version auxVersion = aux.getVersion();
            if (version.equals(auxVersion))
            {
               bundle = aux;
               break;
            }
         }
      }
      return bundle;
   }

   private DeploymentRegistryService getDeploymentRegistryService()
   {
      if (registry == null)
      {
         ServiceReference sref = context.getServiceReference(DeploymentRegistryService.class.getName());
         if (sref == null)
            throw new IllegalStateException("Cannot obtain DeploymentRegistryService");
         registry = (DeploymentRegistryService)context.getService(sref);
      }
      return registry;
   }

   private StartLevel getStartLevel()
   {
      ServiceReference sref = context.getServiceReference(StartLevel.class.getName());
      return sref != null ? (StartLevel)context.getService(sref) : null;
   }
}