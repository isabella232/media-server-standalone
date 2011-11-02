/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and individual contributors
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
package org.mobicents.media.server.mgcp.tx;

import org.mobicents.media.server.scheduler.TaskChain;
import org.mobicents.media.server.scheduler.Task;
import java.util.Random;
import org.mobicents.media.server.scheduler.Scheduler;
import org.mobicents.media.server.scheduler.DefaultClock;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author kulikov
 */
public class TransactionManagerTest {
    
    private DefaultClock clock;
    private Scheduler scheduler;
    
    private TransactionManager txManager;
    
    private Action action;
    
    public TransactionManagerTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
        clock = new DefaultClock();
        
        scheduler = new Scheduler();
        scheduler.setClock(clock);
        scheduler.start();
        
        txManager = new TransactionManager(scheduler, 3);        
        action = new Action();
        
        TaskChain actionHandler = new TaskChain(2);
        actionHandler.add(new MyTask(scheduler));
        actionHandler.add(new MyTask(scheduler));
        
        action.setActionHandler(actionHandler);
    }
    
    @After
    public void tearDown() {
        scheduler.stop();
    }

    /**
     * Test of find method, of class TransactionManager.
     */
    @Test
    public void testFind() {
        Transaction tx = txManager.find(1);
        assertTrue("Transaction not found", tx != null);

        tx = txManager.find(2);
        assertTrue("Transaction not found", tx != null);
        
        tx = txManager.find(3);
        assertTrue("Transaction not found", tx != null);
        
        tx = txManager.find(4);
        assertTrue("Transaction still in pool", tx == null);
    }
    
    @Test
    public void testTermination() {
        Transaction tx = txManager.find(1);
        assertTrue("Transaction not found", tx != null);        
        assertEquals(2, txManager.remainder());
        
        txManager.terminate(tx);
        assertEquals(3, txManager.remainder());
    }
    
    public void testTransaction(int id) throws InterruptedException {
        Transaction tx = txManager.find(id);
        tx.process(action);
        
        Thread.sleep(1500);
        assertEquals(3, txManager.remainder());
    }
    
    @Test
    public void testExecution() throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            System.out.println("Transaction: Test# " + i);
            testTransaction(i);
        }
    }

    private class MyTask extends Task {
        private Random rnd = new Random();
        
        public MyTask(Scheduler scheduler) {
            super(scheduler);
        }

        public int getQueueNumber()
        {
        	return scheduler.MANAGEMENT_QUEUE;
        }

        @Override
        public long perform() {
            boolean flag = rnd.nextBoolean();
System.out.println("TXID=" + action.transaction().getId())            ;
            if (flag) {
                throw new IllegalStateException();
            }
            
            return 0;
        }
    
    }
}