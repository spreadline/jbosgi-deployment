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

import java.util.List;

import org.jboss.osgi.deployment.deployer.Deployment;
import org.jboss.osgi.deployment.deployer.DeploymentRegistryService;
import org.jboss.osgi.deployment.interceptor.AbstractLifecycleInterceptorService;
import org.jboss.osgi.deployment.interceptor.InvocationContext;
import org.jboss.osgi.deployment.interceptor.LifecycleInterceptor;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * A basic service that manages bundle lifecycle interceptors.
 * 
 * @author thomas.diesler@jboss.com
 * @since 15-Oct-2009
 */
public class LifecycleInterceptorServiceImpl extends AbstractLifecycleInterceptorService  
{
   public LifecycleInterceptorServiceImpl(BundleContext context)
   {
      super(context);
   }

   @Override
   public List<LifecycleInterceptor> getInterceptorChain()
   {
      return super.getInterceptorChain();
   }

   @Override
   protected InvocationContext getInvocationContext(Bundle bundle)
   {
      DeploymentRegistryService service = getDeploymentRegistryService();
      Deployment dep = service.getDeployment(bundle.getSymbolicName(), bundle.getVersion());
      if (dep == null)
         throw new IllegalStateException("Cannot get deployment for: " + bundle);
      return new InvocationContextImpl(getSystemContext(), bundle, dep.getRoot(), dep);
   }

   private DeploymentRegistryService getDeploymentRegistryService()
   {
      BundleContext context = getSystemContext();
      ServiceReference sref = context.getServiceReference(DeploymentRegistryService.class.getName());
      if (sref == null)
         throw new IllegalStateException("Cannot obtain deployment registry service");
      
      return (DeploymentRegistryService)context.getService(sref);
   }
}