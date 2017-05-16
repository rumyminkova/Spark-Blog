package com.teamtreehouse.blog.model;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Rumy on 5/15/2017.
 */
public class Tags {
    public static ArrayList<String> load() {
        return new ArrayList<String>(Arrays.asList("Tag1", "Tag2", "Tag3","Tag4","Tag5"));
    }
}
