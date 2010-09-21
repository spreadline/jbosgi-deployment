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

//$Id: SystemDeployerService.java 90894 2009-07-07 11:58:40Z thomas.diesler@jboss.com $

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.jboss.logging.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Version;

/**
 * A Service to register/unregister bundle deployments.
 *
 * @author thomas.diesler@jboss.com
 * @since 27-May-2009
 */
public class DefaultDeploymentRegistryService implements DeploymentRegistryService
{
   // Provide logging
   private static final Logger log = Logger.getLogger(DefaultDeploymentRegistryService.class);

   private List<Deployment> deployments = new CopyOnWriteArrayList<Deployment>();

   public DefaultDeploymentRegistryService(BundleContext context)
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
         Version auxVersion = Version.parseVersion(auxDep.getVersion());
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
      String urlLocation = url.toExternalForm();
      for (Deployment auxDep : deployments)
      {
         String auxLocation = auxDep.getLocation();
         if (urlLocation.equals(auxLocation))
         {
            dep = auxDep;
            break;
         }
      }

      return dep;
   }
}