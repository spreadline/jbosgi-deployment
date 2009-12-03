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
 * You should have received a copy of the GNU Lesser General Public-
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.osgi.deployment.deployer;

import java.net.URL;

import org.jboss.osgi.spi.Attachments;
import org.jboss.virtual.VirtualFile;


//$Id$

/**
 * An abstraction of a bundle deployment
 * 
 * @author thomas.diesler@jboss.com
 * @since 27-May-2009
 */
public interface Deployment extends Attachments
{
   /**
    * Get the root virtual file
    */
   public VirtualFile getRoot();
   
   /**
    * Get the bundle location
    */
   public URL getLocation();

   /**
    * Get the bundle symbolic name
    */
   public String getSymbolicName();

   /**
    * Get the bundle version
    */
   public String getVersion();

   /**
    * Get the manifest header for the given key.
    */
   public String getManifestHeader(String key);
   
   /**
    * Get the start level associated with this deployment
    */
   public int getStartLevel();

   /**
    * Set the start level associated with this deployment
    */
   public void setStartLevel(int startLevel);

   /**
    * Get the autostart flag associated with this deployment
    */
   public boolean isAutoStart();

   /**
    * Set the autostart flag associated with this deployment
    */
   public void setAutoStart(boolean autoStart);
}