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

//$Id$

import static org.jboss.osgi.deployment.interceptor.LifecycleInterceptor.RELATIVE_ORDER_DEFAULT;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.jboss.osgi.deployment.interceptor.AbstractLifecycleInterceptor;
import org.jboss.osgi.deployment.interceptor.AbstractLifecycleInterceptorService;
import org.jboss.osgi.deployment.interceptor.InvocationContext;
import org.jboss.osgi.deployment.interceptor.LifecycleInterceptor;
import org.jboss.osgi.deployment.interceptor.LifecycleInterceptorException;
import org.jboss.osgi.deployment.interceptor.LifecycleInterceptorService;
import org.junit.Test;
import org.osgi.framework.Bundle;

/**
 * Test the {@link LifecycleInterceptorService}
 * 
 * @author thomas.diesler@jboss.com
 * @since 19-Oct-2009
 */
public class InterceptorOrderTestCase
{
   static final int RELATIVE_ORDER = RELATIVE_ORDER_DEFAULT + 1000;
   
   @Test
   public void testRelativeOrder()
   {
      LifecycleInterceptor rel0000 = new MockLifecycleInterceptor();
      LifecycleInterceptor rel2000 = new MockLifecycleInterceptor()
      {
         public int getRelativeOrder()
         {
            return RELATIVE_ORDER;
         }
      };

      // Add ordered
      MockLifecycleInterceptorService service = new MockLifecycleInterceptorService();
      service.addInterceptor(rel0000);
      service.addInterceptor(rel2000);

      List<LifecycleInterceptor> chain = service.getInterceptorChain();
      assertEquals(2, chain.size());
      assertEquals(RELATIVE_ORDER_DEFAULT, chain.get(0).getRelativeOrder());
      assertEquals(RELATIVE_ORDER, chain.get(1).getRelativeOrder());

      // Add unordered
      service = new MockLifecycleInterceptorService();
      service.addInterceptor(rel2000);
      service.addInterceptor(rel0000);

      chain = service.getInterceptorChain();
      assertEquals(2, chain.size());
      assertEquals(RELATIVE_ORDER_DEFAULT, chain.get(0).getRelativeOrder());
      assertEquals(RELATIVE_ORDER, chain.get(1).getRelativeOrder());
   }

   @Test
   public void testInputOutput()
   {
      class A
      {
      }

      AbstractLifecycleInterceptor inA = new MockLifecycleInterceptor();
      inA.addInput(A.class);

      AbstractLifecycleInterceptor outA = new MockLifecycleInterceptor();
      outA.addOutput(A.class);

      // Add ordered
      MockLifecycleInterceptorService service = new MockLifecycleInterceptorService();
      service.addInterceptor(outA);
      service.addInterceptor(inA);

      List<LifecycleInterceptor> chain = service.getInterceptorChain();
      assertEquals(2, chain.size());
      assertEquals(outA, chain.get(0));
      assertEquals(inA, chain.get(1));

      // Add unordered
      service = new MockLifecycleInterceptorService();
      service.addInterceptor(inA);
      service.addInterceptor(outA);

      chain = service.getInterceptorChain();
      assertEquals(2, chain.size());
      assertEquals(outA, chain.get(0));
      assertEquals(inA, chain.get(1));
   }
   
   class MockLifecycleInterceptor extends AbstractLifecycleInterceptor
   {
      public void invoke(int state, InvocationContext context) throws LifecycleInterceptorException
      {
         // do nothing
      }
   }
   
   class MockLifecycleInterceptorService extends AbstractLifecycleInterceptorService
   {
      protected MockLifecycleInterceptorService()
      {
         super(new MockBundleContext());
      }

      @Override
      public void addInterceptor(LifecycleInterceptor interceptor)
      {
         super.addInterceptor(interceptor);
      }

      @Override
      public void removeInterceptor(LifecycleInterceptor interceptor)
      {
         super.removeInterceptor(interceptor);
      }

      @Override
      public List<LifecycleInterceptor> getInterceptorChain()
      {
         return super.getInterceptorChain();
      }

      @Override
      protected InvocationContext getInvocationContext(Bundle bundle)
      {
         // do nothing
         return null;
      }
   }
}
