package com.db;

import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
/**
 * Created by jaivalis on 3/9/14.
 */
public class TestMongo {


    public static void main(String[] args) {
        try {
            DateFormat dateformat = new SimpleDateFormat("EE MMM dd yyyy HH:mm:ss 'GMT'Z (zzz)", Locale.ENGLISH);
            MongoDBDataModel mm =
                    new MongoDBDataModel("localhost",
                            27017,
                            "recsys",
                            "3336",
                            false,
                            false,
                            dateformat,
                            "_id",
                            ""
                    );
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }


    }
}
