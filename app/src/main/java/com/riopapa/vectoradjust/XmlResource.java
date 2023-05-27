package com.riopapa.vectoradjust;

import android.content.res.Resources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class XmlResource {
    public String readXML(Resources res, String file) {
        BufferedReader in = null;
        try {//  w  ww  . j  a  va 2  s  . co  m
            in = new BufferedReader(new InputStreamReader(res.getAssets()
                    .open(file)));
            String line;
            StringBuilder buffer = new StringBuilder();
            while ((line = in.readLine()) != null)
                buffer.append(line).append('\n');
            return buffer.toString();
        } catch (IOException e) {
            return "";
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }
    }
}
