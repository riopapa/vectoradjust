package com.riopapa.vectoradjust;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    TextView tvGo, tvInp, tvOup;
    String oup;
    int p;
    int oX = 24, oY = 24, srcX, srcY;
    String strX, strY, strZ, cmd = "@", nbr, chr, oStr;
    float xR, yR, baseX = -1, baseY = -1, valX = -1, valY = -1, valZ;

    String org;
    String xml = "<vector android:height=\"24dp\"\n" +
            "        android:viewportHeight=\"24\" android:viewportWidth=\"24\"\n" +
            "        android:width=\"24dp\" xmlns:android=\"http://schemas.android.com/apk/res/android\">\n" +
            "    <path android:fillColor=\"@android:color/white\" android:pathData=\"M15.5,14h-0.79l-0.28,-0.27C15.41,12.59 16,11.11 16,9.5 16,5.91 13.09,3 9.5,3S3,5.91 3,9.5 5.91,16 9.5,16c1.61,0 3.09,-0.59 4.23,-1.57l0.27,0.28v0.79l5,4.99L20.49,19l-4.99,-5z\n" +
            "        M9.5,14C7.01,14 5,11.99 5,9.5S7.01,5 9.5,5 14,7.01 14,9.5 11.99,14 9.5,14z\n" +
            "\"/>\n" +
            "</vector>\n" +
            "\n";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvGo = findViewById(R.id.go);
        tvInp = findViewById(R.id.txt_src);
        tvOup = findViewById(R.id.txt_dst);
        tvInp.setText(xml);

        String src[] = xml.split("\n");
        String oup = "";
        String pathData = "pathData";
        for (int i = 0; i < src.length; i++) {
            String s = src[i];
            if (s.contains(pathData)) {
                int p = s.indexOf(pathData);
                int pe = p + pathData.length() + 1;
                oup += s.substring(0, pe+1);
                int p1 = s.indexOf("\"",pe+2);
                if (p1 == -1)
                    p1 = s.length();
                oStr = "";
                String path = pathData(s.substring(pe+1, p1));
                oup += path + s.substring(p1)+"\n";
            } else
                oup += s +"\n";
        }
        oup += "\n";
        tvOup.setText(oup);
        Log.w("oup", oup);
    }


    String pathData(String s) {

        oStr = "";
        p = 0;
        org = s;
        while (s.length() > 0) {
            chr = s.substring(0,1);
            if (chr.equals("M")) {
                s = cmd_M(s);
            } else if (chr.equals("z") || chr.equals("Z")) {
                oStr += chr;
                if (s.length()> 1)
                    s = s.substring(1);
                else
                    break;
            } else if (chr.equals("m")) {
                s = cmd_m(s);
            } else if (chr.equals("L")) {
                s = cmd_L(s);
            } else if (chr.equals("l")) {
                s = cmd_l(s);
            } else if (chr.equals("V")) {
                s = cmd_V(s);
            } else if (chr.equals("v")) {
                s = cmd_v(s);
            } else if (chr.equals("H")) {
                s = cmd_H(s);
            } else if (chr.equals("h")) {
                s = cmd_h(s);
            } else if (chr.equals("a")) {
                s = cmd_a(s);
            } else if (chr.equals("A")) {
                s = cmd_A(s);
            } else if (chr.equals("C")) {
                s = cmd_C(s);
            } else if (chr.equals("c")) {
                s = cmd_c(s);
            } else if (chr.equals("S")) {
                s = cmd_S(s);
            } else if (chr.equals("s")) {
                s = cmd_s(s);
            } else {
                Log.w("Invalid CMd ",chr+" abort");
                valX = 0;
                Log.w("Abort", "div " + (valY / valX));
                break;
            }
        }
        return oStr;
    }
    @NonNull
    private String cmd_M(String s) {
        cmd = chr;
        oStr += cmd;
        s = getXYPos(s.substring(1));
        baseX = valX; baseY = valY;
        oStr += baseX+","+baseY;
        return s;
    }
    @NonNull
    private String cmd_m(String s) {
        cmd = chr;
        oStr += cmd;
        s = getXYPos(s.substring(1));
        baseX += valX; baseY += valY;
        oStr += valX+","+valY;
        return s;
    }
    @NonNull
    private String cmd_C(String s) {
        cmd = chr.toLowerCase();
        oStr += cmd;
        s = s.substring(1);
        while (s.length()>0 && isDigit(s.substring(0,1))) {
            s = getXYPos(s);
            oStr += fmt(valX-baseX)+","+fmt(valY-baseY);
            s = skipBlank(s);
        }
        baseX = valX; baseY = valY;
        return s;
    }
    @NonNull
    private String cmd_c(String s) {
        cmd = chr;
        oStr += cmd;
        s = s.substring(1);
        while (s.length()>0 && isDigit(s.substring(0,1))) {
            s = getXYPos(s);
            oStr += fmt(valX)+","+fmt(valY);
            s = skipBlank(s);
        }
        baseX += valX; baseY += valY;
        return s;
    }
    @NonNull
    private String cmd_S(String s) {
        cmd = chr.toLowerCase();
        oStr += cmd;
        s = s.substring(1);
        while (s.length()>0 && isDigit(s.substring(0,1))) {
            s = getXYPos(s);
            oStr += fmt(valX-baseX)+","+fmt(valY-baseY);
            s = skipBlank(s);
        }
        baseX = valX; baseY = valY;
        return s;
    }
    @NonNull
    private String cmd_s(String s) {
        cmd = chr;
        oStr += cmd;
        s = s.substring(1);
        while (s.length()>0 && isDigit(s.substring(0,1))) {
            s = getXYPos(s);
            oStr += fmt(valX)+","+fmt(valY);
            s = skipBlank(s);
        }
        baseX += valX; baseY += valY;
        return s;
    }
    @NonNull
    private String cmd_A(String s) {
        cmd = chr.toLowerCase();
        oStr += cmd;
        s = s.substring(1);
        s = getXYPos(s);    // rx, ry
        oStr += fmt(valX-baseX)+","+fmt(valY-baseY);
        s = skipBlank(s);
        s = getXYPos(s);    // two flags
        oStr += strX+","+strY;
        s = skipBlank(s);
        s = getXYPos(s);    // final x,y position
        oStr += fmt(valX-baseX)+","+fmt(valY-baseY);
//        oStr += strX+","+strY;
        s = skipBlank(s);
        s = getZVal(s);  // rotation
        oStr += strZ;
        s = skipBlank(s);
        baseX = valX; baseY = valY;
        return s;
    }
    @NonNull
    private String cmd_a(String s) {
        cmd = chr;
        oStr += cmd;
        s = s.substring(1);
        s = getXYPos(s);    // rx, ry
        oStr += strX+","+strY;
        s = skipBlank(s);
        s = getXYPos(s);    // two flags
        oStr += strX+","+strY;
        s = skipBlank(s);
        s = getXYPos(s);    // final x,y position
        oStr += strX+","+strY;
        s = skipBlank(s);
        s = getZVal(s);  // rotation
        oStr += strZ;
        s = skipBlank(s);
        baseX += valX; baseY += valY;
        Log.w("after a",fmt(baseX)+" x "+fmt(baseY));
        return s;
    }
    @NonNull
    private String cmd_L(String s) {
        cmd = chr.toLowerCase();
        oStr += cmd;
        s = s.substring(1);
        while (s.length()>0 && isDigit(s.substring(0,1))) {
            s = getXYPos(s);
            oStr += fmt(valX-baseX)+","+fmt(valY-baseY);
            baseX = valX; baseY = valY;
            s = skipBlank(s);
        }
        return s;
    }
    @NonNull
    private String cmd_l(String s) {
        cmd = chr;
        oStr += cmd;
        s = s.substring(1);
        while (s.length()>0 && isDigit(s.substring(0,1))) {
            s = getXYPos(s);
            oStr += fmt(valX)+","+fmt(valY);
            baseX += valX; baseY += valY;
            s = skipBlank(s);
        }
        return s;
    }

    @NonNull
    private String cmd_V(String s) {
        cmd = chr.toLowerCase();
        oStr += cmd;
        s = s.substring(1);
        while (s.length()>0 && isDigit(s.substring(0,1))) {
            s = getYPos(s);
            oStr += fmt(valY-baseY);
            baseY = valY;
            s = skipBlank(s);
        }
        return s;
    }
    @NonNull
    private String cmd_v(String s) {
        cmd = chr;
        oStr += cmd;
        s = s.substring(1);
        while (s.length()>0 && isDigit(s.substring(0,1))) {
            s = getYPos(s);
            oStr += fmt(valY);
            baseY += valY;
            s = skipBlank(s);
        }
        return s;
    }
    @NonNull
    private String cmd_H(String s) {
        cmd = chr.toLowerCase();
        oStr += cmd;
        s = s.substring(1);
        while (s.length()>0 && isDigit(s.substring(0,1))) {
            s = getXPos(s);
            oStr += fmt(valX-baseX);
            baseX = valX;
            s = skipBlank(s);
        }
        return s;
    }
    @NonNull
    private String cmd_h(String s) {
        cmd = chr.toLowerCase();
        oStr += cmd;
        s = s.substring(1);
        while (s.length()>0 && isDigit(s.substring(0,1))) {
            s = getXPos(s);
            oStr += fmt(valX);
            baseX += valX;
            s = skipBlank(s);
        }
        return s;
    }


    @NonNull
    private String getXYPos(String s) {
        strX = getDigits(s);
        valX = Float.parseFloat(strX);
        s = s.substring(strX.length());
        s = skipBlank(s);
        if (s.charAt(0) == ',') { // y value comming
            s = s.substring(1);
            strY = getDigits(s);
            valY = Float.parseFloat(strY);
            s = s.substring(strY.length());
        } else {
            Log.w(" x,y Err ["+chr+"]", baseX + " x " + baseY + " pos : " + s);
        }
        return s;
    }
    @NonNull
    private String getZVal(String s) {
        strZ = getDigits(s);
        valZ = Float.parseFloat(strZ);
        s = s.substring(strZ.length());
        return s;
    }
    @NonNull
    private String getXPos(String s) {
        strX = getDigits(s);
        valX = Float.parseFloat(strX);
        s = s.substring(strX.length());
        return s;
    }
    @NonNull
    private String getYPos(String s) {
        strY = getDigits(s);
        valY = Float.parseFloat(strY);
        s = s.substring(strY.length());
        return s;
    }

    String getDigits(String s) {
        String p = "";
        int i = 0;
        while (s.length() > 0) {
            String chr = s.substring(0,1);
            if (!"0123456789.-".contains(chr))
                break;
            p += chr;
            s = s.substring(1);
        }
        return p;
    }

    String skipBlank(String inp) {
        if (inp.length() == 0)
            return inp;
        while (inp.charAt(0) == ' ') {
            inp = inp.substring(1);
            oStr += " ";
            if (inp.length() == 0)
                return inp;
        }
        return inp;
    }
    String vector(String s) {
        int p, p1;

        String viewportHeight = "viewportHeight";
        p = s.indexOf(viewportHeight);
        p1 = p + viewportHeight.length()+2;
        if (p > 0) {
            String ss = s.substring(p1, s.indexOf("\"", p1));
            srcY = Integer.parseInt(ss);
            s = s.substring(0,p1)+oX+s.substring(p1+ss.length()+1);
        }
        String viewportWidth = "viewportWidth";
        p = s.indexOf(viewportWidth);
        p1 = p + viewportWidth.length()+2;
        if (p > 0) {
            String ss = s.substring(p1, s.indexOf("\"", p1));
            srcX = Integer.parseInt(ss);
            s = s.substring(0,p1)+oX+s.substring(p1+ss.length()+1);
        }
        xR = oX / srcX; yR = oY / srcY;
        return s;
    }

    boolean isCommand(String name) {
        Pattern ps = Pattern.compile("([a-yA-Y])");    // except z
        Matcher ms = ps.matcher(name);
        return ms.matches();
    }
    boolean isDigit(String chr) {
        if ("0123456789.-".contains(chr))
            return true;
        return false;
    }

    String fmt(float d) {
        if(d == (long) d)
            return String.format("%d",(long)d);
        else
            return String.format("%.2f",d);
    }

    // ↓ ↓ ↓ P E R M I S S I O N    RELATED /////// ↓ ↓ ↓ ↓
    ArrayList<String> permissions = new ArrayList<>();
    private final static int ALL_PERMISSIONS_RESULT = 101;
    ArrayList<String> permissionsToRequest;
    ArrayList<String> permissionsRejected = new ArrayList<>();

    private void askPermission() {
//        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissionsToRequest = findUnAskedPermissions(permissions);
        if (permissionsToRequest.size() != 0) {
            requestPermissions(permissionsToRequest.toArray(new String[0]),
//            requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]),
                    ALL_PERMISSIONS_RESULT);
        }
    }

    private ArrayList<String> findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList <String> result = new ArrayList<>();
        for (String perm : wanted) if (hasPermission(perm)) result.add(perm);
        return result;
    }
    private boolean hasPermission(String permission) {
        return (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ALL_PERMISSIONS_RESULT) {
            for (String perms : permissionsToRequest) {
                if (hasPermission(perms)) {
                    permissionsRejected.add(perms);
                }
            }
            if (permissionsRejected.size() > 0) {
                if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                    String msg = "These permissions are mandatory for the application. Please allow access.";
                    showDialog(msg);
                }
            } else
                Toast.makeText(getApplicationContext(), "Permissions not granted.", Toast.LENGTH_LONG).show();
        }
    }
    private void showDialog(String msg) {
        showMessageOKCancel(msg,
                (dialog, which) -> requestPermissions(permissionsRejected.toArray(
                        new String[0]), ALL_PERMISSIONS_RESULT));
    }
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

// ↑ ↑ ↑ ↑ P E R M I S S I O N    RELATED /////// ↑ ↑ ↑

}