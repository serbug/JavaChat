package org.serbug;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

interface IOperation {
    CompletableFuture<String> asyncRead();
    CompletableFuture<Void> asyncWrite(String message);
    CompletableFuture<Void> asyncReload();
}
