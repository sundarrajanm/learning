package com.sundar.learn;

import io.reactivex.Observable;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

/**
 * Unit test for simple App.
 */
public class AppTest
{
    String result="";

    @Test
    public void returnAValue(){
        result = "";
        Observable<String> observer = Observable.just("Hello");
        observer.subscribe(s -> result=s);
        assertTrue(result.equals("Hello"));
    }
}
