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


import java.util.HashSet;
import java.util.Set;

import org.jboss.osgi.deployment.interceptor.InvocationContext;
import org.jboss.osgi.deployment.interceptor.LifecycleInterceptor;
import org.jboss.osgi.deployment.interceptor.LifecycleInterceptorException;

/**
 * A wrapper around lifecycle interceptors.
 * 
 * @author thomas.diesler@jboss.com
 * @since 15-Oct-2009
 */
public class InterceptorWrapper implements LifecycleInterceptor  
{
   private LifecycleInterceptor delegate;

   public InterceptorWrapper(LifecycleInterceptor delegate)
   {
      if (delegate == null)
         throw new IllegalArgumentException("Null interceptor");
      
      this.delegate = delegate;
   }

   public Set<Class<?>> getInput()
   {
      return delegate.getInput();
   }

   public Set<Class<?>> getOutput()
   {
      return delegate.getOutput();
   }

   public int getRelativeOrder()
   {
      return delegate.getRelativeOrder();
   }

   public void invoke(int state, InvocationContext context) throws LifecycleInterceptorException
   {
      delegate.invoke(state, context);
   }

   public String toLongString()
   {
      String classToken = getLastNameToken(delegate.getClass());
      
      Set<String> input = null;
      if (getInput() != null)
      {
         input = new HashSet<String>();
         for(Class<?> aux : getInput())
            input.add(getLastNameToken(aux));
      }
      
      Set<String> output = null; 
      if (getOutput() != null)
      {
         output = new HashSet<String>();
         for(Class<?> aux : getOutput())
            output.add(getLastNameToken(aux));
      }
      
      return "[" + classToken + ",order=" + getRelativeOrder() + ",input=" + input + ",output=" + output + "]";
   }

   @Override
   public String toString()
   {
      String className = delegate.getClass().getName();
      return "[" + className + ",order=" + getRelativeOrder() + "]";
   }

   private String getLastNameToken(Class<?> clazz)
   {
      String token = clazz.getName();
      return token.substring(token.lastIndexOf(".") + 1);
   }
}