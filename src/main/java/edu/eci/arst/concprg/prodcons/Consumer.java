/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arst.concprg.prodcons;

import java.util.Queue;

/**
 *
 * @author hcadavid
 */
public class Consumer extends Thread{
    
    private Queue<Integer> queue;
    private Object lock;
    
    public Consumer(Queue<Integer> queue, Object lock){
        this.queue=queue;
        this.lock = lock;        
    }
    
    @Override
    public void run() {
        while (true) {

            if (queue.isEmpty()) {
                try {
                    synchronized(lock){
                        lock.wait();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                                            
            }
            
            int elem=queue.poll();
            //System.out.println("Consumer consumes "+elem);    
        }
    }
}
