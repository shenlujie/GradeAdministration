package com.example.slj.gradeadministration.utils;


import java.io.Serializable;

public class ResultGson<T> implements Serializable{

    public boolean success ;
    public String message;
    public T object;

}
