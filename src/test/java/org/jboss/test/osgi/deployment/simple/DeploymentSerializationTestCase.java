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
package org.jboss.test.osgi.deployment.simple;

//$Id$

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.jboss.osgi.deployment.deployer.Deployment;
import org.jboss.osgi.deployment.internal.DeploymentImpl;
import org.jboss.osgi.spi.util.BundleInfo;
import org.junit.Test;

/**
 * Test Deployment serialization
 * 
 * @author thomas.diesler@jboss.com
 * @since 12-Nov-2009
 */
public class DeploymentSerializationTestCase
{
   @Test
   public void testSerializeBundleInfo() throws Exception
   {
      BundleInfo info = getBundleInfo();
      
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream(baos);
      oos.writeObject(info);
      baos.close();
      
      ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
      ObjectInputStream ois = new ObjectInputStream(bais);
      Object res = ois.readObject();
      assertTrue("Instance of BundleInfo: " + res, res instanceof BundleInfo);
      
      assertEquals(info, res);
   }

   @Test
   public void testSerializeDeployment() throws Exception
   {
      BundleInfo info = getBundleInfo();
      Deployment dep = new DeploymentImpl(info);
      
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream(baos);
      oos.writeObject(dep);
      baos.close();
      
      ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
      ObjectInputStream ois = new ObjectInputStream(bais);
      Object res = ois.readObject();
      assertTrue("Instance of Deployment: " + res, res instanceof Deployment);
      
      assertEquals(dep, res);
   }

   private BundleInfo getBundleInfo() throws Exception
   {
      File file = new File("target/test-libs/simple-bundle.jar");
      assertTrue("File exists: " + file, file.exists());
      
      BundleInfo info = BundleInfo.createBundleInfo(file.toURI().toURL());
      return info;
   }
}
