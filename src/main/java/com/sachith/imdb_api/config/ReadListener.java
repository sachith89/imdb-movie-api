package com.sachith.imdb_api.config;


import org.springframework.batch.core.ItemReadListener;
import org.springframework.stereotype.Component;

@Component
public class ReadListener<T> implements ItemReadListener<T> {

    @Override
    public void beforeRead() {
        System.out.println("ReaderListener::beforeRead()");
    }

    @Override
    public void afterRead(T item) {
        System.out.println("ReaderListener::afterRead() -> " + item);
    }

    @Override
    public void onReadError(Exception ex) {
        System.out.println("ReaderListener::onReadError() -> " + ex);
    }

}