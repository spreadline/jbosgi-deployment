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
package org.jboss.test.osgi.deployment.interceptor;


import java.io.File;
import java.io.InputStream;
import java.util.Dictionary;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.BundleListener;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

/**
 * A mock bundle context
 * 
 * @author thomas.diesler@jboss.com
 * @since 26-Oct-2009
 */
class MockBundleContext implements BundleContext
{
   public void addBundleListener(BundleListener listener)
   {
   }

   public void addFrameworkListener(FrameworkListener listener)
   {
   }

   public void addServiceListener(ServiceListener listener)
   {
   }

   public void addServiceListener(ServiceListener listener, String filter) throws InvalidSyntaxException
   {
   }

   public Filter createFilter(String filter) throws InvalidSyntaxException
   {
      return null;
   }

   public ServiceReference[] getAllServiceReferences(String clazz, String filter) throws InvalidSyntaxException
   {
      return null;
   }

   public Bundle getBundle()
   {
      return null;
   }

   public Bundle getBundle(long id)
   {
      return null;
   }

   public Bundle[] getBundles()
   {
      return null;
   }

   public File getDataFile(String filename)
   {
      return null;
   }

   public String getProperty(String key)
   {
      return null;
   }

   public Object getService(ServiceReference reference)
   {
      return null;
   }

   public ServiceReference getServiceReference(String clazz)
   {
      return null;
   }

   public ServiceReference[] getServiceReferences(String clazz, String filter) throws InvalidSyntaxException
   {
      return null;
   }

   public Bundle installBundle(String location) throws BundleException
   {
      return null;
   }

   public Bundle installBundle(String location, InputStream input) throws BundleException
   {
      return null;
   }

   @SuppressWarnings("rawtypes")
   public ServiceRegistration registerService(String[] clazzes, Object service, Dictionary properties)
   {
      return null;
   }

   @SuppressWarnings("rawtypes")
   public ServiceRegistration registerService(String clazz, Object service, Dictionary properties)
   {
      return null;
   }

   public void removeBundleListener(BundleListener listener)
   {
   }

   public void removeFrameworkListener(FrameworkListener listener)
   {
   }

   public void removeServiceListener(ServiceListener listener)
   {
   }

   public boolean ungetService(ServiceReference reference)
   {
      return false;
   }
}