package com.example.demo.handler;

import java.util.List;

public interface MatchingEngine {


    List<Decision> exceute(Order order);
}
