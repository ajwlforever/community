package com.ajwlforever.community.test;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;

public class BlockingQueueTests
{
    public static void main(String[] args) {
        ArrayBlockingQueue<Integer> queue = new ArrayBlockingQueue<>(10);
        //一个生产者两个消费者
        new Thread(new Producer(queue)).start();
        new Thread(new Consumer(queue)).start();
        new Thread(new Consumer(queue)).start();
        new Thread(new Consumer(queue)).start();
    }

}

class Producer implements  Runnable
{
    ArrayBlockingQueue<Integer> queue;

    public Producer(ArrayBlockingQueue<Integer> queue)
    {
        this.queue = queue;

    }

    @Override
    public void run() {
            try
            {
                for(int i = 0; i < 100; ++i)
                {
                    Thread.sleep(20);
                    queue.put(i);
                    System.out.println(Thread.currentThread().getName()+"生产了:"+queue.size());
                }
            }catch (Exception e)
            {
                e.printStackTrace();
            }
    }
}

class Consumer implements  Runnable
{
    ArrayBlockingQueue<Integer> queue;

    public Consumer(ArrayBlockingQueue<Integer> queue)
    {
        this.queue = queue;

    }

    @Override
    public void run() {
        try
        {
            while (true)
            {
                Thread.sleep(new Random().nextInt(1000));
                queue.take();
                System.out.println(Thread.currentThread().getName()+"消费"+queue.size());
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}