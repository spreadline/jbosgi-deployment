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

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.jboss.osgi.deployment.deployer.Deployment;
import org.jboss.osgi.spi.util.AttachmentSupport;
import org.jboss.osgi.vfs.AbstractVFS;
import org.jboss.osgi.vfs.VFSUtils;
import org.jboss.osgi.vfs.VirtualFile;
import org.osgi.framework.Version;


/**
 * An abstraction of a bundle deployment
 *
 * @author thomas.diesler@jboss.com
 * @since 27-May-2009
 */
public class DeploymentImpl extends AttachmentSupport implements Deployment, Serializable
{
   private static final long serialVersionUID = 6216977125749367927L;

   private transient VirtualFile rootFile;
   private URL rootURL;
   private Manifest manifest;
   private String location;
   private String symbolicName;
   private String version;
   private Integer startLevel;
   private boolean autoStart;
   private boolean update;

   public DeploymentImpl(VirtualFile rootFile, String location, String symbolicName, Version version)
   {
      if (rootFile == null)
         throw new IllegalArgumentException("Null rootFile");

      if (location == null)
         location = rootFile.getPathName();
      if (symbolicName == null)
         symbolicName = rootFile.getName();
      if (version == null)
         version = Version.emptyVersion;

      this.rootFile = rootFile;
      this.location = location;
      this.symbolicName = symbolicName;
      this.version = version.toString();

      try
      {
         this.rootURL = rootFile.toURL();
      }
      catch (IOException ex)
      {
         throw new IllegalStateException("Cannot obtain root URL", ex);
      }
   }

   @Override
   public VirtualFile getRoot()
   {
      if (rootFile == null)
      {
         try
         {
            rootFile = AbstractVFS.getRoot(rootURL);
         }
         catch (IOException ex)
         {
            throw new IllegalStateException("Cannot obtain rootFile", ex);
         }
      }
      return rootFile;
   }

   @Override
   public String getLocation()
   {
      return location;
   }

   @Override
   public String getSymbolicName()
   {
      return symbolicName;
   }

   @Override
   public String getVersion()
   {
      return version;
   }

   @Override
   public String getManifestHeader(String key) throws IOException
   {
      if (manifest == null)
      {
         try
         {
            manifest = VFSUtils.getManifest(getRoot());
            if (manifest == null)
               throw new IOException("Cannot get manifest from: " + getRoot());
         }
         catch (IOException ex)
         {
            throw new IOException("Cannot get manifest from: " + getRoot(), ex);
         }
      }
      Attributes atts = manifest.getMainAttributes();
      return atts.getValue(key);
   }

   @Override
   public Integer getStartLevel()
   {
      return startLevel;
   }

   @Override
   public void setStartLevel(Integer startLevel)
   {
      if (startLevel == null || startLevel < 1)
         throw new IllegalArgumentException("Start level must be greater than one: " + startLevel);

      this.startLevel = startLevel;
   }

   @Override
   public boolean isAutoStart()
   {
      return autoStart;
   }

   @Override
   public void setAutoStart(boolean autoStart)
   {
      this.autoStart = autoStart;
   }

   @Override
   public boolean isBundleUpdate()
   {
      return update;
   }

   @Override
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
      boolean matchLocation = getLocation().equals(other.getLocation());
      boolean matchName = getSymbolicName().equals(other.getSymbolicName());
      boolean matchVersion = getVersion().equals(other.getVersion());
      return matchLocation && matchName && matchVersion;
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
      String location = getLocation();
      return "[" + symbolicName + "-" + version + ",location=" + location + "]";
   }
}