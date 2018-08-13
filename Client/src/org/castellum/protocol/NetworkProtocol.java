package org.castellum.protocol;

public class NetworkProtocol {

    public static byte LOGIN = 0;

    public static byte SELECT_DATABASE = 4;
    public static byte CREATE_DATABASE = 1;
    public static byte REMOVE_DATABASE = 5;
    public static byte GET_DATABASE = 11;

    public static byte CREATE_TABLE = 2;
    public static byte REMOVE_TABLE = 6;
    public static byte GET_TABLE = 12;

    public static byte CREATE_FIELD = 3;
    public static byte REMOVE_FIELD = 7;

    public static byte INSERT_VALUE = 8;
    public static byte REMOVE_VALUE = 9;
    public static byte SELECT_VALUE = 10;

}
