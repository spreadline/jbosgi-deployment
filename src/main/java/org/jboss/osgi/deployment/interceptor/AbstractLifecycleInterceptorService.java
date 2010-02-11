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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jboss.logging.Logger;
import org.jboss.osgi.deployment.internal.InterceptorWrapper;
import org.jboss.osgi.spi.util.ConstantsHelper;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

/**
 * A basic service that manages bundle lifecycle interceptors.
 * 
 * @author thomas.diesler@jboss.com
 * @since 15-Oct-2009
 */
public abstract class AbstractLifecycleInterceptorService implements LifecycleInterceptorService 
{
   // Provide logging
   private static final Logger log = Logger.getLogger(AbstractLifecycleInterceptorService.class);

   // The system bundle context
   private BundleContext context;
   
   // The interceptor chain
   private List<LifecycleInterceptor> interceptorChain = new ArrayList<LifecycleInterceptor>();
   
   protected AbstractLifecycleInterceptorService(BundleContext context)
   {
      if (context == null)
         throw new IllegalStateException("Null context");
      this.context = context;
      
      // Track the LifecycleInterceptor services
      ServiceTracker tracker = new ServiceTracker(context, LifecycleInterceptor.class.getName(), null)
      {
         @Override
         public Object addingService(ServiceReference reference)
         {
            LifecycleInterceptor interceptor = (LifecycleInterceptor)super.addingService(reference);
            addInterceptor(interceptor);
            return interceptor;
         }

         @Override
         public void removedService(ServiceReference reference, Object service)
         {
            super.removedService(reference, service);
            LifecycleInterceptor interceptor = (LifecycleInterceptor)service;
            removeInterceptor(interceptor);
         }
      };
      tracker.open();
   }
   
   public BundleContext getSystemContext()
   {
      return context;
   }

   /**
    * Add a LifecycleInterceptor to the service.
    * 
    * The interceptor is added according to its input requirements 
    * and relative order. 
    * 
    * @param interceptor The interceptor
    */
   protected void addInterceptor(LifecycleInterceptor interceptor)
   {
      if (interceptor == null)
         throw new IllegalArgumentException("Null interceptor");
      
      log.debug("Add interceptor: " + new InterceptorWrapper(interceptor));
      
      synchronized (interceptorChain)
      {
         Set<LifecycleInterceptor> unsortedSet = new HashSet<LifecycleInterceptor>();
         unsortedSet.addAll(interceptorChain);
         unsortedSet.add(interceptor);
         
         List<LifecycleInterceptor> sortedList = new ArrayList<LifecycleInterceptor>();
         
         // Add interceptors with no inputs first 
         Iterator<LifecycleInterceptor> itUnsorted = unsortedSet.iterator();
         while(itUnsorted.hasNext())
         {
            LifecycleInterceptor aux = itUnsorted.next();
            if (aux.getInput() == null)
            {
               addWithRelativeOrder(sortedList, aux);
               itUnsorted.remove();
            }
         }

         // Get the set of provided outputs
         Set<Class<?>> providedOutputs = new HashSet<Class<?>>();
         for (LifecycleInterceptor aux : sortedList)
         {
            Set<Class<?>> auxOutput = aux.getOutput();
            if (auxOutput != null)
               providedOutputs.addAll(auxOutput);
         }
         
         // Add interceptors with sattisfied inputs 
         itUnsorted = unsortedSet.iterator();
         while(itUnsorted.hasNext())
         {
            LifecycleInterceptor aux = itUnsorted.next();
            Set<Class<?>> input = aux.getInput();
            if (input == null)
               throw new IllegalStateException("Interceptor with no inputs should have been added already");
            
            if (providedOutputs.containsAll(input))
            {
               addWithRelativeOrder(sortedList, aux);
               itUnsorted.remove();
            }
         }

         // Add the remaining interceptors
         for (LifecycleInterceptor aux : unsortedSet)
         {
            addWithRelativeOrder(sortedList, aux);
         }
         
         // Log the interceptor order
         StringBuffer buffer = new StringBuffer();
         for (LifecycleInterceptor aux : sortedList)
         {
            InterceptorWrapper wrapper = new InterceptorWrapper(aux);
            buffer.append("\n  " + wrapper.toLongString());
         }
         log.debug("Resulting interceptor chain" + buffer.toString());
         
         // Use the sorted result as the new interceptor chain
         interceptorChain.clear();
         interceptorChain.addAll(sortedList);
      }
   }

   private void addWithRelativeOrder(List<LifecycleInterceptor> sortedList, LifecycleInterceptor interceptor)
   {
      Set<Class<?>> providedOutputs = new HashSet<Class<?>>();
      int relOrder = interceptor.getRelativeOrder();
      Set<Class<?>> input = interceptor.getInput();
      
      for (int i = 0; i < sortedList.size(); i++)
      {
         LifecycleInterceptor aux = sortedList.get(i);
         int auxOrder = aux.getRelativeOrder();
         
         // Add if all inputs are satisfied and the rel order is less or equal
         boolean inputsProvided = (input == null || providedOutputs.containsAll(input));
         if (inputsProvided && relOrder <= auxOrder)
         {
            sortedList.add(i, interceptor);
            return;
         }

         // Add the this interceptor output the list
         Set<Class<?>> auxOutput = aux.getOutput();
         if (auxOutput != null)
            providedOutputs.addAll(auxOutput);
      }
      
      // If not added yet, add at end
      sortedList.add(interceptor);
   }
   
   /**
    * Remove an LifecycleInterceptor to the service.
    * 
    * @param interceptor The interceptor
    */
   protected void removeInterceptor(LifecycleInterceptor interceptor)
   {
      if (interceptor == null)
         throw new IllegalArgumentException("Null interceptor");
      
      log.debug("Remove interceptor: " + new InterceptorWrapper(interceptor));
      
      synchronized (interceptorChain)
      {
         interceptorChain.remove(interceptor);
      }
   }

   protected List<LifecycleInterceptor> getInterceptorChain()
   {
      synchronized (interceptorChain)
      {
         return Collections.unmodifiableList(interceptorChain);
      }
   }

   /**
    * Invoke the registered set of interceptors for the given bundle state change.
    *  
    * @param state The future state of the bundle
    * @param bundle The bundle that changes state
    * @throws LifecycleInterceptorException if the invocation of an interceptor fails 
    */
   public void handleStateChange(int state, Bundle bundle)
   {
      synchronized (interceptorChain)
      {
         // Nothing to do
         if (interceptorChain.size() == 0)
            return;
         
         InvocationContext inv = getInvocationContext(bundle);
         if (inv == null)
            throw new IllegalStateException("Cannot get invocation context for: " + bundle);
         
         // Call the interceptor chain
         for (LifecycleInterceptor aux : interceptorChain)
         {
            Set<Class<?>> input = aux.getInput();
            
            boolean doInvocation = true;
            if (input != null)
            {
               // Check if all required input is available 
               for (Class<?> clazz : input)
               {
                  if (inv.getAttachment(clazz) == null)
                  {
                     doInvocation = false;
                     break;
                  }
               }
            }
            
            if (doInvocation == true)
            {
               InterceptorWrapper wrapper = new InterceptorWrapper(aux);
               String stateName = ConstantsHelper.bundleState(state);
               String location = inv.getBundle().getLocation();
               log.trace("Invoke: " + wrapper + " with state " + stateName + " on " + location);
               aux.invoke(state, inv);
            }
         }
      }
   }

   /**
    * Get the InvocationContext for the given bundle. 
    */
   protected abstract InvocationContext getInvocationContext(Bundle bundle);
}