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
package org.jboss.osgi.deployment;

//$Id$

import java.util.Properties;

import javax.management.MBeanServer;

import org.jboss.osgi.deployment.deployer.DeployerService;
import org.jboss.osgi.deployment.deployer.DeploymentRegistryService;
import org.jboss.osgi.deployment.interceptor.LifecycleInterceptorService;
import org.jboss.osgi.deployment.internal.DeploymentRegistryServiceImpl;
import org.jboss.osgi.deployment.internal.LifecycleInterceptorServiceImpl;
import org.jboss.osgi.deployment.internal.SystemDeployerService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

/**
 * An activator for the deployment services.
 * 
 * This can be used like a BundleActivator altough the deployment services 
 * are not installed as a bundle.
 * 
 * @author thomas.diesler@jboss.com
 * @since 15-Oct-2009
 */
public class DeploymentServicesActivator
{
   public void start(BundleContext context)
   {
      if (context == null)
         throw new IllegalArgumentException("Null BundleContext");

      // Register the DeploymentRegistryService
      DeploymentRegistryService registry = new DeploymentRegistryServiceImpl(context);
      context.registerService(DeploymentRegistryService.class.getName(), registry, null);

      // Register the SystemDeployerService
      Properties props = new Properties();
      props.put("provider", "system");
      final SystemDeployerService deployerService = new SystemDeployerService(context);
      context.registerService(DeployerService.class.getName(), deployerService, props);

      // Register the lifecycle interceptor related services
      LifecycleInterceptorService service = new LifecycleInterceptorServiceImpl(context);
      context.registerService(LifecycleInterceptorService.class.getName(), service, null);

      // Track other DeployerService implementations and register as MBean
      ServiceTracker serviceTracker = new ServiceTracker(context, DeployerService.class.getName(), null)
      {
         @Override
         public Object addingService(ServiceReference reference)
         {
            DeployerService service = (DeployerService)super.addingService(reference);
            ServiceReference sref = context.getServiceReference(MBeanServer.class.getName());
            if (sref != null)
            {
               MBeanServer mbeanServer = (MBeanServer)context.getService(sref);
               deployerService.registerDeployerServiceMBean(context, mbeanServer);
            }
            return service;
         }

         @Override
         public void removedService(ServiceReference reference, Object service)
         {
            ServiceReference sref = context.getServiceReference(MBeanServer.class.getName());
            if (sref != null)
            {
               MBeanServer mbeanServer = (MBeanServer)context.getService(sref);
               deployerService.unregisterDeployerServiceMBean(mbeanServer);
            }
            super.removedService(reference, service);
         }
      };
      serviceTracker.open();
   }

   public void stop(BundleContext context)
   {
   }
}