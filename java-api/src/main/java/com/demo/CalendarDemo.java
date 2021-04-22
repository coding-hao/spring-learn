package com.demo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CalendarDemo {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("开始");
        CompletableFuture<Integer> future
                = CompletableFuture.supplyAsync(() ->1);
        future.thenApplyAsync(x -> x + 1)   // first step
                .thenApplyAsync(x -> x + 1)   // second step
                .thenAccept(x -> System.out.println(x)); // third step

        Thread.sleep(5000);

    }
}
