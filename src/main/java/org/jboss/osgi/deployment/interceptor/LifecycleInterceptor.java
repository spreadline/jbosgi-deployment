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
package org.jboss.osgi.deployment.interceptor;

//$Id$

import java.util.Set;

/**
 * An OSGi bundle lifecycle interceptor.
 * 
 * @author thomas.diesler@jboss.com
 * @since 15-Oct-2009
 */
public interface LifecycleInterceptor
{
   /** The default relative order: 1000 */
   public static final int RELATIVE_ORDER_DEFAULT = 1000;

   /**
    * Get the relative order of this interceptor
    */
   int getRelativeOrder();

   /**
    * Get the required set of inputs. 
    * 
    * @return null if there are no inputs required
    */
   Set<Class<?>> getInput();
   
   /**
    * Get the provided set of outputs. 
    * 
    * @return null if there are no outputs provided
    */
   Set<Class<?>> getOutput();
   
   /**
    * Called by the {@link LifecycleInterceptorService} when the
    * given bundle is about to change to the given state
    * 
    * @param state The future state of the bundle
    * @param context The interceptor context
    * @throws LifecycleInterceptorException if the invocation of the interceptor fails 
    */
   void invoke(int state, InvocationContext context) throws LifecycleInterceptorException;
}