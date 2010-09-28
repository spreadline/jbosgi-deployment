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
import java.net.URL;

import org.jboss.osgi.vfs.AbstractVFS;
import org.jboss.osgi.vfs.VirtualFile;
import org.osgi.framework.Version;


/**
 * An abstraction of a bundle deployment
 *
 * @author thomas.diesler@jboss.com
 * @since 27-May-2009
 */
public class VirtualFileDeployment extends AbstractDeployment
{
   private static final long serialVersionUID = -3331145101532992381L;
   
   private transient VirtualFile rootFile;
   private URL rootURL;

   public VirtualFileDeployment(VirtualFile rootFile, String location, String symbolicName, Version version)
   {
      super(location, symbolicName, version);
      if (rootFile == null)
         throw new IllegalArgumentException("Null rootFile");

      try
      {
         this.rootFile = rootFile;
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
}