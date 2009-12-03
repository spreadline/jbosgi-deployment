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
package org.jboss.osgi.deployment.internal;

//$Id$

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.StandardMBean;

import org.jboss.osgi.deployment.deployer.AbstractDeployerService;
import org.jboss.osgi.deployment.deployer.DeployerService;
import org.jboss.osgi.deployment.deployer.Deployment;
import org.jboss.osgi.deployment.deployer.DeploymentRegistryService;
import org.jboss.osgi.spi.util.ExportedPackageHelper;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.service.startlevel.StartLevel;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link DeployerService} that installs/uninstalls the bundles directly on the OSGi framework.
 * 
 * @author thomas.diesler@jboss.com
 * @since 27-May-2009
 */
public class SystemDeployerService extends AbstractDeployerService
{
   // Provide logging
   private Logger log = LoggerFactory.getLogger(SystemDeployerService.class);
   
   private BundleContext context;
   private ServiceTracker startLevelTracker;

   public SystemDeployerService(BundleContext context)
   {
      this.context = context;

      // Track the MBeanServer and register the DeployerService
      ServiceTracker jmxTracker = new ServiceTracker(context, MBeanServer.class.getName(), null)
      {
         @Override
         public Object addingService(ServiceReference reference)
         {
            MBeanServer mbeanServer = (MBeanServer)super.addingService(reference);
            registerDeployerServiceMBean(context, mbeanServer);
            return mbeanServer;
         }

         @Override
         public void removedService(ServiceReference reference, Object service)
         {
            MBeanServer mbeanServer = (MBeanServer)service;
            unregisterDeployerServiceMBean(mbeanServer);
            super.removedService(reference, service);
         }
      };
      jmxTracker.open();
   }

   public void deploy(Deployment[] depArr) throws BundleException
   {
      DeploymentRegistryService registry = getDeploymentRegistry();

      List<Bundle> resolvableBundles = new ArrayList<Bundle>();
      Map<Deployment, Bundle> bundleMap = new HashMap<Deployment, Bundle>();
      
      for (Deployment dep : depArr)
      {
         try
         {
            log.debug("Install: " + dep.getLocation());
            
            String location = dep.getLocation().toExternalForm();
            Bundle bundle = context.installBundle(location);

            bundleMap.put(dep, bundle);
            if (dep.isAutoStart())
               resolvableBundles.add(bundle);

            registry.registerDeployment(dep);
         }
         catch (BundleException ex)
         {
            log.error("Cannot install bundle: " + dep, ex);
         }
      }

      // Resolve the installed bundles through the PackageAdmin
      ServiceReference packageAdminRef = context.getServiceReference(PackageAdmin.class.getName());
      if (packageAdminRef != null && resolvableBundles.isEmpty() == false)
      {
         PackageAdmin packageAdmin = (PackageAdmin)context.getService(packageAdminRef);
         Bundle[] resolvableBundleArr = new Bundle[resolvableBundles.size()];
         resolvableBundles.toArray(resolvableBundleArr);
         packageAdmin.resolveBundles(resolvableBundleArr);
      }
      
      // Start the installed bundles
      for (Entry<Deployment, Bundle> entry : bundleMap.entrySet())
      {
         Deployment dep = entry.getKey();
         Bundle bundle = entry.getValue();

         StartLevel startLevel = getStartLevel();
         if (dep.getStartLevel() > 0)
         {
            startLevel.setBundleStartLevel(bundle, dep.getStartLevel());
         }

         if (dep.isAutoStart())
         {
            int state = bundle.getState();
            if (state == Bundle.RESOLVED || packageAdminRef == null)
            {
               try
               {
                  log.debug("Start: " + bundle);

                  // Added support for Bundle.START_ACTIVATION_POLICY on start
                  // http://issues.apache.org/jira/browse/FELIX-1317
                  // bundle.start(Bundle.START_ACTIVATION_POLICY);

                  bundle.start();

                  ExportedPackageHelper packageHelper = new ExportedPackageHelper(context);
                  packageHelper.logExportedPackages(bundle);
               }
               catch (BundleException ex)
               {
                  log.error("Cannot start bundle: " + bundle, ex);
               }
            }
         }
      }
   }

   public void undeploy(Deployment[] depArr) throws BundleException
   {
      DeploymentRegistryService registry = getDeploymentRegistry();

      for (Deployment dep : depArr)
      {
         Bundle bundle = getBundle(dep);
         if (bundle != null)
         {
            log.debug("Uninstall: " + bundle);
            
            registry.unregisterDeployment(dep);

            bundle.uninstall();
         }
         else
         {
            log.warn("Cannot obtain bundle for: " + dep);
         }
      }
   }

   public void deploy(URL url) throws BundleException
   {
      Deployment dep = createDeployment(url);
      deploy(new Deployment[] { dep });
   }

   public void undeploy(URL url) throws BundleException
   {
      DeploymentRegistryService registry = getDeploymentRegistry();
      Deployment dep = registry.getDeployment(url);
      if (dep != null)
      {
         undeploy(new Deployment[] { dep });
      }
      else
      {
         log.warn("Cannot find deployment for: " + url);
      }
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

   private DeploymentRegistryService getDeploymentRegistry()
   {
      ServiceReference sref = context.getServiceReference(DeploymentRegistryService.class.getName());
      if (sref == null)
         throw new IllegalStateException("Cannot obtain DeploymentRegistryService");
      
      return (DeploymentRegistryService)context.getService(sref);
   }

   private StartLevel getStartLevel()
   {
      if (startLevelTracker == null)
      {
         startLevelTracker = new ServiceTracker(context, StartLevel.class.getName(), null);
         startLevelTracker.open();
      }
      return (StartLevel)startLevelTracker.getService();
   }

   public void registerDeployerServiceMBean(BundleContext context, MBeanServer mbeanServer)
   {
      try
      {
         // Get the DeployerService with the highest ranking
         ServiceReference sref = context.getServiceReference(DeployerService.class.getName());
         if (sref == null)
            throw new IllegalStateException("Cannot obtain deployer service");

         // Unregister the DeployerService with a potentialy lower ranking
         if (mbeanServer.isRegistered(DeployerService.MBEAN_DEPLOYER_SERVICE))
            mbeanServer.unregisterMBean(DeployerService.MBEAN_DEPLOYER_SERVICE);

         // Register the DeployerService with the highest ranking
         DeployerService service = (DeployerService)context.getService(sref);
         StandardMBean mbean = new StandardMBean(service, DeployerService.class);
         mbeanServer.registerMBean(mbean, DeployerService.MBEAN_DEPLOYER_SERVICE);
      }
      catch (JMException ex)
      {
         throw new IllegalStateException("Cannot register DeployerService MBean", ex);
      }
   }

   public void unregisterDeployerServiceMBean(MBeanServer mbeanServer)
   {
      try
      {
         if (mbeanServer.isRegistered(DeployerService.MBEAN_DEPLOYER_SERVICE))
            mbeanServer.unregisterMBean(DeployerService.MBEAN_DEPLOYER_SERVICE);
      }
      catch (JMException ex)
      {
         log.error("Cannot unregister DeployerService MBean", ex);
      }
   }
}