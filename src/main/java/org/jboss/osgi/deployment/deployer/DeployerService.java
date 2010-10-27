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


import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;

/**
 * A Service that can be used to deploy/undeploy bundles or archives to/from the runtime.
 *
 * @author thomas.diesler@jboss.com
 * @since 23-Jan-2009
 */
public interface DeployerService
{
   /**
    * Deploy a bundle
    */
   Bundle deploy(Deployment bundleDep) throws BundleException;

   /**
    * Deploy an array of bundles
    */
   void deploy(Deployment[] bundleDeps) throws BundleException;

   /**
    * Undeploy a bundle
    */
   Bundle undeploy(Deployment bundleDep) throws BundleException;

   /**
    * Undeploy an array of bundles
    */
   void undeploy(Deployment[] bundleDeps) throws BundleException;
}