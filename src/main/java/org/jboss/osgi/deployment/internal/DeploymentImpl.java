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

import java.io.Serializable;

import org.jboss.osgi.deployment.deployer.Deployment;
import org.jboss.osgi.spi.util.AttachmentSupport;
import org.jboss.osgi.spi.util.BundleInfo;
import org.jboss.osgi.vfs.VirtualFile;

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
   private boolean update;

   public DeploymentImpl(BundleInfo info)
   {
      if (info == null)
         throw new IllegalArgumentException("Bundle info cannot be null");
      
      this.info = info;
   }
   
   public VirtualFile getRoot()
   {
      return info.getRoot();
   }

   public String getLocation()
   {
      return info.getLocation();
   }

   public String getSymbolicName()
   {
      return info.getSymbolicName();
   }

   public String getVersion()
   {
      return info.getVersion().toString();
   }

   public String getManifestHeader(String key)
   {
      return info.getManifestHeader(key);
   }
   
   public int getStartLevel()
   {
      return startLevel;
   }

   public void setStartLevel(int startLevel)
   {
      this.startLevel = startLevel;
   }

   public boolean isAutoStart()
   {
      return autoStart;
   }

   public void setAutoStart(boolean autoStart)
   {
      this.autoStart = autoStart;
   }

   public boolean isBundleUpdate()
   {
      return update;
   }

   public void setBundleUpdate(boolean update)
   {
      this.update = update;
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