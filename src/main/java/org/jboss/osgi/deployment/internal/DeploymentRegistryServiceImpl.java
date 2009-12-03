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

//$Id: SystemDeployerService.java 90894 2009-07-07 11:58:40Z thomas.diesler@jboss.com $

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.jboss.osgi.deployment.deployer.DeployerService;
import org.jboss.osgi.deployment.deployer.Deployment;
import org.jboss.osgi.deployment.deployer.DeploymentRegistryService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link DeployerService} that installs/uninstalls the bundles directly on the OSGi framework without going through the MC registered deployers.
 * 
 * @author thomas.diesler@jboss.com
 * @since 27-May-2009
 */
public class DeploymentRegistryServiceImpl implements DeploymentRegistryService
{
   // Provide logging
   private Logger log = LoggerFactory.getLogger(DeploymentRegistryServiceImpl.class);
   
   private List<Deployment> deployments = new CopyOnWriteArrayList<Deployment>();

   public DeploymentRegistryServiceImpl(BundleContext context)
   {
   }

   public List<Deployment> getDeployments()
   {
      return Collections.unmodifiableList(deployments);
   }

   public void registerDeployment(Deployment dep)
   {
      log.debug("Register: " + dep);
      deployments.add(dep);
   }

   public void unregisterDeployment(Deployment dep)
   {
      log.debug("Unregister: " + dep);
      deployments.remove(dep);
   }
   
   public Deployment getDeployment(String symbolicName, Version version)
   {
      if (symbolicName == null)
         throw new IllegalArgumentException("Cannot obtain bundle deployment for null symbolic name");

      Deployment dep = null;
      for (Deployment auxDep : deployments)
      {
         String auxName = auxDep.getSymbolicName();
         String auxVersion = auxDep.getVersion();
         if (symbolicName.equals(auxName) && version.equals(auxVersion))
         {
            dep = auxDep;
            break;
         }
      }

      return dep;
   }

   public Deployment getDeployment(URL url)
   {
      if (url == null)
         throw new IllegalArgumentException("Cannot obtain bundle deployment for: null");

      Deployment dep = null;
      for (Deployment auxDep : deployments)
      {
         if (url.equals(auxDep.getLocation()))
         {
            dep = auxDep;
            break;
         }
      }

      return dep;
   }
}