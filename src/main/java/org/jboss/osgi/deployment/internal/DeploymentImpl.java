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

import java.io.Serializable;

import org.jboss.osgi.deployment.deployer.Deployment;
import org.jboss.osgi.spi.util.AttachmentSupport;
import org.jboss.osgi.spi.util.BundleInfo;
import org.jboss.virtual.VirtualFile;

//$Id$

/**
 * An abstraction of a bundle deployment
 * 
 * @author thomas.diesler@jboss.com
 * @since 27-May-2009
 */
public class DeploymentImpl extends AttachmentSupport implements Deployment, Serializable
{
   private static final long serialVersionUID = 6216977125749367927L;
   
   private BundleInfo info;
   private int startLevel;
   private boolean autoStart;

   public DeploymentImpl(BundleInfo info)
   {
      if (info == null)
         throw new IllegalArgumentException("Bundle info cannot be null");
      
      this.info = info;
   }
   
   /**
    * Get the root virtual file
    */
   public VirtualFile getRoot()
   {
      return info.getRoot();
   }

   /**
    * Get the bundle location
    */
   public String getLocation()
   {
      return info.getLocation();
   }

   /**
    * Get the bundle symbolic name
    */
   public String getSymbolicName()
   {
      return info.getSymbolicName();
   }

   /**
    * Get the bundle version
    */
   public String getVersion()
   {
      return info.getVersion().toString();
   }

   /**
    * Get the manifest header for the given key.
    */
   public String getManifestHeader(String key)
   {
      return info.getManifestHeader(key);
   }
   
   /**
    * Get the start level associated with this deployment
    */
   public int getStartLevel()
   {
      return startLevel;
   }

   /**
    * Set the start level associated with this deployment
    */
   public void setStartLevel(int startLevel)
   {
      this.startLevel = startLevel;
   }

   /**
    * Get the autostart flag associated with this deployment
    */
   public boolean isAutoStart()
   {
      return autoStart;
   }

   /**
    * Set the autostart flag associated with this deployment
    */
   public void setAutoStart(boolean autoStart)
   {
      this.autoStart = autoStart;
   }

   @Override
   public boolean equals(Object obj)
   {
      if (!(obj instanceof DeploymentImpl))
         return false;
      
      DeploymentImpl other = (DeploymentImpl)obj;
      return info.equals(other.info);
   }

   @Override
   public int hashCode()
   {
      return toString().hashCode();
   }

   @Override
   public String toString()
   {
      String symbolicName = getSymbolicName();
      String version = getVersion();
      String url = info.getLocation();
      return "[" + symbolicName + "-" + version + ",url=" + url + "]";
   }
}